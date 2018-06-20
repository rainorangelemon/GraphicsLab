package ui.chooser;

import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import model.ModelImport3D;
import model.ModelShape;

public class Import3DChooser extends ShapeChooser{

	private Callback<ModelShape, Integer> saver;
	private ModelImport3D import3D;

	public Import3DChooser(int width, int height, Color color, ModelImport3D import3D, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		if(import3D!=null){
			this.import3D = import3D;
		}else{
				try {
					this.import3D = new ModelImport3D(width, height, null);
				} catch (IOException e) {
					// do nothing
				}
		}
	}
	
	@Override
	public Node showEditor() {
		FileChooser fileChooser = new FileChooser();
        
        //Set extension filter
        FileChooser.ExtensionFilter extFilterOBJ = new FileChooser.ExtensionFilter("OBJ files (*.obj)", "*.OBJ");
        fileChooser.getExtensionFilters().addAll(extFilterOBJ);

        File file = fileChooser.showOpenDialog(null);
        if(file!=null){
			try {
				import3D.setFile(file);
			} catch (IOException e) {
				try {
					import3D.setFile(null);
				} catch (IOException e1) {
					// do nothing
				}
				makeFormatError();
			}
	        //Show open file dialog
	        saver.call(import3D);
        }else{
        	saver.call(null);
        }
		return null;
	}
	
	private void makeFormatError(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Format Error");
		alert.setHeaderText("The format of the obj file is wrong");
		alert.setContentText("Please check and change the texts in the file before import it!");
		alert.showAndWait();
	}
}
