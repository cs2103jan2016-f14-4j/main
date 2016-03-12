package taskey.logic;

import taskey.parser.Parser;
import taskey.parser.TimeConverter;
import taskey.storage.History;
import taskey.storage.Storage;
import taskey.storage.StorageException;
import taskey.ui.UiConstants.ContentBox;
import taskey.logic.Task;

import java.util.ArrayList;

import taskey.logic.LogicConstants.ListID;
import taskey.logic.ProcessedObject;

/**
 * TODO: class description
 *
 * @author Hubert Wong
 */
public class Logic {
	private Parser parser;
	private TimeConverter timeConverter;
	private Storage storage;
	private History history;
	private ArrayList<ArrayList<Task>> taskLists; //Can be moved to a LogicMemory component next time
	
	public Logic() {
		parser = new Parser();
		timeConverter = new TimeConverter();
		storage = new Storage();
		history = new History();

		//Get lists from Storage
		taskLists = new ArrayList<ArrayList<Task>>();
		taskLists = storage.loadAllTasklists();
		ArrayList<Task> thisWeek = new ArrayList<Task>();
		taskLists.add(0, thisWeek); //Reserve first slot in taskLists for this week's task list

		//Update THIS_WEEK list based on the current date and time
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		for (Task t : pendingList) {
			//Add pending deadline or event tasks to the weekly list if they belong to the same week as
			//the current week
			if (timeConverter.isSameWeek(t.getDeadlineEpoch(), timeConverter.getCurrTime())
				|| timeConverter.isSameWeek(t.getStartDateEpoch(), timeConverter.getCurrTime())) {
				thisWeek.add(t);
			}
		}
		
		history.add(taskLists);
	}

	public ArrayList<ArrayList<Task>> getAllTaskLists() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized
		assert (!taskLists.contains(null)); //All lists should be instantiated
		
		return taskLists;
	}

	public ArrayList<Task> getThisWeekList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> thisWeekList = taskLists.get(ListID.THIS_WEEK.getIndex());
		assert (thisWeekList != null);
		return thisWeekList;
	}

	public ArrayList<Task> getPendingList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		assert (pendingList != null);

		return pendingList;
	}

	public ArrayList<Task> getExpiredList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> expiredList = taskLists.get(ListID.EXPIRED.getIndex());
		assert (expiredList != null);

		return expiredList;
	}

	public ArrayList<Task> getGeneralList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> generalList = taskLists.get(ListID.GENERAL.getIndex());
		assert (generalList != null);

		return generalList;
	}

	public ArrayList<Task> getDeadlineList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> deadlineList = taskLists.get(ListID.DEADLINE.getIndex());
		assert (deadlineList != null);

		return deadlineList;
	}

	public ArrayList<Task> getEventList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> eventList = taskLists.get(ListID.EVENT.getIndex());
		assert (eventList != null);

		return eventList;
	}

	public ArrayList<Task> getCompletedList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> completedList = taskLists.get(ListID.COMPLETED.getIndex());
		assert (completedList != null);

		return completedList;
	}

	/**
	 * Executes the user supplied command.
	 *
	 * @param currentContent specifies the current tab that user is in.
	 * @param input			 the input String entered by the user
	 * @return               an object encapsulating the information required to update UI display
	 */
	public LogicFeedback executeCommand(ContentBox currentContent, String input) {
    	if (input.equalsIgnoreCase("clear")) { //"clear" command is for developer testing only
			clearAllLists();
			try {
				saveAllTasks();
			} catch (Exception e) {
				return new LogicFeedback(taskLists, new ProcessedObject("CLEAR"), e); //Stub
			}
			return new LogicFeedback(taskLists, new ProcessedObject("CLEAR"), null); //Stub
    	}

    	ProcessedObject po = parser.parseInput(input);
    	String command = po.getCommand();
    	Task task = po.getTask();
    	int taskIndex = po.getIndex(); //Only used for commands that specify the index of a task
    	String viewType = po.getViewType(); //Only used for view commands
    	String errorType = po.getErrorType(); //Only used for invalid commands
    	String searchPhrase = po.getSearchPhrase(); //Only used for search commands
    	String newTaskName = po.getNewTaskName(); //Only used for commands that change the name of a task
    	Task done;
    	ArrayList<Task> doneList;

    	System.out.println("Command: " + command);

    	switch (command) {
			case "ADD_FLOATING":
				return addFloating(task, po);
				
			case "ADD_DEADLINE":
				return addDeadline(task, po);

			/*case "ADD_EVENT":
				statusCode = addEvent(task);
				break;

			case "DELETE_BY_INDEX":
				statusCode = deleteByIndex(currentContent, taskIndex);
				break;

			case "DELETE_BY_NAME":
				statusCode = deleteByName(currentContent, task.getTaskName());
				break;

			case "VIEW":
				statusCode = view(viewType);
				break;

			case "SEARCH":
				statusCode = search(searchPhrase);
				break;

			case "DONE_BY_INDEX":
				statusCode = doneByIndex(currentContent, taskIndex);
				break;

			case "DONE_BY_NAME":
				statusCode = doneByName(currentContent, task.getTaskName());
				break;

			case "UPDATE_BY_INDEX_CHANGE_NAME":
				statusCode = updateByIndexChangeName(currentContent, taskIndex, newTaskName);
				break;

			case "UPDATE_BY_INDEX_CHANGE_DATE":
				statusCode = updateByIndexChangeDate(currentContent, taskIndex, task);
				break;

			case "UPDATE_BY_NAME_CHANGE_NAME":
				toUpdate = getTaskByName(targetList, task.getTaskName());
				toUpdate.setTaskName(newTaskName);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;

			case "UPDATE_BY_NAME_CHANGE_DATE":
				toUpdate = getTaskByName(targetList, task.getTaskName());
				targetList.remove(toUpdate);
				targetList.add(task);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;

			case "UNDO":
				break; */

			default:
				break;
		}

		return new LogicFeedback(new ArrayList<ArrayList<Task>>(), po,
                				 new Exception("Failed to execute command.")); //Stub
	}

	//Adds a floating task to the relevant lists, and saves the updated lists to disk.
	protected LogicFeedback addFloating(Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task names not allowed
			return new LogicFeedback(new ArrayList<ArrayList<Task>>(), po,  new Exception("The task "
		                             + task.getTaskName() + " already exists!"));
		}

		pendingList.add(task);
		taskLists.get(ListID.GENERAL.getIndex()).add(task);

		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}

	//Adds a deadline task to the relevant lists.
	private LogicFeedback addDeadline(Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return new LogicFeedback(new ArrayList<ArrayList<Task>>(), po, new Exception("The task "
		                             + task.getTaskName() + " already exists!"));
		}

		pendingList.add(task);
		taskLists.get(ListID.DEADLINE.getIndex()).add(task);

		if (timeConverter.isSameWeek(task.getDeadlineEpoch(), timeConverter.getCurrTime())) {
			taskLists.get(ListID.THIS_WEEK.getIndex()).add(task);
		}
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}
	/*
	//Updates UI with a new event task. Returns a status code reflecting outcome of command execution.
	private int addEvent(Task task) {
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return -1; //Stub
		}

		pendingList.add(task);
		taskLists.get(ListID.EVENT.getIndex()).add(task);

		if (timeConverter.isSameWeek(task.getStartDateEpoch(), timeConverter.getCurrTime())) {
			taskLists.get(ListID.THIS_WEEK.getIndex()).add(task);
		}

		refreshUiTabDisplay();
		refreshUiCategoryDisplay();
		uiController.displayTabContents(ContentBox.PENDING.getValue());

		return 0; //Stub
	}

	//Removes an indexed task from the "PENDING" tab.
	//TODO: support removal from the "THIS_WEEK" tab.
	private int deleteByIndex(ContentBox currentContent, int taskIndex) {
		if (!currentContent.equals(ContentBox.PENDING)) { //"del" command only allowed in pending tab
			return -1; //Stub
		}

    	ArrayList<Task> targetList = getListFromContentBox(currentContent);
    	Task toDelete;

		try {
			toDelete = targetList.remove(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}

		removeFromAllLists(toDelete);
		refreshUiTabDisplay();
		refreshUiCategoryDisplay();

		return 0; //Stub
	}

	//Removes a named task from the "PENDING" tab.
	//TODO: support removal from the "THIS_WEEK" tab.
	private int deleteByName(ContentBox currentContent, String taskName) {
		if (!currentContent.equals(ContentBox.PENDING)) { //"del" command only allowed in pending tab
			return -1; //Stub
		}

		Task toDelete = new Task(taskName);

		//Named task does not exist in the list
		if (!taskLists.get(ListID.PENDING.getIndex()).contains(toDelete)) {
			return -1; //Stub
		}

		removeFromAllLists(toDelete);
		refreshUiTabDisplay();
		refreshUiCategoryDisplay();

		return 0; //Stub
	}

	//View the type of task specified by viewType.
	private int view(String viewType) {
		if (viewType.equals("GENERAL")) {
			uiController.updateActionDisplay(taskLists.get(ListID.GENERAL.getIndex()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		} else if (viewType.equals("DEADLINES")) {
			uiController.updateActionDisplay(taskLists.get(ListID.DEADLINE.getIndex()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		} else if (viewType.equals("EVENTS")) {
			uiController.updateActionDisplay(taskLists.get(ListID.EVENT.getIndex()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		} else if (viewType.equals("ARCHIVE")) {
			uiController.updateActionDisplay(taskLists.get(ListID.COMPLETED.getIndex()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		}

		return 0; //Stub
	}

	//Search for all pending Tasks whose names contain searchPhrase. searchPhrase is not case sensitive.
	private int search(String searchPhrase) {
		ArrayList<Task> matches = new ArrayList<Task>();
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());

		for (Task t : pendingList) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				matches.add(t);
			}
		}

		uiController.updateActionDisplay(matches, ActionListMode.TASKLIST);
		uiController.displayTabContents(ContentBox.ACTION.getValue());

		return 0; //Stub
	}

	//Mark an indexed task as done by adding it to the "completed" list and removing it from all the
	//lists of incomplete tasks. Also updates the UI display to reflect the updated lists.
	private int doneByIndex(ContentBox currentContent, int taskIndex) {
		Task toMarkAsDone = null;
		if (currentContent.equals(ContentBox.THIS_WEEK)) {
			try {
				toMarkAsDone = taskLists.get(ListID.THIS_WEEK.getIndex()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return -1;
			}
		} else if (currentContent.equals(ContentBox.PENDING)) {
			try {
				toMarkAsDone = taskLists.get(ListID.PENDING.getIndex()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return -1;
			}
		} else { //"done" command is not allowed in tabs other than "this week" or "pending"
			return -1;
		}

		removeFromAllLists(toMarkAsDone);
		taskLists.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		refreshUiTabDisplay();
		refreshUiCategoryDisplay();

		return 0;
	}

	//Mark a named task as done by adding it to the "completed" list and removing it from all the
	//lists of incomplete tasks. Also updates the UI display to reflect the updated lists.
	private int doneByName(ContentBox currentContent, String taskName) {
		Task toMarkAsDone = new Task(taskName);

		//"done" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return -1;
		}

		//Named task does not exist in the list
		if (!taskLists.get(ListID.PENDING.getIndex()).contains(toMarkAsDone)) {
			return -1;
		}

		removeFromAllLists(toMarkAsDone);
		taskLists.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		refreshUiTabDisplay();
		refreshUiCategoryDisplay();

		return 0;
	}

	//Update an indexed task's name with newTaskName.
	//Also updates the UI display to reflect the updated lists.
	private int updateByIndexChangeName(ContentBox currentContent, int taskIndex, String newTaskName) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return -1;
		}

		ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}

		updateAllLists(toUpdate.getTaskName(), newTaskName);
		refreshUiTabDisplay();

		return 0; //Stub
	}

	//Replace the indexed Task with a Task that has the changed date.
	//Also updates the UI display to reflect the updated lists.
	private int updateByIndexChangeDate(ContentBox currentContent, int taskIndex, Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return -1;
		}

		ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return -1;
		}

		changedTask.setTaskName(toUpdate.getTaskName());
		updateAllLists(toUpdate.getTaskName(), changedTask);
		refreshUiTabDisplay();
		refreshUiCategoryDisplay();

		return 0; //Stub
	}*/

	//Gets the list corresponding to the given ContentBox.
	private ArrayList<Task> getListFromContentBox(ContentBox currentContent) {
		ArrayList<Task> targetList = null;
		switch (currentContent) {
			case PENDING:
				targetList = taskLists.get(ListID.PENDING.getIndex());
				break;
			case EXPIRED:
				targetList = taskLists.get(ListID.EXPIRED.getIndex());
				break;
			case THIS_WEEK:
				targetList = taskLists.get(ListID.THIS_WEEK.getIndex());
				break;
			/*case ACTION:
				targetList = lists.get(ListsID.ACTION.getValue());
				break;*/
			default:
				System.out.println("ContentBox invalid");
		}

		return targetList;
	}
	/*
	//Refresh all UI tabs except the "ACTION" tab.
	private void refreshUiTabDisplay() {
		uiController.updateDisplay(taskLists.get(ListID.THIS_WEEK.getIndex()), ContentBox.THIS_WEEK);
		uiController.updateDisplay(taskLists.get(ListID.PENDING.getIndex()), ContentBox.PENDING);
		uiController.updateDisplay(taskLists.get(ListID.EXPIRED.getIndex()), ContentBox.EXPIRED);
	}

	private void refreshUiCategoryDisplay() {
		categorySizes.set(CategoryID.GENERAL.getIndex(), taskLists.get(ListID.GENERAL.getIndex()).size());
		categorySizes.set(CategoryID.DEADLINE.getIndex(), taskLists.get(ListID.DEADLINE.getIndex()).size());
		categorySizes.set(CategoryID.EVENT.getIndex(), taskLists.get(ListID.EVENT.getIndex()).size());
		categorySizes.set(CategoryID.COMPLETED.getIndex(), taskLists.get(ListID.COMPLETED.getIndex()).size());
		uiController.updateCategoryDisplay(categoryList, categorySizes, colorList);
	}*/

	/*
	//Returns the first Task whose name matches the given name, or null otherwise.
	private Task getTaskByName(ArrayList<Task> targetList, String name ) {
		for (int i = 0; i < targetList.size(); i++) {
			Task theTask = targetList.get(i);
			if ( theTask.getTaskName().equals(name)) {
				return theTask;
			}
		}

		return null;
	}*/

	//Removes the given Task from all existing lists except the "EXPIRED" and "COMPLETED" lists.
	//The intended Task may not be removed if duplicate Task names are allowed.
	private void removeFromAllLists(Task toRemove) {
		taskLists.get(ListID.THIS_WEEK.getIndex()).remove(toRemove);
		taskLists.get(ListID.PENDING.getIndex()).remove(toRemove);
		taskLists.get(ListID.GENERAL.getIndex()).remove(toRemove);
		taskLists.get(ListID.DEADLINE.getIndex()).remove(toRemove);
		taskLists.get(ListID.EVENT.getIndex()).remove(toRemove);
	}

	private void clearAllLists() {
		for (int i = 0; i < taskLists.size(); i++) {
			taskLists.get(i).clear();
		}
	}

	//Change the Task whose name is oldTaskName, if any, to have a new name newTaskName.
	//Also updates all lists containing the updated Task.
	private void updateAllLists(String oldTaskName, String newTaskName) {
		Task t = new Task(oldTaskName);

		for (int i = 0; i < taskLists.size(); i++) {
			int taskIndex = taskLists.get(i).indexOf(t);

			if (taskIndex != -1) { //List contains the Task
				taskLists.get(i).get(taskIndex).setTaskName(newTaskName);
			}
		}
	}

	//Replace the Task whose name is oldTaskName with another task changedTask.
	//Also updates all lists containing the updated Task.
	private void updateAllLists(String oldTaskName, Task changedTask) {
		Task toRemove = new Task(oldTaskName);
		removeFromAllLists(toRemove);

		for (int i = 0; i < taskLists.size(); i++) {
			if (belongsToList(changedTask, i)) {
				taskLists.get(i).add(changedTask);
			}
		}
	}

	//Returns true if and only if a given task should be classified under the list specified by listIndex.
	private boolean belongsToList(Task task, int listIndex) {
		String taskType = task.getTaskType();

		if (listIndex == ListID.THIS_WEEK.getIndex()) {
			return (timeConverter.isSameWeek(task.getDeadlineEpoch(), timeConverter.getCurrTime())
					|| timeConverter.isSameWeek(task.getStartDateEpoch(), timeConverter.getCurrTime()));
		} else if (listIndex == ListID.PENDING.getIndex()) {
			return true;
		} else if (listIndex == ListID.GENERAL.getIndex()) {
			return (taskType == "FLOATING");
		} else if (listIndex == ListID.DEADLINE.getIndex()) {
			return (taskType == "DEADLINE");
		} else if (listIndex == ListID.EVENT.getIndex()) {
			return (taskType == "EVENT");
		}

		return false; //Stub
	}

	//Save all task lists to Storage. If the save failed, the task lists will be reverted to the states
	//they were in before they were modified.
	private void saveAllTasks() throws Exception {
		try {
			storage.saveAllTasklists(taskLists);
			System.out.println("All tasklists saved.");
		} catch (StorageException se) {
			System.out.println(se.getMessage());
			taskLists = se.getLastModifiedTasklists(); //Dylan: this hasn't been tested. Will test next time.
			throw new Exception (se.getMessage());
		}
	}
}