package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelBrevier extends ModelShape{

	private ArrayList<Pair<Integer, Integer>> interpolationDots;
	private int width, height;
	
	public ModelBrevier(List<Pair<Integer, Integer>> interpolationDots, int width, int height, Color color) {
		super(color);
		this.height = height;
		this.width = width;
		this.interpolationDots = new ArrayList<Pair<Integer, Integer>>(interpolationDots);
	}
	
	public List<Pair<Integer, Integer>> getInterpolationDots() {
		return interpolationDots;
	}

	public void setInterpolationDots(List<Pair<Integer, Integer>> interpolationDots) {
		this.interpolationDots = new ArrayList<Pair<Integer, Integer>>(interpolationDots);
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		if(interpolationDots.size()==0){
			return result;
		}
		int n = this.interpolationDots.size() - 1;
		double iter_step =  1.0/((n+1)*Math.sqrt(new Double(height*height + width*width)));
		double iter_id;
		for (iter_id = 0; iter_id <= 1; iter_id += iter_step) {  
			  
				Double[] px = new Double[n+1]; 
				Double[] py = new Double[n+1];
	            for (int i = 0; i <= n; i++) {  
	            	px[i] = new Double(this.interpolationDots.get(i).getKey());
	            	py[i] = new Double(this.interpolationDots.get(i).getValue()); 
	            }  
	  
	            for (int r = 1; r <= n; r++) {  
	                for (int i = 0; i <= n - r; i++) {  
	                    px[i] = (1 - iter_id) * px[i] + iter_id * px[i + 1];  
	                    py[i] = (1 - iter_id) * py[i] + iter_id * py[i + 1];  
	                }  
	            }
	            result.add(new ModelDot(px[0].intValue(), py[0].intValue(), super.getColor())); 
	        }  
		return result;
	}

	@Override
	protected void subTranslation(int offsetX, int offsetY) {
		super.dotsTranslation(offsetX, offsetY, super.getColor(), interpolationDots);
		super.resetTranslation();
	}

	@Override
	protected void subRotation(int rotationX, int rotationY, int rotationDegree) {
		super.dotsRotation(rotationX, rotationY, rotationDegree, super.getColor(), interpolationDots);
		super.resetRotation();
	}

	@Override
	protected void subScaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		super.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, super.getColor(), interpolationDots);
		super.resetScaling();
	}

}
