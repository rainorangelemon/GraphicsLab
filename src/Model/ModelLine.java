package Model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class ModelLine extends ModelShape{

	private int x0, x1, y0, y1;
	
	public ModelLine(int x0, int x1, int y0, int y1, Color color) {
		super(color);
		setPos(x0, y0, x1, y1);
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
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
}
