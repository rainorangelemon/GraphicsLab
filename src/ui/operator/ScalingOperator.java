package ui.operator;

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
import model.operation.OperationScaling;
import view.UIComponentFactory;

public class ScalingOperator extends Operator{
	
	private int width, height;
	private ShapeManager manager;
	private BorderPane root = new BorderPane();
	private Callback<ModelOperation, Integer> saver;
	private OperationScaling scaling;
	
	public ScalingOperator(int width, int height, ShapeManager manager, OperationScaling scaling, Callback<ModelOperation, Integer> saver){
		this.height = height;
		this.width = width;
		this.manager = manager;
		this.saver = saver;
		this.scaling = scaling;
		if(scaling!=null){
			this.scaling = scaling;
		}else{
			this.scaling = new OperationScaling(manager.getCurrentIndex(), -1, 0, 0, 1, 1);
		}
	}
	
	
	@Override
	public Node showEditor(){	
		VBox shapeChooser = new VBox();
		Label start = new Label("target shape's index");
		ChoiceBox<String> box = createShapeBox(manager, scaling);
		box.getSelectionModel()
	    .selectedItemProperty()
	    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
	    	scaling.setShapeIndex(new Integer(newValue.substring(0, newValue.indexOf(':'))));
	    	showEditor();
	    });
		shapeChooser.getChildren().addAll(start, box);
		
		VBox positionModifier = new VBox();
		if((scaling.getShapeIndex()<manager.getSteps().size())&&(scaling.getShapeIndex()>=0)){
			Label position = new Label("pivot point's position");
			HBox start_x = UIComponentFactory.unsignedIntSlider(scaling.getScalePointX(), 0, width-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					scaling.setScalePointX(param);
					return null;
				}
				}, 
				"pivot point' x");
			HBox start_y = UIComponentFactory.unsignedIntSlider(scaling.getScalePointY(), 0, height-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					scaling.setScalePointY(param);
					return null;
				}
				}, 
				"pivot point' y");
			Label scale = new Label("scaling's size");
			HBox scale_x = UIComponentFactory.doubleSlider(scaling.getScaleSizeX(), 0.5, 1.5, new Callback<Double, Integer>(){
				@Override
				public Integer call(Double param) {
					scaling.setScaleSizeX(param);
					return null;
				}
				}, 
				"pivot point' y");
			HBox scale_y = UIComponentFactory.doubleSlider(scaling.getScaleSizeY(), 0.5, 1.5, new Callback<Double, Integer>(){
				@Override
				public Integer call(Double param) {
					scaling.setScaleSizeY(param);
					return null;
				}
				}, 
				"pivot point' y");
			positionModifier.getChildren().addAll(position, start_x, start_y, scale, scale_x, scale_y);
		}
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			saver.call(scaling);
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
