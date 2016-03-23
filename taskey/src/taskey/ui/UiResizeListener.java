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
 * Taken and modified from: https://geektortoise.wordpress.com/2014/02/07/how-to-programmatically-resize-the-stage-in-a-javafx-app/
 * 
 * @author JunWei
 */
class UiResizeListener implements EventHandler<MouseEvent> {
	double dx, dy;
	double deltaX, deltaY;
	double border = 10; // border for picking
	boolean moveH, moveV;
	boolean resizeH = false, resizeV = false;
	Scene scene;
	Stage stage;

	public UiResizeListener(Scene theScene, Stage theStage) {
		scene = theScene;
		stage = theStage;
	}

	@Override
	public void handle(MouseEvent t) {
		if (MouseEvent.MOUSE_MOVED.equals(t.getEventType())) {
			if (t.getX() < border && t.getY() > scene.getHeight() - border) {
				scene.setCursor(Cursor.SW_RESIZE);
				resizeH = true;
				resizeV = true;
				moveH = true;
				moveV = false;
			} else if (t.getX() > scene.getWidth() - border && t.getY() > scene.getHeight() - border) {
				scene.setCursor(Cursor.SE_RESIZE);
				resizeH = true;
				resizeV = true;
				moveH = false;
				moveV = false;
			}  else {
				scene.setCursor(Cursor.DEFAULT);
				resizeH = false;
				resizeV = false;
				moveH = false;
				moveV = false;
			}
		} else if (MouseEvent.MOUSE_PRESSED.equals(t.getEventType())) {
			dx = stage.getWidth() - t.getX();
			dy = stage.getHeight() - t.getY();
		} else if (MouseEvent.MOUSE_DRAGGED.equals(t.getEventType())) {
			if (resizeH) {
				if (stage.getWidth() <= UiConstants.MIN_SIZE.width) {
					if (moveH) {
						deltaX = stage.getX() - t.getScreenX();
						if (t.getX() < 0) {// if new > old, it's permitted
							stage.setWidth(deltaX + stage.getWidth());
							stage.setX(t.getScreenX());
						}
					} else {
						if (t.getX() + dx - stage.getWidth() > 0) {
							stage.setWidth(t.getX() + dx);
						}
					}
				} else if (stage.getWidth() > UiConstants.MIN_SIZE.width) {
					if (moveH) {
						deltaX = stage.getX() - t.getScreenX();
						stage.setWidth(deltaX + stage.getWidth());
						stage.setX(t.getScreenX());
					} else {
						stage.setWidth(t.getX() + dx);
					}
				}
			}
			if (resizeV) {
				if (stage.getHeight() <= UiConstants.MIN_SIZE.height) {
					if (moveV) {
						deltaY = stage.getY() - t.getScreenY();
						if (t.getY() < 0) {// if new > old, it's permitted
							stage.setHeight(deltaY + stage.getHeight());
							stage.setY(t.getScreenY());
						}
					} else {
						if (t.getY() + dy - stage.getHeight() > 0) {
							stage.setHeight(t.getY() + dy);
						}
					}
				} else if (stage.getHeight() > UiConstants.MIN_SIZE.height) {
					if (moveV) {
						deltaY = stage.getY() - t.getScreenY();
						stage.setHeight(deltaY + stage.getHeight());
						stage.setY(t.getScreenY());
					} else {
						stage.setHeight(t.getY() + dy);
					}
				}
			}
			UiPopupManager.getInstance().resizeAllPopups(stage);
		}
	}
}
