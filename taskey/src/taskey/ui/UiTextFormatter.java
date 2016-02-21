package taskey.ui;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;

/**
 * This class handles all text display related operations
 * @author JunWei
 *
 */
public class UiTextFormatter {
	
	private ArrayList<TextFlow> contentBoxes = new ArrayList<TextFlow>(); // list of references to the TextFlow objects
	private UiClockService clockService; // reference
	public UiTextFormatter( UiClockService _clockService ) {
		clockService = _clockService;
	}
	public void addTextContent(TextFlow textFlow) {
		textFlow.getStyleClass().add(UiConstants.STYLE_TAB_WINDOW);
		contentBoxes.add(textFlow);
	}
	
	public void updateContentBox(ArrayList<Task> myTaskList, ContentBox contentID) {
		TextFlow myText = contentBoxes.get(contentID.getValue());
		myText.getChildren().removeAll(myText.getChildren());
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			Text newText = new Text((i + 1) + ". " + theTask.getTaskName() + " on " );
			Text deadLine = new Text(theTask.getDeadline() + "\n\n");
			deadLine.setFill(Color.RED);
			
			myText.getChildren().addAll(newText,deadLine);
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

		TextFlow weekList = contentBoxes.get(UiConstants.ContentBox.WEEKlY.getValue());
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			if (theTask.getDeadline() == "" || Integer.parseInt(theTask.getDeadline().split(" ")[0]) >= clockService.getDayOfMonth()) {
				String taskText = theTask.getTaskName();
				if (taskText.length() > UiConstants.WORD_LIMIT_WEEKLIST) {
					taskText = taskText.substring(0, UiConstants.WORD_LIMIT_WEEKLIST) + "...*";
				}
				Text newText = new Text("- " + taskText + " (" + theTask.getDeadline() + ") \n\n");
				newText.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
				weekList.getChildren().add(newText);
			}
		}
	}
	
	public void cleanUp() {
		contentBoxes.removeAll(contentBoxes);
	}

}
