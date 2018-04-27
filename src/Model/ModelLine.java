package Model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class ModelLine extends ModelShape{

	public ModelLine(int x0, int x1, int y0, int y1, Color color) {
		super(color);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		// TODO Auto-generated method stub
		result.add(new ModelDot(20, 10, Color.BLACK));
		return result;
	}
	
}
