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
		myText.getStyleClass().add(UiConstants.STYLE_TAB_WINDOW);
		myText.setMinHeight(pane.getHeight());
		pane.setFitToWidth(true);
		pane.setContent(myText); // default to TextFlow as the content
		
	}
	
	// In case for image placements etc.
	public void createGrid(ContentBox contentID) {
		GridPane newGrid = new GridPane();
		contentBoxes.get(contentID.getValue()).setContent(newGrid);
	}
	
	public ArrayList<Text> convertStringToTextNodes(String line) {
		ArrayList<Text> myTexts = new ArrayList<Text>();
		String modifier = "#000000";
		for ( int i = 0; i < line.length(); i ++ ) {
			String myChar = String.valueOf(line.charAt(i));
			if ( myChar.equals("<")) {
				modifier = "";
				i++;
				myChar = String.valueOf(line.charAt(i));
				while ( myChar.equals(">") == false ) {
					modifier += myChar;	
					i++;
					myChar = String.valueOf(line.charAt(i));
				}
				i++;
				myChar = String.valueOf(line.charAt(i));
			}
			Text newText = new Text(myChar);
			newText.setFill(Color.web(modifier));
			myTexts.add(newText);
		}
		return myTexts;
	}
	
	public ArrayList<Text> getTextNodesFromTask(int taskNo, Task theTask) {
		ArrayList<Text> myTexts = new ArrayList<Text>();
		Text addedNode = new Text("["+(taskNo+1)+"]: ");
		addedNode.setFill(Color.BLUEVIOLET);
		myTexts.add(addedNode);
		addedNode = new Text(theTask.getTaskName());
		addedNode.setFill(Color.BLACK);
		myTexts.add(addedNode);
		if ( theTask.getDeadline() != "") {
			addedNode = new Text(" on " + theTask.getDeadline());
			addedNode.setFill(Color.RED);
			myTexts.add(addedNode);
		}
		addedNode = new Text("\n\n");
		myTexts.add(addedNode);
		return myTexts;
	}
	public void updateContentBox(ArrayList<Task> myTaskList, ContentBox contentID) {
		TextFlow myText = (TextFlow) contentBoxes.get(contentID.getValue()).getContent();
		myText.getChildren().removeAll(myText.getChildren());
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			myText.getChildren().addAll(getTextNodesFromTask(i,theTask));
	
			ObservableList<Node> text = myText.getChildren();
			for ( int j = 0; j < text.size(); j++ ) {
				((Text)text.get(j)).setFont(Font.font("Comic Sans MS", FontWeight.SEMI_BOLD, 13));
			}	
		}
		if (contentID == ContentBox.PENDING) {
			updateWeeklyList(myTaskList);
		}
	}

	public void updateWeeklyList(ArrayList<Task> myTaskList) {

		TextFlow weekList = (TextFlow) contentBoxes.get(UiConstants.ContentBox.WEEKlY.getValue()).getContent();
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			if (theTask.getDeadline() == "" || Integer.parseInt(theTask.getDeadline().split(" ")[0]) >= clockService.getDayOfMonth()) {
				String taskText = theTask.getTaskName();
				if (taskText.length() > UiConstants.WORD_LIMIT_WEEKLIST) {
					taskText = taskText.substring(0, UiConstants.WORD_LIMIT_WEEKLIST) + "...*";
				}
				Text newText = new Text("- " + taskText + " (" + theTask.getDeadline() + ")\n\n");
				newText.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
				weekList.getChildren().add(newText);
			}
		}
	}
	
	public void cleanUp() {
		contentBoxes.removeAll(contentBoxes);
	}

}
