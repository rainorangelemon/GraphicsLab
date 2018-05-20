package controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import model.ModelShape;
import ui.ShapeChooser;
import view.SketchPad;
import view.UIComponentFactory;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
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
		this.add("Circle");
		this.add("Fill");
	}};
	private Color currentColor = Color.BLACK;
	
	public Board(Stage mainStage, int height, int width){
		addMenuBar(mainStage, height, width);
		addSketchPad(mainStage, height, width);
		addPosInformation(mainStage);
		root.setLeft(paper.getStepEditor());
		scene = new Scene(root);
		mainStage.setScene(scene);
		mainStage.sizeToScene();
		mainStage.centerOnScreen();
		mainStage = UIComponentFactory.renderStage(mainStage);
		Image anotherIcon = new Image("resources/favicon.png");
        mainStage.getIcons().add(anotherIcon);
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
			 newStage = UIComponentFactory.renderStage(newStage);
			 new StartMenu(newStage);
		 });
		 MenuItem importMenuItem = new MenuItem("Import");
		 importMenuItem.setOnAction(e -> {
			 paper.importPic();
		 });
		 MenuItem exportMenuItem = new MenuItem("Export");
		 exportMenuItem.setOnAction(e -> {
			 paper.captureAndSaveDisplay();
		 });
		 MenuItem exitMenuItem = new MenuItem("Exit");
		 exitMenuItem.setOnAction(e -> mainStage.close());

		 fileMenu.getItems().addAll(newMenuItem,
		        new SeparatorMenuItem(), importMenuItem, exportMenuItem, new SeparatorMenuItem(), exitMenuItem);
		 
		 Menu shapeMenu = addShapeOptions(height, width);
		 
		 Menu editMenu = new Menu("Edit");
		 MenuItem undo = new MenuItem("Undo");
		 undo.setOnAction(e->{
			 paper.undo();
		 });
		 MenuItem redo = new MenuItem("Redo");
		 redo.setOnAction(e->{
			 paper.redo();
		 });
		 editMenu.getItems().addAll(undo, redo);
		 
		 menuBar.getMenus().addAll(fileMenu, shapeMenu, editMenu);
		 root.setTop(menuBar);
	}
	
	private void addPosInformation(Stage mainStage){
		String OUTSIDE_TEXT = "Outside SketchPad";
		Label reporter = new Label("");
	    paper.setOnMouseMoved(new EventHandler<MouseEvent>() {
	      @Override public void handle(MouseEvent event) {
	    	if((new Double(event.getY()).intValue()<paper.getDotMatrix().length)&&(new Double(event.getX()).intValue()<paper.getDotMatrix()[0].length)){
		        String msg =
		          "(x: "       + event.getX()      + ", y: "       + event.getY()       + ") -- " +
		          "(width: "  + paper.getWidth() + ", height: "  + paper.getHeight()  + ") -- " +
		          "Color: " + paper.getDotMatrix()[new Double(event.getY()).intValue()][new Double(event.getX()).intValue()].getColorString();
		        reporter.setText(msg);
	    	}
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
					Class<?> shapeClass = Class.forName("model.Model"+shapeName);
					Class<?> shapeEditorClass = Class.forName("ui."+shapeName+"Chooser");
					Constructor<?> chooserConstructor = shapeEditorClass.getConstructor(new Class[]{int.class, int.class, Color.class, shapeClass, Callback.class});
					ShapeChooser shapeChooser = (ShapeChooser) chooserConstructor.newInstance(width, height, currentColor, null, new Callback<ModelShape, Integer>(){
						@Override
						public Integer call(ModelShape param) {
							stage.close();
							paper.addNewShape(param);
							return null;
						}
					});
					Image anotherIcon = new Image("resources/favicon.png");
			        stage.getIcons().add(anotherIcon);
			        stage.setTitle("Yu's Lab");
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
