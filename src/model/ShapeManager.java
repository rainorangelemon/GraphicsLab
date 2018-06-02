package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import model.operation.ModelOperation;
import model.operation.OperationClip;
import javafx.scene.paint.Color;

public class ShapeManager {
	private Vector<ModelStep> steps = new Vector<ModelStep>();
	private int currentIndex;
	private ModelDot[][] dots;
	private int height, width;
//	private ModelShape[][] dot2modelShape;
	
	
	public ShapeManager(int height, int width){
		this.height = height;
		this.width = width;
		currentIndex = 0;
//		dot2modelShape = new ModelShape[height][width];
		dots = new ModelDot[height][width];
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
//				dot2modelShape[y][x] = null;
				dots[y][x] = new ModelDot(x, y, Color.WHITE);
			}
		}
	}
	
	public List<ModelDot> addNewStepShape(ModelShape newShape){
		if(currentIndex != steps.size()){
			Vector<ModelStep> result = new Vector<ModelStep>();
			for(int i=0; i<currentIndex;i++){
				result.add(steps.get(i));
			}
			steps = result;
		}
		steps.add(newShape);
		currentIndex = steps.size();
		List<ModelDot> newDots = newShape.getModelDots(dots);
		for(ModelDot newDot: newDots){
			if((newDot.getY()>=0)&&(newDot.getY()<height)&&(newDot.getX()>=0)&&(newDot.getX()<width)){
				dots[newDot.getY()][newDot.getX()].color = newDot.color;
//				dot2modelShape[newDot.getY()][newDot.getX()] = newShape;
			}
		}
		return newDots;
	}
	
	public void addNewStepOperation(ModelOperation newOperation){
		if(currentIndex != steps.size()){
			Vector<ModelStep> result = new Vector<ModelStep>();
			for(int i=0; i<currentIndex;i++){
				result.add(steps.get(i));
			}
			steps = result;
		}
		if(steps.size()!=0){
			newOperation.setCurrentIndex(steps.size());
			steps.add(newOperation);
		}
		currentIndex = steps.size();
	}
	
	public ModelDot[][] undo(){
		currentIndex = (currentIndex>0)?(currentIndex-1):currentIndex;
		return getModelDots();
	}
	
	public ModelDot[][] redo(){
		currentIndex = (currentIndex<(steps.size()))?(currentIndex+1):currentIndex;
		return getModelDots();
	}
	
	private ModelDot[][] getModelDots(){
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				dots[y][x].color=Color.WHITE;
//				dot2modelShape[y][x] = null;
			}
		}
		for(int step=0; step<currentIndex; step++){
			if(steps.get(step) instanceof ModelShape){
				ModelShape shape = (ModelShape) steps.get(step);
				for(int i=step+1; i<currentIndex; i++){
					if(steps.get(i) instanceof ModelOperation){
						if(!(steps.get(i) instanceof OperationClip)){
							ModelOperation operation = (ModelOperation) steps.get(i);
							if(operation.getShapeIndexes().contains(step)){
								shape = operation.operate(shape);
							}
						}
					}
				}
				List<ModelDot> newDots = shape.getModelDots(dots);
				for(ModelDot newDot: newDots){
					if((newDot.getY()>=0)&&(newDot.getY()<height)&&(newDot.getX()>=0)&&(newDot.getX()<width)){
						dots[newDot.getY()][newDot.getX()].color = newDot.color;
//						dot2modelShape[newDot.getY()][newDot.getX()] = steps.get(step);
					}
				}
			}else if(steps.get(step) instanceof OperationClip){
				List<ModelDot> newDots = new ArrayList<ModelDot>();
				OperationClip clip = (OperationClip) steps.get(step);
				for(Integer index: clip.getShapeIndexes()){
					ModelShape clipShape = (ModelShape) steps.get(index);
					if((clipShape instanceof ModelLine)||(clipShape instanceof ModelPolygon)){
						newDots.addAll(clip.operate(clipShape).getModelDots(dots));
					}
				}
				for(int y=0;y<height;y++){
					for(int x=0;x<width;x++){
						if((y<clip.getWindowY0())||(y>clip.getWindowY1())||(x<clip.getWindowX0())||(x>clip.getWindowX1())){
							dots[y][x].color = Color.WHITE;
						}
					}
				}
				for(ModelDot newDot: newDots){
					if((newDot.getY()>=0)&&(newDot.getY()<height)&&(newDot.getX()>=0)&&(newDot.getX()<width)){
						dots[newDot.getY()][newDot.getX()].color = newDot.color;
//						dot2modelShape[newDot.getY()][newDot.getX()] = steps.get(step);
					}
				}
			}
		}
		return dots;
	}
	
	public Vector<ModelStep> getSteps(){
		return steps;
	}
	
	public int getCurrentIndex(){
		return currentIndex;
	}
	
	public ModelDot[][] refresh(){
		return getModelDots();
	}

//	public ModelShape[][] getDot2modelShape() {
//		return dot2modelShape;
//	}
	
	
}
