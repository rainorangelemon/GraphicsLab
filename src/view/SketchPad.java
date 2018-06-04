package view;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import model.ModelDot;
import model.ModelShape;
import model.ModelStep;
import model.ShapeManager;
import model.operation.ModelOperation;
import ui.chooser.ImportChooser;
import ui.chooser.ShapeChooser;
//import ui.editor.CircleEditor;
//import ui.editor.LineEditor;
import ui.operator.Operator;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
//import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SketchPad extends Pane{
	private int height, width;
	private Dot[][] dotMatrix;
	private ShapeManager shapeManager;
//	private boolean isChanged = false;
	Label reporter = new Label("");
	static String OUTSIDE_TEXT = "Outside SketchPad";
	private VBox stepEditor = new VBox();
	
	
	public SketchPad(int height, int width){
		super();
		this.setMinSize(width, height);
		this.setMaxSize(width, height);
		this.height = height;
		this.width = width;
		initAllDots();
		shapeManager = new ShapeManager(height, width);
	}
	
	public EventHandler<? super MouseEvent> registerCheckPosition(){
		return new EventHandler<MouseEvent>() {
		      @Override public void handle(MouseEvent event) {
		    	if((new Double(event.getY()).intValue()< getDotMatrix().length)&&(new Double(event.getX()).intValue()< getDotMatrix()[0].length)){
			        String msg =
			          "(x: "       + event.getX()      + ", y: "       + event.getY()       + ") -- " +
			          "(width: "  + getWidth() + ", height: "  + getHeight()  + ") -- " +
			          "Color: " + getDotMatrix()[new Double(event.getY()).intValue()][new Double(event.getX()).intValue()].getColorString();
			        reporter.setText(msg);
		    	}
		      }
		    };
	}
	
	private void initAllDots() {
		dotMatrix = new Dot[height][width];
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				dotMatrix[i][j]=new Dot(j, i, Color.WHITE);
				this.getChildren().add(dotMatrix[i][j]);
			}
		}
	}
	
	public Dot[][] getDotMatrix(){
		return dotMatrix;
	}
	
	public void addNewShape(ModelShape newShape){
		List<ModelDot> modelDots = shapeManager.addNewStepShape(newShape);
		for(ModelDot newDot: modelDots){
			if((newDot.getY()>=0)&&(newDot.getY()<height)&&(newDot.getX()>=0)&&(newDot.getX()<width)){
				dotMatrix[newDot.getY()][newDot.getX()].changeColor(newDot.getColor());
			}
		}
		refreshStepEditor();
	}
	
	public void addNewOperation(ModelOperation newOperation){
		shapeManager.addNewStepOperation(newOperation);
		refreshStepEditor();
		refreshScreen(shapeManager.refresh());
	}
	
	public void redo(){
		ModelDot[][] modelDots = shapeManager.redo();
		refreshScreen(modelDots);
		refreshStepEditor();
	}
	
	public void undo(){
		ModelDot[][] modelDots = shapeManager.undo();
		refreshScreen(modelDots);
		refreshStepEditor();
	}

	public void refreshScreen(ModelDot[][] modelDots) {
		for(ModelDot[] dotRow: modelDots){
			for(ModelDot dot: dotRow){
				dotMatrix[dot.getY()][dot.getX()].changeColor(dot.getColor());
			}
		}
	}

	public void refreshStepEditor(){
		stepEditor.getChildren().clear();
		Label currentIndex = new Label("current index: " + new Integer(shapeManager.getCurrentIndex()-1).toString());
		stepEditor.getChildren().add(currentIndex);
		Vector<ModelStep> steps = shapeManager.getSteps();
		for(int i=0;i<shapeManager.getCurrentIndex();i++){
			if(steps.get(i) instanceof ModelShape){
				ModelShape modelShape = (ModelShape) steps.get(i);
				Button newButton = createShapeButton(i, modelShape);
				stepEditor.getChildren().add(newButton);
			}else if(steps.get(i) instanceof ModelOperation){
				ModelOperation modelOperation = (ModelOperation) steps.get(i);
				Button newButton = createOperationButton(i, modelOperation);
				stepEditor.getChildren().add(newButton);
			}
		}
		
	}

	private Button createOperationButton(int i, ModelOperation modelOperation) {
		Button newButton = new Button(new Integer(i).toString() + ": " + modelOperation.getClass().getSimpleName().substring(9));
		newButton.setOnMouseClicked(e->{
			try {
				Stage stage = new Stage();
				Class<?> shapeClass = modelOperation.getClass();
				Class<?> shapeEditorClass = Class.forName("ui.operator."+modelOperation.getClass().getSimpleName().substring(9)+"Operator");
				Constructor<?> chooserConstructor = shapeEditorClass.getConstructor(new Class[]{int.class, int.class, ShapeManager.class, shapeClass, Callback.class});
				Operator shapeChooser = (Operator) chooserConstructor.newInstance(width, height, getShapeManager(), modelOperation, new Callback<ModelOperation, Integer>(){
					@Override
					public Integer call(ModelOperation param) {
						stage.close();
						refreshScreen(shapeManager.refresh());
						return null;
					}
				});
				if(shapeChooser.showEditor()!=null){
					stage.setScene(new Scene(new Pane(shapeChooser.showEditor())));
					stage.sizeToScene();
					stage.show();
				}
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
				e1.printStackTrace(); //handled by exiting the program
				System.exit(1);
			}
		});
		return newButton;
	}

	private Button createShapeButton(int i, ModelShape modelShape) {
		Button newButton = new Button(new Integer(i).toString() + ": " + modelShape.getClass().getSimpleName().substring(5));
		newButton.setOnMouseClicked(e->{
			try {
				Stage stage = new Stage();
				Class<?> shapeClass = modelShape.getClass();
				Class<?> shapeEditorClass = Class.forName("ui.chooser."+modelShape.getClass().getSimpleName().substring(5)+"Chooser");
				Constructor<?> chooserConstructor = shapeEditorClass.getConstructor(new Class[]{int.class, int.class, Color.class, shapeClass, Callback.class});
				ShapeChooser shapeChooser = (ShapeChooser) chooserConstructor.newInstance(width, height, modelShape.getColor(), modelShape, new Callback<ModelShape, Integer>(){
					@Override
					public Integer call(ModelShape param) {
						stage.close();
						refreshScreen(shapeManager.refresh());
						return null;
					}
				});
				if(shapeChooser.showEditor()!=null){
					stage.setScene(new Scene(new Pane(shapeChooser.showEditor())));
					stage.sizeToScene();
					stage.show();
				}
			} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
				e1.printStackTrace(); //handled by exiting the program
				System.exit(1);
			}
		});
		return newButton;
	}
	
	public ScrollPane getStepEditor() {
		ScrollPane result = new ScrollPane();
		result.setContent(stepEditor);
		result.setPannable(true);
		result.setPrefSize(120, height);
		return result;
	}
	
	public void captureAndSaveDisplay(){
	    FileChooser fileChooser = new FileChooser();

	    //Set extension filter
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));

	    //Prompt user to select a file
	    File file = fileChooser.showSaveDialog(null);

	    if(file != null){
	        try {
	            //Pad the capture area
	            WritableImage writableImage = new WritableImage((int)getWidth(),
	                    (int)getHeight());
	            snapshot(null, writableImage);
	            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
	            //Write the snapshot to the chosen file
	            ImageIO.write(renderedImage, "png", file);
	        } catch (IOException ex) { ex.printStackTrace(); }
	    }
	}
	
	public void importPic(){
		ShapeChooser shapeChooser = new ImportChooser(width, height, Color.WHITE, null, new Callback<ModelShape, Integer>(){
			@Override
			public Integer call(ModelShape param) {
				addNewShape(param);
				return null;
			}
		});
		shapeChooser.showEditor();
	}
	
	public Label getReport(){
	    this.setOnMouseExited(new EventHandler<MouseEvent>() {
		      @Override public void handle(MouseEvent event) {
		        reporter.setText(OUTSIDE_TEXT);
		      }
		    });
		return reporter;
	}

	public ShapeManager getShapeManager() {
		return shapeManager;
	}
	
}
