package taskey.ui.content;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
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
	public UiFormatter(GridPane _gridPane, UiClockService _clockService) {
		currentGrid = _gridPane;
		clockService = _clockService;
		myGrids = new ArrayList<GridPane>();
		myGrids.add(currentGrid);
		defaultWrapperStyle = UiConstants.STYLE_WHITE_BOX;
	}
	
	public void setGrid(int index) {
		currentGrid = myGrids.get(index);
	}
	public void addGrid(GridPane newGrid) {
		myGrids.add(newGrid);
	}
	public GridPane getGrid() {
		return currentGrid;
	}
	public void clearGridContents() {
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
	public void format(ArrayList<Task> myTaskList) {
	}
	
	protected StackPane createStyledCell(int col, int row, String cellStyle, GridPane gridPane) {
		StackPane styledCell = new StackPane();
		styledCell.getStyleClass().add(cellStyle);
		gridPane.add(styledCell, col, row);
		return styledCell;
	}
	
	protected StackPane getWrapperAtCell(int col, int row, GridPane gridPane ) {
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
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		cellWrapper.getChildren().add(textFlow);
		textFlow.setTextAlignment(align);	
	}
	protected void addCircleToCell(int col, int row, Circle circle, GridPane gridPane) {
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		cellWrapper.getChildren().add(circle);
	}
	
	// Wraps image to be placed anywhere in cell
	protected StackPane addImageToCell( int col, int row, Image img, int width, int height, GridPane gridPane) {
		StackPane imageWrapper = createStackPaneInCell(col,row,"",gridPane);
		ImageView myImg = new ImageView(img);
		myImg.setFitHeight(width);
		myImg.setFitWidth(height);
		imageWrapper.getChildren().add(myImg);
		return imageWrapper;
	}
	
	// Such that the stackpane does not stretch with cell size
	protected StackPane createStackPaneInCell( int col, int row, String paneStyle, GridPane gridPane) {
		StackPane pane = new StackPane();
		pane.getStyleClass().add(paneStyle);
		GridPane.setFillHeight(pane, false);
		GridPane.setFillWidth(pane,false);
		gridPane.add(pane,col,row);
		return pane;
	}
}
