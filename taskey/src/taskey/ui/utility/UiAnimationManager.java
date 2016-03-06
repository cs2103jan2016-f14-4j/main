package taskey.ui.utility;

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * This class provides abstractions for some basic animations in javafx
 * as well as custom ones
 * @author JunWei
 *
 */

public class UiAnimationManager {

	private static UiAnimationManager instance = null;
	
	public static UiAnimationManager getInstance() {
		if ( instance == null ) {
			instance = new UiAnimationManager();
		}
		return instance;
	}
	/**
	 * This method creates fade transitions for a node
	 * 
	 * @param theNode - node object to place animation on
	 * @param startDelay - delay before animation starts
	 * @param animDuration - how long to play the animation
	 * @param fromAlpha - start opacity
	 * @param toAlpha - end opacity
	 * @return FadeTransition object for custom handling
	 */
	public FadeTransition createFadeTransition(Node theNode, int startDelay, int animDuration, double fromAlpha, double toAlpha) {
		assert(theNode != null);
		FadeTransition ft = new FadeTransition(Duration.millis(animDuration), theNode);
		ft.setDelay(Duration.millis(startDelay));
		theNode.setOpacity(fromAlpha);
		ft.setFromValue(theNode.getOpacity());
		ft.setToValue(toAlpha);
		return ft;
	}
}
