package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextConfig;

public class UiNormalFormatter extends UiFormatter {

	public UiNormalFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane,_clockService);
	}

	@Override
	public void format(ArrayList<Task> myTaskList) {
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			UiTextConfig myConfig = new UiTextConfig();
			TextFlow element = new TextFlow();
			int col = 0;
			
			myConfig.addMarker(0,"textBlue");
			String line = "" + (i+1);
			element.getChildren().addAll(myConfig.format(line));
			addStyledCellTextFlow(element,gridPane,col,i,"numberIcon",TextAlignment.CENTER);
			col++; // move to next cell
			
			element = new TextFlow();
			myConfig.removeMarkers();
			myConfig.addMarker(0,"textBlack");
			line = theTask.getTaskName();
			myConfig.addMarker(line.length(),"textRed");
			if ( theTask.getDeadline() != "") {
				line += (" on " + theTask.getDeadline());
			}
			element.getChildren().addAll(myConfig.format(line));
			addStyledCellTextFlow(element,gridPane,col,i,"whiteBox",TextAlignment.LEFT);
			col++;
		}
	}
}
