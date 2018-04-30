package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class ModelCircle extends ModelShape{
	private int xc, yc, radius;
	
	public ModelCircle(int xc, int yc, int radius, Color color) {
		super(color);
		setPos(xc, yc, radius);
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		result.addAll(drawCircle());
		return result;
	}
	

	public int getXc() {
		return xc;
	}

	public int getYc() {
		return yc;
	}

	public int getRadius() {
		return radius;
	}

	public void setPos(int xc, int yc, int radius) {
		this.xc = xc;
		this.yc = yc;
		this.radius = radius;
	}
	
	// the midpoint circle algorithm
	private List<ModelDot> drawCircle(){
		Color color = super.getColor();
		List<ModelDot> result = new ArrayList<ModelDot>();
		int x=0, y=radius, d=1-radius;
		circlePoint(result, x, y, color); 
		while(y > x){ 
			if(d<0){ 
				d+=x*2+3; 
				x++; 
			} else{ 
				d+=(x-y)*2+5; 
				x++;
				y--; 
			} 
			circlePoint(result, x, y, color); 
		}
	    return result;
	}

	private void circlePoint(List<ModelDot> result, int x, int y, Color color) {
		result.add(new ModelDot(xc+x, yc-y, color));
		result.add(new ModelDot(xc+x, yc+y, color));
		result.add(new ModelDot(xc-x, yc+y, color));
		result.add(new ModelDot(xc-x, yc-y, color));
		result.add(new ModelDot(xc+y, yc-x, color));
		result.add(new ModelDot(xc+y, yc+x, color));
		result.add(new ModelDot(xc-y, yc+x, color));
		result.add(new ModelDot(xc-y, yc-x, color));
	}
}
