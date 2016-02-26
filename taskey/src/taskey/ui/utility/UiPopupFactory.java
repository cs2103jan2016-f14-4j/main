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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        Bounds screenBounds = getScreenBoundsOfNode(node);
		Label content = new Label();
		content.setText(text);
		content.getStyleClass().add("prompt");
		newPopup.getContent().add(content);
		newPopup.show(node, screenBounds.getMinX() + offsetX, screenBounds.getMinY() + offsetY); // lower left hand corner
		popupList.add(newPopup);
		return newPopup;
	}

	public Popup createPopupMenu(int numRows) {
		Popup newPopup = new Popup();
		VBox container = new VBox(); // VBox is used only for formatting purposes
		for ( int i = 0; i < numRows; i ++ ) {
			StackPane row = new StackPane();
			row.getStyleClass().add("prompt");
			container.getChildren().add(row);
		}
		newPopup.getContent().add(container);
		popupList.add(newPopup);
		return newPopup;
	}
	
	public Bounds getScreenBoundsOfNode(Node node) {
		Bounds bounds = node.getBoundsInLocal();
        Bounds screenBounds = node.localToScreen(bounds);
        return screenBounds;
	}
	/**
	 * This method creates fade transitions for first content (usually the container) of the popup
	 * @param thePopup
	 * @param startDelay
	 * @param totalDuration
	 * @param deleteOnFinished
	 * @return 
	 */
	public FadeTransition createFadeTransition (Popup thePopup, int startDelay, int animDuration, double fromAlpha, double toAlpha, boolean deleteOnFinished) {
		ObservableList<Node> myList = thePopup.getContent(); // usually only the container needs to be animated
		Node target = myList.get(0);
		FadeTransition ft = new FadeTransition(Duration.millis(animDuration),target);
		ft.setDelay(Duration.millis(startDelay));
		target.setOpacity(fromAlpha);
		ft.setFromValue(target.getOpacity());
		ft.setToValue(0);
		
		if (deleteOnFinished) {
			ft.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
						removePopupWindow(thePopup);
					}
			});
		}
		return ft;
	}
	
	public void removePopupWindow(PopupWindow target ) {
		popupList.remove(target);
	}
	
	public void cleanUp() {
		popupList.clear();
	}
}
