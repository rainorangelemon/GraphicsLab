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
import model.ModelShape;
import model.ShapeManager;

public class RotationOperator extends Operator{
	
	private int width, height;
	private ShapeManager manager;
	private BorderPane root = new BorderPane();
	private ModelShape shape = null;
	private Callback<ModelShape, Integer> saver;
	
	public RotationOperator(int width, int height, ShapeManager manager, Callback<ModelShape, Integer> saver){
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
			Label position = new Label("the pivot point of rotation");
			HBox start_x = UIComponentFactory.unsignedIntSlider(shape.getRotationX(), 0, width-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					shape.setRotationX(param);
					return null;
				}
				}, 
				"x of point");
			HBox start_y = UIComponentFactory.unsignedIntSlider(shape.getRotationY(), 0, height-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					shape.setRotationY(param);
					return null;
				}
				}, 
				"y of point");
			Label degree = new Label("the degree of rotation");
			HBox start_degree = UIComponentFactory.unsignedIntSlider(shape.getRotationDegree(), 0, 359, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					shape.setRotationDegree(param);
					return null;
				}
				}, 
				"degree");
			positionModifier.getChildren().addAll(position, start_x, start_y, degree, start_degree);
		}
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			if(shape!=null){
				shape.rotation(shape.getRotationX(), shape.getRotationY(), shape.getRotationDegree());
			}
			saver.call(null);
		});
		positionModifier.getChildren().addAll(button);
		root.setLeft(shapeChooser);
		root.setRight(positionModifier);
		return root;
	}
}
