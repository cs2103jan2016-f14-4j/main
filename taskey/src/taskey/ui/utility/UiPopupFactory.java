package taskey.ui.utility;


import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This class creates a small label at a point, and supports the use of animations
 * @author JunWei
 *
 */
public class UiPopupFactory {

	private static UiPopupFactory instance = null;
		
	private ArrayList<Node> popupList = new ArrayList<Node>();
	
	public static UiPopupFactory getInstance () { 
    	if ( instance == null ) {
    		instance = new UiPopupFactory();
    	}
    	return instance;
    }
	
	public Node createPopup( String text, Pane pane, double layoutX, double layoutY ) {
		Label newPopup = new Label();
		newPopup.setLayoutX(layoutX);
		newPopup.setLayoutY(layoutY);
		newPopup.setText(text);
		newPopup.getStyleClass().add("popup");
		pane.getChildren().add(newPopup);
		popupList.add(newPopup);
		return newPopup;
	}

	public void startFadeTransition (Node target, int startDelay, int totalDuration) {
		FadeTransition ft = new FadeTransition(Duration.millis(totalDuration),target);
		ft.setDelay(Duration.millis(startDelay));
		ft.setFromValue(target.getOpacity());
		ft.setToValue(0);
		ft.play();
		ft.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				removePopup(ft.getNode());
				}
		});
	}
	
	/**
	 * This method assumes the popup is added to a pane object which should be true most of the time
	 * @param target : Node
	 */
	public void removePopup(Node target ) {
		Pane pane = (Pane) target.getParent();
		pane.getChildren().remove(target);
		popupList.remove(target);
	}
}
