package taskey.ui.content;

import java.util.ArrayList;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
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

	protected UiClockService clockService;
	protected GridPane currentGrid;
	protected ArrayList<GridPane> myGrids;
	public UiFormatter(GridPane _gridPane, UiClockService _clockService) {
		currentGrid = _gridPane;
		clockService = _clockService;
		myGrids = new ArrayList<GridPane>();
		myGrids.add(currentGrid);
	}

	public void format(ArrayList<Task> myTaskList) {
	}

	protected void addStyledCellTextFlow(TextFlow element, GridPane gridPane, int col, int row, String style,TextAlignment align) {
		element.setTextAlignment(align);
		StackPane wrapper = new StackPane();
		wrapper.getChildren().add(element);
		wrapper.getStyleClass().add(style);
		gridPane.add(wrapper, col, row);
		GridPane.setFillHeight(wrapper, false);
	}

	protected void addStyledCellImage(String path, GridPane gridPane, int col, int row, String style) {
		Image img = new Image(getClass().getResourceAsStream(path));
		ImageView myImg = new ImageView(img);
		myImg.setFitHeight(10);
		myImg.setFitWidth(10);
		StackPane wrapper = new StackPane();
		wrapper.getChildren().add(myImg);
		wrapper.getStyleClass().add(style);
		gridPane.add(wrapper, col, row);
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
}
