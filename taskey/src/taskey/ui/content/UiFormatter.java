package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;

/**
 * This class contains methods to add content to grids,
 * is extended by other classes to provide more specialized functionality
 * @author Junwei
 *
 */
public abstract class UiFormatter {
	
	public GridPane gridPane;
	
	public UiFormatter(GridPane _gridPane) {
		gridPane = _gridPane;
	}
	
	public void format(int taskNo, Task theTask) {
	}	

	public void addStyledCellTextFlow(TextFlow element, GridPane gridPane, int col, int row, String style, TextAlignment align) {
		element.setTextAlignment(align);
		StackPane wrapper = new StackPane();
		wrapper.getChildren().add(element);
		wrapper.getStyleClass().add(style);
		gridPane.add(wrapper, col, row);
	}
	
	public void addStyledCellImage(String path, GridPane gridPane, int col, int row, String style) {
		Image img = new Image(getClass().getResourceAsStream(path));
		ImageView myImg = new ImageView(img);
		myImg.setFitHeight(10);
		myImg.setFitWidth(10);
		StackPane wrapper = new StackPane();
		wrapper.getChildren().add(myImg);
		wrapper.getStyleClass().add(style);
		gridPane.add(wrapper, col, row);
	}
	
	public void clearGrid() {
		Node node = null;
		if ( gridPane.isGridLinesVisible()) {
			node = gridPane.getChildren().get(0); // retain grid lines
		}
		gridPane.getChildren().clear();
		if ( gridPane.isGridLinesVisible()) {
			gridPane.getChildren().add(0,node);
		}
		
	}
}
