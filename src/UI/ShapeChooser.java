package UI;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public abstract class ShapeChooser {
	
	public abstract Node showEditor();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ColorPicker getColorPicker(Color initColor, Callback<Color, Integer> saver){
		ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(initColor);
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                saver.call(colorPicker.getValue());               
            }
        });
        return colorPicker;
	}
}
