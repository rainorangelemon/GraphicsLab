package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import model.operation.OperationRotation.Axis;
import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelFill extends ModelShape{
	private int seedX, seedY;
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
	
	public ModelFill(boolean connectivityFour, int seedX, int seedY, 
			Color targetColor, boolean isInteriorPointMethod, Color definedColor){
		super(targetColor);
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
	protected List<ModelDot> getModelDots(Color[][] dots) {
		boolean[][] visit = new boolean[dots.length][dots[0].length];
		List<ModelDot> paintDots = new ArrayList<ModelDot>();
		Stack<ModelDot> waitingStack = new Stack<ModelDot>();
		if(((dots[0].length>seedX)&&(seedX>=0))&&((dots.length>seedY)&&(seedY>=0))){
			waitingStack.push(new ModelDot(seedX, seedY, dots[seedY][seedX]));
			if(isInteriorPointMethod){
				definedColor = dots[seedY][seedX];
			}
		}
		while(!waitingStack.empty()){
			ModelDot temp = waitingStack.pop();
			paintDots.add(new ModelDot(temp.getX(), temp.getY(), super.getColor()));
			waitingStack.addAll(getNeighbours(dots, temp, visit));
		}
		List<ModelDot> result = new ArrayList<ModelDot>();
		for(ModelDot dot: paintDots){
			if((dot.getX()>=0)&&(dot.getX()<dots[0].length)&&(dot.getY()>=0)&&(dot.getY()<dots.length)){
				result.add(dot);
			}
		}
		return result;
	}
	
	List<ModelDot> getNeighbours(Color[][] dots, ModelDot seedDot, boolean[][] visit){
		List<ModelDot> result = new ArrayList<ModelDot>();
		List<ModelDot> candidates = new ArrayList<ModelDot>();
		List<Pair<Integer, Integer>> neighbourOffSet;
		if(connectivityFour){
			neighbourOffSet = fourNeighbours;
		}else{
			neighbourOffSet = eightNeighbours;
		}
		for(Pair<Integer, Integer> offset: neighbourOffSet){
			if(((seedDot.getX() + offset.getKey())<dots[0].length)&&(seedDot.getX() + offset.getKey()>=0)
					&&((seedDot.getY() + offset.getValue())<dots.length)&&(seedDot.getY() + offset.getValue()>=0)){
				int x = seedDot.getX() + offset.getKey();
				int y = seedDot.getY() + offset.getValue();
				candidates.add(new ModelDot(x, y, dots[y][x]));
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
	public ModelFill translation(int offsetX, int offsetY, int offsetZ) {
		return this;
	}

	@Override
	public ModelFill rotation(int rotationX, int rotationY, Axis axis, int rotationDegree) {
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
