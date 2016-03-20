package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import taskey.constants.Triplet;
import taskey.constants.UiConstants;
import taskey.logic.Task;

/**
 * @@author A0125419H
 * This class contains a default grid-based formatter for the content windows.
 * It is extended by specialized classes to provide different functionality
 * 
 * @author Junwei
 *
 */

public abstract class UiFormatter {
	
	protected ScrollPane mainPane;
	protected GridPane currentGrid;
	protected ArrayList<GridPane> myGrids;
	protected UiGridHelper gridHelper;
	protected ArrayList<Triplet<Color,String,Integer>> categoryList;
	// Abstract methods that are handled by extended classes
	public abstract void format(ArrayList<Task> myTaskList);
	public abstract void processArrowKey(KeyEvent event);
	public abstract int processDeleteKey();
	public abstract int processEnterKey();
	
	public UiFormatter(ScrollPane thePane) {
		mainPane = thePane;
		mainPane.setFitToWidth(true);
		myGrids = new ArrayList<GridPane>();
		gridHelper = new UiGridHelper(UiConstants.STYLE_DEFAULT_BOX);
	}
	public void setCategories(ArrayList<Triplet<Color,String,Integer>> _categoryList) {
		categoryList = _categoryList;
	}
	
	public void cleanUp() {
		myGrids.clear();
	}
	
	protected void setGrid(int index) {
		assert(index >= 0 && index < myGrids.size());
		currentGrid = myGrids.get(index);
		mainPane.setContent(currentGrid);
	}
	
	/**
	 * Adds the grid, with a boolean whether to use that as the current grid
	 *
	 * @param newGrid - the new grid
	 * @param setToCurrent - whether to use
	 */
	protected void addGrid(GridPane newGrid, boolean setToCurrent) {
		assert(newGrid != null);
		if ( setToCurrent == true ) {
			currentGrid = newGrid;
		}
		myGrids.add(newGrid);
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
