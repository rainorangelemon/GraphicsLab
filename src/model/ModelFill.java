package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelFill extends ModelShape{
	private int seedX, seedY;
	private int width, height;
	private boolean connectivityFour, isInteriorPointMethod;
	private Color definedColor;
	private List<Pair<Integer, Integer>> fourNeighbours = new ArrayList<Pair<Integer, Integer>>(){
		private static final long serialVersionUID = 3491779744257928988L;
		{
			this.add(new Pair<Integer, Integer>(new Integer(1), new Integer(0)));
			this.add(new Pair<Integer, Integer>(new Integer(-1), new Integer(0)));
			this.add(new Pair<Integer, Integer>(new Integer(0), new Integer(1)));
			this.add(new Pair<Integer, Integer>(new Integer(0), new Integer(-1)));
		}
	};
	private List<Pair<Integer, Integer>> eightNeighbours = new ArrayList<Pair<Integer, Integer>>(){
		private static final long serialVersionUID = -7094768688902802903L;
		{
			this.add(new Pair<Integer, Integer>(new Integer(1), new Integer(0)));
			this.add(new Pair<Integer, Integer>(new Integer(-1), new Integer(0)));
			this.add(new Pair<Integer, Integer>(new Integer(0), new Integer(1)));
			this.add(new Pair<Integer, Integer>(new Integer(0), new Integer(-1)));
			this.add(new Pair<Integer, Integer>(new Integer(1), new Integer(1)));
			this.add(new Pair<Integer, Integer>(new Integer(-1), new Integer(1)));
			this.add(new Pair<Integer, Integer>(new Integer(1), new Integer(-1)));
			this.add(new Pair<Integer, Integer>(new Integer(-1), new Integer(-1)));
		}
	};
	
	public ModelFill(boolean connectivityFour, int width, int height, int seedX, int seedY, 
			Color targetColor, boolean isInteriorPointMethod, Color definedColor){
		super(targetColor);
		this.width = width;
		this.height = height;
		this.seedX = seedX;
		this.seedY = seedY;
		this.connectivityFour = connectivityFour;
		this.isInteriorPointMethod = isInteriorPointMethod;
		this.definedColor = definedColor;
	}
	
	

	public int getSeedX() {
		return seedX;
	}

	public void setSeedX(int seedX) {
		this.seedX = seedX;
	}

	public int getSeedY() {
		return seedY;
	}

	public void setSeedY(int seedY) {
		this.seedY = seedY;
	}

	public boolean isConnectivityFour() {
		return connectivityFour;
	}

	public void setConnectivityFour(boolean connectivityFour) {
		this.connectivityFour = connectivityFour;
	}


	public boolean isInteriorPointMethod() {
		return isInteriorPointMethod;
	}

	public void setInteriorPointMethod(boolean isInteriorPointMethod) {
		this.isInteriorPointMethod = isInteriorPointMethod;
	}

	public Color getDefinedColor() {
		return definedColor;
	}

	public void setDefinedColor(Color definedColor) {
		this.definedColor = definedColor;
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		boolean[][] visit = new boolean[height][width];
		List<ModelDot> result = new ArrayList<ModelDot>();
		Stack<ModelDot> waitingStack = new Stack<ModelDot>();
		if(((width>seedX)&&(seedX>=0))&&((height>seedY)&&(seedY>=0))){
			waitingStack.push(dots[seedY][seedY]);
			if(isInteriorPointMethod){
				definedColor = dots[seedY][seedX].getColor();
			}
		}
		while(!waitingStack.empty()){
			ModelDot temp = waitingStack.pop();
			result.add(new ModelDot(temp.getX(), temp.getY(), super.getColor()));
			waitingStack.addAll(getNeighbours(dots, temp, visit));
		}
		return result;
	}
	
	List<ModelDot> getNeighbours(ModelDot[][] dots, ModelDot seedDot, boolean[][] visit){
		List<ModelDot> result = new ArrayList<ModelDot>();
		List<ModelDot> candidates = new ArrayList<ModelDot>();
		List<Pair<Integer, Integer>> neighbourOffSet;
		if(connectivityFour){
			neighbourOffSet = fourNeighbours;
		}else{
			neighbourOffSet = eightNeighbours;
		}
		for(Pair<Integer, Integer> offset: neighbourOffSet){
			if((seedDot.getX() + offset.getKey()<width)&&(seedDot.getX() + offset.getKey()>=0)
					&&(seedDot.getY() + offset.getValue()<height)&&(seedDot.getY() + offset.getValue()>=0)){
				candidates.add(dots[seedDot.getY() + offset.getValue()][seedDot.getX() + offset.getKey()]);
			}
		}
		for(ModelDot candidate: candidates){
			if(isInteriorPointMethod){
				// 内点定义
				if((candidate.color==definedColor)&&(!visit[candidate.getY()][candidate.getX()])){
					result.add(candidate);
					visit[candidate.getY()][candidate.getX()] = true;
				}
			}else{
				// 边界定义
				if((candidate.color!=definedColor)&&(!visit[candidate.getY()][candidate.getX()])){
					result.add(candidate);
					visit[candidate.getY()][candidate.getX()] = true;
				}
			}
		}
		return result;
	}

	@Override
	public ModelFill translation(int offsetX, int offsetY) {
		return this;
	}

	@Override
	public ModelFill rotation(int rotationX, int rotationY, int rotationDegree) {
		return this;
	}

	@Override
	public ModelFill scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		return this;
	}



	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		return this;
	}
}
