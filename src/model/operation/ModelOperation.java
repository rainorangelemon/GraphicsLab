package model.operation;

import model.ModelShape;
import model.ModelStep;

// Operation takes in a shape, and output a new shape

public abstract class ModelOperation extends ModelStep{
	int currentIndex;
	int shapeIndex;
	
	public ModelOperation(int currentIndex, int shapeIndex){
		this.currentIndex = currentIndex;
		this.shapeIndex = shapeIndex;
	}
	
	public abstract ModelShape operate(ModelShape origin);

	public int getCurrentIndex() {
		return currentIndex;
	}

	public int getShapeIndex() {
		return shapeIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public void setShapeIndex(int shapeIndex) {
		this.shapeIndex = shapeIndex;
	}
}
