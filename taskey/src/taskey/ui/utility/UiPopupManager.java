package taskey.ui.utility;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import taskey.ui.UiConstants;

/**
 * This class creates various types of pop up windows, and supports the use of
 * animations
 * 
 * @author JunWei
 *
 */
public class UiPopupManager {
	private static UiPopupManager instance = null;
	private ArrayList<PopupWindow> popupList = new ArrayList<PopupWindow>();

	public static UiPopupManager getInstance() {
		if (instance == null) {
			instance = new UiPopupManager();
		}
		return instance;
	}

	/**
	 * This method creates a label at the node position with offset Note that
	 * this offset is in screen space coordinates
	 * 
	 * @param text - what to display in the label
	 * @param offsetX - from node X position
	 * @param offsetY - from node Y position
	 * @return Popup object
	 */
	public Popup createPopupLabelAtNode(String text, Node node, double offsetX, double offsetY, boolean deleteAfter) {
		assert(node != null);
		Popup newPopup = new Popup();
		Bounds screenBounds = getScreenBoundsOfNode(node);
		Label content = new Label();
		content.setText(text);
		content.getStyleClass().add(UiConstants.STYLE_PROMPT);
		newPopup.getContent().add(content);
		newPopup.show(node, screenBounds.getMinX() + offsetX, screenBounds.getMinY() + offsetY); // lower left hand corner  
		popupList.add(newPopup);
		
		if ( deleteAfter == true ) {
			FadeTransition fade = UiAnimationManager.getInstance().createFadeTransition(newPopup.getContent().get(0), 2000, UiConstants.DEFAULT_FADE_TIME, 1.0, 0.0);
			fade.play();
			fade.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					removePopup(newPopup);
				}
			});
		}
		return newPopup;
	}

	public Popup createPopupMenu(int numRows) {
		assert(numRows >= 0);
		Popup newPopup = new Popup();
		VBox container = new VBox(); // VBox is used only for formatting purposes purposes
		for (int i = 0; i < numRows; i++) {
			StackPane row = new StackPane();
			row.getStyleClass().add(UiConstants.STYLE_PROMPT);
			container.getChildren().add(row);
		}
		newPopup.getContent().add(container);
		popupList.add(newPopup);
		return newPopup;
	}

	public Bounds getScreenBoundsOfNode(Node node) {
		assert(node != null);
		Bounds bounds = node.getBoundsInLocal();
		Bounds screenBounds = node.localToScreen(bounds);
		return screenBounds;
	}
	
	public void removePopup(Popup thePopup) {
		assert(thePopup != null);
		popupList.remove(thePopup);
	}
	
	public void cleanUp() {
		popupList.clear();
	}
}
