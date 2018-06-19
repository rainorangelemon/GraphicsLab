package ui.chooser;

import model.ModelCircle;
import model.ModelShape;
import util.UIComponentFactory;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class CircleChooser extends ShapeChooser{
	
	private Callback<ModelShape, Integer> saver;
	private int width, height;
	private ModelCircle circle;

	public CircleChooser(int width, int height, Color color, ModelCircle circle, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		this.height = height;
		this.width = width;
		if(circle!=null){
			this.circle = circle;
		}else{
			this.circle = new ModelCircle(0, 0, 10, 10, true, color);
		}
	}
	
	@Override
	public Node showEditor() {
		BorderPane root = new BorderPane();
		VBox positionModifier = new VBox();
		Label start = new Label("center of circle:");
		HBox start_x = UIComponentFactory.unsignedIntSlider(circle.getXc(), 0, width-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				circle.setPos(param, circle.getYc(), circle.getA(), circle.getB());
				return null;
			}
			}, 
			"x");
		HBox start_y = UIComponentFactory.unsignedIntSlider(circle.getYc(), 0, height-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				circle.setPos(circle.getXc(), param, circle.getA(), circle.getB());
				return null;
			}
			}, 
			"y");
		Label start_horizontal_radius = new Label("the horizontal raduis of circle:");
		HBox horizontal_radius = UIComponentFactory.unsignedIntSlider(circle.getA(), 0, Math.min(height-1, width-1), new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				circle.setPos(circle.getXc(), circle.getYc(), param, circle.getB());
				return null;
			}
			}, 
			"horizontal radius");
		Label start_vertical_radius = new Label("the vertical raduis of circle:");
		HBox vertical_radius = UIComponentFactory.unsignedIntSlider(circle.getB(), 0, Math.min(height-1, width-1), new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				circle.setPos(circle.getXc(), circle.getYc(), circle.getA(), param);
				return null;
			}
			}, 
			"vertical radius");
		
		ChoiceBox<String> box = new ChoiceBox<String>();
		box.getItems().add("MidPoint");
		box.getItems().add("Bresenham");
		if(circle.isBresenham()){
			box.getSelectionModel().select("Bresenham");
		}else{
			box.getSelectionModel().select("MidPoint");
		}
		box.getSelectionModel()
		    .selectedItemProperty()
		    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    	if(newValue.equals(new String("MidPoint"))){
		    		circle.setBresenham(false);
		    	}else{
		    		circle.setBresenham(true);
		    	}
		    });
		
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(circle);});
		positionModifier.getChildren().addAll(start, start_x, start_y, 
				start_horizontal_radius, horizontal_radius,
				start_vertical_radius, vertical_radius,
				box,
				button);
		root.setLeft(positionModifier);
		root.setRight(super.getColorPicker(circle.getColor(), new Callback<Color, Integer>(){
			@Override
			public Integer call(Color param) {
				circle.setColor(param);
				return null;
			}
		}));
		return root;
	}

}
