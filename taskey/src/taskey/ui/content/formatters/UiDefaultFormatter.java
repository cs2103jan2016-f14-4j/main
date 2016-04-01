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
	private UiTaskView myTaskView;
	private ArrayList<Task> prevList = null;
	
	public UiDefaultFormatter(ScrollPane thePane) {
		super(thePane);
		myTaskView = new UiTaskView(UiConstants.ENTRIES_PER_PAGE_DEFAULT);
		
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
			int totalPages = (int) Math.ceil(myTaskList.size()/1.0/
											 UiConstants.ENTRIES_PER_PAGE_DEFAULT); // convert to double	
			myTaskView.createPaginationGrids(myTaskList,totalPages);
			
			if ( prevList != null && myTaskList.size() > prevList.size() ) { // addition of a task
				int index = findIndexOfAddedTask(prevList,myTaskList);	
				myTaskView.getView().selectInPage(index/UiConstants.ENTRIES_PER_PAGE_DEFAULT, 
												  index%UiConstants.ENTRIES_PER_PAGE_DEFAULT); // select last
			} 
			prevList = cloneList(myTaskList); // Need to clone a new list because otherwise the task list is the same
		}
	}
	
	private ArrayList<Task> cloneList(ArrayList<Task> toClone) {
		ArrayList<Task> cloneList = new ArrayList<Task>();
		for ( int i = 0; i < toClone.size(); i ++ ) {
			cloneList.add(new Task(toClone.get(i)));
		}
		return cloneList;
	}
	
	private int findIndexOfAddedTask(ArrayList<Task> prevList, ArrayList<Task> currentList) {
		for ( int i = 0; i < prevList.size(); i++) {
			if ( prevList.get(i).equals(currentList.get(i)) == false ) {
				return i; // insertion point
			}
		}
		return currentList.size();
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
