package controller;

import view.UIComponentFactory;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
	private int height=320, width=320;
	/**
	 * Displays the splash screen on stage
	 * @param stage The stage to display the splash screen
	 */
	public StartMenu(Stage stage) {	
	    mainStage=stage;
		new UIComponentFactory();
		root.getChildren().add(new Label("Please choose the size of picture:"));
		root.getChildren().add(UIComponentFactory.unsignedIntSlider(320, 320, 1000, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				height = param;
				return null;
			}
			}, 
			"height"));
		root.getChildren().add(UIComponentFactory.unsignedIntSlider(320, 320, 1000, new Callback<Integer, Integer>(){
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
		stage.setScene(new Scene(root));
		stage.show();
	}


	private void setupBoard() {
		new Board(mainStage, height, width);
	}
}