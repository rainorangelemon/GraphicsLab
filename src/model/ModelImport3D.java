package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import util.ObjImporter;

public class ModelImport3D extends ModelShape{
	private File file;
	private ObjImporter objImporter;
	
	public ModelImport3D(int width, int height, File file) throws FileNotFoundException, IOException {
		super(Color.WHITE);
		setFile(file);
	}
	
	public ModelImport3D(int width, int height, File file, ArrayList<Double> vertices, ArrayList<Double> normals, ArrayList<Double> uvs) throws FileNotFoundException, IOException {
		super(Color.WHITE);
		setFile(file);
		this.objImporter.setVertices(vertices);
		this.objImporter.setNormals(normals);
		this.objImporter.setUvs(uvs);
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		result.addAll(draw3D());
		return result;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) throws FileNotFoundException, IOException {
		if(file!=null){
			this.file = file;
			this.objImporter = new ObjImporter(file.getAbsolutePath());
		}
	}

	//algorithm to choose the file
	private List<ModelDot> draw3D(){
    	List<ModelDot> result = new ArrayList<ModelDot>();
    	if(objImporter!=null){
			objImporter.updateMeshes();
			for(String key: objImporter.getMeshes()){
				MeshView meshView = objImporter.buildMeshView(key);
				meshView.setDrawMode(DrawMode.FILL);
				result.add(new ModelDot(meshView));
			}
    	}
        return result;
	}

	@Override
	public ModelShape translation(int offsetX, int offsetY) {
		ArrayList<Double> vertices = new ArrayList<Double>(objImporter.getVertices());
		ArrayList<Double> normals = new ArrayList<Double>(objImporter.getNormals());
		ArrayList<Double> uvs = new ArrayList<Double>(objImporter.getUvs());
		ArrayList<Double> newVertices = new ArrayList<Double>(),
				newUvs = new ArrayList<Double>(uvs), 
				newNormals = new ArrayList<Double>(normals);
		for(int i=0; i<vertices.size(); i=i+3){
			newVertices.add(vertices.get(i)+offsetX);
			newVertices.add(vertices.get(i+1)+offsetY);
			newVertices.add(vertices.get(i+2));
		}
//		for(int i=0; i<uvs.size(); i=i+2){
//			newUvs.add(uvs.get(i)+offsetX);
//			newUvs.add(uvs.get(i+1)+offsetY);
//		}
		try {
			return new ModelImport3D(0, 0, file, newVertices, newNormals, newUvs);
		} catch (IOException e) {
			return this;
		}
	}

	@Override
	public ModelShape rotation(int rotationX, int rotationY, int rotationDegree) {
		ArrayList<Double> vertices = new ArrayList<Double>(objImporter.getVertices());
		ArrayList<Double> normals = new ArrayList<Double>(objImporter.getNormals());
		ArrayList<Double> uvs = new ArrayList<Double>(objImporter.getUvs());
		ArrayList<Double> newVertices = new ArrayList<Double>(),
				newUvs = new ArrayList<Double>(uvs), 
				newNormals = new ArrayList<Double>(normals);
		double theta = ((double)rotationDegree)*Math.PI/180.0;
		for(int i=0; i<vertices.size(); i=i+3){
			double vectorX = vertices.get(i) - (double)rotationX;
			double vectorY = vertices.get(i+1) - (double)rotationY;
			double x1 = (double)rotationX + vectorX * Math.cos(theta) - vectorY * Math.sin(theta);
			double y1 = (double)rotationY + vectorX * Math.sin(theta) + vectorY * Math.cos(theta);
			newVertices.add(x1);
			newVertices.add(y1);
			newVertices.add(vertices.get(i+2));
		}
		try {
			return new ModelImport3D(0, 0, file, newVertices, newNormals, newUvs);
		} catch (IOException e) {
			return this;
		}
	}

	@Override
	public ModelShape scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
		ArrayList<Double> vertices = new ArrayList<Double>(objImporter.getVertices());
		ArrayList<Double> normals = new ArrayList<Double>(objImporter.getNormals());
		ArrayList<Double> uvs = new ArrayList<Double>(objImporter.getUvs());
		ArrayList<Double> newVertices = new ArrayList<Double>(),
				newUvs = new ArrayList<Double>(uvs), 
				newNormals = new ArrayList<Double>(normals);
		for(int i=0; i<vertices.size(); i=i+3){
			newVertices.add(vertices.get(i) * scaleSizeX);
			newVertices.add(vertices.get(i+1) * scaleSizeY);
			newVertices.add(vertices.get(i+2));
		}
		try {
			return new ModelImport3D(0, 0, file, newVertices, newNormals, newUvs);
		} catch (IOException e) {
			return this;
		}
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		return this;
	}
}
