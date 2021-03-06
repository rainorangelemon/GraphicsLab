package controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import model.ModelShape;
import model.ShapeManager;
import model.operation.ModelOperation;
import ui.chooser.ShapeChooser;
import ui.operator.Operator;
import util.UIComponentFactory;
import view.SketchPad;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
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
	private Map<String, String> availableShapes = new HashMap<String, String>(){{
		this.put("Line", "Line");
		this.put("Circle/Oval", "Circle");
		this.put("Polygon", "Polygon");
		this.put("Fill", "Fill");
		this.put("Bezier", "Bezier");
	}};
	
	@SuppressWarnings("serial")
	private Map<String, String> availableOperations = new HashMap<String, String>(){{
		this.put("Translation", "Translation");
		this.put("Rotation", "Rotation");
		this.put("Scaling", "Scaling");
		this.put("Clip", "Clip");
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
		preview.setContent(paper.createScene3D());
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
		 
		 MenuItem import2DMenuItem = new MenuItem("Import 2D: PNG/JPG");
		 import2DMenuItem.setOnAction(e -> {
			 paper.importPic();
		 });
		 
		 MenuItem import3DMenuItem = new MenuItem("Import 3D: OFF/OBJ");
		 import3DMenuItem.setOnAction(e -> {
			 paper.import3D();
		 });
		 
		 MenuItem exportMenuItem = new MenuItem("Export");
		 exportMenuItem.setOnAction(e -> {
			 paper.captureAndSaveDisplay();
		 });
		 MenuItem exitMenuItem = new MenuItem("Exit");
		 exitMenuItem.setOnAction(e -> mainStage.close());

		 fileMenu.getItems().addAll(newMenuItem,
		        new SeparatorMenuItem(), import2DMenuItem, import3DMenuItem, 
		        new SeparatorMenuItem(), exportMenuItem, new SeparatorMenuItem(), exitMenuItem);
		 
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
		 
		 Menu operationMenu = addOperationOptions(height, width);
		 
		 menuBar.getMenus().addAll(fileMenu, shapeMenu, operationMenu, editMenu);
		 root.setTop(menuBar);
	}

	private void addPosInformation(Stage mainStage){
	    paper.setOnMouseMoved(e->{
//	    	paper.registerCheckHitOnShape().handle(e);
	    	paper.registerCheckPosition().handle(e);
	    });
	    root.setBottom(paper.getReport());
	}
	
	private Menu addShapeOptions(int height, int width){
		Menu shapeMenu = new Menu("Shapes");
		for(String shapeName: availableShapes.keySet()){
			MenuItem shapeOption = new MenuItem(shapeName);
			String className = availableShapes.get(shapeName);
			shapeOption.setOnAction((f)->{
				try {
					Stage stage = new Stage();
					Class<?> shapeClass = Class.forName("model.Model"+className);
					Class<?> shapeEditorClass = Class.forName("ui.chooser."+className+"Chooser");
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
	
	private Menu addOperationOptions(int height, int width) {
		Menu operationMenu = new Menu("Operations");
		for(String operationName: availableOperations.keySet()){
			MenuItem shapeOption = new MenuItem(operationName);
			String className = availableOperations.get(operationName);
			shapeOption.setOnAction((f)->{
				try {
					Stage stage = new Stage();
					Class<?> shapeClass = Class.forName("model.operation.Operation"+className);
					Class<?> shapeEditorClass = Class.forName("ui.operator."+className+"Operator");
					Constructor<?> chooserConstructor = shapeEditorClass.getConstructor(new Class[]{int.class, int.class, ShapeManager.class, shapeClass, Callback.class});
					Operator operationChooser = (Operator) chooserConstructor.newInstance(width, height, paper.getShapeManager(), null, new Callback<ModelOperation, Integer>(){
						@Override
						public Integer call(ModelOperation param) {
							stage.close();
							paper.addNewOperation(param);
							return null;
						}
					});
					Image anotherIcon = new Image("resources/favicon.png");
			        stage.getIcons().add(anotherIcon);
			        stage.setTitle("Yu's Lab");
					stage.setScene(new Scene(new Pane(operationChooser.showEditor())));
					stage.sizeToScene();
					stage.show();
				} catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e1) {
					e1.printStackTrace(); //handled by exiting the program
					System.exit(1);
				}
			});
			operationMenu.getItems().add(shapeOption);
		}
		return operationMenu;
	}
}
