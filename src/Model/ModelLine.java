package Model;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;

public class ModelLine extends ModelShape{

	private int x0, x1, y0, y1;
	
	public ModelLine(int x0, int x1, int y0, int y1, Color color) {
		super(color);
		setPos(x0, y0, x1, y1);
	}

	@Override
	protected List<ModelDot> getModelDots(ModelDot[][] dots) {
		List<ModelDot> result = new ArrayList<ModelDot>();
		// TODO Auto-generated method stub
		result.add(new ModelDot(x0, y0, Color.BLACK));
		return result;
	}
	
	public int getX0() {
		return x0;
	}

	public int getX1() {
		return x1;
	}

	public int getY0() {
		return y0;
	}

	public int getY1() {
		return y1;
	}

	public void setPos(int x0, int y0, int x1, int y1) {
		this.x0 = x0;
		this.x1 = x1;
		this.y0 = y0;
		this.y1 = y1;
	}
	
}
