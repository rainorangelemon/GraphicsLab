package model.operation;

import java.util.ArrayList;

import model.ModelDot;
import model.ModelShape;

public class OperationTranslation extends ModelOperation{
	private int translationX, translationY;
	private int translationZ;
	
	public OperationTranslation(int currentIndex, int shapeIndex, int translationX, int translationY) {
		super(currentIndex, shapeIndex);
		this.translationX = translationX;
		this.translationY = translationY;
		this.translationZ = 0;
	}

	@Override
	public ModelShape operate(ModelShape origin) {
		return origin.translation(translationX, translationY, translationZ);
	}
	
	public static ModelDot dotTranslation(int offsetX, int offsetY, ModelDot dot){
		return new ModelDot(dot.getX()+offsetX, dot.getY()+offsetY, dot.getColor());
	}
	
	public static ArrayList<ModelDot> dotsTranslation(int offsetX, int offsetY, boolean newList, ArrayList<ModelDot> dots){
		int size = dots.size();
		ArrayList<ModelDot> result = new ArrayList<ModelDot>();
		for(int i=0; i<size; i++){
			ModelDot oldDot = dots.get(i);
			ModelDot newDot = dotTranslation(offsetX, offsetY, new ModelDot(oldDot.getX(), oldDot.getY(), oldDot.getColor()));
			if(!newList){
				dots.remove(i);
				dots.add(i, newDot);
			}else{
				result.add(newDot);
			}
		}
		if(!newList){
			dots.trimToSize();
			return dots;
		}else{
			return result;
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

	public int getTranslationZ() {
		return translationZ;
	}

	public void setTranslationZ(int transalationZ) {
		this.translationZ = transalationZ;
	}
	
}
