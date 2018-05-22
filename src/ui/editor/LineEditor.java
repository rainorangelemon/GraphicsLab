package ui.editor;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import model.ModelLine;
import model.ModelShape;
import view.SketchPad;

public class LineEditor extends ShapeEditor{

	public LineEditor(){
		
	}
	
	@Override
	public void editShape(SketchPad paper, ModelShape shape) {
		ModelLine line = (ModelLine) shape;
		paper.setOnMouseClicked(this.registerMouseClick(paper, line));
	}
	
	private EventHandler<? super MouseEvent> registerMouseClick(SketchPad paper, ModelLine line){
		return e->{
			if(e.getClickCount() == 2){
				paper.setOnMouseMoved(h->{
					paper.registerCheckHitOnShape().handle(h);
					paper.registerCheckPosition().handle(h);
				});
				paper.setOnMouseClicked(h->{
					
				});
			}else{
				int x = new Double(e.getX()).intValue();
				int y = new Double(e.getY()).intValue();
				if(super.isOverlap(x, y, line.getX0(), line.getY0())){
					paper.getScene().getRoot().setCursor(Cursor.HAND);
					paper.setOnMouseMoved(h->{
						int x1 = new Double(h.getX()).intValue();
						int y1 = new Double(h.getY()).intValue();
						if((x1<paper.getDotMatrix()[0].length)&&(x1>=0)&&(y1>=0)&&(y1<paper.getDotMatrix().length)){
							line.setPos(x1, y1, line.getX1(), line.getY1());
							paper.refreshScreen(paper.getShapeManager().refresh());
						}
					});
					paper.setOnMouseClicked(h->{
						paper.setOnMouseMoved(i->{});
						paper.setOnMouseClicked(registerMouseClick(paper, line));
					});
				}else if(super.isOverlap(x, y, line.getX1(), line.getY1())){
					paper.getScene().getRoot().setCursor(Cursor.HAND);
					paper.setOnMouseMoved(h->{
						int x1 = new Double(h.getX()).intValue();
						int y1 = new Double(h.getY()).intValue();
						if((x1<paper.getDotMatrix()[0].length)&&(x1>=0)&&(y1>=0)&&(y1<paper.getDotMatrix().length)){
							line.setPos(line.getX0(), line.getY0(), x1, y1);
							paper.refreshScreen(paper.getShapeManager().refresh());
						}
					});
					paper.setOnMouseClicked(h->{
						paper.setOnMouseMoved(i->{});
						paper.setOnMouseClicked(registerMouseClick(paper, line));
					});
				}else{
					paper.getScene().getRoot().setCursor(Cursor.DEFAULT);
				}
			}
		};
	}
	
}