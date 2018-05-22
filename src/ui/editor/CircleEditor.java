package ui.editor;

import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import model.ModelCircle;
import model.ModelDot;
import model.ModelShape;
import view.Dot;
import view.SketchPad;

public class CircleEditor extends ShapeEditor{

	public CircleEditor(){
		
	}
	
	@Override
	public void editShape(SketchPad paper, ModelShape shape) {
		ModelCircle circle = (ModelCircle) shape;
		showBox(paper, circle);
		paper.setOnMouseClicked(this.registerMouseClick(paper, circle));
	}
	
	private EventHandler<? super MouseEvent> registerMouseClick(SketchPad paper, ModelCircle circle){
		return e->{
			if(e.getClickCount() == 2){
				paper.setOnMouseMoved(h->{
					paper.registerCheckHitOnShape().handle(h);
					paper.registerCheckPosition().handle(h);
				});
				paper.setOnMouseClicked(h->{
					
				});
			}else{
				int x0 = circle.getXc() - circle.getA();
				int x1 = circle.getXc() + circle.getA();
				int y0 = circle.getYc() - circle.getB();
				int y1 = circle.getYc() + circle.getB();
				int x = new Double(e.getX()).intValue();
				int y = new Double(e.getY()).intValue();
				if(super.isOverlap(x, x0)){
					paper.getScene().getRoot().setCursor(Cursor.HAND);
					paper.setOnMouseMoved(h->{
						if(x<circle.getXc()){
							circle.setPos(x + circle.getA(), circle.getYc(), circle.getA(), circle.getB());
							paper.refreshScreen(paper.getShapeManager().refresh());
						}
					});
					paper.setOnMouseClicked(h->{
						paper.setOnMouseMoved(i->{});
						paper.setOnMouseClicked(registerMouseClick(paper, circle));
					});
				}else if(super.isOverlap(x, x1)){
					System.out.printf("overlapped!\n");
					paper.getScene().getRoot().setCursor(Cursor.HAND);
					paper.setOnMouseMoved(h->{
						if(x>circle.getXc()){
							circle.setPos(x - circle.getA(), circle.getYc(), circle.getA(), circle.getB());
							paper.refreshScreen(paper.getShapeManager().refresh());
						}
					});
					paper.setOnMouseClicked(h->{
						paper.setOnMouseMoved(i->{this.registerMouseClick(paper, circle);});
						paper.setOnMouseClicked(registerMouseClick(paper, circle));
					});
				}else if(super.isOverlap(y, y0)){
					paper.getScene().getRoot().setCursor(Cursor.HAND);
					paper.setOnMouseMoved(h->{
						if(y<circle.getYc()){
							circle.setPos(circle.getXc(), y + circle.getB(), circle.getA(), circle.getB());
							paper.refreshScreen(paper.getShapeManager().refresh());
						}
					});
					paper.setOnMouseClicked(h->{
						paper.setOnMouseMoved(i->{});
						paper.setOnMouseClicked(registerMouseClick(paper, circle));
					});
				}else if(super.isOverlap(y, y1)){
					paper.getScene().getRoot().setCursor(Cursor.HAND);
					paper.setOnMouseMoved(h->{
						if(y>circle.getYc()){
							circle.setPos(circle.getXc(), y - circle.getB(), circle.getA(), circle.getB());
							paper.refreshScreen(paper.getShapeManager().refresh());
						}
					});
					paper.setOnMouseClicked(h->{
						paper.setOnMouseMoved(i->{});
						paper.setOnMouseClicked(registerMouseClick(paper, circle));
					});
				}else{
					paper.getScene().getRoot().setCursor(Cursor.DEFAULT);
				}
			}
		};
	}
	
	private void showBox(SketchPad paper, ModelCircle circle){
		Dot[][] dots = paper.getDotMatrix();
		List<ModelDot> box = circle.getBox();
		int height = dots.length;
		int width = dots[0].length;
		for(ModelDot dot: box){
			if((dot.getX()>=0)&&(dot.getX()<width)&&(dot.getY()>=0)&&(dot.getY()<height)){
				dots[dot.getY()][dot.getX()].changeColor(circle.getColor());
			}
		}
	}

}
