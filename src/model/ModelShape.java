package model;

import java.util.List;

import javafx.scene.paint.Color;

public abstract class ModelShape {
	private Color color;
	
	public ModelShape(Color color){
		this.color = color;
	}
	
	protected abstract List<ModelDot> getModelDots(ModelDot[][] dots);
	
	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}

}