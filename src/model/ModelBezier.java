package model;

import java.util.ArrayList;
import java.util.List;

import model.operation.OperationClip;
import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationTranslation;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelBezier extends ModelShape{

	private ArrayList<Pair<Integer, Integer>> interpolationDots;
	private int width, height;
	
	public ModelBezier(List<Pair<Integer, Integer>> interpolationDots, int width, int height, Color color) {
		super(color);
		this.height = height;
		this.width = width;
		this.interpolationDots = new ArrayList<Pair<Integer, Integer>>(interpolationDots);
	}
	
	private ModelBezier(ModelBezier newBezier){
		super(newBezier);
		this.height = newBezier.height;
		this.width = newBezier.width;
		this.interpolationDots = new ArrayList<Pair<Integer, Integer>>();
		for(Pair<Integer, Integer> pair: newBezier.interpolationDots){
			this.interpolationDots.add(new Pair<Integer, Integer>(pair.getKey(), pair.getValue()));
		}
	}
	
	public List<Pair<Integer, Integer>> getInterpolationDots() {
		return interpolationDots;
	}

	public void setInterpolationDots(List<Pair<Integer, Integer>> interpolationDots) {
		this.interpolationDots = new ArrayList<Pair<Integer, Integer>>(interpolationDots);
	}

	// Bezier Algorithm
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
	public ModelBezier translation(int offsetX, int offsetY) {
		ModelBezier result = new ModelBezier(this);
		ArrayList<ModelDot> pivots = new ArrayList<ModelDot>(ModelShape.pairs2dots(result.getInterpolationDots(), super.getColor()));
		OperationTranslation.dotsTranslation(offsetX, offsetY, pivots);
		result.setInterpolationDots(ModelShape.dots2pairs(pivots));
		return result;
	}

	@Override
	public ModelBezier rotation(int rotationX, int rotationY, int rotationDegree) {
		ModelBezier result = new ModelBezier(this);
		ArrayList<ModelDot> pivots = new ArrayList<ModelDot>(ModelShape.pairs2dots(result.getInterpolationDots(), super.getColor()));
		OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, false, 1.0, pivots);
		result.setInterpolationDots(ModelShape.dots2pairs(pivots));
		return result;
	}

	@Override
	public ModelBezier scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		ModelBezier result = new ModelBezier(this);
		ArrayList<ModelDot> pivots = new ArrayList<ModelDot>(ModelShape.pairs2dots(result.getInterpolationDots(), super.getColor()));
		OperationScaling.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, false, 1.0, pivots);
		result.setInterpolationDots(ModelShape.dots2pairs(pivots));
		return result;
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1,
			int windowY1) {
		List<ModelDot> dots = this.getModelDots(null);
		List<ModelDot> newDots = OperationClip.dotsClip(windowX0, windowY0, windowX1, windowY1, dots);
		ModelDots result = new ModelDots(newDots);
		return result;
	}
}
