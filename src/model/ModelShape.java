package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.util.Pair;

public abstract class ModelShape extends ModelStep{
	private Color color;
	
	public ModelShape(Color color){
		this.color = color;
	}
	
	protected ModelShape(ModelShape source){
		this.color = source.color;
	}
	
	protected abstract List<ModelDot> getModelDots(ModelDot[][] dots);
	
	public abstract ModelShape translation(int offsetX, int offsetY);
	
	public abstract ModelShape rotation(int rotationX, int rotationY, int rotationDegree);
	
	public abstract ModelShape scaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY);
	
	public abstract ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1);

	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public static List<ModelDot> pairs2dots(List<Pair<Integer, Integer>> pairs, Color color){
		List<ModelDot> result = new ArrayList<ModelDot>();
		for(Pair<Integer, Integer> pair: pairs){
			result.add(new ModelDot(pair.getKey(), pair.getValue(), color));
		}
		return result;
	}
	
	public static List<Pair<Integer, Integer>> dots2pairs(List<ModelDot> dots){
		List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
		for(ModelDot dot: dots){
			result.add(new Pair<Integer, Integer>(dot.getX(), dot.getY()));
		}
		return result;
	}
}
