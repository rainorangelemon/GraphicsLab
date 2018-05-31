package model;

import java.util.ArrayList;
import java.util.List;

import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationTranslation;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelDots extends ModelShape{

	private ArrayList<Pair<Integer, Integer>> dots;
	
	public ModelDots(Color color, List<Pair<Integer, Integer>> dots) {
		super(color);
		this.dots = new ArrayList<Pair<Integer, Integer>>(dots);
	}
	
	protected ModelDots(ModelDots source){
		super(source);
		this.dots = new ArrayList<Pair<Integer, Integer>>();
		for(Pair<Integer, Integer> pair: source.dots){
			this.dots.add(new Pair<Integer, Integer>(pair.getKey(), pair.getValue()));
		}
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		for(Pair<Integer, Integer> pair : this.dots){
			result.add(new ModelDot(pair.getKey(), pair.getValue(), super.getColor()));
		}
		return result;
	}

	@Override
	public ModelShape translation(int offsetX, int offsetY) {
		ModelDots result = new ModelDots(this);
		OperationTranslation.dotsTranslation(offsetX, offsetY, super.getColor(), result.dots);
		return result;
	}

	@Override
	public ModelShape rotation(int rotationX, int rotationY,
			int rotationDegree) {
		ModelDots result = new ModelDots(this);
		OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, super.getColor(), result.dots);
		return result;
	}

	@Override
	public ModelShape scaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY) {
		ModelDots result = new ModelDots(this);
		OperationScaling.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, super.getColor(), result.dots);
		return result;
	}

}
