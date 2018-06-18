package ui.chooser;

import java.io.File;

import javafx.scene.Node;
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
			this.import3D = new ModelImport3D(width, height, null);
		}
	}
	
	@Override
	public Node showEditor() {
		FileChooser fileChooser = new FileChooser();
        
        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("OBJ files (*.obj)", "*.OBJ");
        fileChooser.getExtensionFilters().addAll(extFilterJPG);

        File file = fileChooser.showOpenDialog(null);
        if(file!=null)
        	import3D.setFile(file);
        //Show open file dialog
        saver.call(import3D);
        
		return null;
	}
}
