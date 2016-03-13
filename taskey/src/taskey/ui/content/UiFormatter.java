package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import taskey.logic.Task;
import taskey.ui.UiConstants;

/**
 * This class contains a default grid-based formatter for the content windows.
 * classes to provide more specialized functionality
 * 
 * @author Junwei
 *
 */
public abstract class UiFormatter {
	
	protected ScrollPane mainPane;
	protected GridPane currentGrid;
	protected ArrayList<GridPane> myGrids;
	
	protected UiGridHelper gridHelper;
	public abstract void format(ArrayList<Task> myTaskList);
	public abstract void processArrowKey(KeyEvent event);
	public abstract int processDeleteKey();
	public abstract int processEnterKey();
	
	public UiFormatter(ScrollPane thePane) {
		mainPane = thePane;
		mainPane.setFitToWidth(true);
		myGrids = new ArrayList<GridPane>();
		gridHelper = new UiGridHelper(UiConstants.STYLE_WHITE_BOX);
	}
	
	protected void setGrid(int index) {
		assert(index >= 0 && index < myGrids.size());
		currentGrid = myGrids.get(index);
		mainPane.setContent(currentGrid);
	}
	protected void addGrid(GridPane newGrid, boolean setToCurrent) {
		assert(newGrid != null);
		if ( setToCurrent == true ) {
			currentGrid = newGrid;
		}
		myGrids.add(newGrid);
	}
	
	/**
	 * Clear contents of currentGrid variable
	 */
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
	public void cleanUp() {
		myGrids.clear();
	}
}
