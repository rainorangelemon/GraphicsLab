package ui.operator;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.ModelLine;
import model.ModelPolygon;
import model.ModelShape;
import model.ShapeManager;
import model.operation.ModelOperation;
import model.operation.OperationClip;
import util.UIComponentFactory;

public class ClipOperator extends Operator{
	
	private int width, height;
	private ShapeManager manager;
	private BorderPane root = new BorderPane();
	private Callback<ModelOperation, Integer> saver;
	private OperationClip clip;
	
	public ClipOperator(int width, int height, ShapeManager manager, OperationClip clip, Callback<ModelOperation, Integer> saver){
		this.height = height;
		this.width = width;
		this.manager = manager;
		this.saver = saver;
		if(clip!=null){
			this.clip = clip;
		}else{
			ArrayList<Integer> targets = new ArrayList<Integer>();
			for(int i=0; i<manager.getCurrentIndex();i++){
				if(manager.getSteps().get(i) instanceof ModelShape){
					targets.add(i);
				}
			}
			this.clip = new OperationClip(manager.getCurrentIndex(), targets, 0, 0, width-1, height-1);
		}
	}
	
	
	@Override
	public Node showEditor(){
		
		VBox shapeChooser = new VBox();
		MenuButton box = new MenuButton("shapes to clip");
		for(int i=0; (i<manager.getSteps().size())&&(i<clip.getCurrentIndex()); i++){
			if((manager.getSteps().get(i) instanceof ModelLine)||(manager.getSteps().get(i) instanceof ModelPolygon)){
		        int index = i;
				CheckBox cb1 = new CheckBox(new Integer(i).toString()+ ": " + manager.getSteps().get(i).getClass().getSimpleName().substring(5));  
		        CustomMenuItem item1 = new CustomMenuItem(cb1);  
		        item1.setHideOnClick(false);
		        if(clip.getShapeIndexes().contains(i)){
		        	cb1.setSelected(true);
		        }else{
		        	cb1.setSelected(false);
		        }
		        cb1.setOnMouseClicked(e->{
		        	if(clip.getShapeIndexes().contains(index)){
		        		ArrayList<Integer> newIndexes = new ArrayList<Integer>();
		        		ArrayList<Integer> indexes = new ArrayList<Integer>(clip.getShapeIndexes());
		        		for(Integer j: indexes){
		        			if(j!=index){
		        				newIndexes.add(j);
		        			}
		        		}
		        		clip.setShapeIndexes(newIndexes);
		        	}else{
		        		clip.getShapeIndexes().add(index);
		        	}
		        });
		        box.getItems().add(item1);
			}
		}
		shapeChooser.getChildren().addAll(box);
		
		VBox positionModifier = new VBox();
		Label position = new Label("the points of window");
		HBox start_x0 = UIComponentFactory.unsignedIntSlider(clip.getWindowX0(), 0, width-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				clip.setWindowX0(param);
				return null;
			}
			}, 
			"x0 of window");
		HBox start_y0 = UIComponentFactory.unsignedIntSlider(clip.getWindowY0(), 0, height-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				clip.setWindowY0(param);
				return null;
			}
			}, 
			"y0 of window");
		HBox start_x1 = UIComponentFactory.unsignedIntSlider(clip.getWindowX1(), 0, width-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				clip.setWindowX1(param);
				return null;
			}
			}, 
			"x1 of window");
		HBox start_y1 = UIComponentFactory.unsignedIntSlider(clip.getWindowY1(), 0, height-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				clip.setWindowY1(param);
				return null;
			}
			}, 
			"y1 of window");
		positionModifier.getChildren().addAll(position, start_x0, start_y0, start_x1, start_y1);
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			saver.call(clip);
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
