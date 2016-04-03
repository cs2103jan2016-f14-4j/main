package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ActionMode;
import taskey.messenger.Task;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;

/**
 * @@author A0125419H
 * This class is responsible to formatting the Action Content box
 * It provides additional functions like the help menu
 * 
 * @author junwei
 * 
 */

public class UiActionFormatter extends UiFormatter {
	private UiTaskView taskView;
	private UiHelpView helpView;
	private UiPagination currentView;

	public UiActionFormatter(ScrollPane thePane) {
		super(thePane);	
		taskView = new UiTaskView(UiConstants.ENTRIES_PER_PAGE_DEFAULT);
		helpView = new UiHelpView();
		mainPane.setFitToHeight(true);
	}

	@Override
	public void processArrowKey(KeyEvent event) {
		if ( currentView != null ) {
			currentView.processKey(event);
		}
	}
	@Override
	public int processDeleteKey() {	
		if ( currentView == taskView.getView() ) {
			return currentView.getSelection() + 1; // add one as selection is from 0
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
	
	@Override
	public void processPageUpAndDown(KeyEvent event) {
		if ( currentView != null ) {
			currentView.processKey(event);
		}
	}

	/**
	 * Update content mode 
	 *
	 * @param mode - the mode of display
	 */
	public void updateMode(ActionMode mode) {
		switch ( mode ) {
			case HELP: 
				helpView.resetView();
				currentView = helpView.getView();
				mainPane.setContent(currentView.getPagination());				
				break;
			case LIST:
				currentView = taskView.getView();
				mainPane.setContent(currentView.getPagination());
				break;
			default:
				System.out.println(UiConstants.ACTION_MODE_INVALID);
		}
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		taskView.getView().clear();
		int totalPages = (int) Math.ceil(myTaskList.size()/1.0/
										 UiConstants.ENTRIES_PER_PAGE_DEFAULT); // convert to double	
		taskView.createPaginationGrids(myTaskList,totalPages);
	}

	@Override
	public void cleanUp() {
		helpView.clear();
		taskView.clear();
	}
}
