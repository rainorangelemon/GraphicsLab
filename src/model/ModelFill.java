package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.scene.paint.Color;
import javafx.util.Pair;

public class ModelFill extends ModelShape{
	private int seedX, seedY;
	private int width, height;
	private int x0, y0;
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
	
	public ModelFill(boolean connectivityFour, int x0, int y0, int width, int height, int seedX, int seedY, 
			Color targetColor, boolean isInteriorPointMethod, Color definedColor){
		super(targetColor);
		this.width = width;
		this.height = height;
		this.x0 = x0;
		this.y0 = y0;
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
		boolean[][] visit = new boolean[dots.length][dots[0].length];
		List<ModelDot> result = new ArrayList<ModelDot>();
		Stack<ModelDot> waitingStack = new Stack<ModelDot>();
		if((((width+x0)>seedX)&&(seedX>=x0))&&(((height+y0)>seedY)&&(seedY>=y0))){
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
			if((seedDot.getX() + offset.getKey()<(width+x0))&&(seedDot.getX() + offset.getKey()>=x0)
					&&(seedDot.getY() + offset.getValue()<(height+y0))&&(seedDot.getY() + offset.getValue()>=y0)){
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

	// 第二大元素
	private int find2Max(int[] a){  
	    int max1 = 0;     
	    int max2 = 0;    
	    for(int i=1; i< a.length; i++)    
	    {    
	        if(a[i] > a[max1]) {    
	            max2 = max1;    
	            max1 = i;    
	        } else if(a[i] >  a[max2] && a[i] < a[max1]){
	            max2 = i;    
	        }
	    }  
	    return a[max2];  
	} 
	
	// 第二小元素
	private int find2Min(int[] a){  
	    int min1 = 0;     
	    int min2 = 0;    
	    for(int i=1; i< a.length; i++)    
	    {    
	        if(a[i] < a[min1]) {    
	            min2 = min1;    
	            min1 = i;    
	        } else if(a[i] <  a[min2] && a[i] > a[min1]){
	            min2 = i;    
	        }
	    }  
	    return a[min2];  
	} 
	
	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
//		if((windowX0>(x0+width))||(windowX1<x0)||(windowY0>(y0+height))||(windowY1<y0)){
//			return new ModelDots(new ArrayList<ModelDot>());
//		}else{
			int minX = find2Min(new int[]{windowX0, windowX1, x0, x0+width});
			int minY = find2Min(new int[]{windowY0, windowY1, y0, y0+height});
			int maxX = find2Max(new int[]{windowX0, windowX1, x0, x0+width});
			int maxY = find2Max(new int[]{windowY0, windowY1, y0, y0+height});
			return new ModelFill(connectivityFour, minX, minY, maxX-minX+1, maxY-minY+1, seedX, seedY, super.getColor(), isInteriorPointMethod, definedColor);
//		}
	}
}
