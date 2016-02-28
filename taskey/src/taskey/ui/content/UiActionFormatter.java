package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ActionContentMode;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextConfig;

public class UiActionFormatter extends UiFormatter {

	private ActionContentMode currentMode;
	public UiActionFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
		currentMode = ActionContentMode.TASKLIST;
	}

	public void updateGrid(ActionContentMode mode) {
		switch ( mode ) {
			case HELP_MAIN:
			case HELP_ADD:
			case HELP_DEL:
				setGrid(1);
				break;
			default:
				setGrid(0);
				break;
		}
		currentMode = mode;
	}
	
	public void updateContents(ArrayList<Task> myList) {
		switch ( currentMode ) {
			case HELP_MAIN: 
				showHelp();
				break;
			case HELP_ADD: 
				break;
			case HELP_DEL: 
				break;
			default:
				format(myList);
		}
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
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
		String line = "" + (row + 1);
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element, currentGrid, col, row, UiConstants.STYLE_NUMBER_ICON, TextAlignment.CENTER);
	}

	private void addTaskName(Task theTask, int col, int row) {
		UiTextConfig myConfig = new UiTextConfig();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLACK);
		String line = theTask.getTaskName();
		element.getChildren().addAll(myConfig.format(line));
		addStyledCellTextFlow(element, currentGrid, col, row, UiConstants.STYLE_WHITE_BOX, TextAlignment.CENTER);
	}

	public void showHelp() {
		ArrayList<UiTextConfig> lineConfigs = new ArrayList<UiTextConfig>();
		String line = "";

		// hard coded colorings, i think have to summarize the commands instead or another way, maybe just place an image
		// very inefficient too
		lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED));
		line += "Taskey's list of $commands\n\n";
		lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$add $<task name>\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_GREEN,UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $go for a run\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK, UiConstants.STYLE_TEXT_BLUE));
	    line += "$add $<task name> $on/by $<date>\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $project meeting at three $on $7 Feb 2016\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $complete essay $by $today\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $complete essay $by $next Friday\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK, UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK, UiConstants.STYLE_TEXT_BLUE));
	    line += "$add $<task name> $from $<date> $to $<date>\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $meeting $from $tomorrow $to $18 Feb\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_GREEN));
	    line += "$add $<task name> $#<tag name>\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$del $<task name>\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$del $<ID>\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_GREEN));
	    line += "$del $#<tag name>\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK));
	    line += "$set $<task name>/<id> $¡°new task name¡±\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK));
	    line += "$set $<task name>/<id> $[<date>¡­<date>]\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK));
	    line += "$e.g. $set $meeting $[16 feb, 17 feb]\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$search $<phrase>\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_GREEN));
	    line += "$search $#<tag name>\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$done $<task name/id>\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED));
	    line += "$undo\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $all\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $general\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $deadline\n\n";
	    lineConfigs.add(new UiTextConfig(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $events\n";
	    
	    formatHelp(line,lineConfigs);
	}


	public void formatHelp(String passage, ArrayList<UiTextConfig> lineConfigs) {
		String [] text = passage.split("\n");
		int currentConfig = 0;
		for ( int i = 0; i < text.length; i ++ ) {
			UiTextConfig myConfig = lineConfigs.get(currentConfig); // each line has its own config
			TextFlow element = new TextFlow();
			element.getChildren().addAll(myConfig.formatBySymbol(text[i]));
			String style = UiConstants.STYLE_WHITE_BOX;
			if ( text[i].equals("")) {
				style = UiConstants.STYLE_RED_BOX;
			} else {
				currentConfig++; // go to next config
			}
			addStyledCellTextFlow(element, currentGrid, 0, i, style, TextAlignment.LEFT);
		}
	}
}
