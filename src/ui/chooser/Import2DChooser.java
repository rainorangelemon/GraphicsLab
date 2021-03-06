package ui.chooser;

import java.io.File;

import model.ModelImport2D;
import model.ModelShape;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class Import2DChooser extends ShapeChooser{

	private Callback<ModelShape, Integer> saver;
	private ModelImport2D importPic;

	public Import2DChooser(int width, int height, Color color, ModelImport2D importPic, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		if(importPic!=null){
			this.importPic = importPic;
		}else{
			this.importPic = new ModelImport2D(width, height, null);
		}
	}
	
	@Override
	public Node showEditor() {
		FileChooser fileChooser = new FileChooser();
        
        //Set extension filter
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        File file = fileChooser.showOpenDialog(null);
        if(file!=null){
        	importPic.setFile(file);
        	saver.call(importPic);
        }else{
        	saver.call(null);
        }
        
		return null;
	}
	
}
