package model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.util.Pair;

public abstract class ModelShape {
	private Color color;
	private int translationX = 0, translationY = 0;
	private int rotationX = 0, rotationY = 0;
	private int rotationDegree = 0;
	private int scalePointX = 0, scalePointY = 0;
	private double scaleSizeX = 1.0, scaleSizeY = 1.0;
	
	
	public ModelShape(Color color){
		this.color = color;
	}
	
	protected abstract List<ModelDot> getModelDots(ModelDot[][] dots);
	
	protected abstract void subTranslation(int offsetX, int offsetY);
	
	protected abstract void subRotation(int rotationX, int rotationY, int rotationDegree);
	
	protected abstract void subScaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY);
	
	public void translation(int offsetX, int offsetY){
		this.translationX = offsetX;
		this.translationY = offsetY;
		subTranslation(translationX, translationY);
	}
	
	public void rotation(int rotationX, int rotationY, int rotationDegree){
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationDegree = rotationDegree;
		subRotation(rotationX, rotationY, rotationDegree);
	}
	
	public void scaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY){
		this.scalePointX = scalePointX;
		this.scalePointY = scalePointY;
		this.scaleSizeX = scaleSizeX;
		this.scaleSizeY = scaleSizeY;
		subScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY);
	}
	
	public int getTranslationX() {
		return translationX;
	}

	public int getTranslationY() {
		return translationY;
	}

	public int getRotationX() {
		return rotationX;
	}

	public int getRotationY() {
		return rotationY;
	}

	public int getRotationDegree() {
		return rotationDegree;
	}

	public int getScalePointX() {
		return scalePointX;
	}

	public int getScalePointY() {
		return scalePointY;
	}

	public double getScaleSizeX() {
		return scaleSizeX;
	}

	public double getScaleSizeY() {
		return scaleSizeY;
	}

	public void setTranslationX(int translationX) {
		this.translationX = translationX;
	}

	public void setTranslationY(int translationY) {
		this.translationY = translationY;
	}

	public void setRotationX(int rotationX) {
		this.rotationX = rotationX;
	}

	public void setRotationY(int rotationY) {
		this.rotationY = rotationY;
	}

	public void setRotationDegree(int rotationDegree) {
		this.rotationDegree = rotationDegree;
	}

	public void setScalePointX(int scalePointX) {
		this.scalePointX = scalePointX;
	}

	public void setScalePointY(int scalePointY) {
		this.scalePointY = scalePointY;
	}

	public void setScaleSizeX(double scaleSizeX) {
		this.scaleSizeX = scaleSizeX;
	}

	public void setScaleSizeY(double scaleSizeY) {
		this.scaleSizeY = scaleSizeY;
	}

	public Color getColor(){
		return color;
	}
	
	public void setColor(Color color){
		this.color = color;
	}

	public static ModelDot dotTranslation(int offsetX, int offsetY, ModelDot dot){
		return new ModelDot(dot.getX()+offsetX, dot.getY()+offsetY, dot.getColor());
	}
	
	public static List<ModelDot> dotRotation(int rotationX, int rotationY, int rotationDegree, double sampleStep, ModelDot dot){
		List<ModelDot> result = new ArrayList<ModelDot>();
		rotationDegree = rotationDegree % 360;
		// 这里进行超采样
		for(double x = dot.getX(); (x-dot.getX())<1; x+=sampleStep){
			for(double y = dot.getY(); (y-dot.getY())<1; y+=sampleStep){
				double vectorX = x - (double)rotationX;
				double vectorY = y - (double)rotationY;
				double theta = rotationDegree*Math.PI/180.0;
				double x1 = (double)rotationX + vectorX * Math.cos(theta) - vectorY * Math.sin(theta);
				double y1 = (double)rotationY + vectorX * Math.sin(theta) + vectorY * Math.cos(theta);
				result.add(new ModelDot((int)Math.round(x1), (int)Math.round(y1), dot.getColor()));
			}
		}
		return result;
	}
	
	public static List<ModelDot> dotScaling(int scalePointX, int scalePointY, double scaleSizeX, double scaleSizeY, double sampleStep, ModelDot dot){
		List<ModelDot> result = new ArrayList<ModelDot>();
		// 这里进行超采样
		for(double x = dot.getX(); (x-dot.getX())<1; x+=sampleStep){
			for(double y = dot.getY(); (y-dot.getY())<1; y+=sampleStep){
				double x1 = x * scaleSizeX + (double)scalePointX * (1-scaleSizeX);
				double y1 = y * scaleSizeY + (double)scalePointY * (1-scaleSizeY);
				result.add(new ModelDot((int)Math.round(x1), (int)Math.round(y1), dot.getColor()));
			}
		}
		return result;
	}
	
	public static void dotsTranslation(int offsetX, int offsetY, Color color, ArrayList<Pair<Integer, Integer>> dots){
		int size = dots.size();
		for(int i=0; i<size; i++){
			Pair<Integer, Integer> oldDot = dots.get(i);
			ModelDot newDot = dotTranslation(offsetX, offsetY, new ModelDot(oldDot.getKey(), oldDot.getValue(), color));
			dots.remove(i);
			dots.trimToSize();
			dots.add(i, new Pair<Integer, Integer>(newDot.getX(), newDot.getY()));
		}
	}
	
	public static void dotsRotation(int rotationX, int rotationY, int rotationDegree, Color color, ArrayList<Pair<Integer, Integer>> dots) {
		int size = dots.size();
		for(int i=0; i<size; i++){
			Pair<Integer, Integer> oldDot = dots.get(i);
			List<ModelDot> newDot = dotRotation(rotationX, rotationY, rotationDegree, 1.0, new ModelDot(oldDot.getKey(), oldDot.getValue(), color));
			dots.remove(i);
			dots.trimToSize();
			dots.add(i, new Pair<Integer, Integer>(newDot.get(0).getX(), newDot.get(0).getY()));
		}
	}
	
	public static void dotsScaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY, 
			Color color, ArrayList<Pair<Integer, Integer>> dots) {
		int size = dots.size();
		for(int i=0; i<size; i++){
			Pair<Integer, Integer> oldDot = dots.get(i);
			List<ModelDot> newDot = dotScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, 1.0, new ModelDot(oldDot.getKey(), oldDot.getValue(), color));
			dots.remove(i);
			dots.trimToSize();
			dots.add(i, new Pair<Integer, Integer>(newDot.get(0).getX(), newDot.get(0).getY()));
		}
	}
	
	protected void resetTranslation(){
		this.translationX = 0;
		this.translationY = 0;
	}
	
	protected void resetRotation(){
		this.rotationX = 0;
		this.rotationY = 0;
		this.rotationDegree = 0;
	}
	
	protected void resetScaling(){
		this.setScalePointX(0);
		this.setScalePointY(0);
		this.setScaleSizeX(1.0);
		this.setScaleSizeY(1.0);
	}
}
