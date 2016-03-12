package taskey.ui.content;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiGridSettings;

/**
 * This class contains methods to add content to grids, is extended by other
 * classes to provide more specialized functionality
 * 
 * @author Junwei
 *
 */
public abstract class UiFormatter {
	protected String defaultWrapperStyle; // per cell
	protected ScrollPane mainPane;
	protected GridPane currentGrid;
	protected ArrayList<GridPane> myGrids;
	
	public abstract void format(ArrayList<Task> myTaskList);
	public abstract void processArrowKey(KeyEvent event);
	public abstract int processDeleteKey();
	
	public UiFormatter(ScrollPane thePane) {
		mainPane = thePane;
		mainPane.setFitToWidth(true);
		myGrids = new ArrayList<GridPane>();
		defaultWrapperStyle = UiConstants.STYLE_WHITE_BOX;
	}
	
	/**
	 * This sets up the grid using predefined GridSettings which only set for column constraints
	 * Row constraints have to be set manually
	 * @param settings - the UiGridSettings
	 * @return
	 */
	protected GridPane setUpGrid(UiGridSettings settings) {
		assert(settings != null);
		GridPane gridPane = new GridPane();
		//gridPane.setGridLinesVisible(true);
		gridPane.setPadding(settings.getPaddings());
		gridPane.setHgap(settings.getHGap());
		gridPane.setVgap(settings.getVGap());
		ArrayList<Integer> colPercents = settings.getColPercents();
		for (int i = 0; i < colPercents.size(); i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(colPercents.get(i));
			gridPane.getColumnConstraints().add(column);
		}
		return gridPane;
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
	
	// All helper methods for manipulating a GridPane below
	protected StackPane createStyledCell(int col, int row, String cellStyle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane styledCell = new StackPane();
		styledCell.getStyleClass().add(cellStyle);
		gridPane.add(styledCell, col, row);
		return styledCell;
	}
	
	protected StackPane getWrapperAtCell(int col, int row, GridPane gridPane ) {
		assert(gridPane != null);
		Node theNode = null;
		ObservableList<Node> childrens = gridPane.getChildren();
		for(int i = 0; i < childrens.size(); i ++ ) {
			if ( i == 0 && gridPane.isGridLinesVisible()) {
				continue; // skip the grid lines which have no row/col
			}
			Node node = childrens.get(i);
		    if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
		    	theNode = node;
		        break;
		    }
		}
		// create a cell if not available
		if ( theNode == null ) {
			theNode = createStyledCell(col,row,defaultWrapperStyle,gridPane);
		}
		return (StackPane)theNode;
	}
	protected void addTextFlowToCell(int col, int row, TextFlow textFlow, TextAlignment align, GridPane gridPane ) {
		assert(gridPane != null);
		assert(textFlow != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		cellWrapper.getChildren().add(textFlow);
		textFlow.setTextAlignment(align);	
	}
	protected void addCircleToCell(int col, int row, Circle circle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		cellWrapper.getChildren().add(circle);
	}
	
	protected ImageView createImageInCell( int col, int row, Image img, int width, int height, GridPane gridPane) {
		assert(gridPane != null);
		StackPane imageWrapper = getWrapperAtCell(col,row,gridPane);
		ImageView myImg = new ImageView(img);
		myImg.setFitHeight(width);
		myImg.setFitWidth(height);
		imageWrapper.getChildren().add(myImg);
		return myImg;
	}
	
	protected Label createLabelInCell( int col, int row, String text, String labelStyle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		Label myLabel = new Label(text);
		myLabel.getStyleClass().add(labelStyle);
		cellWrapper.getChildren().add(myLabel);
		return myLabel;
	}		
	
	// Stacks a pane onto the cell, note that to place elements in this new pane, it has to be done manually for different
	// formatters, methods for the single wrapper can still be used, but switch the parents
	protected StackPane createStackPaneInCell( int col, int row, String paneStyle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		StackPane pane = new StackPane();
		pane.getStyleClass().add(paneStyle);
		cellWrapper.getChildren().add(pane);
		return pane;
	}
}
