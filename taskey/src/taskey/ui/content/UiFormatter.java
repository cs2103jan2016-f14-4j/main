package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import taskey.constants.UiConstants;
import taskey.messenger.Task;

/**
 * @@author A0125419H
 * This class contains a default grid-based formatter for the content panes.
 * It provides some basic variables or methods that can be used
 * It is extended by specialized classes to provide extended functionalities
 * 
 * @author Junwei
 *
 */

public abstract class UiFormatter {
	
	protected ScrollPane mainPane;
	protected GridPane currentGrid;
	protected UiGridHelper gridHelper;

	// Abstract methods that are handled by extended classes
	public abstract void format(ArrayList<Task> myTaskList);
	public abstract void processArrowKey(KeyEvent event);
	public abstract void processPageUpAndDown(KeyEvent event);
	public abstract int processDeleteKey();
	public abstract int processEnterKey();
	public abstract void cleanUp();
	
	public UiFormatter(ScrollPane thePane) {
		mainPane = thePane;
		mainPane.setFitToWidth(true);
		gridHelper = new UiGridHelper(UiConstants.STYLE_DEFAULT_BOX);
	}

	/**
	 * Sets the current grid variable only, note needs mainPane.setContent() for effect
	 *
	 * @param newGrid - the new grid
	 */
	protected void setGrid(GridPane newGrid) {
		assert(newGrid != null);
		currentGrid = newGrid;
	}
	
	protected void clearCurrentGridContents() {
		assert(currentGrid != null);
		Node node = null;
		if (currentGrid.isGridLinesVisible()) {
			node = currentGrid.getChildren().get(0); // retain grid lines
		}
		currentGrid.getChildren().clear();
		if (currentGrid.isGridLinesVisible()) {
			currentGrid.getChildren().add(0, node);
		}
	}
}
