package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ActionListMode;
import taskey.ui.content.UiFormatter;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextBuilder;

public class UiActionFormatter extends UiFormatter {

	private ActionListMode currentMode;
	public UiActionFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
		currentMode = ActionListMode.TASKLIST;
	}

	public void updateGrid(ActionListMode mode) {
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
		assert(myTaskList != null);
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			addTaskID(theTask, 0, i);
			addTaskName(theTask, 1, i);
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
		element.getChildren().addAll(myConfig.build(line));
		addTextFlowToCell(col, row, element,TextAlignment.CENTER, currentGrid);
	}

	private void showHelp() {
		ArrayList<UiTextBuilder> lineConfigs = new ArrayList<UiTextBuilder>();
		String line = "";

		// hard coded colorings, i think have to summarize the commands instead or another way, maybe just place an image
		// very inefficient too
		lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED));
		line += "Taskey's list of $commands\n\n";
		lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$add $<task name>\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_GREEN,UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $go for a run\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK, UiConstants.STYLE_TEXT_BLUE));
	    line += "$add $<task name> $on/by $<date>\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $project meeting at three $on $7 Feb 2016\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $complete essay $by $today\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $complete essay $by $next Friday\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK, UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK, UiConstants.STYLE_TEXT_BLUE));
	    line += "$add $<task name> $from $<date> $to $<date>\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK,UiConstants.STYLE_TEXT_BLUE));
	    line += "$e.g. $add $meeting $from $tomorrow $to $18 Feb\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_GREEN));
	    line += "$add $<task name> $#<tag name>\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$del $<task name>\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$del $<ID>\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_GREEN));
	    line += "$del $#<tag name>\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK));
	    line += "$set $<task name>/<id> $¡°new task name¡±\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE, UiConstants.STYLE_TEXT_BLACK));
	    line += "$set $<task name>/<id> $[<date>¡­<date>]\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_GREEN, UiConstants.STYLE_TEXT_RED, UiConstants.STYLE_TEXT_BLUE,UiConstants.STYLE_TEXT_BLACK));
	    line += "$e.g. $set $meeting $[16 feb, 17 feb]\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$search $<phrase>\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_GREEN));
	    line += "$search $#<tag name>\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$done $<task name/id>\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED));
	    line += "$undo\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $all\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $general\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $deadline\n\n";
	    lineConfigs.add(new UiTextBuilder(UiConstants.STYLE_TEXT_RED,UiConstants.STYLE_TEXT_BLUE));
	    line += "$view $events\n";
	    
	    formatHelp(line,lineConfigs);
	}


	private void formatHelp(String passage, ArrayList<UiTextBuilder> lineConfigs) {
		assert(lineConfigs != null);
		String [] text = passage.split("\n");
		int currentConfig = 0;
		for ( int i = 0; i < text.length; i ++ ) {
			UiTextBuilder myConfig = lineConfigs.get(currentConfig); // each line has its own config
			TextFlow element = new TextFlow();
			element.getChildren().addAll(myConfig.buildBySymbol(text[i]));
			String style = UiConstants.STYLE_WHITE_BOX;
			if ( text[i].equals("")) {
				style = UiConstants.STYLE_RED_BOX;
			} else {
				currentConfig++; // go to next config
			}
			createStyledCell(0, i,style, currentGrid);
			addTextFlowToCell(0, i, element,TextAlignment.LEFT, currentGrid);
		}
	}
}
