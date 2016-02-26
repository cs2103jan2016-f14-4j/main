package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextConfig;

public class UiNormalFormatter extends UiFormatter {

	private String theDate = ""; // temporary until the parameters are separated in task object
	
	public UiNormalFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
	}

	@Override
	public void format(ArrayList<Task> myTaskList) {
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			theDate = "";
			// Add Task number
			addTaskID(theTask, 0, i);
			// Add Task Name / Time
			addTaskName(theTask, 1, i);
			// Add Task Date
			addTaskDate(theTask, 2, i);
		}
	}

	private void addTaskID(Task theTask, int col, int row) {
		UiTextConfig myConfig = new UiTextConfig();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, "textBlue");
		String line = "" + (row + 1);
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element, gridPane, col, row, "numberIcon", TextAlignment.CENTER);
	}

	private void addTaskName(Task theTask, int col, int row) {
		UiTextConfig myConfig = new UiTextConfig();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, "textBlack");
		String line = theTask.getTaskName();
		theDate = "";
		if (theTask.getDeadline() != "") {
			String[] params = theTask.getDeadline().split(" ");
			theDate = params[0] + params[1] + params[2];
			line += " at ";
			myConfig.addMarker(line.length(), "textRed");
			line += params[3];
		}
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element, gridPane, col, row, "whiteBox", TextAlignment.LEFT);
	}

	private void addTaskDate(Task theTask, int col, int row) {
		UiTextConfig myConfig = new UiTextConfig();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, "textBlue");
		String line;
		if (theDate == "") {
			line = "------";
		} else {
			line = theDate;
		}
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element, gridPane, col, row, "whiteBox", TextAlignment.CENTER);
	}
}
