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

public class TranslationOperator extends Operator{
	
	private ModelShape shape = null;
	private int width, height;
	private ShapeManager manager;
	private BorderPane root = new BorderPane();
	private Callback<ModelShape, Integer> saver;
	
	public TranslationOperator(int width, int height, ShapeManager manager, Callback<ModelShape, Integer> saver){
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
		if((index < manager.getSteps().size())&&(index>=0)){
			ModelShape shape = manager.getSteps().get(index);
			this.shape = shape;
			Label position = new Label("the offset");
			HBox start_x = UIComponentFactory.intSlider(shape.getTranslationX(), -width/2, width/2, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					shape.setTranslationX(param);
					return null;
				}
				}, 
				"offset on x-axis");
			HBox start_y = UIComponentFactory.intSlider(shape.getTranslationY(), -height/2, height/2, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					shape.setTranslationY(param);
					return null;
				}
				}, 
				"offset on y-axis");
			positionModifier.getChildren().addAll(position, start_x, start_y);
			positionModifier.setPrefWidth(300);
		}
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			if(shape!=null){
				shape.translation(shape.getTranslationX(), shape.getTranslationY());
			}
			saver.call(null);
		});
		positionModifier.getChildren().addAll(button);
		root.setLeft(shapeChooser);
		root.setRight(positionModifier);
		return root;
	}
}
