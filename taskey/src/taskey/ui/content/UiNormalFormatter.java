package taskey.ui.content;

import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.utility.UiTextConfig;

public class UiNormalFormatter extends UiFormatter {

	public UiNormalFormatter(GridPane _gridPane) {
		super(_gridPane);
	}

	@Override
	public void format(int taskNo, Task theTask) {
		UiTextConfig myConfig = new UiTextConfig();
		TextFlow element = new TextFlow();
		int col = 0;
		
		myConfig.addMarker(0,"blue");
		String line = "["+(taskNo+1)+"]: ";
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element,gridPane,col,taskNo,"gridGreen",TextAlignment.CENTER);
		col++; // move to next cell
		
		element = new TextFlow();
		myConfig.removeMarkers();
		myConfig.addMarker(0,"black");
		line = theTask.getTaskName();
		myConfig.addMarker(line.length(),"red");
		if ( theTask.getDeadline() != "") {
			line += (" on " + theTask.getDeadline());
		}
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element,gridPane,col,taskNo,"gridCyan",TextAlignment.LEFT);
		col++;
	}
}
