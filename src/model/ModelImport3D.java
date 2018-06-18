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
import model.operation.OperationClip;
import model.operation.OperationRotation;
import model.operation.OperationScaling;
import model.operation.OperationTranslation;

public class ModelImport3D extends ModelShape{
	private File file;
	private ObjImporter objImporter;
	
	public ModelImport3D(int width, int height, File file) throws FileNotFoundException, IOException {
		super(Color.WHITE);
		setFile(file);
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
//		ModelDots result = new ModelDots(this.draw3D());
//		OperationTranslation.dotsTranslation(offsetX, offsetY, result.getDots());
//		return result;
		return this;
	}

	@Override
	public ModelShape rotation(int rotationX, int rotationY, int rotationDegree) {
//		ModelDots result = new ModelDots(this.draw3D());
//		result.setDots(OperationRotation.dotsRotation(rotationX, rotationY, rotationDegree, true, 0.3, result.getDots()));
//		return result;
		return this;
	}

	@Override
	public ModelShape scaling(int scalePointX, int scalePointY,
			double scaleSizeX, double scaleSizeY) {
//		ModelDots result = new ModelDots(this.draw3D());
//		result.setDots(OperationScaling.dotsScaling(scalePointX, scalePointY, scaleSizeX, scaleSizeY, true, 0.3, result.getDots()));
//		return result;
		return this;
	}

	@Override
	public ModelShape clip(int windowX0, int windowY0, int windowX1, int windowY1) {
		return this;
	}
}
