package model;

import java.util.ArrayList;
import java.util.List;

import model.operation.OperationClip;
import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationTranslation;
import model.operation.OperationRotation.Axis;
import javafx.scene.paint.Color;

public class ModelDots extends ModelShape{

	private ArrayList<ModelDot> dots;
	
	public ModelDots(List<ModelDot> dots) {
		super(Color.WHITE);
		this.dots = new ArrayList<ModelDot>(dots);
	}
	
	protected ModelDots(ModelDots source){
		super(source);
		this.dots = new ArrayList<ModelDot>();
		for(ModelDot dot: source.dots){
			this.dots.add(new ModelDot(dot.getX(), dot.getY(), dot.getColor()));
		}
	}

	@Override
	protected List<ModelDot> getModelDots(Color[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		for(ModelDot dot : this.dots){
			result.add(new ModelDot(dot.getX(), dot.getY(), dot.getColor()));
		}
		return result;
	}

	@Override
	public ModelShape translation(int offsetX, int offsetY, int offsetZ) {
		ModelDots result = new ModelDots(this);
		result.setDots(OperationTranslation.dotsTranslation(offsetX, offsetY, true, result.dots));
		return result;
	}

	@Override
	public ModelShape rotation(int rotationX, int rotationY, Axis axis, int rotationDegree) {
		ModelDots result = new ModelDots(this);
		result.setDots(OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, true, 0.3, result.dots));
		return result;
	}

	@Override
	public ModelShape scaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY) {
		ModelDots result = new ModelDots(this);
		result.setDots(OperationScaling.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, true, 0.3, result.dots));
		return result;
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		List<ModelDot> dots = this.getModelDots(null);
		List<ModelDot> newDots = OperationClip.dotsClip(windowX0, windowY0, windowX1, windowY1, dots);
		ModelDots result = new ModelDots(newDots);
		return result;
	}

	public ArrayList<ModelDot> getDots() {
		return dots;
	}

	public void setDots(ArrayList<ModelDot> dots) {
		this.dots = dots;
	}

}
