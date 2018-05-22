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
import model.ModelShape;
import model.ShapeManager;
import view.UIComponentFactory;

public class ScalingOperator extends Operator{
	
	private int width, height;
	private ShapeManager manager;
	private BorderPane root = new BorderPane();
	private Callback<ModelShape, Integer> saver;
	private ModelShape shape = null;
	
	public ScalingOperator(int width, int height, ShapeManager manager, Callback<ModelShape, Integer> saver){
		this.height = height;
		this.width = width;
		this.manager = manager;
		this.saver = saver;
	}
	
	
	@Override
	public Node showEditor(int index){	
		VBox shapeChooser = new VBox();
		Label start = new Label("target shape's index");
		ChoiceBox<String> box = new ChoiceBox<String>();
		for(int i=0;i<manager.getSteps().size();i++){
			box.getItems().add(new Integer(i).toString()+ ": " + manager.getSteps().get(i).getClass().getSimpleName().substring(5));
		}
		if((index<manager.getSteps().size())&&(index>=0)){
			box.getSelectionModel().select(box.getItems().get(index));
		}
		box.getSelectionModel()
		    .selectedItemProperty()
		    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    	showEditor(new Integer(newValue.substring(0, newValue.indexOf(':'))));
		    });
		shapeChooser.getChildren().addAll(start, box);
		
		VBox positionModifier = new VBox();
		if((index<manager.getSteps().size())&&(index>=0)){
			ModelShape shape = manager.getSteps().get(index);
			this.shape = shape;
			Label position = new Label("pivot point's position");
			HBox start_x = UIComponentFactory.unsignedIntSlider(shape.getScalePointX(), 0, width-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					shape.setScalePointX(param);
					return null;
				}
				}, 
				"pivot point' x");
			HBox start_y = UIComponentFactory.unsignedIntSlider(shape.getScalePointY(), 0, height-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					shape.setScalePointY(param);
					return null;
				}
				}, 
				"pivot point' y");
			Label scale = new Label("scaling's size");
			HBox scale_x = UIComponentFactory.doubleSlider(shape.getScaleSizeX(), 0.5, 1.5, new Callback<Double, Integer>(){
				@Override
				public Integer call(Double param) {
					shape.setScaleSizeX(param);
					return null;
				}
				}, 
				"pivot point' y");
			HBox scale_y = UIComponentFactory.doubleSlider(shape.getScaleSizeY(), 0.5, 1.5, new Callback<Double, Integer>(){
				@Override
				public Integer call(Double param) {
					shape.setScaleSizeY(param);
					return null;
				}
				}, 
				"pivot point' y");
			positionModifier.getChildren().addAll(position, start_x, start_y, scale, scale_x, scale_y);
		}
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			if(shape!=null){
				shape.scaling(shape.getScalePointX(), shape.getScalePointY(), shape.getScaleSizeX(), shape.getScaleSizeY());
			}
			saver.call(null);
		});
		positionModifier.getChildren().addAll(button);
		root.setLeft(shapeChooser);
		root.setRight(positionModifier);
		return root;
	}
	
}
