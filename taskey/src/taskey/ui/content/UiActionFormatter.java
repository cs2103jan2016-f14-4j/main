package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextConfig;

public class UiActionFormatter extends UiFormatter {

	public UiActionFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
	}

	public void format(ArrayList<Task> myTaskList) {
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			addTaskID(theTask, 0, i);
			addTaskName(theTask, 1, i);
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
		element = new TextFlow();
		myConfig.removeMarkers();
		myConfig.addMarker(0, "textBlack");
		String line = theTask.getTaskName();
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element, gridPane, col, row, "whiteBox", TextAlignment.CENTER);
	}

}
