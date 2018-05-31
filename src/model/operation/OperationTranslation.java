package model.operation;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.ModelDot;
import model.ModelShape;

public class OperationTranslation extends ModelOperation{
	private int translationX, translationY;
	
	public OperationTranslation(int currentIndex, int shapeIndex, int translationX, int translationY) {
		super(currentIndex, shapeIndex);
		this.translationX = translationX;
		this.translationY = translationY;
	}

	@Override
	public ModelShape operate(ModelShape origin) {
		return origin.translation(translationX, translationY);
	}
	
	public static ModelDot dotTranslation(int offsetX, int offsetY, ModelDot dot){
		return new ModelDot(dot.getX()+offsetX, dot.getY()+offsetY, dot.getColor());
	}
	
	public static void dotsTranslation(int offsetX, int offsetY, Color color, ArrayList<Pair<Integer, Integer>> dots){
		int size = dots.size();
		for(int i=0; i<size; i++){
			Pair<Integer, Integer> oldDot = dots.get(i);
			ModelDot newDot = dotTranslation(offsetX, offsetY, new ModelDot(oldDot.getKey(), oldDot.getValue(), color));
			dots.remove(i);
			dots.trimToSize();
			dots.add(i, new Pair<Integer, Integer>(newDot.getX(), newDot.getY()));
		}
	}

	public int getTranslationX() {
		return translationX;
	}

	public void setTranslationX(int translationX) {
		this.translationX = translationX;
	}

	public int getTranslationY() {
		return translationY;
	}

	public void setTranslationY(int translationY) {
		this.translationY = translationY;
	}
	
}
