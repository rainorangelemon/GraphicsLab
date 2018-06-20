package model.operation;

import java.util.ArrayList;
import java.util.List;

import model.ModelDot;
import model.ModelShape;

public class OperationRotation extends ModelOperation{

	private int rotationX, rotationY, rotationDegree;
	
	public OperationRotation(int currentIndex, int shapeIndex, int rotationX, int rotationY, int rotationDegree) {
		super(currentIndex, shapeIndex);
		this.rotationDegree = rotationDegree;
		this.rotationX = rotationX;
		this.rotationY = rotationY;
	}

	@Override
	public ModelShape operate(ModelShape origin) {
		return origin.rotation(rotationX, rotationY, rotationDegree);
	}

	public int getRotationX() {
		return rotationX;
	}

	public void setRotationX(int rotationX) {
		this.rotationX = rotationX;
	}

	public int getRotationY() {
		return rotationY;
	}

	public void setRotationY(int rotationY) {
		this.rotationY = rotationY;
	}

	public int getRotationDegree() {
		return rotationDegree;
	}

	public void setRotationDegree(int rotationDegree) {
		this.rotationDegree = rotationDegree;
	}
	
	public static List<ModelDot> dotRotation(int rotationX, int rotationY, int rotationDegree, double sampleStep, ModelDot dot){
		List<ModelDot> result = new ArrayList<ModelDot>();
		rotationDegree = rotationDegree % 360;
		// 这里进行超采样
		for(double x = dot.getX(); (x-dot.getX())<1.0; x+=sampleStep){
			for(double y = dot.getY(); (y-dot.getY())<1.0; y+=sampleStep){
				double vectorX = x - (double)rotationX;
				double vectorY = y - (double)rotationY;
				double theta = ((double)rotationDegree)*Math.PI/180.0;
				double x1 = (double)rotationX + vectorX * Math.cos(theta) - vectorY * Math.sin(theta);
				double y1 = (double)rotationY + vectorX * Math.sin(theta) + vectorY * Math.cos(theta);
				result.add(new ModelDot((int)Math.round(x1), (int)Math.round(y1), dot.getColor()));
			}
		}
		return result;
	}
	
	public static ArrayList<ModelDot> dotsRotation(int rotationX, int rotationY, int rotationDegree, boolean newList, double grain, ArrayList<ModelDot> dots) {
		int size = dots.size();
		ArrayList<ModelDot> result = new ArrayList<ModelDot>();
		for(int i=0; i<size; i++){
			ModelDot oldDot = dots.get(i);
			List<ModelDot> newDot = dotRotation(rotationX, rotationY, rotationDegree, grain, new ModelDot(oldDot.getX(), oldDot.getY(), oldDot.getColor()));
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

}