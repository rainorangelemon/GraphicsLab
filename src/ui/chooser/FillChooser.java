package ui.chooser;

import model.ModelFill;
import model.ModelShape;
import view.UIComponentFactory;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class FillChooser extends ShapeChooser{

	private ModelFill fill; 
	private Callback<ModelShape, Integer> saver;
	private int width, height;
	
	public FillChooser(int width, int height, Color color, ModelFill fill, Callback<ModelShape, Integer> saver){
		this.saver = saver;
		this.height = height;
		this.width = width;
		if(fill!=null){
			this.fill = fill;
		}else{
			this.fill = new ModelFill(true, 0, 0, color, false, color);
		}
	}
	
	@Override
	public Node showEditor() {
		BorderPane root = new BorderPane();
		VBox positionModifier = new VBox();
		Label start = new Label("seed position");
		HBox start_x = UIComponentFactory.unsignedIntSlider(fill.getSeedX(), 0, width-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				fill.setSeedX(param);
				return null;
			}
			}, 
			"x");
		HBox start_y = UIComponentFactory.unsignedIntSlider(fill.getSeedY(), 0, height-1, new Callback<Integer, Integer>(){
			@Override
			public Integer call(Integer param) {
				fill.setSeedY(param);
				return null;
			}
			}, 
			"y");
		Button button = new Button("Confirm");
		button.setOnMouseClicked(e->{saver.call(fill);});
		
		ChoiceBox<String> box = new ChoiceBox<String>();
		box.getItems().add("four connectivity");
		box.getItems().add("eight connectivity");
		if(fill.isConnectivityFour()){
			box.getSelectionModel().select("four connectivity");
		}else{
			box.getSelectionModel().select("eight connectivity");
		}
		box.getSelectionModel()
		    .selectedItemProperty()
		    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    	if(newValue.equals(new String("eight connectivity"))){
		    		fill.setConnectivityFour(false);
		    	}else{
		    		fill.setConnectivityFour(true);
		    	}
		    });
		
		ChoiceBox<String> box2 = new ChoiceBox<String>();
		box2.getItems().add("interior point presentation");
		box2.getItems().add("boundary representation");
		if(fill.isInteriorPointMethod()){
			box2.getSelectionModel().select("interior point presentation");
		}else{
			box2.getSelectionModel().select("boundary representation");
		}
		box2.getSelectionModel()
		    .selectedItemProperty()
		    .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
		    	if(newValue.equals(new String("boundary representation"))){
		    		fill.setInteriorPointMethod(false);
		    	}else{
		    		fill.setInteriorPointMethod(true);
		    	}
		    });
		
		
		positionModifier.getChildren().addAll(start, start_x, start_y, box, box2, button);
		root.setLeft(positionModifier);
		
		Label definedColor = new Label("color of boundary (for boundary representation)");
		ColorPicker definedColorPicker = super.getColorPicker(fill.getDefinedColor(), new Callback<Color, Integer>(){
			@Override
			public Integer call(Color param) {
				fill.setDefinedColor(param);
				return null;
			}
		});
		
		Label targetColor = new Label("which color you want to paint?");
		ColorPicker targetColorPicker = super.getColorPicker(fill.getColor(), new Callback<Color, Integer>(){
			@Override
			public Integer call(Color param) {
				fill.setColor(param);
				return null;
			}
		});
		
		VBox colorModifier = new VBox();
		colorModifier.getChildren().addAll(definedColor, definedColorPicker, targetColor, targetColorPicker);
		
		root.setRight(colorModifier);
		return root;
	}

}
