package ui.editor;

import model.ModelShape;
import view.SketchPad;

public abstract class ShapeEditor {
	public abstract void editShape(SketchPad paper, ModelShape shape);
	
	public static boolean isOverlap(int x0, int y0, int x1, int y1){
		if((Math.abs(x1-x0)<=10)&&(Math.abs(y1-y0))<=10){
			return true;
		}
		return false;
	}
	
	public static boolean isOverlap(int x0, int x1){
		if(Math.abs(x1-x0)<=10){
			return true;
		}
		return false;
	}
}
