package Vision;

import java.util.List;

import Model.ModelDot;
import Model.ModelShape;
import Model.ShapeManager;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SketchPad extends Pane{
	private int height, width;
	private Dot[][] dotMatrix;
	private ShapeManager shapeManager;
	
	public SketchPad(int height, int width){
		super();
		this.setMinSize(width, height);
		this.setMaxSize(width, height);
		this.height = height;
		this.width = width;
		initAllDots();
		shapeManager = new ShapeManager(height, width);
	}
	
	private void initAllDots() {
		dotMatrix = new Dot[height][width];
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				dotMatrix[i][j]=new Dot(j, i, Color.WHITE);
				this.getChildren().add(dotMatrix[i][j]);
			}
		}
	}
	
	public Dot[][] getDotMatrix(){
		return dotMatrix;
	}
	
	public void addNewShape(ModelShape newShape){
		List<ModelDot> modelDots = shapeManager.addNewStep(newShape);
		for(ModelDot dot: modelDots){
			dotMatrix[dot.getY()][dot.getX()].changeColor(dot.getColor());
		}
	}
}
