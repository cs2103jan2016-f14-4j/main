package taskey.ui.content;

import java.util.ArrayList;
import java.util.Collections;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import taskey.constants.UiConstants;
import taskey.messenger.Task;
import taskey.ui.content.views.UiTaskView;

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
			myTaskView.clear();
			
			int totalPages = (int) Math.ceil(myTaskList.size()/1.0/
											 UiConstants.ENTRIES_PER_PAGE_DEFAULT); // convert to double	
			myTaskView.createPaginationGrids(myTaskList,totalPages);

			updateSelection(myTaskList);		
		}
	}
	
	private void updateSelection(ArrayList<Task> myTaskList) {
		ArrayList<Task> cloned = cloneList(myTaskList); // as list is going to be changed
		if ( prevList != null ) { 
			int index = 0;
			if ( myTaskList.size() >= prevList.size()) {
				index = findIndexOfModifiedTask(prevList,cloned);	// modification of a task
				if ( index == -1) {
					// no op as lists are the same
				} else {
					myTaskView.getView().selectInPage(index/UiConstants.ENTRIES_PER_PAGE_DEFAULT, 
							  index%UiConstants.ENTRIES_PER_PAGE_DEFAULT); 
				}
			}
		} 
		prevList = myTaskList; // note that myTaskList is cloned from Logic
	}
	
	private ArrayList<Task> cloneList(ArrayList<Task> toClone) {
		ArrayList<Task> cloneList = new ArrayList<Task>();
		for ( int i = 0; i < toClone.size(); i ++ ) {
			cloneList.add(new Task(toClone.get(i)));
		}
		return cloneList;
	}
	
	/**
	 * This method finds the index of the modified task to select in a page
	 * Note that this method changes the currentList passed in
	 * @param prevList
	 * @param currentList
	 * @return index
	 */
	private int findIndexOfModifiedTask(ArrayList<Task> prevList, ArrayList<Task> currentList) {
		int initialSize = currentList.size();
		for ( int i = 0; i < prevList.size(); i++) {
			Task task = prevList.get(i);
			int indexOfTask = currentList.indexOf(task); 
			if ( indexOfTask != -1 ) {
				currentList.set(indexOfTask, null); // we pick out elements, but retain the array
			}
		}
		for ( int i = 0; i < currentList.size(); i++ )  {
			if ( currentList.get(i) != null ) { // return first mismatch
				return i;
			}
		}
		currentList.removeAll(Collections.singleton(null));
		if ( currentList.size() == 0) { // all tasks match
			return -1;
		} else {
			return initialSize; // return newly added task
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
