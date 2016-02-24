package taskey.ui.content;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiGridSettings;

/**
 * This class acts as the interface for all content display related operations
 * @author JunWei
 *
 */
public class UiContentManager {
	private UiClockService clockService; // reference to ui clock
	private ArrayList<ScrollPane> contentBoxes; // list of references to the ScrollPane objects
	private ArrayList<UiFormatter> myFormatters; // for the grid panes
	
	public UiContentManager( UiClockService _clockService ) {
		clockService = _clockService;
		contentBoxes = new ArrayList<ScrollPane>();
		myFormatters = new ArrayList<UiFormatter>();
	}
	 
	public void addScrollPane(ScrollPane pane, ContentBox contentID) {
		contentBoxes.add(pane);
		pane.setFitToWidth(true);
		if ( contentID == ContentBox.WEEKlY) {
			myFormatters.add(new UiWeeklyFormatter(setUpGrid(pane, UiConstants.weeklySettings)));
		} else {
			myFormatters.add(new UiNormalFormatter(setUpGrid(pane, UiConstants.normSettings)));
		}
	}
	
	public GridPane setUpGrid(ScrollPane pane, UiGridSettings settings) {
		GridPane gridPane = new GridPane();
		pane.setContent(gridPane);
		//gridPane.setGridLinesVisible(true);
		gridPane.setPadding(settings.getPaddings());
        gridPane.setHgap(settings.getHGap());
        gridPane.setVgap(settings.getVGap());
        ArrayList<Integer> colPercents = settings.getColPercents();
        for ( int i = 0; i < colPercents.size(); i ++ ) {
        	ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(colPercents.get(i));
            gridPane.getColumnConstraints().add(column);
        }
        return gridPane;
	}
	
	public void updateContentBox(ArrayList<Task> myTaskList, ContentBox contentID) {
		UiFormatter myFormatter = myFormatters.get(contentID.getValue());
		myFormatter.clearGrid();
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			myFormatter.format(i,theTask);
		}
		if (contentID == ContentBox.PENDING) { // update weekly list also when pending list is updated
			updateWeeklyList(myTaskList);
		}
	}

	public void updateWeeklyList(ArrayList<Task> myTaskList) {
		int contentID = ContentBox.WEEKlY.getValue();
		UiFormatter myFormatter = myFormatters.get(contentID);
		myFormatter.clearGrid();
		int currentNo = 0;
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			if (theTask.getDeadline() == "" || Integer.parseInt(theTask.getDeadline().split(" ")[0]) >= clockService.getDayOfMonth()) {
				myFormatter.format(currentNo,theTask);
				currentNo++;
			}
		}
	}
	
	public void cleanUp() {
		myFormatters.clear();
		contentBoxes.clear();
	}

}
