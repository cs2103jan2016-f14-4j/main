package taskey.ui.utility;


import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.util.Duration;

/**
 * This class creates various types of pop up windows, and supports the use of animations
 * @author JunWei
 *
 */
public class UiPopupFactory {

	private static UiPopupFactory instance = null;
	private ArrayList<PopupWindow> popupList = new ArrayList<PopupWindow>();
	
	public static UiPopupFactory getInstance () { 
    	if ( instance == null ) {
    		instance = new UiPopupFactory();
    	}
    	return instance;
    }
	
	/**
	 * This method creates a label at the node position with offset
	 * Note that this offset is in screen space coordinates
	 * @param text
	 * @param node
	 * @param offsetX
	 * @param offsetY
	 * @return
	 */
	public Popup createPopupLabelAtNode(String text, Node node, double offsetX, double offsetY) {
		Popup newPopup = new Popup();
		Bounds bounds = node.getBoundsInLocal();
        Bounds screenBounds = node.localToScreen(bounds);
		Label content = new Label();
		content.setText(text);
		content.getStyleClass().add("prompt");
		newPopup.getContent().add(content);
		newPopup.show(node, screenBounds.getMinX() + offsetX, screenBounds.getMinY() + offsetY); // lower left hand corner
		popupList.add(newPopup);
		return newPopup;
	}

	public ContextMenu createPopupMenu() {
		ContextMenu newMenu = new ContextMenu();
		popupList.add(newMenu);
		return newMenu;
	}
	
	/**
	 * This method creates fade transitions for all content of the popup
	 * @param thePopup
	 * @param startDelay
	 * @param totalDuration
	 * @param deleteOnFinished
	 */
	public void startFadeTransition (Popup thePopup, int startDelay, int totalDuration, boolean deleteOnFinished) {
		ObservableList<Node> myList = thePopup.getContent();
		for ( int i = 0; i < myList.size(); i ++ ) {
			Node target = myList.get(i);
			FadeTransition ft = new FadeTransition(Duration.millis(totalDuration),target);
			ft.setDelay(Duration.millis(startDelay));
			ft.setFromValue(target.getOpacity());
			ft.setToValue(0);
			ft.play();
			
			if (deleteOnFinished) {
				ft.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
							removePopupWindow(thePopup);
						}
				});
			}
		}
	}
	
	public void removePopupWindow(PopupWindow target ) {
		popupList.remove(target);
	}
	
	public void cleanUp() {
		popupList.clear();
	}
}
