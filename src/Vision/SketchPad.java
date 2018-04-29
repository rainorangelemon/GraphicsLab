package Vision;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Vector;

import Model.ModelDot;
import Model.ModelShape;
import Model.ShapeManager;
import UI.ShapeChooser;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SketchPad extends Pane{
	private int height, width;
	private Dot[][] dotMatrix;
	private ShapeManager shapeManager;
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
		List<ModelDot> modelDots = shapeManager.addNewStep(newShape);
		for(ModelDot newDot: modelDots){
			if((newDot.getY()>=0)&&(newDot.getY()<height)&&(newDot.getX()>=0)&&(newDot.getX()<width))
				dotMatrix[newDot.getY()][newDot.getX()].changeColor(newDot.getColor());
		}
		refreshStepEditor();
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

	private void refreshScreen(ModelDot[][] modelDots) {
		for(ModelDot[] dotRow: modelDots){
			for(ModelDot dot: dotRow){
				dotMatrix[dot.getY()][dot.getX()].changeColor(dot.getColor());
			}
		}
	}

	private void refreshStepEditor(){
		stepEditor.getChildren().clear();
		Label currentIndex = new Label("current index: " + new Integer(shapeManager.getCurrentIndex()-1).toString());
		stepEditor.getChildren().add(currentIndex);
		Vector<ModelShape> steps = shapeManager.getSteps();
		for(int i=0;i<steps.size();i++){
			ModelShape modelShape = steps.get(i);
			Button newButton = new Button(new Integer(i).toString() + ": " + modelShape.getClass().getSimpleName().substring(5));
			newButton.setOnMouseClicked(e->{
				try {
					Stage stage = new Stage();
					Class<?> shapeClass = modelShape.getClass();
					Class<?> shapeEditorClass = Class.forName("UI."+modelShape.getClass().getSimpleName().substring(5)+"Chooser");
					Constructor<?> chooserConstructor = shapeEditorClass.getConstructor(new Class[]{int.class, int.class, Color.class, shapeClass, Callback.class});
					ShapeChooser shapeChooser = (ShapeChooser) chooserConstructor.newInstance(width, height, modelShape.getColor(), modelShape, new Callback<ModelShape, Integer>(){
						@Override
						public Integer call(ModelShape param) {
							stage.close();
							refreshScreen(shapeManager.refresh());
							return null;
						}
					});
					stage.setScene(new Scene(new Pane(shapeChooser.showEditor())));
					stage.sizeToScene();
					stage.show();
				} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
					e1.printStackTrace(); //handled by exiting the program
					System.exit(1);
				}
			});
			stepEditor.getChildren().add(newButton);
		}
		
	}
	
	public ScrollPane getStepEditor() {
		ScrollPane result = new ScrollPane();
		result.setContent(stepEditor);
		result.setPannable(true);
		result.setPrefSize(120, height);
		return result;
	}
}
