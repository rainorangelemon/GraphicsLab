package model.operation;

import java.util.ArrayList;
import java.util.List;

import model.ModelDot;
import model.ModelShape;

public class OperationClip extends ModelOperation{
	private int windowX0, windowY0, windowX1, windowY1;
	
	public OperationClip(int currentIndex, List<Integer> shapeIndexes, int windowX0, int windowY0, int windowX1, int windowY1) {
		super(currentIndex, shapeIndexes);
		this.setWindow(windowX0, windowY0, windowX1, windowY1);
	}

	@Override
	public ModelShape operate(ModelShape origin) {
		setWindow(windowX0, windowY0, windowX1, windowY1);
		return origin.clip(windowX0, windowY0, windowX1, windowY1);
	}
	
	private void setWindow(int windowX0, int windowY0, int windowX1, int windowY1){
		this.windowX0 = Math.min(windowX0, windowX1);
		this.windowX1 = Math.max(windowX0, windowX1);
		this.windowY0 = Math.min(windowY0, windowY1);
		this.windowY1 = Math.max(windowY0, windowY1);
	}
	
	public static ModelDot dotClip(int windowX0, int windowY0, int windowX1, int windowY1, ModelDot dot){
		int x = dot.getX();
		int y = dot.getY();
		if((x>=windowX0)&&(x<=windowX1)&&(y>=windowY0)&&(y<=windowY1)){
			return new ModelDot(x, y, dot.getColor());
		}else{
			return null;
		}
	}
	
	public static List<ModelDot> dotsClip(int windowX0, int windowY0, int windowX1, int windowY1, List<ModelDot> dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		for(ModelDot dot: dots){
			ModelDot newDot = dotClip(windowX0, windowY0, windowX1, windowY1, dot);
			if(newDot!=null){
				result.add(new ModelDot(newDot.getX(), newDot.getY(), newDot.getColor()));
			}else{
				// do nothing
			}
		}
		return result;
	}

	public int getWindowX0() {
		return windowX0;
	}

	public int getWindowY0() {
		return windowY0;
	}

	public int getWindowX1() {
		return windowX1;
	}

	public int getWindowY1() {
		return windowY1;
	}

	public void setWindowX0(int windowX0) {
		this.windowX0 = windowX0;
	}

	public void setWindowY0(int windowY0) {
		this.windowY0 = windowY0;
	}

	public void setWindowX1(int windowX1) {
		this.windowX1 = windowX1;
	}

	public void setWindowY1(int windowY1) {
		this.windowY1 = windowY1;
	}
}
