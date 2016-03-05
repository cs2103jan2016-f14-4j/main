package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.content.UiFormatter;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextBuilder;

public class UiDefaultFormatter extends UiFormatter {

	private String theDate = ""; // temporary until the parameters are separated in task object
	
	public UiDefaultFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
	}

	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);
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
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
		String line = "" + (row + 1);
		element.getChildren().addAll(myConfig.build(line));
		createStyledCell(col, row, UiConstants.STYLE_NUMBER_ICON, currentGrid);
		addTextFlowToCell(col, row, element,TextAlignment.CENTER, currentGrid);
	}

	private void addTaskName(Task theTask, int col, int row) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLACK);
		String line = theTask.getTaskName();
		theDate = "";
		if (theTask.getDeadline().length() != 0 ) {
			String[] params = theTask.getDeadline().split(" ");
			theDate = params[0] + params[1] + params[2];
		}
		element.getChildren().addAll(myConfig.build(line));
		addTextFlowToCell(col, row, element,TextAlignment.LEFT, currentGrid);
	}

	private void addTaskDate(Task theTask, int col, int row) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		
		String line;
		if (theDate.length() == 0) {
			myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
			line = "------";
		} else {
			myConfig.addMarker(0, UiConstants.STYLE_TEXT_RED);
			line = theDate;
		}
		element.getChildren().addAll(myConfig.build(line));
		addTextFlowToCell(col, row, element,TextAlignment.CENTER, currentGrid);
	}
}
