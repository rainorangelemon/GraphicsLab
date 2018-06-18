import util.ObjImporter;
import javafx.application.Application;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;



public class test extends Application{

    private static final double MODEL_SCALE_FACTOR = 3;
    private static final double MODEL_X_OFFSET = 0; // standard
    private static final double MODEL_Y_OFFSET = -200; // standard
    private static final int VIEWPORT_SIZE = 400;
    private Rotate rotateX = new Rotate(180, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(90, Rotate.Y_AXIS);
    private double mouseOldX, mouseOldY = 0;
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Group grp = new Group();
//		File initialFile = new File();
//	    InputStream targetStream = new FileInputStream(initialFile);
        // Scooter-normals.obj
		ObjImporter objImporter = new ObjImporter("src/resources/Luka/Luka.obj");
		objImporter.updateMeshes();
		for(String key: objImporter.getMeshes()){
			MeshView newMesh = objImporter.buildMeshView(key);
			MeshView meshView = newMesh;
			meshView.setDrawMode(DrawMode.FILL);
            meshView.setTranslateX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
            meshView.setTranslateY(- VIEWPORT_SIZE  + MODEL_Y_OFFSET);
            meshView.setTranslateZ(VIEWPORT_SIZE / 2);
			grp.getChildren().add(newMesh);
		}
		AmbientLight pointLight = new AmbientLight(Color.WHITE);
		pointLight.setOpacity(1);
//	    pointLight.setTranslateX(100);
//	    pointLight.setTranslateY(100);
//	    pointLight.setTranslateZ(-300);
//	    pointLight.setRotate(90);
	    grp.getChildren().add(pointLight);
		
		Scene newScene  = new Scene(grp, 500, 500);
        rotateX.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        rotateX.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
        rotateX.setPivotZ(VIEWPORT_SIZE / 2);

        rotateY.setPivotX(VIEWPORT_SIZE / 2 + MODEL_X_OFFSET);
        rotateY.setPivotY(VIEWPORT_SIZE / 2 + MODEL_Y_OFFSET);
        rotateY.setPivotZ(VIEWPORT_SIZE / 2);
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
        camera.getTransforms().addAll (rotateX, rotateY);

        
		newScene.setCamera(camera);
		primaryStage.setScene(newScene);
		primaryStage.show();
	}

	public static void main(String[] args){
		launch(args);
	}
	
}
