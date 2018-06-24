package view;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import model.ModelDot;
import model.ModelShape;
import model.ModelStep;
import model.ShapeManager;
import model.operation.ModelOperation;
import ui.chooser.Import3DChooser;
import ui.chooser.Import2DChooser;
import ui.chooser.ShapeChooser;
import ui.operator.Operator;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

public class SketchPad extends Pane{
	private int height, width;
	private Color[][] dotMatrix;
	private PixelWriter pixelWriter;
	private ShapeManager shapeManager;
	private List<MeshView> current3D = new ArrayList<MeshView>();
	Label reporter = new Label("");
	static String OUTSIDE_TEXT = "Outside SketchPad";
	private VBox stepEditor = new VBox();
	private SubScene scene3d;
	
	
	public SketchPad(int height, int width){
		super();
		this.setMinSize(width, height);
		this.setMaxSize(width, height);
		this.height = height;
		this.width = width;
		this.setMinSize(width, height);
		this.setMaxSize(width, height);
		initAllDots();
		AmbientLight pointLight = new AmbientLight(Color.WHITE);
		pointLight.setOpacity(1);
		this.getChildren().add(pointLight);
		shapeManager = new ShapeManager(height, width);
		refreshStepEditor();
		dotMatrix = shapeManager.getDots();
	}
	
	public EventHandler<? super MouseEvent> registerCheckPosition(){
		return new EventHandler<MouseEvent>() {
		      @Override 
		      public void handle(MouseEvent event) {
		    	    int eventX = new Double(event.getX()).intValue();
			    	int eventY = new Double(event.getY()).intValue();
			    	if((eventY < height)&&(eventX< width)){
				        String msg =
				          "(x: "       + eventX      + ", y: "       + eventY       + ") -- " +
				          "(width: "  + getWidth() + ", height: "  + getHeight()  + ") -- " +
				          "Color: " + Dot.getColorString(dotMatrix[eventY][eventX]);
				        reporter.setText(msg);
			    	}
		      }
		    };
	}
	
	private void initAllDots() {
		Canvas canvas = new Canvas();
		canvas.setWidth(width);
		canvas.setHeight(height);
		this.pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
		this.getChildren().add(canvas);
		for(int x =0; x<width;x++){
			for(int y =0; y<height;y++){
				pixelWriter.setColor(x, y, Color.WHITE);
			}
		}
	}
	
	public void addNewShape(ModelShape newShape){
		this.getChildren().removeAll(current3D);
		List<ModelDot> modelDots = shapeManager.addNewStepShape(newShape);
		for(ModelDot newDot: modelDots){
			int x = newDot.getX();
			int y = newDot.getY();
			Color color = newDot.getColor();
			if((y>=0)&&(y<height)&&(x>=0)&&(x<width)){
				pixelWriter.setColor(x, y, color);
			}else if(newDot.getMeshView()!=null){
				this.current3D.add(newDot.getMeshView());
			}
		}
		this.getChildren().addAll(current3D);
		refreshStepEditor();
	}
	
	public void addNewOperation(ModelOperation newOperation){
		this.getChildren().removeAll(current3D);
		shapeManager.addNewStepOperation(newOperation);
		refreshStepEditor();
		refreshScreen(shapeManager.refresh());
	}
	
	public void redo(){
		refreshScreen(shapeManager.redo());
		refreshStepEditor();
	}
	
	public void undo(){
		refreshScreen(shapeManager.undo());
		refreshStepEditor();
	}

	public void refreshScreen(Pair<List<MeshView>, Color[][]> modelShapes) {
		this.getChildren().removeAll(current3D);
		Color[][] modelDots = modelShapes.getValue();
		for(int y = 0; y<height; y++){
			for(int x=0;x<width;x++){
				Color color = modelDots[y][x];
				pixelWriter.setColor(x, y, color);
			}
		}
		current3D = new ArrayList<MeshView>(modelShapes.getKey());
		this.getChildren().addAll(current3D);
	}

	public void refreshStepEditor(){
		stepEditor.getChildren().clear();
		String index = "";
		if((shapeManager.getCurrentIndex()-1)>=0){
			index = "current index: " + new Integer(shapeManager.getCurrentIndex()-1).toString();
		}else{
			index = "  no shapes";
		}
		Label currentIndex = new Label(index);
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
					Image anotherIcon = new Image("resources/favicon.png");
			        stage.getIcons().add(anotherIcon);
			        stage.setTitle("Yu's Lab");
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
					Image anotherIcon = new Image("resources/favicon.png");
			        stage.getIcons().add(anotherIcon);
			        stage.setTitle("Yu's Lab");
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
	            scene3d.snapshot(null, writableImage);
	            BufferedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
	            //Write the snapshot to the chosen file
	            ImageIO.write(renderedImage, "png", file);
	        } catch (IOException ex) { 
	        	ex.printStackTrace(); 
	        }
	    }
	}
	
	public void importPic(){
		ShapeChooser shapeChooser = new Import2DChooser(width, height, Color.WHITE, null, new Callback<ModelShape, Integer>(){
			@Override
			public Integer call(ModelShape param) {
				if(param!=null){
					addNewShape(param);
				}
				return null;
			}
		});
		shapeChooser.showEditor();
	}
	
	public void import3D(){
		ShapeChooser shapeChooser = new Import3DChooser(width, height, Color.WHITE, null, new Callback<ModelShape, Integer>(){
			@Override
			public Integer call(ModelShape param) {
				if(param!=null){
					addNewShape(param);
				}
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
	
	public SubScene createScene3D() {
	    scene3d = new SubScene(this, width, height, true, SceneAntialiasing.BALANCED);
	    scene3d.setFill(Color.WHITE);
	    scene3d.setCamera(new PerspectiveCamera());
	    return scene3d;
	}
	
}
