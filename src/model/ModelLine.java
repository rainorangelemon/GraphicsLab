package model;

import java.util.ArrayList;
import java.util.List;

import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationRotation.Axis;
import javafx.scene.paint.Color;

public class ModelLine extends ModelShape{

	private int x0, x1, y0, y1;
	
	public ModelLine(int x0, int x1, int y0, int y1, Color color) {
		super(color);
		setPos(x0, y0, x1, y1);
	}

	@Override
	protected List<ModelDot> getModelDots(Color[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		result.addAll(drawLine());
		return result;
	}
	
	public int getX0() {
		return x0;
	}

	public int getX1() {
		return x1;
	}

	public int getY0() {
		return y0;
	}

	public int getY1() {
		return y1;
	}

	public void setPos(int x0, int y0, int x1, int y1) {
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
	}
	
	// the Bresenham algorithm
	private List<ModelDot> drawLine(){
		List<ModelDot> result = new ArrayList<ModelDot>();
		int x0 = this.x0; int x1 = this.x1; int y0 = this.y0; int y1 = this.y1;
	    Boolean steep = Math.abs(y1 - y0) > Math.abs(x1 - x0);
	    if(steep){
	        x0 = (x0+y0) - (y0=x0);
	        x1 = (x1+y1) - (y1=x1);
	    }
	    if(x0 > x1){
	    	x1 = (x0+x1) - (x0=x1);
	    	y1 = (y0+y1) - (y0=y1);
	    }
	    int deltax = x1 - x0;
	    int deltay = Math.abs(y1 - y0);
	    int error = deltax / 2;
	    int ystep;
	    int y = y0;
	    if(y0 < y1){
	    	ystep = 1;
	    }else{
	    	ystep = -1;
	    }
	    for(int x=x0;x<=x1;x++){
	        if(steep)
	    		result.add(new ModelDot(y, x, super.getColor()));
	        else 
	    		result.add(new ModelDot(x, y, super.getColor()));
	        error = error - deltay;
	        if(error < 0){
	            y = y + ystep;
	            error = error + deltax;
	        }
	    }
	    return result;
	}

	@Override
	public ModelLine translation(int offsetX, int offsetY, int offsetZ) {
		int newX0 = x0 + offsetX;
		int newX1 = x1 + offsetX;
		int newY0 = y0 + offsetY;
		int newY1 = y1 + offsetY;
		ModelLine result = new ModelLine(newX0, newX1, newY0, newY1, super.getColor());
		return result;
	}

	@Override
	public ModelLine rotation(int rotationX, int rotationY, Axis axis, int rotationDegree) {
		List<ModelDot> newPoint0 = OperationRotation.dotRotation(rotationX, rotationY, rotationDegree, 1.0, new ModelDot(x0, y0, super.getColor()));
		List<ModelDot> newPoint1 = OperationRotation.dotRotation(rotationX, rotationY, rotationDegree, 1.0, new ModelDot(x1, y1, super.getColor())); 
		int newX0 = newPoint0.get(0).getX();
		int newY0 = newPoint0.get(0).getY();
		int newX1 = newPoint1.get(0).getX();
		int newY1 = newPoint1.get(0).getY();
		ModelLine result = new ModelLine(newX0, newX1, newY0, newY1, super.getColor());
		return result;
	}

	@Override
	public ModelLine scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		List<ModelDot> newPoint0 = OperationScaling.dotScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, 1.0, new ModelDot(x0, y0, super.getColor()));
		List<ModelDot> newPoint1 = OperationScaling.dotScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, 1.0, new ModelDot(x1, y1, super.getColor()));
		int newX0 = newPoint0.get(0).getX();
		int newY0 = newPoint0.get(0).getY();
		int newX1 = newPoint1.get(0).getX();
		int newY1 = newPoint1.get(0).getY();
		ModelLine result = new ModelLine(newX0, newX1, newY0, newY1, super.getColor());
		return result;
	}

	
	// Liang-Barsky Algorithm
	@Override
	public ModelShape clip(int Xmin, int Ymin, int Xmax, int Ymax) {
		int deltaX = x1 - x0;
		int deltaY = y1 - y0;
		int[] p = {-deltaX, deltaX, -deltaY, deltaY};
		int[] q = {x0 - Xmin, Xmax - x0, y0 - Ymin, Ymax - y0};
		double u1 = 0.0;
		double u2 = 1.0;
		for(int i=0; i < 4; i++){
			double r = ((double)q[i])/((double)p[i]);
			if(p[i]<0){
				u1 = Math.max(u1, r);
			}else if(p[i]>0){
				u2 = Math.min(u2, r);
			}else{
				if(q[i]<0){
					return new ModelDots(new ArrayList<ModelDot>());
				}else{
					// do nothing
				}
			}
			if(u1 > u2){
				return new ModelDots(new ArrayList<ModelDot>());
			}else{
				// do nothing
			}
		}
		return new ModelLine((int)Math.round(u1*deltaX + x0), (int)Math.round(u2*deltaX + x0), (int)Math.round(u1*deltaY + y0), (int)Math.round(u2*deltaY + y0), super.getColor());
	}
}
