package model.operation;

import java.util.ArrayList;
import java.util.List;

import model.ModelDot;
import model.ModelShape;

public class OperationScaling extends ModelOperation{
	private int scalePointX = 0, scalePointY = 0;
	private double scaleSizeX = 1.0, scaleSizeY = 1.0;
	
	public OperationScaling(int currentIndex, int shapeIndex, int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY){
		super(currentIndex, shapeIndex);
		this.scalePointX = scalePointX;
		this.scalePointY = scalePointY;
		this.scaleSizeX = scaleSizeX;
		this.scaleSizeY = scaleSizeY;
	}

	@Override
	public ModelShape operate(ModelShape origin) {
		return origin.scaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY);
	}
	
	public static List<ModelDot> dotScaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY, double sampleStep, ModelDot dot){
		List<ModelDot> result = new ArrayList<ModelDot>();
		// 这里进行超采样
		for(double x = dot.getX(); (x-dot.getX())<1; x+=sampleStep){
			for(double y = dot.getY(); (y-dot.getY())<1; y+=sampleStep){
				double x1 = x * scaleSizeX + (double)scalePointX * (1-scaleSizeX);
				double y1 = y * scaleSizeY + (double)scalePointY * (1-scaleSizeY);
				result.add(new ModelDot((int)Math.round(x1), (int)Math.round(y1), dot.getColor()));
			}
		}
		return result;
	}
	
	public static ArrayList<ModelDot> dotsScaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY, boolean newList, double sampleStep, ArrayList<ModelDot> dots) {
		int size = dots.size();
		ArrayList<ModelDot> result = new ArrayList<ModelDot>();
		for(int i=0; i<size; i++){
			ModelDot oldDot = dots.get(i);
			List<ModelDot> newDot = dotScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, 1.0, new ModelDot(oldDot.getX(), oldDot.getY(), oldDot.getColor()));
			if(!newList){
				dots.remove(i);
				dots.add(i, newDot.get(0));
			}else{
				result.addAll(newDot);
			}
		}
		if(!newList){
			dots.trimToSize();
			return dots;
		}else{
			return result;
		}
	}

	public int getScalePointX() {
		return scalePointX;
	}

	public void setScalePointX(int scalePointX) {
		this.scalePointX = scalePointX;
	}

	public int getScalePointY() {
		return scalePointY;
	}

	public void setScalePointY(int scalePointY) {
		this.scalePointY = scalePointY;
	}

	public double getScaleSizeX() {
		return scaleSizeX;
	}

	public void setScaleSizeX(double scaleSizeX) {
		this.scaleSizeX = scaleSizeX;
	}

	public double getScaleSizeY() {
		return scaleSizeY;
	}

	public void setScaleSizeY(double scaleSizeY) {
		this.scaleSizeY = scaleSizeY;
	}
}
