package ui.chooser;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Pair;
import model.ModelPolygon;
import model.ModelShape;
import util.UIComponentFactory;

public class PolygonChooser extends ShapeChooser{
	private Callback<ModelShape, Integer> saver;
	private int width, height;
	private ModelPolygon polygon;
	private BorderPane root;

	public PolygonChooser(int width, int height, Color color, ModelPolygon polygon, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		this.height = height;
		this.width = width;
		if(polygon!=null){
			this.polygon = polygon;
		}else{
			this.polygon = new ModelPolygon(color, new ArrayList<Pair<Integer, Integer>>());
		}
	}
	
	@Override
	public Node showEditor() {
		root = new BorderPane();
		VBox positionModifier = new VBox();
		VBox points = new VBox();
		
		
		makePointList(points, polygon.getVertices());
		positionModifier.getChildren().addAll(points);
		
		root.setLeft(positionModifier);
		
		Label targetColor = new Label("which color you want to paint?");
		ColorPicker targetColorPicker = super.getColorPicker(polygon.getColor(), new Callback<Color, Integer>(){
			@Override
			public Integer call(Color param) {
				polygon.setColor(param);
				return null;
			}
		});
		
		VBox colorModifier = new VBox();
		colorModifier.getChildren().addAll(targetColor, targetColorPicker);
		
		root.setRight(colorModifier);
		return root;
	}
	
	private void makePointList(VBox result, List<Pair<Integer, Integer>> vertices){
		result.getChildren().clear();
		
		Button addButton = new Button("add a new point");
		Button deleteButton = new Button("delete the last point");
		HBox buttons = new HBox();
		buttons.getChildren().addAll(addButton, deleteButton);
		
		ArrayList<Pair<Integer, Integer>> interPoints = new ArrayList<Pair<Integer, Integer>>(vertices);
		
		addButton.setOnMouseClicked(e->{
			if(interPoints.size()>0){
				Pair<Integer, Integer> last = interPoints.get(interPoints.size()-1);
				interPoints.add(new Pair<Integer, Integer>(new Integer(last.getKey()), new Integer(last.getValue())));
			}else{
				interPoints.add(new Pair<Integer, Integer>(new Integer(0), new Integer(0)));
			}
			makePointList(result, interPoints);
		});
		
		deleteButton.setOnMouseClicked(e->{
			if(interPoints.size()>0){
				interPoints.remove(interPoints.size()-1);
				interPoints.trimToSize();
			}
			makePointList(result, interPoints);
		});
		result.getChildren().add(buttons);

		int index = 0;
		for(Pair<Integer, Integer> pair: interPoints){
			final int index2 = index;
			Integer x = pair.getKey();
			Integer y = pair.getValue();
			HBox start_x = UIComponentFactory.unsignedIntSlider(x, 0, width-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					Integer x = param;
					Integer y = interPoints.get(index2).getValue();
					interPoints.remove(index2);
					interPoints.trimToSize();
					interPoints.add(index2, new Pair<Integer, Integer>(x, y));
					return null;
				}
				}, 
				"point " + index +"'x");
			HBox start_y = UIComponentFactory.unsignedIntSlider(y, 0, height-1, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					Integer x = interPoints.get(index2).getKey();
					Integer y = param;
					interPoints.remove(index2);
					interPoints.trimToSize();
					interPoints.add(index2, new Pair<Integer, Integer>(x, y));
					return null;
				}
				}, 
				"point " + index +"'y");
			result.getChildren().add(start_x);
			result.getChildren().add(start_y);
			index ++;
		}
		
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			if(ModelPolygon.checkConvex(interPoints)){
				if(interPoints.size()>2){
					polygon.setVertices(interPoints);
					saver.call(polygon);
				}else{
					makeSizeError();
				}
			}else{
				makeConvexError();
			}
		});
		result.getChildren().add(button);
		
		if(root.getScene()!=null){
			root.getScene().getWindow().sizeToScene();
		}
	}
	
	private void makeConvexError(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Convexity Error");
		alert.setHeaderText("The polygon after operation is not a convex polygon");
		alert.setContentText("Please change the points of polygon and make it convex before you do the operation!");
		alert.showAndWait();
	}
	
	private void makeSizeError(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Number of Vertices Error");
		alert.setHeaderText("A polygon should have at least three vertices");
		alert.setContentText("Please change the points of polygon and make there at least three vertices before you do the operation!");
		alert.showAndWait();
	}
}
