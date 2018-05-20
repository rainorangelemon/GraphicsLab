package ui;

import java.util.ArrayList;
import java.util.List;

import view.UIComponentFactory;
import model.ModelBrevier;
import model.ModelShape;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Pair;

public class BrevierChooser extends ShapeChooser{

	private ModelBrevier brevier; 
	private Callback<ModelShape, Integer> saver;
	private int width, height;
	private BorderPane root;
	
	public BrevierChooser(int width, int height, Color color, ModelBrevier brevier, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		this.height = height;
		this.width = width;
		if(brevier!=null){
			this.brevier = brevier;
		}else{
			this.brevier = new ModelBrevier(new ArrayList<Pair<Integer, Integer>>(), width, height, color);
		}
	}
	
	@Override
	public Node showEditor() {
		root = new BorderPane();
		VBox positionModifier = new VBox();
		VBox points = new VBox();
		Button addButton = new Button("add a new point");
		Button deleteButton = new Button("delete the last point");
		HBox buttons = new HBox();
		buttons.getChildren().addAll(addButton, deleteButton);
		
		addButton.setOnMouseClicked(e->{
			ArrayList<Pair<Integer, Integer>> interPoints = new ArrayList<Pair<Integer, Integer>>(brevier.getInterpolationDots());
			if(interPoints.size()>0){
				Pair<Integer, Integer> last = interPoints.get(interPoints.size()-1);
				interPoints.add(new Pair<Integer, Integer>(new Integer(last.getKey()), new Integer(last.getValue())));
			}else{
				interPoints.add(new Pair<Integer, Integer>(new Integer(0), new Integer(0)));
			}
			brevier.setInterpolationDots(interPoints);
			makePointList(points);
		});
		
		deleteButton.setOnMouseClicked(e->{
			ArrayList<Pair<Integer, Integer>> interPoints = new ArrayList<Pair<Integer, Integer>>(brevier.getInterpolationDots());
			if(interPoints.size()>0){
				interPoints.remove(interPoints.size()-1);
				interPoints.trimToSize();
			}
			brevier.setInterpolationDots(interPoints);
			makePointList(points);
		});
		
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(brevier);});
		
		
		makePointList(points);
		positionModifier.getChildren().addAll(buttons, points, button);
		
		root.setLeft(positionModifier);
		
		Label targetColor = new Label("which color you want to paint?");
		ColorPicker targetColorPicker = super.getColorPicker(brevier.getColor(), new Callback<Color, Integer>(){
			@Override
			public Integer call(Color param) {
				brevier.setColor(param);
				return null;
			}
		});
		
		VBox colorModifier = new VBox();
		colorModifier.getChildren().addAll(targetColor, targetColorPicker);
		
		root.setRight(colorModifier);
		return root;
	}
	
	private void makePointList(VBox result){
		result.getChildren().clear();
		List<Pair<Integer, Integer>> interPoints = brevier.getInterpolationDots();
		int index = 0;
		for(Pair<Integer, Integer> pair: interPoints){
			final int index2 = index;
			Integer x = pair.getKey();
			Integer y = pair.getValue();
			HBox start_x = UIComponentFactory.intSlider(x, 0, width-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					Integer x = param;
					Integer y = interPoints.get(index2).getValue();
					interPoints.remove(index2);
					interPoints.add(index2, new Pair<Integer, Integer>(x, y));
					return null;
				}
				}, 
				"point " + index +"'x");
			HBox start_y = UIComponentFactory.intSlider(y, 0, height-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					Integer x = interPoints.get(index2).getKey();
					Integer y = param;
					interPoints.remove(index2);
					interPoints.add(index2, new Pair<Integer, Integer>(x, y));
					return null;
				}
				}, 
				"point " + index +"'y");
			result.getChildren().add(start_x);
			result.getChildren().add(start_y);
			index ++;
		}
		if(root.getScene()!=null)
			root.getScene().getWindow().sizeToScene();
	}

}
