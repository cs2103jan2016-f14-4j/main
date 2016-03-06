package taskey.ui.content;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

/**
 * This class contains methods to add content to grids, is extended by other
 * classes to provide more specialized functionality
 * 
 * @author Junwei
 *
 */
public abstract class UiFormatter {
	protected String defaultWrapperStyle;
	protected UiClockService clockService;
	protected GridPane currentGrid;
	protected ArrayList<GridPane> myGrids;
	
	public abstract void clearOtherVariables();
	public abstract void format(ArrayList<Task> myTaskList);
	
	public UiFormatter(GridPane _gridPane, UiClockService _clockService) {
		currentGrid = _gridPane;
		clockService = _clockService;
		myGrids = new ArrayList<GridPane>();
		myGrids.add(currentGrid);
		defaultWrapperStyle = UiConstants.STYLE_WHITE_BOX;
	}
	
	public void setGrid(int index) {
		assert(index >= 0 && index < myGrids.size());
		currentGrid = myGrids.get(index);
	}
	public void addGrid(GridPane newGrid) {
		assert(newGrid != null);
		myGrids.add(newGrid);
	}
	public GridPane getGrid() {
		assert(currentGrid != null);
		return currentGrid;
	}
	public void clearGridContents() {
		assert(currentGrid != null);
		Node node = null;
		if (currentGrid.isGridLinesVisible()) {
			node = currentGrid.getChildren().get(0); // retain grid lines
		}
		currentGrid.getChildren().clear();
		if (currentGrid.isGridLinesVisible()) {
			currentGrid.getChildren().add(0, node);
		}
		clearOtherVariables();
	}
	public void cleanUp() {
		myGrids.clear();
	}
	
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
