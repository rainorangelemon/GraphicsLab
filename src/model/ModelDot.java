package model;

import javafx.scene.paint.Color;

public class ModelDot {
	int x, y;
	Color color;

	public ModelDot(int x, int y, Color color){
		this.x = x;
		this.y = y;
		this.color = color;
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
	
}
