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
    	String errorType = po.getErrorType(); //Only used for invalid commands
    	String searchPhrase = po.getSearchPhrase(); //Only used for search commands
    	String newTaskName = po.getNewTaskName(); //Only used for commands that change the name of a task


    	System.out.println("Command: " + command);

    	switch (command) {
			case "ADD_FLOATING":
				return addFloating(task, po);
				
			case "ADD_DEADLINE":
				return addDeadline(task, po);

			case "ADD_EVENT":
				return addEvent(task, po);

			case "DELETE_BY_INDEX":
				return deleteByIndex(currentContent, po, taskIndex);

			case "DELETE_BY_NAME":
				return deleteByName(currentContent, po, task.getTaskName());

			case "VIEW":
				return new LogicFeedback(taskLists, po, null);

			case "SEARCH":
				return search(po, searchPhrase);

			case "DONE_BY_INDEX":
				return doneByIndex(currentContent, po, taskIndex);

			case "DONE_BY_NAME":
				return doneByName(currentContent, po, task.getTaskName());

			case "UPDATE_BY_INDEX_CHANGE_NAME":
				return updateByIndexChangeName(currentContent, po, taskIndex, newTaskName);

			case "UPDATE_BY_INDEX_CHANGE_DATE":
				return updateByIndexChangeDate(currentContent, po, taskIndex, task);
				
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				return updateByIndexChangeBoth(currentContent, po, taskIndex, newTaskName, task);

			case "UPDATE_BY_NAME_CHANGE_NAME":
				return updateByNameChangeName(currentContent, po, task.getTaskName(), newTaskName);

			case "UPDATE_BY_NAME_CHANGE_DATE":
				return updateByNameChangeDate(currentContent, po, task.getTaskName(), task);

			case "UNDO":
				//TODO
				break;
				
			case "ERROR":
				//TODO:
				break;

			default:
				break;
		}

		return new LogicFeedback(taskLists, po, new Exception("Failed to execute command.")); 
	}

	//Adds a floating task to the relevant lists, and saves the updated lists to disk.
	LogicFeedback addFloating(Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task names not allowed
			return new LogicFeedback(taskLists, po,  new Exception("The task "
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

	//Adds a deadline task to the relevant lists, and saves the updated lists to disk.
	LogicFeedback addDeadline(Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return new LogicFeedback(taskLists, po, new Exception("The task "
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
	
	//Adds an event task to the relevant lists, and saves the updated lists to disk.
	LogicFeedback addEvent(Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return new LogicFeedback(taskLists, po, new Exception("The task "
								     + task.getTaskName() + " already exists!"));
		}

		pendingList.add(task);
		taskLists.get(ListID.EVENT.getIndex()).add(task);

		if (timeConverter.isSameWeek(task.getStartDateEpoch(), timeConverter.getCurrTime())) {
			taskLists.get(ListID.THIS_WEEK.getIndex()).add(task);
		}

		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}
	
	//Removes an indexed task from the current tab and saves the updated lists to disk.
	//TODO: support removal from the "ACTION" tab.
	LogicFeedback deleteByIndex(ContentBox currentContent, ProcessedObject po, int taskIndex) {
		//"del" command only allowed in THIS_WEEK or PENDING tab
		if (!currentContent.equals(ContentBox.THIS_WEEK) && !currentContent.equals(ContentBox.PENDING)) { 
			return new LogicFeedback(taskLists, po, new Exception("Cannot delete from this tab!"));
		}

    	ArrayList<Task> targetList = getListFromContentBox(currentContent);
    	Task toDelete;

		try {
			toDelete = targetList.remove(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(taskLists, po, new Exception("Index out of bounds!"));
		}

		removeFromAllLists(toDelete);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}

	
	//Removes an indexed task from the current tab and saves the updated lists to disk.
	//TODO: support removal from the "ACTION" tab.
	LogicFeedback deleteByName(ContentBox currentContent, ProcessedObject po, 
			                           String taskName) {
		//"del" command only allowed in THIS_WEEK or PENDING tab
		if (!currentContent.equals(ContentBox.THIS_WEEK) && !currentContent.equals(ContentBox.PENDING)) { 
			return new LogicFeedback(taskLists, po, new Exception("Cannot delete from this tab!"));
		}
		
    	ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toDelete = new Task(taskName);

		//Named task does not exist in the list
		if (!targetList.contains(toDelete)) {
			return new LogicFeedback(taskLists, po, new Exception(taskName 
					                                              + " not found in this list!"));
		}

		removeFromAllLists(toDelete);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}
	
	
	//Search for all pending Tasks whose names contain searchPhrase. searchPhrase is not case sensitive.
	LogicFeedback search(ProcessedObject po, String searchPhrase) {
		if (searchPhrase.equals("")) {
			return new LogicFeedback(taskLists, po, new Exception("Search phrase cannot be empty!"));
		}
		
		ArrayList<ArrayList<Task>> matches = new ArrayList<ArrayList<Task>>();
		while (matches.size() < 7) {
			matches.add(new ArrayList<Task>());
		}
		
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		for (Task t : pendingList) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				matches.get(0).add(t);
			}
		}

		return new LogicFeedback(matches, po, null);
	}

	
	//Marks an indexed task from the current tab as done and saves the updated lists to disk.
	//TODO: support "done" from the "ACTION" tab. 
	LogicFeedback doneByIndex(ContentBox currentContent, ProcessedObject po, int taskIndex) {
		Task toMarkAsDone = null;
		if (currentContent.equals(ContentBox.THIS_WEEK)) {
			try {
				toMarkAsDone = taskLists.get(ListID.THIS_WEEK.getIndex()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return new LogicFeedback(taskLists, po, new Exception("Index out of bounds!"));
			}
		} else if (currentContent.equals(ContentBox.PENDING)) {
			try {
				toMarkAsDone = taskLists.get(ListID.PENDING.getIndex()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return new LogicFeedback(taskLists, po, new Exception("Index out of bounds!"));
			}
		} else { //"done" command is not allowed in tabs other than "this week" or "pending"
			return new LogicFeedback(taskLists, po, new Exception("Cannot use \"done\" command from this tab!"));
		}

		removeFromAllLists(toMarkAsDone);
		taskLists.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}

	
	//Marks an named task from the current tab as done and saves the updated lists to disk.
	//TODO: support "done" from the "ACTION" tab. 
	LogicFeedback doneByName(ContentBox currentContent, ProcessedObject po, String taskName) {
		Task toMarkAsDone = new Task(taskName);

		//"done" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(taskLists, po, new Exception("Cannot use \"done\" command from this tab!"));
		}

		//Named task does not exist in the list
		if (!taskLists.get(ListID.PENDING.getIndex()).contains(toMarkAsDone)) {
			return new LogicFeedback(taskLists, po, new Exception(taskName + " does not exist in this tab!"));
		}

		removeFromAllLists(toMarkAsDone);
		taskLists.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}
	
	//Updates an indexed task's name on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByIndexChangeName(ContentBox currentContent, ProcessedObject po, int taskIndex, 
			                                      String newTaskName) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(taskLists, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(taskLists, po, new Exception("Index out of bounds!"));
		}

		updateAllLists(toUpdate.getTaskName(), newTaskName);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}

	//Updates an indexed task's date on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByIndexChangeDate(ContentBox currentContent, ProcessedObject po, int taskIndex, 
			                              Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(taskLists, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(taskLists, po, new Exception("Index out of bounds!"));
		}

		changedTask.setTaskName(toUpdate.getTaskName());
		updateAllLists(toUpdate.getTaskName(), changedTask);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}
	
	LogicFeedback updateByIndexChangeBoth(ContentBox currentContent, ProcessedObject po, int taskIndex,
			                              String newTaskName, Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(taskLists, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(taskLists, po, new Exception("Index out of bounds!"));
		}

		changedTask.setTaskName(newTaskName);
		updateAllLists(toUpdate.getTaskName(), changedTask);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}
	
	//Updates an named task's name on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByNameChangeName(ContentBox currentContent, ProcessedObject po, String oldTaskName, 
			                             String newTaskName) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(taskLists, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toUpdate = new Task(oldTaskName);

		if (!targetList.contains(toUpdate)) {
			return new LogicFeedback(taskLists, po, new Exception(oldTaskName + " not found in this list!"));
		}

		updateAllLists(toUpdate.getTaskName(), newTaskName);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}
	
	//Updates an named task's date on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByNameChangeDate(ContentBox currentContent, ProcessedObject po, String taskName, 
			                             Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(taskLists, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(currentContent);
		Task toUpdate = new Task(taskName);

		if (!targetList.contains(toUpdate)) {
			return new LogicFeedback(taskLists, po, new Exception(taskName + " not found in this list!"));
		}

		updateAllLists(taskName, changedTask);
		
		try {
			saveAllTasks();
		} catch (Exception e) {
			return new LogicFeedback(taskLists, po, e);
		}

		return new LogicFeedback(taskLists, po, null);
	}


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