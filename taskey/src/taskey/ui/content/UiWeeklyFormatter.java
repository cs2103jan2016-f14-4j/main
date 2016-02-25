package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextConfig;

public class UiWeeklyFormatter extends UiFormatter{
	public UiWeeklyFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane,_clockService);
	}

	public void format(ArrayList<Task> myTaskList) {
		int currentNo = 1;
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			if ( isWithinWeek(theTask.getDeadline())) {
				UiTextConfig myConfig = new UiTextConfig();
				TextFlow element = new TextFlow();
		
				myConfig.addMarker(0,"textBlackToPurple");
				String line = "";
				String taskText = theTask.getTaskName();
				if (taskText.length() > UiConstants.WORD_LIMIT_WEEKLIST) {
					line += taskText.substring(0, UiConstants.WORD_LIMIT_WEEKLIST) + "...*";
				}
				if ( theTask.getDeadline() != "" ) {
					line += "- " + taskText + " (" + theTask.getDeadline() + ")";
				}
				
				element.getChildren().addAll(myConfig.format(line));
				addStyledCellTextFlow(element,gridPane,0,currentNo,"whiteBox",TextAlignment.CENTER);
				
				currentNo++;
			}
		}
	}
	
	public boolean isWithinWeek(String deadLine) {
		if (deadLine == "" || Integer.parseInt(deadLine.split(" ")[0]) >= clockService.getDayOfMonth() ) {
			return true;
		} else {
			return false;
		}
	}
}
