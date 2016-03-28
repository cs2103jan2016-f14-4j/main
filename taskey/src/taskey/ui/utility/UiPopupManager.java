package taskey.ui.utility;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.stage.PopupWindow.AnchorLocation;
import taskey.constants.UiConstants;

/**
 * @@author A0125419H
 * This class creates various types of pop up windows, and supports the use of
 * animations
 * 
 * @author JunWei
 *
 */

public class UiPopupManager {
	private double X_Ratio = 1, Y_Ratio = 1; // window ratios
	private ArrayList<Popup> popupList = new ArrayList<Popup>();
	private Popup promptMessage;
	private static UiPopupManager instance = null;
	private UiPopupManager() {
	}
	public static UiPopupManager getInstance() {
		if (instance == null) {
			instance = new UiPopupManager();
		}
		return instance;
	}

	/**
	 * This method implements a single prompt message that will be replaced
	 * whenever this method is called
	 * This method uses createPopupLabelAtNode() to replace the prompt
	 */
	public void updatePromptMessage( String text, Node node, double offsetX, double offsetY ) { 
		if ( promptMessage != null ) {
			promptMessage.hide();
			popupList.remove(promptMessage);
		}
		promptMessage = createPopupLabelAtNode(text,node,offsetX,offsetY,true);
	}
	
	/**
	 * This method creates a label at the node position with offset
	 *  Note that this offset is in screen space coordinates
	 * 
	 * @param text - what to display in the label
	 * @param offsetX - from node X position
	 * @param offsetY - from node Y position
	 * @return Popup object
	 */
	public Popup createPopupLabelAtNode(String text, Node node, double offsetX, double offsetY, boolean deleteAfter) {
		assert(node != null);
		Popup thePopup = new Popup();
		Bounds bounds = node.getBoundsInLocal();
		Bounds screenBounds = node.localToScreen(bounds);
		Label content = new Label();
		content.setText(text);
		content.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
		content.getStyleClass().add(UiConstants.STYLE_PROMPT_SELECTED);
		thePopup.getContent().add(content);
		thePopup.show(node, screenBounds.getMinX() + offsetX * X_Ratio, 
							screenBounds.getMinY() + offsetY * Y_Ratio); // lower left hand corner  
		popupList.add(thePopup);
		
		if ( deleteAfter == true ) {
			FadeTransition fade = UiAnimationManager.getInstance().createFadeTransition(
					thePopup.getContent().get(0), 2000, UiConstants.DEFAULT_FADE_TIME, 1.0, 0.0);
			fade.play();
			fade.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					removePopup(thePopup);
				}
			});
		}
		return thePopup;
	}

	/**
	 * This method sets the X and Y ratio of the stage
	 * such that when pop ups are created, they are positioned correctly
	 * but not scaled, or shifted when window changes
	 * these need to depend on anchor points which makes it difficult to alter 
	 * @param mainStage
	 */
	public void updateWindowRatios(Window mainStage) {
		X_Ratio = mainStage.getWidth()/2/UiConstants.WINDOW_MIN_SIZE.getWidth();
		Y_Ratio = mainStage.getHeight()/2/UiConstants.WINDOW_MIN_SIZE.getHeight();
		
		// one solution is to hide the pop up when window changes
		for ( int i = 0; i < popupList.size(); i ++  ) {
			Popup thePopup = (Popup) popupList.get(i);
			thePopup.hide();
		}
	}
	
	public double getYRatio() {
		return Y_Ratio;
	}
	
	public double getXRatio() {
		return X_Ratio;
	}
	
	/**
	 * This method creates a pop up menu with a Popup container instead of a ContextMenu with MenuItems
	 * To provide more customization, this customization is provided by other classes, for example UiDropDown
	 * @param numRows
	 * @return Popup
	 */
	public Popup createPopupMenu() {
		Popup newPopup = new Popup();
		VBox container = new VBox(); // VBox is used only for formatting purposes purposes
		container.setFillWidth(true);
		newPopup.getContent().add(container);
		popupList.add(newPopup);
		return newPopup;
	}
	
	public void removePopup(Popup thePopup) {
		assert(thePopup != null);
		popupList.remove(thePopup);
	}
	
	public void cleanUp() {
		popupList.clear();
	}
}
