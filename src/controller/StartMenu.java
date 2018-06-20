package controller;

import util.UIComponentFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;


/**
 * StartMenu is for users to choose the configurations of the whole project
 * @author rainorangelemon
 *
 */

public class StartMenu {
    
	private Stage mainStage;
	private VBox root = new VBox();
	private static int MIN_SIZE=400, MAX_SIZE=1000, DEFAULT_SIZE=600;
	int width = DEFAULT_SIZE, height = DEFAULT_SIZE;
	/**
	 * Displays the splash screen on stage
	 * @param stage The stage to display the splash screen
	 */
	public StartMenu(Stage stage) {	
	    mainStage=stage;
		new UIComponentFactory();
		root.getChildren().add(new Label("Please choose the size of picture:\n\n"));
		root.getChildren().add(UIComponentFactory.unsignedIntSlider(DEFAULT_SIZE, MIN_SIZE, MAX_SIZE, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				height = param;
				return null;
			}
			}, 
			"height"));
		root.getChildren().add(UIComponentFactory.unsignedIntSlider(DEFAULT_SIZE, MIN_SIZE, MAX_SIZE, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				width = param;
				return null;
			}
			}, 
			"width"));
		Button goBoard = new Button("Go to Board!");
		goBoard.setOnMouseClicked(e->setupBoard());
		root.getChildren().add(goBoard);
		double height = 150;
		double width = 230;
		root.setPrefSize(width, height);
		root.setMinSize(width, height);
		root.setMaxSize(width, height);
		BorderPane pane = new BorderPane();
		pane.setPrefSize(300, 200);
		pane.setCenter(root);
		stage.setScene(new Scene(pane));
		stage.show();
	}


	private void setupBoard() {
		new Board(mainStage, height, width);
	}
}