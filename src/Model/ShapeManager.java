package Model;

import java.util.List;
import java.util.Vector;

import javafx.scene.paint.Color;

public class ShapeManager {
	private Vector<ModelShape> steps = new Vector<ModelShape>();
	private int currentIndex;
	private ModelDot[][] dots;
	private int height, width;
	
	public ShapeManager(int height, int width){
		this.height = height;
		this.width = width;
		currentIndex = 0;
		dots = new ModelDot[height][width];
		for(int y=0;y<height;y++){
			for(int x=0;x<width;x++){
				dots[y][x] = new ModelDot(x, y, Color.WHITE);
			}
		}
	}
	
	public List<ModelDot> addNewStep(ModelShape newShape){
		if(currentIndex != steps.size()){
			Vector<ModelShape> result = new Vector<ModelShape>();
			for(int i=0; i<currentIndex;i++){
				result.add(steps.get(i));
			}
			steps = result;
		}
		steps.add(newShape);
		currentIndex = steps.size();
		List<ModelDot> newDots = newShape.getModelDots(dots);
		for(ModelDot newDot: newDots){
			dots[newDot.getY()][newDot.getX()].color = newDot.color;
		}
		return newDots;
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
			}
		}
		for(int step=0; step<currentIndex; step++){
			List<ModelDot> newDots = steps.get(step).getModelDots(dots);
			for(ModelDot newDot: newDots){
				dots[newDot.getY()][newDot.getX()].color = newDot.color;
			}
		}
		return dots;
	}
	
	public Vector<ModelShape> getSteps(){
		return steps;
	}
	
	public int getCurrentIndex(){
		return currentIndex;
	}
	
	public ModelDot[][] refresh(){
		return getModelDots();
	}
}
