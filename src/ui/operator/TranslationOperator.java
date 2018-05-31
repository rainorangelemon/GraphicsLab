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
import model.ShapeManager;
import model.operation.ModelOperation;
import model.operation.OperationTranslation;
import view.UIComponentFactory;

public class TranslationOperator extends Operator{
	
	private int width, height;
	private ShapeManager manager;
	private BorderPane root = new BorderPane();
	private Callback<ModelOperation, Integer> saver;
	private OperationTranslation translation;
	
	public TranslationOperator(int width, int height, ShapeManager manager, OperationTranslation translation, Callback<ModelOperation, Integer> saver){
		this.height = height;
		this.width = width;
		this.manager = manager;
		this.saver = saver;
		if(translation != null){
			this.translation = translation;
		}else{
			this.translation = new OperationTranslation(manager.getCurrentIndex(), -1, 0, 0);
		}
	}
	
	
	@Override
	public Node showEditor(){	
		VBox shapeChooser = new VBox();
		Label start = new Label("target shape's index");
		ChoiceBox<String> box = createShapeBox(manager, translation);
		box.getSelectionModel()
		    .selectedItemProperty()
		    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    	translation.setShapeIndex(new Integer(newValue.substring(0, newValue.indexOf(':'))));
		    	showEditor();
		    });
		shapeChooser.getChildren().addAll(start, box);
		
		VBox positionModifier = new VBox();
		if((translation.getShapeIndex()<manager.getSteps().size())&&(translation.getShapeIndex()>=0)){
			Label position = new Label("the offset");
			HBox start_x = UIComponentFactory.intSlider(translation.getTranslationX(), -width/2, width/2, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					translation.setTranslationX(param);
					return null;
				}
				}, 
				"offset on x-axis");
			HBox start_y = UIComponentFactory.intSlider(translation.getTranslationY(), -height/2, height/2, new Callback<Integer, Integer>(){
				@Override
				public Integer call(Integer param) {
					translation.setTranslationY(param);
					return null;
				}
				}, 
				"offset on y-axis");
			positionModifier.getChildren().addAll(position, start_x, start_y);
			positionModifier.setPrefWidth(300);
		}
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{
			saver.call(translation);
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
