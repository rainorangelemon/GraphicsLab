package model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.operation.OperationRotation.Axis;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import util.File3dImporter;

public class ModelImport3D extends ModelShape{
	private File3dImporter fileImporter;
	
	public ModelImport3D(int width, int height, File3dImporter fileImporter) throws FileNotFoundException, IOException {
		super(Color.WHITE);
		this.fileImporter = fileImporter;
	}
	
	public ModelImport3D(int width, int height, File3dImporter fileImporter, ArrayList<Double> vertices) throws FileNotFoundException, IOException {
		super(Color.WHITE);
		this.fileImporter = fileImporter.clone();
		this.fileImporter.setVertices(vertices);
	}

	@Override
	protected List<ModelDot> getModelDots(Color[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		result.addAll(draw3D());
		return result;
	}

	public File3dImporter getFileImporter() {
		return fileImporter;
	}

	public void setFileImporter(File3dImporter fileImporter) {
		this.fileImporter = fileImporter;
	}

	// algorithm to choose the file
	private List<ModelDot> draw3D(){
    	List<ModelDot> result = new ArrayList<ModelDot>();
    	if(fileImporter!=null){
    		fileImporter.updateMeshes();
    		for(MeshView meshView: fileImporter.getMeshViews()){
    			result.add(new ModelDot(meshView));
    		}
    	}
        return result;
	}

	@Override
	public ModelShape translation(int offsetX, int offsetY, int offsetZ) {
		ArrayList<Double> vertices = new ArrayList<Double>(fileImporter.getVertices());
		ArrayList<Double> newVertices = new ArrayList<Double>();
		for(int i=0; i<vertices.size(); i=i+3){
			newVertices.add(vertices.get(i)+offsetX);
			newVertices.add(vertices.get(i+1)+offsetY);
			newVertices.add(vertices.get(i+2)+offsetZ);
		}
		try {
			return new ModelImport3D(0, 0, this.fileImporter, newVertices);
		} catch (IOException e) {
			return this;
		}
	}

	@Override
	public ModelShape rotation(int rotationX, int rotationY, Axis axis, int rotationDegree) {
		ArrayList<Double> vertices = new ArrayList<Double>(fileImporter.getVertices());
		ArrayList<Double> newVertices = new ArrayList<Double>();
		double theta = ((double)rotationDegree)*Math.PI/180.0;
		for(int i=0; i<vertices.size(); i=i+3){
			double x, y;
			if(axis.equals(Axis.AxisZ)){
				x = vertices.get(i);
				y = vertices.get(i+1);
			}else if(axis.equals(Axis.AxisX)){
				x = vertices.get(i+1);
				y = vertices.get(i+2);
			}else{
				x = vertices.get(i+2);
				y = vertices.get(i);
			}
			double vectorX = x - (double)rotationX;
			double vectorY = y - (double)rotationY;
			double x1 = (double)rotationX + vectorX * Math.cos(theta) - vectorY * Math.sin(theta);
			double y1 = (double)rotationY + vectorX * Math.sin(theta) + vectorY * Math.cos(theta);
			if(axis.equals(Axis.AxisZ)){
				newVertices.add(x1);
				newVertices.add(y1);
				newVertices.add(vertices.get(i+2));
			}else if(axis.equals(Axis.AxisX)){
				newVertices.add(vertices.get(i));
				newVertices.add(x1);
				newVertices.add(y1);
			}else{
				newVertices.add(y1);
				newVertices.add(vertices.get(i+1));
				newVertices.add(x1);
			}
		}
		try {
			return new ModelImport3D(0, 0, this.fileImporter, newVertices);
		} catch (IOException e) {
			return this;
		}
	}

	@Override
	public ModelShape scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		ArrayList<Double> vertices = new ArrayList<Double>(fileImporter.getVertices());
		ArrayList<Double> newVertices = new ArrayList<Double>();
		for(int i=0; i<vertices.size(); i=i+3){
			Double x = vertices.get(i);
			Double y = vertices.get(i+1);
			double x1 = x * scaleSizeX + (double)scalePointX * (1-scaleSizeX);
			double y1 = y * scaleSizeY + (double)scalePointY * (1-scaleSizeY);
			newVertices.add(x1);
			newVertices.add(y1);
			newVertices.add(vertices.get(i+2));
		}
		try {
			return new ModelImport3D(0, 0, this.fileImporter, newVertices);
		} catch (IOException e) {
			return this;
		}
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		return this;
	}
}
