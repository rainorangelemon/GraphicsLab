package ui.operator;

import model.ModelShape;
import model.ShapeManager;
import model.operation.ModelOperation;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;

public abstract class Operator {
	public abstract Node showEditor(); 
	
	public static ChoiceBox<String> createShapeBox(ShapeManager manager, ModelOperation operation) {
		ChoiceBox<String> box = new ChoiceBox<String>();
		for(int i=0; (i<manager.getSteps().size())&&(i<operation.getCurrentIndex()); i++){
			if(manager.getSteps().get(i) instanceof ModelShape){
				box.getItems().add(new Integer(i).toString()+ ": " + manager.getSteps().get(i).getClass().getSimpleName().substring(5));
			}
		}
		if((operation.getShapeIndex()<manager.getSteps().size())&&(operation.getShapeIndex()>=0)){
			int index = box.getItems().indexOf(new Integer(operation.getShapeIndex()).toString()+ ": " + manager.getSteps().get(operation.getShapeIndex()).getClass().getSimpleName().substring(5));
			box.getSelectionModel().select(box.getItems().get(index));
		}
		return box;
	}
}
