package taskey.ui;

import java.util.ArrayList;

import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import taskey.logic.Task;

/**
 * This class contains static default display formats for the various content boxes
 * @author Junwei
 *
 */
public class UiContentFormats {

	public static ArrayList<Text> getTextNodesFromTaskPending(int taskNo, Task theTask) {
		UiTextConfig myConfig = new UiTextConfig();
		myConfig.addMarker(0,"blue");
		String line = "["+(taskNo+1)+"]: ";
		myConfig.addMarker(line.length(),"black");
		line += theTask.getTaskName();
		myConfig.addMarker(line.length(),"red");
		if ( theTask.getDeadline() != "") {
			line += (" on " + theTask.getDeadline());
		}
		line += "\n\n";
		return myConfig.format(line);
	}
	
	public static ArrayList<Text> getTextNodesFromTaskWeekly(Task theTask) {
		UiTextConfig myConfig = new UiTextConfig();
		myConfig.addMarker(0,"weekly");
		String line = "";
		String taskText = theTask.getTaskName();
		if (taskText.length() > UiConstants.WORD_LIMIT_WEEKLIST) {
			line += taskText.substring(0, UiConstants.WORD_LIMIT_WEEKLIST) + "...*";
		}
		if ( theTask.getDeadline() != "" ) {
			line += "- " + taskText + " (" + theTask.getDeadline() + ")";
		}
		line += "\n\n";
		return myConfig.format(line);
	}
}
