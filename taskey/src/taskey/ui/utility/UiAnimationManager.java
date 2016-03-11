package taskey.ui.utility;

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.util.Duration;
import javafx.util.Pair;
import taskey.ui.UiConstants.ActionListMode;

/**
 * This class provides modified abstractions for some basic animations in javafx
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
	
	public TranslateTransition createTranslateTransition(Node theNode, Pair<Double,Double> source, Pair<Double,Double> dest,
														 int animDuration) {
		assert(theNode != null);
		TranslateTransition shift = new TranslateTransition();
		shift.setFromX(source.getKey());
		shift.setFromY(source.getValue());
		shift.setToX(dest.getKey());
		shift.setToY(dest.getValue());
		shift.setDuration(Duration.millis(animDuration));
		shift.setNode(theNode);
		return shift;	
	}
	
	public Timeline createTimelineAnimation( Label theLabel, int interval, int charsToSkip, String filler ) {
			//theLabel.setText(theLabel.getText() + filler);
	        Timeline timeline = new Timeline();
	        timeline.setCycleCount(Timeline.INDEFINITE);
	        timeline.setAutoReverse(true);
 
	        KeyValue keyValue = new KeyValue(theLabel.scaleXProperty(), 1);
	        Duration duration = Duration.millis(interval);
	        EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent t) {
	            	String myText = theLabel.getText();
	        		myText = myText.substring(charsToSkip, myText.length()) + myText.substring(0,charsToSkip);
	            	theLabel.setText(myText);
	            }
	        };
	 
	        KeyFrame keyFrame = new KeyFrame(duration, onFinished , keyValue);
	        timeline.getKeyFrames().add(keyFrame);
	        return timeline;
	}
}
