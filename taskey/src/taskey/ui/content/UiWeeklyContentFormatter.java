package taskey.ui.content;

import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.utility.UiTextConfig;

public class UiWeeklyContentFormatter extends UiContentFormatter{
	public UiWeeklyContentFormatter(GridPane _gridPane) {
		super(_gridPane);
	}

	public void format(int taskNo, Task theTask) {
		UiTextConfig myConfig = new UiTextConfig();
		TextFlow element = new TextFlow();

		myConfig.addMarker(0,"weekly");
		String line = "";
		String taskText = theTask.getTaskName();
		if (taskText.length() > UiConstants.WORD_LIMIT_WEEKLIST) {
			line += taskText.substring(0, UiConstants.WORD_LIMIT_WEEKLIST) + "...*";
		}
		if ( theTask.getDeadline() != "" ) {
			line += "- " + taskText + " (" + theTask.getDeadline() + ")";
		}
		
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element,gridPane,0,taskNo,"gridCyan",TextAlignment.CENTER);
	}
}
