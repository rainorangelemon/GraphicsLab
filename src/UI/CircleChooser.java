package UI;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import Model.ModelCircle;
import Model.ModelShape;
import Vision.UIComponentFactory;

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
			this.circle = new ModelCircle(0, 0, 10, color);
		}
	}
	
	@Override
	public Node showEditor() {
		BorderPane root = new BorderPane();
		VBox positionModifier = new VBox();
		Label start = new Label("center of circle:");
		HBox start_x = UIComponentFactory.intSlider(circle.getXc(), 0, width-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				circle.setPos(param, circle.getYc(), circle.getRadius());
				return null;
			}
			}, 
			"x");
		HBox start_y = UIComponentFactory.intSlider(circle.getYc(), 0, height-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				circle.setPos(circle.getXc(), param, circle.getRadius());
				return null;
			}
			}, 
			"y");
		Label start_radius = new Label("radius of circle:");
		HBox radius = UIComponentFactory.intSlider(circle.getRadius(), 0, Math.min(height-1, width-1), new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				circle.setPos(circle.getXc(), circle.getYc(), param);
				return null;
			}
			}, 
			"radius");
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(circle);});
		positionModifier.getChildren().addAll(start, start_x, start_y, start_radius, radius, button);
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
