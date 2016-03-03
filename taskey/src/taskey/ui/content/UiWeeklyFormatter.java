package taskey.ui.content;

import java.util.ArrayList;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextConfig;

public class UiWeeklyFormatter extends UiFormatter {

	private int numEntries;

	public UiWeeklyFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
	}

	public void format(ArrayList<Task> myTaskList) {
		numEntries = 0;
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			if (isWithinWeek(theTask.getDeadline())) {
				addTaskEntry(theTask);
				numEntries++;
			}
		}
	}

	public void addTaskEntry(Task theTask) {
		UiTextConfig myConfig = new UiTextConfig();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLACK_TO_PURPLE);
		String line = "";
		String taskText = theTask.getTaskName();
		line += "NAME: " + taskText + "\n";
		line += "DUE: " + "(" + theTask.getDeadline() + ")\n";
		line += "\n";
		line += "TAGS: ";
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element, currentGrid, 0, numEntries, UiConstants.STYLE_WHITE_BOX, TextAlignment.LEFT);
		
		element = new TextFlow();
		element.getChildren().addAll(myConfig.format("ID: 0"));
		addStyledCellTextFlow(element, currentGrid, 0, numEntries, UiConstants.STYLE_NUMBER_ICON, TextAlignment.RIGHT);
		
		StackPane parent = (StackPane) element.getParent();
		GridPane.setFillWidth(parent, false);
		GridPane.setValignment(parent, VPos.TOP);
		GridPane.setHalignment(parent, HPos.RIGHT);
	}

	public boolean isWithinWeek(String deadLine) {
		if (deadLine == "" || Integer.parseInt(deadLine.split(" ")[0]) - clockService.getDayOfMonth() <= 7) {
			return true;
		} else {
			return false;
		}
	}
}
