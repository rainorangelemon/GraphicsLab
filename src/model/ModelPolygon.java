package model;

import java.util.ArrayList;
import java.util.List;

import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationTranslation;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelPolygon extends ModelShape{
	private ArrayList<Pair<Integer, Integer>> vertices;
	
	public ModelPolygon(Color color, List<Pair<Integer, Integer>> vertices){
		super(color);
		this.vertices = new ArrayList<Pair<Integer, Integer>>(vertices);
	}
	
	protected ModelPolygon(ModelPolygon source){
		super(source);
		this.vertices = new ArrayList<Pair<Integer, Integer>>();
		for(Pair<Integer, Integer> pair: source.vertices){
			vertices.add(new Pair<Integer, Integer>(pair.getKey(), pair.getValue()));
		}
	}
	
	public List<Pair<Integer, Integer>> getVertices() {
		return vertices;
	}

	public void setVertices(List<Pair<Integer, Integer>> vertices) {
		this.vertices = new ArrayList<Pair<Integer, Integer>>(vertices);
	}

	public static boolean checkConvex(List<Pair<Integer, Integer>> vertices){
        int num = vertices.size();
        boolean isNegative = false;
        boolean isPositive = false;
        for (int i = 0; i < num;i++)  
        {  
             if((isNegative==true)&&(isPositive==true)){
            	 return false;
             }else{
            	 int direction = crossProduct(vertices.get(i), vertices.get((i+1)%num), vertices.get((i+2)%num));
            	 isNegative = (isNegative==true)?true:(direction<0);
            	 isPositive = (isNegative==true)?true:(direction>0);
             }
        }  
        return true;  
	}
	
	private static int crossProduct(Pair<Integer, Integer> p0, Pair<Integer, Integer> p1, Pair<Integer, Integer> p2){
		Pair<Integer, Integer> number1 = dotMinus(p1, p0);
		Pair<Integer, Integer> number2 = dotMinus(p2, p1);
		int result = number1.getKey()*number2.getValue() - number1.getValue()*number2.getKey();
		return result;
	}
	
	private static Pair<Integer, Integer> dotMinus(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2){
		return new Pair<Integer, Integer>(new Integer(p1.getKey()-p2.getKey()), 
				new Integer(p1.getValue()-p2.getValue()));
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		if(checkConvex(vertices)&&(vertices.size()>=2)){
			for(int i=0; i<vertices.size(); i++){
				Pair<Integer, Integer> point0 = vertices.get(i);
				Pair<Integer, Integer> point1 = vertices.get((i+1)%vertices.size());
				result.addAll(new ModelLine(point0.getKey(), point1.getKey(), 
						point0.getValue(), point1.getValue(), super.getColor()).getModelDots(dots));
			}
		}
		return result;
	}

	@Override
	public ModelPolygon translation(int offsetX, int offsetY) {
		ModelPolygon result = new ModelPolygon(this);
		ArrayList<ModelDot> pivots = new ArrayList<ModelDot>(ModelShape.pairs2dots(result.getVertices(), super.getColor()));
		OperationTranslation.dotsTranslation(offsetX, offsetY, pivots);
		result.setVertices(ModelShape.dots2pairs(pivots));
		return result;
	}

	@Override
	public ModelPolygon rotation(int rotationX, int rotationY, int rotationDegree) {
		ModelPolygon result = new ModelPolygon(this);
		ArrayList<ModelDot> pivots = new ArrayList<ModelDot>(ModelShape.pairs2dots(result.getVertices(), super.getColor()));
		OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, pivots);
		result.setVertices(ModelShape.dots2pairs(pivots));
		return result;
	}

	@Override
	public ModelPolygon scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		ModelPolygon result = new ModelPolygon(this);
		ArrayList<ModelDot> pivots = new ArrayList<ModelDot>(ModelShape.pairs2dots(result.getVertices(), super.getColor()));
		OperationScaling.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, pivots);
		result.setVertices(ModelShape.dots2pairs(pivots));
		return result;
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		if(allInsideWindow(vertices, windowX0, windowY0, windowX1, windowY1)){
			return this;
		}else if(intersectAnySide(vertices, windowX0, windowY0, windowX1, windowY1)){
			List<Pair<Integer, Integer>> tempVer = vertices;
			tempVer = cutPolygonOneSide(tempVer, true, true, windowX0, windowY0, windowX1, windowY1);
			tempVer = cutPolygonOneSide(tempVer, true, false, windowX0, windowY0, windowX1, windowY1);
			tempVer = cutPolygonOneSide(tempVer, false, true, windowX0, windowY0, windowX1, windowY1);
			tempVer = cutPolygonOneSide(tempVer, false, false, windowX0, windowY0, windowX1, windowY1);
			return new ModelPolygon(super.getColor(), tempVer);
		}else{
			return new ModelDots(new ArrayList<ModelDot>());
		}
	}
	
	private boolean allInsideWindow(List<Pair<Integer, Integer>> vertices, int minX, int minY, int maxX, int maxY){
		for(Pair<Integer, Integer> vertice: vertices){
			if(!(insideWindow(vertice.getKey(), vertice.getValue(), true, true, minX, minY, maxX, maxY)
					&&insideWindow(vertice.getKey(), vertice.getValue(), true, false, minX, minY, maxX, maxY)
					&&insideWindow(vertice.getKey(), vertice.getValue(), false, true, minX, minY, maxX, maxY)
					&&insideWindow(vertice.getKey(), vertice.getValue(), false, false, minX, minY, maxX, maxY))){
				return false;
			}
		}
		return true;
	}
	
	private boolean intersectAnySide(List<Pair<Integer, Integer>> vertices, int minX, int minY, int maxX, int maxY){
		List<ModelLine> lines = createModelLine(vertices);
		for(ModelLine line: lines){
			if(lineIntersect(line, minX, minY, maxX, maxY, true, true, true)
					||(lineIntersect(line, minX, minY, maxX, maxY, true, false, true))
					||(lineIntersect(line, minX, minY, maxX, maxY, false, false, true))
					||(lineIntersect(line, minX, minY, maxX, maxY, false, true, true))){
				return true;
			}
		}
		return false;
	}
	
	private List<Pair<Integer, Integer>> cutPolygonOneSide(List<Pair<Integer, Integer>> vertices, boolean leftOrRight, boolean leftOrTop, int minX, int minY, int maxX, int maxY){
		List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		List<ModelLine> lines = createModelLine(vertices);
		for(ModelLine line: lines){
			List<ModelDot> dots = intersectPointOnWindow(line, leftOrRight, leftOrTop, minX, minY, maxX, maxY);
			for(ModelDot dot: dots){
				if(dot!=null){
					result.add(new Pair<Integer, Integer>(dot.getX(), dot.getY()));
				}
			}
		}
		return result;
	}
	
	private List<ModelLine> createModelLine(List<Pair<Integer, Integer>> vertices){
		List<ModelLine> lines = new ArrayList<ModelLine>();
		for(int i=0;i<vertices.size();i++){
			Pair<Integer, Integer> point0 = vertices.get(i);
			Pair<Integer, Integer> point1 = vertices.get((i+1)%vertices.size());
			lines.add(new ModelLine(point0.getKey(), point1.getKey(), 
					point0.getValue(), point1.getValue(), super.getColor()));
		}
		return lines;
	}
	
	public List<ModelDot> intersectPointOnWindow(ModelLine line, boolean leftOrRight, boolean leftOrTop, int minX, int minY, int maxX, int maxY){
		List<ModelDot> result = new ArrayList<ModelDot>();
		if(insideWindow(line.getX0(), line.getY0(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
			if(insideWindow(line.getX1(), line.getY1(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
				result.add(new ModelDot(line.getX1(), line.getY1(), line.getColor()));
			}else if(onWindow(line.getX1(), line.getY1(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
				// do nothing
			}else{
				result.add(intersectPointOnWindowBorder(line, super.getColor(), minX, minY, maxX, maxY, leftOrRight, leftOrTop));
			}
		}else if(onWindow(line.getX0(), line.getY0(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
			result.add(new ModelDot(line.getX0(), line.getY0(), line.getColor()));
			if(insideWindow(line.getX1(), line.getY1(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
				result.add(new ModelDot(line.getX1(), line.getY1(), line.getColor()));
			}else if(onWindow(line.getX1(), line.getY1(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
				// do nothing
			}else{
				// do nothing
			}
		}else{
			if(insideWindow(line.getX1(), line.getY1(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
				result.add(intersectPointOnWindowBorder(line, super.getColor(), minX, minY, maxX, maxY, leftOrRight, leftOrTop));
				result.add(new ModelDot(line.getX1(), line.getY1(), line.getColor()));
			}else if(onWindow(line.getX1(), line.getY1(), leftOrRight, leftOrTop, minX, minY, maxX, maxY)){
				// do nothing
			}else{
				// do nothing
			}
		}
		return result;
	}
	
	public static List<ModelLine> createLineFromWindow(int minX, int minY, int maxX, int maxY, Color color){
		List<ModelLine> result = new ArrayList<ModelLine>();
		result.add(new ModelLine(minX, maxX, minY, minY, color));
		result.add(new ModelLine(maxX, maxX, minY, maxY, color));
		result.add(new ModelLine(maxX, minX, maxY, maxY, color));
		result.add(new ModelLine(minX, minX, maxY, minY, color));
		return result;
	}
	
	public static ModelLine createLineFromWindow(int minX, int minY, int maxX, int maxY, boolean leftOrRight, boolean leftOrTop, Color color){
		ModelLine result;
		if(leftOrRight){
			if(leftOrTop){
				result = new ModelLine(minX, minX, maxY, minY, color);
			}else{
				result = new ModelLine(maxX, maxX, minY, maxY, color);
			}
		}else{
			if(leftOrTop){
				result = new ModelLine(minX, maxX, minY, minY, color);
			}else{
				result = new ModelLine(maxX, minX, maxY, maxY, color);
			}
		}
		return result;
	}
	
	public static boolean insideWindow(int x, int y, boolean leftOrRight, boolean leftOrTop, int minX, int minY, int maxX, int maxY){
		if(leftOrRight){
			if(leftOrTop){
				return (x>minX);
			}else{
				return (x<maxX);
			}
		}else{
			if(leftOrTop){
				return (y>minY);
			}else{
				return (y<maxY);
			}
		}
	}
	
	public static boolean onWindow(int x, int y, boolean leftOrRight, boolean leftOrTop, int minX, int minY, int maxX, int maxY){
		if(leftOrRight){
			if(leftOrTop){
				return (x==minX);
			}else{
				return (x==maxX);
			}
		}else{
			if(leftOrTop){
				return (y==minY);
			}else{
				return (y==maxY);
			}
		}
	}
	
	public static boolean outsideWindow(int x, int y, boolean leftOrRight, boolean leftOrTop, int minX, int minY, int maxX, int maxY){
		if(leftOrRight){
			if(leftOrTop){
				return (x<minX);
			}else{
				return (x>maxX);
			}
		}else{
			if(leftOrTop){
				return (y<minY);
			}else{
				return (y>maxY);
			}
		}
	}
	
	public static ModelDot intersectPointOnWindowBorder(ModelLine line, Color color, int minX, int minY, int maxX, int maxY, boolean leftOrRight, boolean leftOrTop){
		if(lineIntersect(line, minX, minY, maxX, maxY, leftOrRight, leftOrTop, false)){
			if(leftOrRight){
				int x = (leftOrTop)?(minX):(maxX);
				int delta = line.getX1() - line.getX0();
				double u = ((double)x-line.getX0())/((double)delta);
				return new ModelDot(x, (int)Math.round((line.getY1()-line.getY0())*u+line.getY0()), color);
			}else{
				int y = (leftOrTop)?(minY):(maxY);
				int delta = line.getY1() - line.getY0();
				double u = ((double)y-line.getY0())/((double)delta);
				return new ModelDot((int)Math.round((line.getX1()-line.getX0())*u+line.getX0()), y, color);
			}
		}else{
			return null;
		}
	}
	
	// it does not consider the occasion when the line is right on the border!
	public static boolean lineIntersect(ModelLine line, int minX, int minY, int maxX, int maxY, boolean leftOrRight, boolean leftOrTop, boolean finiteBorder){
		if(leftOrRight){
			boolean inRange = (finiteBorder)?intersect(minY, maxY, Math.min(line.getY0(), line.getY1()), Math.max(line.getY0(), line.getY1())):(true);
			if(leftOrTop){
				return ((((minX-line.getX0())*(minX-line.getX1()))<0)&&inRange);
			}else{
				return ((((maxX-line.getX0())*(maxX-line.getX1()))<0)&&inRange);
			}
		}else{
			boolean inRange = (finiteBorder)?intersect(minX, maxX, Math.min(line.getX0(), line.getX1()), Math.max(line.getX0(), line.getX1())):(true);
			if(leftOrTop){
				return ((((minY-line.getY0())*(minY-line.getY1()))<0)&&inRange);
			}else{
				return ((((maxY-line.getY0())*(maxY-line.getY1()))<0)&&inRange);
			}
		}
	}
	
	public static boolean intersect(int min1, int max1, int min2, int max2){
		if((min1>max2)||(min2>max1)){
			return false;
		}else{
			return true;
		}
	}
	
}
