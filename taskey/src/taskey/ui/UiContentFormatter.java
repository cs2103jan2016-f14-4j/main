package taskey.ui;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;

/**
 * This class handles all content display related operations
 * @author JunWei
 *
 */
public class UiContentFormatter {
	
	private ArrayList<ScrollPane> contentBoxes = new ArrayList<ScrollPane>(); // list of references to the TextFlow objects
	private UiClockService clockService; // reference
	public UiContentFormatter( UiClockService _clockService ) {
		clockService = _clockService;
	}
	public void addScrollPane(ScrollPane pane) {
		contentBoxes.add(pane);
		TextFlow myText = new TextFlow();
		myText.getStyleClass().add(UiConstants.TEXT_FLOW_BACKGROUND);
		myText.setMinHeight(pane.getHeight());
		pane.setFitToWidth(true);
		pane.setContent(myText); // default to TextFlow as the content
		
	}
	
	// In case for image placements etc.
	public void createGrid(ContentBox contentID) {
		GridPane newGrid = new GridPane();
		contentBoxes.get(contentID.getValue()).setContent(newGrid);
	}
	
	
	public void updateContentBox(ArrayList<Task> myTaskList, ContentBox contentID) {
		TextFlow myText = (TextFlow) contentBoxes.get(contentID.getValue()).getContent();
		myText.getChildren().removeAll(myText.getChildren());
		
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			myText.getChildren().addAll(UiContentFormats.getTextNodesFromTaskPending(i,theTask));
		}
		if (contentID == ContentBox.PENDING) { // update weekly list also when pending list is updated
			updateWeeklyList(myTaskList);
		}
	}

	public void updateWeeklyList(ArrayList<Task> myTaskList) {
		TextFlow myText = (TextFlow) contentBoxes.get(UiConstants.ContentBox.WEEKlY.getValue()).getContent();
		myText.getChildren().removeAll(myText.getChildren());
		
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			if (theTask.getDeadline() == "" || Integer.parseInt(theTask.getDeadline().split(" ")[0]) >= clockService.getDayOfMonth()) {
				myText.getChildren().addAll(UiContentFormats.getTextNodesFromTaskWeekly(theTask));
			}
		}
	}
	
	public void cleanUp() {
		contentBoxes.removeAll(contentBoxes);
	}

}
