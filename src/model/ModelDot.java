package model;

import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;

public class ModelDot {
	int x, y;
	Color color;
	MeshView meshView; 

	public ModelDot(int x, int y, Color color){
		this.x = x;
		this.y = y;
		this.color = color;
		this.meshView = null;
	}
	
	public ModelDot(MeshView meshView){
		this.x = -1;
		this.y = -1;
		this.meshView = meshView;
	}
	
	public void setPos(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public MeshView getMeshView() {
		return meshView;
	}
	
}
