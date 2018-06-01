package model.operation;

import java.util.ArrayList;
import java.util.List;

import model.ModelShape;
import model.ModelStep;

// Operation takes in a shape, and output a new shape

public abstract class ModelOperation extends ModelStep{
	private int currentIndex;
	private ArrayList<Integer> shapeIndexes;
	
	public ModelOperation(int currentIndex, List<Integer> shapeIndexes){
		this.currentIndex = currentIndex;
		this.shapeIndexes = new ArrayList<Integer>(shapeIndexes);
	}
	
	public ModelOperation(int currentIndex, int shapeIndex){
		this.setCurrentIndex(currentIndex);
		this.setShapeIndex(shapeIndex);
	}
	
	public abstract ModelShape operate(ModelShape origin);

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getShapeIndex() {
		return shapeIndexes.get(0);
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public void setShapeIndex(int shapeIndex) {
		this.shapeIndexes = new ArrayList<Integer>();
		shapeIndexes.add(shapeIndex);
	}

	public ArrayList<Integer> getShapeIndexes() {
		return shapeIndexes;
	}

	public void setShapeIndexes(List<Integer> shapeIndexes) {
		this.shapeIndexes = new ArrayList<Integer>(shapeIndexes);
	}
}
