package ui.chooser;

import java.util.ArrayList;

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
import view.UIComponentFactory;

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
		Button addButton = new Button("add a new point");
		Button deleteButton = new Button("delete the last point");
		HBox buttons = new HBox();
		buttons.getChildren().addAll(addButton, deleteButton);
		
		addButton.setOnMouseClicked(e->{
			ArrayList<Pair<Integer, Integer>> vertices = new ArrayList<Pair<Integer, Integer>>(polygon.getVertices());
			if(vertices.size()>0){
				Pair<Integer, Integer> last = vertices.get(vertices.size()-1);
				vertices.add(new Pair<Integer, Integer>(new Integer(last.getKey()), new Integer(last.getValue())));
			}else{
				vertices.add(new Pair<Integer, Integer>(new Integer(0), new Integer(0)));
			}
			if(ModelPolygon.checkConvex(vertices)){
				polygon.setVertices(vertices);
				makePointList(points);
			}else{
				makeError();
			}
		});
		
		deleteButton.setOnMouseClicked(e->{
			ArrayList<Pair<Integer, Integer>> vertices = new ArrayList<Pair<Integer, Integer>>(polygon.getVertices());
			if(vertices.size()>0){
				vertices.remove(vertices.size()-1);
				vertices.trimToSize();
			}
			if(ModelPolygon.checkConvex(vertices)){
				polygon.setVertices(vertices);
				makePointList(points);
			}else{
				makeError();
			}
		});
		
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(polygon);});
		
		
		makePointList(points);
		positionModifier.getChildren().addAll(buttons, points, button);
		
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
	
	private void makePointList(VBox result){
		result.getChildren().clear();
		ArrayList<Pair<Integer, Integer>> interPoints = new ArrayList<Pair<Integer, Integer>>(polygon.getVertices());
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
					if(ModelPolygon.checkConvex(interPoints)){
						polygon.setVertices(interPoints);
					}else{
						makeError();
					}
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
					if(ModelPolygon.checkConvex(interPoints)){
						polygon.setVertices(interPoints);
					}else{
						makeError();
					}
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
	
	private void makeError(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Convexity Error");
		alert.setHeaderText("The polygon after operation is not a convex polygon");
		alert.setContentText("Please change the points of polygon and make it convex before you do the operation!");
		alert.showAndWait();
	}
}
