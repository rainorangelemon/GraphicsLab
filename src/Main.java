
import Vision.StartMenu;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * This is the Main of the whole project, which should be kept simple,
 * to make its only one function be emphasized: to start the program
 * @author rainorangelemon
 * 
 *
 */

public class Main extends Application{

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		new StartMenu(primaryStage);
	}

}
