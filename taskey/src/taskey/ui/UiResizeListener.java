package taskey.ui;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import taskey.constants.UiConstants;
import taskey.ui.utility.UiPopupManager;

/**
 * @@author A0125419H
 * 
 * This class implements a stage resizelistener
 * Adapted and modified heavily from:  
 * https://geektortoise.wordpress.com/2014/02/07/how-to-programmatically-resize-the-stage-in-a-javafx-app/
 * 
 * @author JunWei
 */
class UiResizeListener implements EventHandler<MouseEvent> {
	private double dx, dy;
	private double border = 10; // border for picking
	private boolean resizeH = false, resizeV = false;
	private Scene scene;
	private Stage stage;
	private double aspectRatio;
	public UiResizeListener(Scene theScene, Stage theStage) {
		scene = theScene;
		stage = theStage;
		aspectRatio = UiConstants.WINDOW_MIN_SIZE.getWidth() / UiConstants.WINDOW_MIN_SIZE.getHeight();
	}
	
	@Override
	public void handle(MouseEvent t) {
		if (MouseEvent.MOUSE_MOVED.equals(t.getEventType())) {
			if (t.getX() > scene.getWidth() - border && t.getY() > scene.getHeight() - border) {
				scene.setCursor(Cursor.SE_RESIZE);
				resizeH = true;
				resizeV = true;
			} else {
				scene.setCursor(Cursor.DEFAULT);
				resizeH = false;
				resizeV = false;
			}
		} else if (MouseEvent.MOUSE_PRESSED.equals(t.getEventType())) {
			dx = stage.getWidth() - t.getX();
			dy = stage.getHeight() - t.getY();
		} else if (MouseEvent.MOUSE_DRAGGED.equals(t.getEventType())) {
			if (resizeH) {
				if ( t.getX() + dx >= UiConstants.WINDOW_MIN_SIZE.getWidth() ) {
					stage.setWidth(t.getX() + dx);		
				} else {
					stage.setWidth(UiConstants.WINDOW_MIN_SIZE.getWidth());
				}
			}
			if (resizeV) {
				if ( t.getY() + dy >= UiConstants.WINDOW_MIN_SIZE.getHeight() ) {
					stage.setHeight(t.getY() + dy);
				} else {
					stage.setHeight(UiConstants.WINDOW_MIN_SIZE.getHeight());
				}
			}
			if ( resizeH || resizeV ) {
				checkAspectRatio();
				UiPopupManager.getInstance().updateWindowRatios(stage);
			}
		}
	}

	/**
	 * This method ensures that the window will always stay in proportions
	 */
	private void checkAspectRatio() {
		double currentAspectRatio = stage.getWidth() / stage.getHeight();
		if ( currentAspectRatio < aspectRatio ) { // width is smaller in aspect
			stage.setWidth(stage.getWidth() * aspectRatio / currentAspectRatio);
		} else if ( currentAspectRatio > aspectRatio ) {
			stage.setHeight(stage.getHeight() * currentAspectRatio/ aspectRatio );
		}
	}
}
