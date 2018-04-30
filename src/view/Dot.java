package view;

import model.ModelDot;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Dot extends Circle{
	public Dot(int x, int y, Color color){
		super(x, y, 1, color);
	}
	
	public void changeColor(Color newColor){
		super.setFill(newColor);
	}
	
	public static Dot modelDot2Dot(ModelDot modelDot){
		return new Dot(modelDot.getX(), modelDot.getY(), modelDot.getColor());
	}
}