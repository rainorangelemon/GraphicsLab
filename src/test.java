import util.ObjImporter;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;



public class test extends Application{

    private static final double MODEL_SCALE_FACTOR = 1000;
    private static final double MODEL_X_OFFSET = 100; // standard
    private static final double MODEL_Y_OFFSET = -200; // standard
    private static final int VIEWPORT_SIZE = 800;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private double mouseOldX, mouseOldY = 0;
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Group grp = new Group();
//		File initialFile = new File();
//	    InputStream targetStream = new FileInputStream(initialFile);
        ObjImporter.setScale(MODEL_SCALE_FACTOR);
		ObjImporter objImporter = new ObjImporter("src/resources/Scooter-normals.obj");
		for(String key: objImporter.getMeshes()){
			MeshView newMesh = objImporter.buildMeshView(key);
			MeshView meshView = newMesh;
            meshView.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
            meshView.setTranslateY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
            meshView.setTranslateZ(VIEWPORT_SIZE / 2);
			grp.getChildren().add(newMesh);
		}
		Scene newScene  = new Scene(grp, 500, 500);
        rotateX.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        rotateX.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
        rotateX.setPivotZ(VIEWPORT_SIZE / 2);

        rotateY.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        rotateY.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
        rotateY.setPivotZ(VIEWPORT_SIZE / 2);

        rotateZ.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        rotateZ.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
        rotateZ.setPivotZ(VIEWPORT_SIZE / 2);
        //################ End pivot points ################\\

        newScene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        newScene.setOnMouseDragged(event -> {
            rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
            rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();

        });
        
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(100);
        camera.setTranslateY(0);
        camera.setTranslateZ(100);
        camera.getTransforms().addAll (rotateX, rotateY, new Translate(0, 0, 400));

        
		newScene.setCamera(camera);
		primaryStage.setScene(newScene);
		primaryStage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
	
}
