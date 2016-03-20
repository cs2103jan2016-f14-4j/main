package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ActionMode;
import taskey.logic.Task;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;

/**
 * @@author A0125419H
 * This class is responsible to formatting the Action Tab
 * It provides additional functions like the help menu
 * @author junwei
 */

public class UiActionFormatter extends UiFormatter {
	private UiTaskView taskView;
	private UiHelpView helpView;
	private UiPagination currentView;
	private int entriesPerPage = 6;
	
	public UiActionFormatter(ScrollPane thePane) {
		super(thePane);	
		taskView = new UiTaskView(entriesPerPage);
		helpView = new UiHelpView();
		mainPane.setFitToHeight(true);
	}

	@Override
	public void processArrowKey(KeyEvent event) {
		if ( currentView != null ) {
			currentView.processArrowKey(event);
		}
	}
	@Override
	public int processDeleteKey() {	
		if ( currentView == taskView.getView() ) {
			return currentView.getSelection() + 1;
		}
		return -1;
	}
	@Override
	public int processEnterKey() {
		if ( currentView != taskView.getView() ) {
			helpView.processEnterKey();
			currentView = helpView.getView();
			mainPane.setContent(currentView.getPagination());
		} 
		return 0;
	}

	/**
	 * Update content based on the mode provided
	 *
	 * @param myList - the task list
	 * @param mode - the mode of display
	 */
	public void updateContents(ArrayList<Task> myList, ActionMode mode) {
		switch ( mode ) {
			case HELP: 
				helpView.resetView();
				currentView = helpView.getView();
				mainPane.setContent(currentView.getPagination());				
				break;
			default:
				currentView = taskView.getView();
				mainPane.setContent(currentView.getPagination());
				format(myList);
		}
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		taskView.getView().clear();
		int totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double	
		taskView.createPaginationGrids(myTaskList,categoryList,totalPages);
	}

	@Override
	public void cleanUp() {
		helpView.clear();
		taskView.clear();
	}
}
