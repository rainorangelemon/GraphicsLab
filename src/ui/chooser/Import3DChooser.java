package ui.chooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import util.File3dImporter;
import util.ObjImporter;
import util.OffImporter;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.ModelImport3D;
import model.ModelShape;

public class Import3DChooser extends ShapeChooser{

	private Callback<ModelShape, Integer> saver;
	private ModelImport3D import3D;
	private int width, height;

	public Import3DChooser(int width, int height, Color color, ModelImport3D import3D, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		this.width = width;
		this.height = height;
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
        FileChooser.ExtensionFilter extFilterOFF = new FileChooser.ExtensionFilter("OFF files (*.off)", "*.OFF");
        fileChooser.getExtensionFilters().addAll(extFilterOFF, extFilterOBJ);

        File file = fileChooser.showOpenDialog(null);
        if(file!=null){
			try {
				if(file.getAbsolutePath().endsWith("obj")){
					File3dImporter fileImporter = new ObjImporter(file.getAbsolutePath());
					this.import3D.setFileImporter(fileImporter);
					saver.call(import3D);
				}else{
					OffImporter fileImporter = new OffImporter(file.getAbsolutePath());
					chooseColor(fileImporter);
				}
			} catch (IOException e) {
				makeFormatError();
			}
        }else{
        	saver.call(null);
        }
		return null;
	}

	private void chooseColor(OffImporter fileImporter) {
		Stage stage = new Stage();
		BorderPane root = new BorderPane();
		Image anotherIcon = new Image("resources/favicon.png");
		stage.getIcons().add(anotherIcon);
		stage.setTitle("Yu's Lab");
		showColorPicker(e->{
			stage.close();
			if(verticeRangeIsSmall(fileImporter)){
				showAlert(fileImporter);
			}else{
				this.import3D.setFileImporter(fileImporter);
				saver.call(import3D);
			}
			return null;
		}, root, fileImporter);
		
		stage.setScene(new Scene(new Pane(root)));
		stage.sizeToScene();
		stage.show();
	}
	
	private boolean verticeRangeIsSmall(OffImporter fileImporter) {
		if(((fileImporter.getMaxX()-fileImporter.getMinX())<((width)/4.0))||
				((fileImporter.getMaxY()-fileImporter.getMinY())<((height)/4.0))||
				(fileImporter.getMaxX()<0)||(fileImporter.getMinX()>width)||
				(fileImporter.getMaxY()<0)||(fileImporter.getMinY()>height)||
				(fileImporter.getMinZ()<0)){
			return true;
		}else{
			return false;
		}
	}

	private void showAlert(OffImporter fileImporter) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Strongly Recommend!");
		alert.setContentText("Your off file may not fit the screen quite well, because of the unsuitable scale or offset.\n"
				+ "Do you want the system to resize it automatically to fit the screen?");
		ButtonType okButton = new ButtonType("Yes (recommended)", ButtonData.YES);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		alert.getButtonTypes().setAll(okButton, noButton, cancelButton);
		alert.showAndWait().ifPresent(type -> {
		        if (type.getButtonData().equals(ButtonData.YES)) {
		        	fileImporter.setVertices(resizeVertice(fileImporter));
		        	this.import3D.setFileImporter(fileImporter);
		        	saver.call(this.import3D);
		        } else if (type.getButtonData().equals(ButtonData.NO)) {
		        	this.import3D.setFileImporter(fileImporter);
		        	saver.call(this.import3D);
		        } else {
		        	saver.call(null);
		        }
		});
	}

	private ArrayList<Double> resizeVertice(OffImporter fileImporter) {
		ArrayList<Double> result = new ArrayList<Double>();
		ArrayList<Double> vertices = fileImporter.getVertices();
		double minX = fileImporter.getMinX();
		double minY = fileImporter.getMinY();
		double scaleX = fileImporter.getMaxX() - fileImporter.getMinX();
		double scaleY = fileImporter.getMaxY() - fileImporter.getMinY();
		double minZ = fileImporter.getMinZ();
		int size = vertices.size();
		for(int i=0; i<size; i=i+3){
			double x = vertices.get(i);
			// rescale x
			x = ((x - minX) / scaleX) * width/2.0;
			double y = vertices.get(i+1);
			// rescale y
			y = ((y - minY) / scaleY) * height/2.0;
			double z = vertices.get(i+2);
			// translate z
			z = - (z - minZ);
			result.add(x);
			result.add(y);
			result.add(z);
		}
		return result;
	}

	private void makeFormatError(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Format Error");
		alert.setHeaderText("The format of the obj file is wrong");
		alert.setContentText("Please check and change the texts in the file before import it!");
		alert.showAndWait();
	}
	
	public void showColorPicker(Callback<Integer, Integer> saver, BorderPane root, OffImporter importer) {
		VBox positionModifier = new VBox();
		Label start = new Label("Which the color mode do you like?");
		
		ChoiceBox<String> box = new ChoiceBox<String>();
		box.getItems().add("Random Color On Faces");
		box.getItems().add("Certain Color");
		if(importer.isRandomColor()){
			box.getSelectionModel().select("Random Color On Faces");
		}else{
			box.getSelectionModel().select("Certain Color");
		}
		box.getSelectionModel()
		    .selectedItemProperty()
		    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    	if(newValue.equals(new String("Random Color On Faces"))){
		    		importer.setMaterial(true, importer.getDiffuseColor(), importer.getSpecularColor());
		    	}else{
		    		importer.setMaterial(false, importer.getDiffuseColor(), importer.getSpecularColor());
		    	}
		    	showColorPicker(saver, root, importer);
		    	root.getParent().getScene().getWindow().sizeToScene();
		    });
		
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(1);});
		positionModifier.getChildren().addAll(start, box, button);
		root.setLeft(positionModifier);
		if(!importer.isRandomColor()){
			VBox colorPicker = new VBox();
			colorPicker.getChildren().add(new Label(" Specular color:"));
			colorPicker.getChildren().add(super.getColorPicker(importer.getSpecularColor(), new Callback<Color, Integer>(){
				@Override
				public Integer call(Color param) {
					importer.setMaterial(false, importer.getDiffuseColor(), param);
					return null;
				}
			}));
			colorPicker.getChildren().add(new Label(" Diffuse color:"));
			colorPicker.getChildren().add(super.getColorPicker(importer.getDiffuseColor(), new Callback<Color, Integer>(){
				@Override
				public Integer call(Color param) {
					importer.setMaterial(false, param, importer.getSpecularColor());
					return null;
				}
			}));
			root.setRight(colorPicker);
		}
	}
}
