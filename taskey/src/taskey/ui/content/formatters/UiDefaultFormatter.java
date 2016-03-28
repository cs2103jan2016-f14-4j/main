package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.RowConstraints;
import taskey.constants.UiConstants;
import taskey.messenger.Task;
import taskey.ui.content.UiFormatter;

/**
 * @@author A0125419H
 * This class is responsible for formatting the Default content box
 * The default box is used by this week, pending, expired
 * 
 * @author junwei
 */

public class UiDefaultFormatter extends UiFormatter {
	private static final int entriesPerPage = 6;
	private UiTaskView myTaskView;
	private int lastNumOfTasks = -1;
	
	public UiDefaultFormatter(ScrollPane thePane) {
		super(thePane);
		myTaskView = new UiTaskView(entriesPerPage);
		
		mainPane.setContent(myTaskView.getView().getPagination());
		mainPane.setFitToHeight(true);
		
		createPromptNoTasks();
	}

	@Override
	public int processEnterKey() {
		return 0;
	}
	
	@Override
	public void processArrowKey(KeyEvent event) {
		myTaskView.getView().processKey(event);
	}

	@Override
	public int processDeleteKey() {
		return myTaskView.getView().getSelection() + 1;
	}
	
	@Override
	public void processPageUpAndDown(KeyEvent event) {
		myTaskView.getView().processKey(event);
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);			
		if ( myTaskList.size() == 0 ) {	
			mainPane.setContent(currentGrid);	
		} else {
			mainPane.setContent(myTaskView.getView().getPagination());
			myTaskView.getView().clear();
			int totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double	
			myTaskView.createPaginationGrids(myTaskList,totalPages);
			if ( lastNumOfTasks != -1 && myTaskList.size() > lastNumOfTasks ) { // addition of a task
				myTaskView.getView().selectInPage(totalPages-1, entriesPerPage); // select last
			} 
			lastNumOfTasks = myTaskList.size();
		}
	}
	
	private void createPromptNoTasks() {
		// create prompt in absence of tasks
		setGrid(gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_SINGLE_CELL));
		RowConstraints row = new RowConstraints();
		row.setPercentHeight(100);
		currentGrid.getRowConstraints().add(row);
		gridHelper.createStyledCell(0, 0, "", currentGrid);
		gridHelper.createLabelInCell(0, 0, "No tasks in window, add some tasks to get started!", "", currentGrid);
	}

	@Override
	public void cleanUp() {
		myTaskView.clear();
		clearCurrentGridContents();
	}	
}
