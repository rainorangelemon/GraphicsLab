package Vision;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import Model.ModelShape;
import UI.ShapeChooser;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Board {
	private BorderPane root = new BorderPane();
	private Scene scene;
	private SketchPad paper;
	@SuppressWarnings({ "serial" })
	private List<String> availableShapes = new ArrayList<String>(){{
		this.add("Line");
	}};
	private Color currentColor = Color.BLACK;
	
	public Board(Stage mainStage, int height, int width){
		addMenuBar(mainStage, height, width);
		addSketchPad(mainStage, height, width);
		addPosInformation(mainStage);
		scene = new Scene(root);
		mainStage.setScene(scene);
		mainStage.sizeToScene();
		mainStage.centerOnScreen();
		mainStage.show();
	}
	
	private void addSketchPad(Stage mainStage, int height, int width){
		ScrollPane preview = new ScrollPane();
		preview.setFitToHeight(true);
		preview.setFitToWidth(true);
		paper = new SketchPad(height, width);
		preview.setContent(paper);
		preview.setPannable(true);
		preview.setPrefSize(width, height);
		BorderPane.setAlignment(preview,Pos.CENTER);
		root.setCenter(preview);
	}
	
	private void addMenuBar(Stage mainStage, int height, int width){
		 MenuBar menuBar = new MenuBar();
		 menuBar.prefWidthProperty().bind(mainStage.widthProperty());
		 // File menu - new, exit
		 Menu fileMenu = new Menu("File");
		 MenuItem newMenuItem = new MenuItem("New");
		 newMenuItem.setOnAction(e -> {
			 Stage newStage = new Stage();
			 new StartMenu(newStage);
		 });
		 MenuItem exitMenuItem = new MenuItem("Exit");
		 exitMenuItem.setOnAction(e -> mainStage.close());

		 fileMenu.getItems().addAll(newMenuItem,
		        new SeparatorMenuItem(), exitMenuItem);
		 
		 Menu shapeMenu = addShapeOptions(height, width);
		 
		 menuBar.getMenus().addAll(fileMenu, shapeMenu);
		 root.setTop(menuBar);
	}
	
	private void addPosInformation(Stage mainStage){
		String OUTSIDE_TEXT = "Outside SketchPad";
		Label reporter = new Label("");
	    paper.setOnMouseMoved(new EventHandler<MouseEvent>() {
	      @Override public void handle(MouseEvent event) {
	        String msg =
	          "(x: "       + event.getX()      + ", y: "       + event.getY()       + ") -- " +
	          "(width: "  + paper.getWidth() + ", height: "  + paper.getHeight()  + ")";

	        reporter.setText(msg);
	      }
	    });

	    paper.setOnMouseExited(new EventHandler<MouseEvent>() {
	      @Override public void handle(MouseEvent event) {
	        reporter.setText(OUTSIDE_TEXT);
	      }
	    });
	    root.setBottom(reporter);
	}
	
	private Menu addShapeOptions(int height, int width){
		Menu shapeMenu = new Menu("Shapes");
		for(String shapeName: availableShapes){
			MenuItem shapeOption = new MenuItem(shapeName);
			shapeOption.setOnAction((f)->{
			try {
				Stage stage = new Stage();
				@SuppressWarnings("unused")
				Class<?> shapeClass = Class.forName("Model.Model"+shapeName);
				Class<?> shapeEditorClass = Class.forName("UI."+shapeName+"Chooser");
				Constructor<?> chooserConstructor = shapeEditorClass.getConstructor(new Class[]{int.class, int.class, Color.class, Callback.class});
				ShapeChooser shapeChooser = (ShapeChooser) chooserConstructor.newInstance(width, height, currentColor, new Callback<ModelShape, Integer>(){
					@Override
					public Integer call(ModelShape param) {
						stage.close();
						paper.addNewShape(param);
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
			shapeMenu.getItems().add(shapeOption);
		}
		return shapeMenu;
	}
}
