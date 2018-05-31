package model.operation;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.util.Pair;
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
	
	public static void dotsRotation(int rotationX, int rotationY, int rotationDegree, Color color, ArrayList<Pair<Integer, Integer>> dots) {
		int size = dots.size();
		for(int i=0; i<size; i++){
			Pair<Integer, Integer> oldDot = dots.get(i);
			List<ModelDot> newDot = dotRotation(rotationX, rotationY, rotationDegree, 1.0, new ModelDot(oldDot.getKey(), oldDot.getValue(), color));
			dots.remove(i);
			dots.trimToSize();
			dots.add(i, new Pair<Integer, Integer>(newDot.get(0).getX(), newDot.get(0).getY()));
		}
	}

}