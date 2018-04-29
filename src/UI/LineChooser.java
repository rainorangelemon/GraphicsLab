package UI;

import Model.ModelShape;
import Model.ModelLine;
import Vision.UIComponentFactory;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class LineChooser extends ShapeChooser{
	
	private Callback<ModelShape, Integer> saver;
	private int width, height;
	private ModelLine line;

	public LineChooser(int width, int height, Color color, ModelLine line, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		this.height = height;
		this.width = width;
		if(line!=null){
			this.line = line;
		}else{
			this.line = new ModelLine(0, 10, 0, 10, color);
		}
	}
	
	@Override
	public Node showEditor() {
		BorderPane root = new BorderPane();
		VBox positionModifier = new VBox();
		Label start = new Label("start position");
		HBox start_x = UIComponentFactory.intSlider(line.getX0(), 0, width-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				line.setPos(param, line.getY0(), line.getX1(), line.getY1());
				return null;
			}
			}, 
			"x");
		HBox start_y = UIComponentFactory.intSlider(line.getY0(), 0, height-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				line.setPos(line.getX0(), param, line.getX1(), line.getY1());
				return null;
			}
			}, 
			"y");
		Label end = new Label("end position");
		HBox end_x = UIComponentFactory.intSlider(line.getX1(), 0, width-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				line.setPos(line.getX0(), line.getY0(), param, line.getY1());
				return null;
			}
			}, 
			"x");
		HBox end_y = UIComponentFactory.intSlider(line.getY1(), 0, height-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				line.setPos(line.getX0(), line.getY0(), line.getX1(), param);
				return null;
			}
			}, 
			"y");
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(line);});
		positionModifier.getChildren().addAll(start, start_x, start_y, end, end_x, end_y, button);
		root.setLeft(positionModifier);
		root.setRight(super.getColorPicker(line.getColor(), new Callback<Color, Integer>(){
			@Override
			public Integer call(Color param) {
				line.setColor(param);
				return null;
			}
		}));
		return root;
	}

}
