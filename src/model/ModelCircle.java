package model;

import java.util.ArrayList;
import java.util.List;

import model.operation.OperationClip;
import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationRotation.Axis;
import javafx.scene.paint.Color;

public class ModelCircle extends ModelShape{
	private int xc, yc, a, b;
	private boolean bresenham;
	private List<ModelDot> box = new ArrayList<ModelDot>();
	
	public ModelCircle(int xc, int yc, int a, int b, boolean bresenham, Color color) {
		super(color);
		this.bresenham = bresenham;
		setPos(xc, yc, a, b);
	}
	
	protected ModelCircle(ModelCircle target){
		super(target);
		this.bresenham = target.bresenham;
		setPos(target.xc, target.yc, target.a, target.b);
	}

	@Override
	protected List<ModelDot> getModelDots(Color[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		List<ModelDot> originDots = new ArrayList<ModelDot>();
		if(a==b){
			if(bresenham){
				originDots.addAll(drawBresenhamCircle());
			}else{
				originDots.addAll(drawMidPointCircle());
			}
		}else{
			if(bresenham){
				originDots.addAll(drawBresenhamOval());
			}else{
				originDots.addAll(drawMidPointOval());
			}
		}
		result.addAll(originDots);
		return result;
	}
	

	public int getXc() {
		return xc;
	}

	public int getYc() {
		return yc;
	}

	public int getA() {
		return a;
	}

	public int getB() {
		return b;
	}

	public void setPos(int xc, int yc, int a, int b) {
		this.xc = xc;
		this.yc = yc;
		this.a = a;
		this.b = b;
		
		box.clear();
		int x0 = xc - a;
		int x1 = xc + a;
		int y0 = yc - b;
		int y1 = yc + b;
		Color circleColor = super.getColor();
		for(int y = y0; y <= y1; y ++){
			box.add(new ModelDot(x0, y, circleColor));	
			box.add(new ModelDot(x1, y, circleColor));	
		}
		for(int x = x0; x <= x1; x ++){
			box.add(new ModelDot(x, y0, circleColor));	
			box.add(new ModelDot(x, y1, circleColor));	
		}
	}
	
	

	public List<ModelDot> getBox() {
		return box;
	}

	public boolean isBresenham() {
		return bresenham;
	}

	public void setBresenham(boolean bresenham) {
		this.bresenham = bresenham;
	}

	// the midpoint circle algorithm
	private List<ModelDot> drawMidPointCircle(){
		Color color = super.getColor();
		List<ModelDot> result = new ArrayList<ModelDot>();
		int x=0, y=a, d=1-a;
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
	
	// bresenham in circle
	private List<ModelDot> drawBresenhamCircle(){
		Color color = super.getColor();
		int r = a;
		List<ModelDot> result = new ArrayList<ModelDot>();
		int x,y,d;
		x=0;
		y=r;
		d=3-2*r;
		while(x<y){
			circlePoint(result, x, y, color);
		    if(d<0){
		    	d=d+4*x+6;
		    }else{
				d=d+4*(x-y)+10;
				y--;
		    }
		    x++;
		  }
		  if(x==y){
		    circlePoint(result, x, y, color);
		  }
		  return result;
	}
	
	// midpoint for oval
	private List<ModelDot> drawMidPointOval(){
		List<ModelDot> result = new ArrayList<ModelDot>();
		double sqa = a * a;
		double sqb = b * b;
		double d = sqb + sqa * (-b + 0.25);
		int x = 0;
		int y = b;
		ovalPoint(result, x, y, super.getColor());
		while( sqb * (x + 1) < sqa * (y - 0.5)){
			 if (d < 0)
			 {
				 d += sqb * (2 * x + 3);
			 }else{
				 d += (sqb * (2 * x + 3) + sqa * (-2 * y + 2));
				 y--;
			 }
			 x++;
			 ovalPoint(result, x, y, super.getColor());
		 }
		 d = (b * (x + 0.5)) * 2 + (a * (y - 1)) * 2 - (a * b) * 2;
		 while(y > 0)
		 {
			 if (d < 0){
				 d += sqb * (2 * x + 2) + sqa * (-2 * y + 3);
			 x++;
			 }else{
				 d += sqa * (-2 * y + 3);
			 }
			 y--;
			 ovalPoint(result, x, y, super.getColor());
		 }
		 return result;
	}
	
	// Bresenham for ellipse
	private List<ModelDot> drawBresenhamOval(){
		List<ModelDot> result = new ArrayList<ModelDot>();
		 int power2a=a*a;
		 int power2b=b*b;
		 int x=0;
		 int y=b;
		 int decision=2*power2b-2*b*power2a+power2a;
		 ovalPoint(result, x, y, super.getColor());
		 int P_x = (int) Math.round( (double)power2a/Math.sqrt((double)(power2a+power2b)) );
		 while(x <= P_x){
			 if(decision < 0){
				 decision += 2 * power2b * (2 * x + 3);
			 }else{
				 decision += 2 * power2b * (2 * x + 3) - 4 * power2a * (y - 1);
				 y--;
			 }
			 x++;
			 ovalPoint(result, x, y, super.getColor());
		 }

		 decision = power2b * (x * x + x) + power2a * (y * y - y) - power2a * power2b;
		 while(y >= 0)
		 {
			 ovalPoint(result, x, y, super.getColor());
			 y--;
			 if(decision < 0){
				 x++;
				 decision = decision - 2 * power2a * y - power2a + 2 * power2b * x + 2 * power2b;
			 }else{
				 decision = decision - 2 * power2a * y - power2a;
			 }
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
	
	private void ovalPoint(List<ModelDot> result, int x, int y, Color color) {
		result.add(new ModelDot(xc+x, yc-y, color));
		result.add(new ModelDot(xc+x, yc+y, color));
		result.add(new ModelDot(xc-x, yc+y, color));
		result.add(new ModelDot(xc-x, yc-y, color));
	}

	@Override
	public ModelCircle translation(int offsetX, int offsetY, int offsetZ) {
		int newXc = xc + offsetX;
		int newYc = yc + offsetY;
		ModelCircle newCircle = new ModelCircle(this);
		newCircle.setPos(newXc, newYc, a, b);
		return newCircle;
	}

	@Override
	public ModelShape rotation(int rotationX, int rotationY, Axis axis, int rotationDegree) {
		if(rotationDegree==0){
			ModelCircle newCircle = new ModelCircle(this);
			return newCircle;
		}else{
			ArrayList<ModelDot> originDots = new ArrayList<ModelDot>(this.getModelDots(null));
			return new ModelDots(OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, true, 0.3, originDots));
		}
	}

	@Override
	public ModelCircle scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		List<ModelDot> newCenter = OperationScaling.dotScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, 1.0, new ModelDot(xc, yc, super.getColor()));
		int newXc = newCenter.get(0).getX();
		int newYc = newCenter.get(0).getY();
		int newA = (int) Math.round((double)a * scaleSizeX);
		int newB= (int) Math.round((double)b* scaleSizeY);
		ModelCircle newCircle = new ModelCircle(this);
		newCircle.setPos(newXc, newYc, newA, newB);
		return newCircle;
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		List<ModelDot> dots = this.getModelDots(null);
		List<ModelDot> newDots = OperationClip.dotsClip(windowX0, windowY0, windowX1, windowY1, dots);
		ModelDots result = new ModelDots(newDots);
		return result;
	}

}
