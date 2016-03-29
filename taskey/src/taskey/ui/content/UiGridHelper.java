package taskey.ui.content;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.constants.UiConstants;

/**
 * @@author A0125419H
 * This class contains convenience methods to manipulate a GridPane,
 * It is used by all operations that involve a gridpane.
 * 
 * @author junwei
 */

public class UiGridHelper {
	
	private String defaultWrapperStyle; // used to style the initial StackPane in a cell on creation 
	
	public UiGridHelper(String wrapperStyle) {
		defaultWrapperStyle = wrapperStyle;
	}

	/**
	 * This sets up the grid using predefined GridSettings which only set for column constraints
	 * Row constraints have to be set manually.
	 *
	 * @param settings - the UiGridSettings
	 * @return - the grid pane
	 */
	public GridPane setUpGrid(UiGridSettings settings) {
		assert(settings != null);
		GridPane gridPane = new GridPane();
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
	

	public StackPane createStyledCell(int col, int row, String cellStyle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane styledCell = new StackPane();
		styledCell.getStyleClass().add(cellStyle);
		gridPane.add(styledCell, col, row);
		return styledCell;
	}
	
	/**
	 * Gets the wrapper at cell. Note that the wrapper is a StackPane object which will be auto created 
	 * by createStyledCell if it does not exist
	 *
	 * @param col - the col
	 * @param row - the row
	 * @param gridPane - the grid pane
	 * @return - the wrapper at cell
	 */
	public StackPane getWrapperAtCell(int col, int row, GridPane gridPane ) {
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
	
	public void addTextFlowToCell(int col, int row, TextFlow textFlow, TextAlignment align, GridPane gridPane ) {
		assert(gridPane != null);
		assert(textFlow != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		cellWrapper.getChildren().add(textFlow);
		textFlow.setTextAlignment(align);	
	}
	
	public void addCircleToCell(int col, int row, Circle circle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		cellWrapper.getChildren().add(circle);
	}

	public ImageView createImageInCell( int col, int row, Image img, int width, int height, GridPane gridPane) {
		assert(gridPane != null);
		StackPane imageWrapper = getWrapperAtCell(col,row,gridPane);
		ImageView myImg = new ImageView(img);
		myImg.setFitHeight(width);
		myImg.setFitWidth(height);
		myImg.setPreserveRatio(true);
		imageWrapper.getChildren().add(myImg);
		return myImg;
	}
	
	public Label createLabelInCell( int col, int row, String text, String labelStyle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		Label myLabel = new Label(text);
		myLabel.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
		myLabel.getStyleClass().add(labelStyle);
		cellWrapper.getChildren().add(myLabel);
		return myLabel;
	}		
	
	/**
	 * Stacks a pane onto the cell, note that to place elements in this new pane, it has to be done manually 
	 * for different formatters, methods for the single wrapper can still be used, but switch the parents
	 * @param col - the col
	 * @param row - the row
	 * @param paneStyle - the pane style
	 * @param gridPane - the grid pane
	 * @return - the stack pane
	 */ 
	public StackPane createStackPaneInCell( int col, int row, String paneStyle, GridPane gridPane) {
		assert(gridPane != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		StackPane pane = new StackPane();
		pane.getStyleClass().add(paneStyle);
		cellWrapper.getChildren().add(pane);
		return pane;
	}
	
	public Rectangle createScaledRectInCell( int col, int row, Color theColor, GridPane gridPane) {
		assert(gridPane != null);
		StackPane cellWrapper = getWrapperAtCell(col,row,gridPane);
		Rectangle scaledRect = new Rectangle(0,0,0,0);
		scaledRect.setFill(Paint.valueOf(theColor.toString()));
		cellWrapper.getChildren().add(scaledRect);
		StackPane.setAlignment(scaledRect, Pos.CENTER_LEFT);
		
		// problem with precision which shifts the whole row, thus we subtract a fixed amount
		scaledRect.widthProperty().bind(cellWrapper.widthProperty().subtract(1.5f)); 
		scaledRect.setTranslateX(0.75f); // this mitigates the issue
		scaledRect.heightProperty().bind(cellWrapper.heightProperty());
		return scaledRect;
	}
}
