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
		OperationTranslation.dotsTranslation(offsetX, offsetY, super.getColor(), result.vertices);
		return result;
	}

	@Override
	public ModelPolygon rotation(int rotationX, int rotationY, int rotationDegree) {
		ModelPolygon result = new ModelPolygon(this);
		OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, super.getColor(), result.vertices);
		return result;
	}

	@Override
	public ModelPolygon scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		ModelPolygon result = new ModelPolygon(this);
		OperationScaling.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, super.getColor(), result.vertices);
		return result;
	}
	
}
