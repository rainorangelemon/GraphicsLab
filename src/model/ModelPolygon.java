package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelPolygon extends ModelShape{
	private List<Pair<Integer, Integer>> vertices;
	
	public ModelPolygon(Color color, List<Pair<Integer, Integer>> vertices){
		super(color);
		this.vertices = vertices;
	}
	
	public List<Pair<Integer, Integer>> getVertices() {
		return vertices;
	}

	public void setVertices(List<Pair<Integer, Integer>> vertices) {
		this.vertices = vertices;
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
	
}
