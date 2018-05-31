package model;

import java.util.List;

import javafx.scene.paint.Color;

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

	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
}
