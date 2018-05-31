package ui.operator;

import view.UIComponentFactory;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.ShapeManager;
import model.operation.ModelOperation;
import model.operation.OperationRotation;

public class RotationOperator extends Operator{
	
	private int width, height;
	private ShapeManager manager;
	private BorderPane root = new BorderPane();
	private Callback<ModelOperation, Integer> saver;
	private OperationRotation rotation;
	
	public RotationOperator(int width, int height, ShapeManager manager, OperationRotation rotation, Callback<ModelOperation, Integer> saver){
		this.height = height;
		this.width = width;
		this.manager = manager;
		this.saver = saver;
		if(rotation!=null){
			this.rotation = rotation;
		}else{
			this.rotation = new OperationRotation(manager.getCurrentIndex(), -1, 0, 0, 0);
		}
	}
	
	
	@Override
	public Node showEditor(){
		
		VBox shapeChooser = new VBox();
		Label start = new Label("target shape's index");
		ChoiceBox<String> box = createShapeBox(manager, rotation);
		box.getSelectionModel()
		    .selectedItemProperty()
		    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    	rotation.setShapeIndex(new Integer(newValue.substring(0, newValue.indexOf(':'))));
		    	showEditor();
		    });
		shapeChooser.getChildren().addAll(start, box);
		
		VBox positionModifier = new VBox();
		if((rotation.getShapeIndex()<manager.getSteps().size())&&(rotation.getShapeIndex()>=0)){
			Label position = new Label("the pivot point of rotation");
			HBox start_x = UIComponentFactory.unsignedIntSlider(rotation.getRotationX(), 0, width-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					rotation.setRotationX(param);
					return null;
				}
				}, 
				"x of point");
			HBox start_y = UIComponentFactory.unsignedIntSlider(rotation.getRotationY(), 0, height-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					rotation.setRotationY(param);
					return null;
				}
				}, 
				"y of point");
			Label degree = new Label("the degree of rotation");
			HBox start_degree = UIComponentFactory.unsignedIntSlider(rotation.getRotationDegree(), 0, 359, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					rotation.setRotationDegree(param);
					return null;
				}
				}, 
				"degree");
			positionModifier.getChildren().addAll(position, start_x, start_y, degree, start_degree);
		}
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			saver.call(rotation);
		});
		positionModifier.getChildren().addAll(button);
		root.setLeft(shapeChooser);
		root.setRight(positionModifier);
		if(root.getScene()!=null){
			root.getScene().getWindow().sizeToScene();
		}
		return root;
	}
}
