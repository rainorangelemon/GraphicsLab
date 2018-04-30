package view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UIComponentFactory {
	public UIComponentFactory(){
		
	}
	
	public static HBox intSlider(int initialValue, int left, int right, Callback<Integer, Integer> saver, String label){
	    HBox result =new HBox();
	    Label levelLabel = new Label(label + String.valueOf(initialValue));
	    Slider slider = new Slider();
	    levelLabel.textProperty().bind(
		    Bindings.format(
			    label+": %.0f",
			    slider.valueProperty()
			    )
		    );
	    slider.setMin(left);
	    slider.setMax(right);
	    slider.setValue(initialValue);
	    slider.setShowTickLabels(true);
	    slider.setShowTickMarks(true);
	    slider.setMajorTickUnit((left+right)/2+1);
	    slider.setMinorTickCount(5);
	    slider.setBlockIncrement(1);
	    slider.valueProperty().addListener(new ChangeListener<Number>() {
		public void changed(ObservableValue<? extends Number> ov,
			Number old_val, Number new_val) {
		    saver.call(Integer.parseInt(levelLabel.getText().substring(label.length() + ": ".length())));
		}
	    });
	    result.getChildren().addAll(slider, levelLabel);
	    return result;
	}
	
	public static Stage renderStage(Stage stage){
		Image anotherIcon = new Image("resources/favicon.png");
        stage.getIcons().add(anotherIcon);
        stage.setTitle("Yu's Lab");
        return stage;
	}
}
