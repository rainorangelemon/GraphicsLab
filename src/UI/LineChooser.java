package UI;

import Model.ModelShape;
import Model.ModelLine;
import Vision.UIComponentFactory;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class LineChooser extends ShapeChooser{
	
	private Callback<ModelShape, Integer> saver;
	private int width, height;
	private Color color;
	int x0=0, y0=0, x1=10, y1=10;

	public LineChooser(int width, int height, Color color, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		this.height = height;
		this.width = width;
		this.color = color;
	}
	
	@Override
	public Node showEditor() {
		VBox root = new VBox();
		Label start = new Label("start position");
		HBox start_x = UIComponentFactory.intSlider(0, 0, width, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				x0 = param;
				return null;
			}
			}, 
			"x");
		HBox start_y = UIComponentFactory.intSlider(0, 0, height, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				y0 = param;
				return null;
			}
			}, 
			"y");
		Label end = new Label("end position");
		HBox end_x = UIComponentFactory.intSlider(0, 10, width, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				x1 = param;
				return null;
			}
			}, 
			"x");
		HBox end_y = UIComponentFactory.intSlider(0, 10, height, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				y1 = param;
				return null;
			}
			}, 
			"y");
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(new ModelLine(x0, x1, y0, y1, color));});
		root.getChildren().addAll(start, start_x, start_y, end, end_x, end_y, button);
		return root;
	}

}
