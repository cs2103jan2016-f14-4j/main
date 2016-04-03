package taskey.ui;

import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import taskey.constants.UiConstants;
import taskey.messenger.Task;
import taskey.ui.content.UiTextBuilder;

/**
 * @@author A0125419H
 * 
 * This class implements a alert object used by UiAlertController, 
 * which wraps an Task object in order to have additional information
 * @author Junwei
 *
 */
public class UiAlert {

	private Task theTask;
	private String alertMessage = "Stub";
	private static int stubID = 0; // used for key generated Alerts
	
	public UiAlert() {
		theTask = new Task();
		theTask.setTaskName(String.valueOf(stubID++));
		theTask.setTaskType("FLOATING");
	}
	
	public UiAlert(Task _theTask) {
		theTask = _theTask;
	}

	public Task getTask() {
		return theTask;
	}
	
	public void setMessage(String msg) {
		alertMessage = msg;
	}
	
	public String getMessage() {
		return alertMessage;
	}

	public TextFlow getTextFlow() {
		UiTextBuilder myBuilder = new UiTextBuilder();
		myBuilder.addMarker(0, UiConstants.STYLE_TEXT_DEFAULT);
		String line = "" + theTask.getTaskName() + "\n";
		myBuilder.addMarker(line.length(), UiConstants.STYLE_TEXT_RED);
		if ( theTask.getTaskType().equals("DEADLINE")) {
			line += theTask.getDeadline();
		} else if ( theTask.getTaskType().equals("EVENT")){
			line += theTask.getStartDate() + " to " + theTask.getEndDate();
		}
		myBuilder.addMarker(line.length(), UiConstants.STYLE_TEXT_BLUE);
		line += "\n" + alertMessage;
		return myBuilder.build(line);
	}
	
	public Color getColor() {
		Color theColor = null;
		switch ( theTask.getPriority()) { 
			case 2: theColor = Color.RED;
					break;
			case 1: theColor = Color.ORANGE;
					break;
			default:
					theColor = Color.GREEN;
		}
		return theColor;
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj == null ) {
			return false;
		}
		UiAlert theAlert = (UiAlert) obj;
		if ( theAlert.getTask().equals(this.getTask())
			&& theAlert.getMessage().equals(this.getMessage())) {
			return true;
		} 
		return false;
	}
}
