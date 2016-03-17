package taskey.logic;

import taskey.parser.Parser;
import taskey.parser.TimeConverter;
import taskey.storage.History;
import taskey.storage.Storage;
import taskey.storage.StorageException;
import taskey.logic.Task;

import java.util.ArrayList;

import taskey.logic.LogicConstants.ListID;
import taskey.constants.UiConstants.ContentBox;
import taskey.logic.ProcessedObject;

/**
 * The Logic class handles the execution of user commands. It contains an internal memory of task lists 
 * which facilitate the addition, deletion and updating of tasks. Each time a command is executed, these lists
 * are modified and then saved to disk accordingly.
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
		history = storage.getHistory();

		//Get lists from Storage
		taskLists = cloneLists(storage.loadAllTasklists());
		ArrayList<Task> thisWeek = new ArrayList<Task>();
		taskLists.add(0, thisWeek); //Reserve first slot in taskLists for this week's task list

		//Update EXPIRED and THIS_WEEK list based on the current date and time
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		for (Task t : pendingList) {
			if (!t.getTaskType().equals("FLOATING")) {
				if (t.getDeadlineEpoch() < timeConverter.getCurrTime() || t.getEndDateEpoch() < timeConverter.getCurrTime()) {
					taskLists.get(ListID.EXPIRED.getIndex()).add(t);
				} else if (timeConverter.isSameWeek(t.getDeadlineEpoch(), timeConverter.getCurrTime())
						   || timeConverter.isSameWeek(t.getStartDateEpoch(), timeConverter.getCurrTime())) {
					thisWeek.add(t);
				}
			}
		}
		
		history.add(cloneLists(taskLists));
	}
	
	/**
	 * Returns a deep copy of all task lists.
	 */
	public ArrayList<ArrayList<Task>> getAllTaskLists() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized
		assert (!taskLists.contains(null)); //All lists should be instantiated
		
		return cloneLists(taskLists);
	}
	
	/**
	 * Returns a deep copy of THIS_WEEK list.
	 */
	public ArrayList<Task> getThisWeekList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> thisWeekList = taskLists.get(ListID.THIS_WEEK.getIndex());
		assert (thisWeekList != null);
		return cloneList(thisWeekList);
	}
	
	/**
	 * Returns a deep copy of PENDING list.
	 */
	public ArrayList<Task> getPendingList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		assert (pendingList != null);

		return cloneList(pendingList);
	}
	
	/**
	 * Returns a deep copy of EXPIRED list.
	 */
	public ArrayList<Task> getExpiredList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> expiredList = taskLists.get(ListID.EXPIRED.getIndex());
		assert (expiredList != null);

		return cloneList(expiredList);
	}
	
	/**
	 * Returns a deep copy of GENERAL list.
	 */
	public ArrayList<Task> getGeneralList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> generalList = taskLists.get(ListID.GENERAL.getIndex());
		assert (generalList != null);

		return cloneList(generalList);
	}
	
	/**
	 * Returns a deep copy of DEADLINE list.
	 */
	public ArrayList<Task> getDeadlineList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> deadlineList = taskLists.get(ListID.DEADLINE.getIndex());
		assert (deadlineList != null);

		return cloneList(deadlineList);
	}

	/**
	 * Returns a deep copy of EVENT list.
	 */
	public ArrayList<Task> getEventList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> eventList = taskLists.get(ListID.EVENT.getIndex());
		assert (eventList != null);

		return cloneList(eventList);
	}

	/**
	 * Returns a deep copy of COMPLETED list.
	 */
	public ArrayList<Task> getCompletedList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7); //taskLists should be fully initialized

		ArrayList<Task> completedList = taskLists.get(ListID.COMPLETED.getIndex());
		assert (completedList != null);

		return cloneList(completedList);
	}

	/**
	 * Executes the user supplied command.
	 *
	 * @param currentContent specifies the current tab that user is in.
	 * @param input			 the input String entered by the user
	 * @return               an object encapsulating the information required to update UI display
	 */
	public LogicFeedback executeCommand(ContentBox currentContent, String input) {
		ArrayList<ArrayList<Task>> copy = cloneLists(taskLists);
    	ProcessedObject po = parser.parseInput(input);
    	String command = po.getCommand();
    	Task task = po.getTask();
    	
    	if (input.equalsIgnoreCase("clear")) { //"clear" command is for developer testing only
			clearAllLists(copy);
			try {
				saveAllTasks(copy);
			} catch (Exception e) {
				return new LogicFeedback(cloneLists(taskLists), new ProcessedObject("CLEAR"), e); //Stub
			}
			
			taskLists = cloneLists(copy);
			return new LogicFeedback(copy, new ProcessedObject("CLEAR"), null); //Stub
    	}

    	switch (command) {
			case "ADD_FLOATING":
				return addFloating(copy, task, po);
				
			case "ADD_DEADLINE":
				return addDeadline(copy, task, po);

			case "ADD_EVENT":
				return addEvent(copy, task, po);

			case "DELETE_BY_INDEX":
				return deleteByIndex(copy, currentContent, po, po.getIndex());

			case "DELETE_BY_NAME":
				return deleteByName(copy, currentContent, po, task.getTaskName());

			case "VIEW":
				return new LogicFeedback(copy, po, null);

			case "SEARCH":
				return search(copy, po, po.getSearchPhrase());

			case "DONE_BY_INDEX":
				return doneByIndex(copy, currentContent, po, po.getIndex());

			case "DONE_BY_NAME":
				return doneByName(copy, currentContent, po, task.getTaskName());

			case "UPDATE_BY_INDEX_CHANGE_NAME":
				return updateByIndexChangeName(copy, currentContent, po, po.getIndex(), po.getNewTaskName());

			case "UPDATE_BY_INDEX_CHANGE_DATE":
				return updateByIndexChangeDate(copy, currentContent, po, po.getIndex(), task);
				
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				return updateByIndexChangeBoth(copy, currentContent, po, po.getIndex(), po.getNewTaskName(), task);

			case "UPDATE_BY_NAME_CHANGE_NAME":
				return updateByNameChangeName(copy, currentContent, po, task.getTaskName(), po.getNewTaskName());

			case "UPDATE_BY_NAME_CHANGE_DATE":
				return updateByNameChangeDate(copy, currentContent, po, task.getTaskName(), task);
				
			case "UPDATE_BY_NAME_CHANGE_BOTH":
				return updateByNameChangeBoth(copy, currentContent, po, task.getTaskName(), po.getNewTaskName(), task);

			case "UNDO":
				return undo(po);
				
			case "ERROR":
				return new LogicFeedback(copy, po, new Exception(po.getErrorType()));

			default:
				break;
		}

		return new LogicFeedback(cloneLists(taskLists), po, new Exception("Failed to execute command.")); 
	}

	//Adds a floating task to the relevant lists, and saves the updated lists to disk.
	LogicFeedback addFloating(ArrayList<ArrayList<Task>> copy, Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = copy.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task names not allowed
			return new LogicFeedback(copy, po,  new Exception("The task \"" + task.getTaskName() 
			                         + "\" already exists!"));
		}

		pendingList.add(task);
		copy.get(ListID.GENERAL.getIndex()).add(task);

		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}

	//Adds a deadline task to the relevant lists, and saves the updated lists to disk.
	LogicFeedback addDeadline(ArrayList<ArrayList<Task>> copy, Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = copy.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return new LogicFeedback(copy, po, new Exception("The task \"" + task.getTaskName() 
			                         + "\" already exists!"));
		}
		
		if (task.getDeadlineEpoch() < timeConverter.getCurrTime()) {
			copy.get(ListID.EXPIRED.getIndex()).add(task);
		} else {
			pendingList.add(task);
			copy.get(ListID.DEADLINE.getIndex()).add(task);

			if (timeConverter.isSameWeek(task.getDeadlineEpoch(), timeConverter.getCurrTime())) {
				copy.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Adds an event task to the relevant lists, and saves the updated lists to disk.
	LogicFeedback addEvent(ArrayList<ArrayList<Task>> copy, Task task, ProcessedObject po) {
		ArrayList<Task> pendingList = copy.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return new LogicFeedback(copy, po, new Exception("The task \"" + task.getTaskName() 
			                         + "\" already exists!"));
		}


		if (task.getEndDateEpoch() < timeConverter.getCurrTime()) {
			copy.get(ListID.EXPIRED.getIndex()).add(task);
		} else {
			pendingList.add(task);
			copy.get(ListID.EVENT.getIndex()).add(task);

			if (timeConverter.isSameWeek(task.getStartDateEpoch(), timeConverter.getCurrTime())) {
				copy.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}

		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Removes an indexed task from the current tab and saves the updated lists to disk.
	//TODO: support removal from the "ACTION" tab.
	LogicFeedback deleteByIndex(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, int taskIndex) {
		if (currentContent.equals(ContentBox.ACTION)) { 
			return new LogicFeedback(copy, po, new Exception("Cannot delete from this tab!"));
		}

    	ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
    	Task toDelete;

		try {
			toDelete = targetList.remove(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(copy, po, new Exception("\"" + taskIndex + "\" is not a valid index!"));
		}
		
		if (!currentContent.equals(ContentBox.EXPIRED)) {
			removeFromAllLists(copy, toDelete);
		}
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}

	
	//Removes an indexed task from the current tab and saves the updated lists to disk.
	//TODO: support removal from the "ACTION" tab.
	LogicFeedback deleteByName(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, 
			                   String taskName) {
		if (currentContent.equals(ContentBox.ACTION)) { 
			return new LogicFeedback(copy, po, new Exception("Cannot delete from this tab!"));
		}
		
    	ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
		Task toDelete = new Task(taskName);

		//Named task does not exist in the list
		if (!targetList.remove(toDelete)) {
			return new LogicFeedback(copy, po, new Exception("\"" + taskName + "\" not found in this list!"));
		}
		
		if (!currentContent.equals(ContentBox.EXPIRED)) {
			removeFromAllLists(copy, toDelete);
		}
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Search for all pending Tasks whose names contain searchPhrase. searchPhrase is not case sensitive.
	//TODO: search list includes expired and completed tasks as well
	LogicFeedback search(ArrayList<ArrayList<Task>> copy, ProcessedObject po, String searchPhrase) {
		if (searchPhrase.equals("")) {
			return new LogicFeedback(copy, po, new Exception("Search phrase cannot be empty!"));
		}
		
		ArrayList<ArrayList<Task>> matches = new ArrayList<ArrayList<Task>>();
		while (matches.size() < 7) {
			matches.add(new ArrayList<Task>());
		}
		
		ArrayList<Task> pendingList = copy.get(ListID.PENDING.getIndex());
		for (Task t : pendingList) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				matches.get(0).add(t);
			}
		}

		return new LogicFeedback(matches, po, null);
	}
	
	//Marks an indexed task from the current tab as done and saves the updated lists to disk.
	//TODO: support "done" from the "ACTION" tab. 
	LogicFeedback doneByIndex(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, int taskIndex) {
		Task toMarkAsDone = null;
		if (currentContent.equals(ContentBox.THIS_WEEK)) {
			try {
				toMarkAsDone = copy.get(ListID.THIS_WEEK.getIndex()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return new LogicFeedback(copy, po, new Exception("\"" + taskIndex + "\" is not a valid index!"));
			}
		} else if (currentContent.equals(ContentBox.PENDING)) {
			try {
				toMarkAsDone = copy.get(ListID.PENDING.getIndex()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return new LogicFeedback(copy, po, new Exception("\"" + taskIndex + "\" is not a valid index!"));
			}
		} else { //"done" command is not allowed in tabs other than "this week" or "pending"
			return new LogicFeedback(copy, po, new Exception("Cannot use \"done\" command from this tab!"));
		}

		removeFromAllLists(copy, toMarkAsDone);
		copy.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		
		try {
			saveAllTasks(cloneLists(copy));
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}

	
	//Marks an named task from the current tab as done and saves the updated lists to disk.
	//TODO: support "done" from the "ACTION" tab. 
	LogicFeedback doneByName(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, String taskName) {
		Task toMarkAsDone = new Task(taskName);

		//"done" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(copy, po, new Exception("Cannot use \"done\" command from this tab!"));
		}

		//Named task does not exist in the list
		if (!copy.get(ListID.PENDING.getIndex()).contains(toMarkAsDone)) {
			return new LogicFeedback(copy, po, new Exception(taskName + " does not exist in this tab!"));
		}

		removeFromAllLists(copy, toMarkAsDone);
		copy.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Updates an indexed task's name on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByIndexChangeName(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, int taskIndex, 
			                                      String newTaskName) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(copy, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(copy, po, new Exception("\"" + taskIndex + "\" is not a valid index!"));
		}

		updateAllLists(copy, toUpdate.getTaskName(), newTaskName);
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}

	//Updates an indexed task's date on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByIndexChangeDate(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, int taskIndex, 
			                              Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(copy, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(copy, po, new Exception("\"" + taskIndex + "\" is not a valid index!"));
		}

		changedTask.setTaskName(toUpdate.getTaskName());
		updateAllLists(copy, toUpdate.getTaskName(), changedTask);
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Updates an indexed task's name and date on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByIndexChangeBoth(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, int taskIndex,
			                              String newTaskName, Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(copy, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			return new LogicFeedback(copy, po, new Exception("\"" + taskIndex + "\" is not a valid index!"));
		}

		changedTask.setTaskName(newTaskName);
		updateAllLists(copy, toUpdate.getTaskName(), changedTask);
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Updates an named task's name on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByNameChangeName(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, String oldTaskName, 
			                             String newTaskName) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(copy, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
		Task toUpdate = new Task(oldTaskName);

		if (!targetList.contains(toUpdate)) {
			return new LogicFeedback(copy, po, new Exception(oldTaskName + " not found in this list!"));
		}

		updateAllLists(copy, toUpdate.getTaskName(), newTaskName);
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Updates an named task's date on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByNameChangeDate(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, String taskName, 
			                             Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(copy, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
		Task toUpdate = new Task(taskName);

		if (!targetList.contains(toUpdate)) {
			return new LogicFeedback(copy, po, new Exception(taskName + " not found in this list!"));
		}

		updateAllLists(copy, taskName, changedTask);
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}
	
	//Updates an named task's name and date on the current tab and saves the updated lists to disk.
	//TODO: support "set" from the "ACTION" tab. 
	LogicFeedback updateByNameChangeBoth(ArrayList<ArrayList<Task>> copy, ContentBox currentContent, ProcessedObject po, String oldTaskName,
			                             String newTaskName, Task changedTask) {
		//"set" command is not allowed in tabs other than "this week" or "pending"
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			return new LogicFeedback(copy, po, new Exception("Cannot use \"set\" command from this tab!"));
		}

		ArrayList<Task> targetList = getListFromContentBox(copy, currentContent);
		Task toUpdate = new Task(oldTaskName);

		if (!targetList.contains(toUpdate)) {
			return new LogicFeedback(copy, po, new Exception(oldTaskName + " not found in this list!"));
		}

		changedTask.setTaskName(newTaskName);
		updateAllLists(copy, oldTaskName, changedTask);
		
		try {
			saveAllTasks(copy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(copy);
		return new LogicFeedback(copy, po, null);
	}

	//Undo the last change to the task lists.
	LogicFeedback undo(ProcessedObject po) {
		assert(!history.isEmpty()); //History must always have at least one item, which is the current superlist
		ArrayList<ArrayList<Task>> currentSuperList = history.pop();
		ArrayList<ArrayList<Task>> previousSuperList = history.peek();
		
		if (previousSuperList == null) {
			history.add(currentSuperList);
			return new LogicFeedback(taskLists, po, new Exception("Nothing to undo!"));
		}
		
		try {
			saveAllTasks(previousSuperList);
		} catch (Exception e) {
			history.add(currentSuperList);
			return new LogicFeedback(currentSuperList, po, e);
		}
		
		taskLists = cloneLists(previousSuperList);
		return new LogicFeedback(previousSuperList, po, null);
	}
	
	//Creates a deep copy of the original list.
	private ArrayList<Task> cloneList(ArrayList<Task> list) {
		ArrayList<Task> copy = new ArrayList<Task>();
		for (Task t : list) {
			copy.add(t.getDuplicate());
		}
		
		return copy;
	}
	
	//Creates a deep copy of the original lists.
	ArrayList<ArrayList<Task>> cloneLists(ArrayList<ArrayList<Task>> lists) {
		assert(lists.size() == 7);
		ArrayList<ArrayList<Task>> copy = new ArrayList<ArrayList<Task>>();
		
		for (int i = 0; i < lists.size(); i++) {
			copy.add(cloneList(lists.get(i)));
		}
		
		return copy;
	}
	
	//Gets the list corresponding to the given ContentBox.
	private ArrayList<Task> getListFromContentBox(ArrayList<ArrayList<Task>> taskLists, ContentBox currentContent) {
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
	private void removeFromAllLists(ArrayList<ArrayList<Task>> taskLists, Task toRemove) {
		taskLists.get(ListID.THIS_WEEK.getIndex()).remove(toRemove);
		taskLists.get(ListID.PENDING.getIndex()).remove(toRemove);
		taskLists.get(ListID.GENERAL.getIndex()).remove(toRemove);
		taskLists.get(ListID.DEADLINE.getIndex()).remove(toRemove);
		taskLists.get(ListID.EVENT.getIndex()).remove(toRemove);
	}

	private void clearAllLists(ArrayList<ArrayList<Task>> taskLists) {
		for (int i = 0; i < taskLists.size(); i++) {
			taskLists.get(i).clear();
		}
	}

	//Change the Task whose name is oldTaskName, if any, to have a new name newTaskName.
	//Also updates all lists containing the updated Task.
	private void updateAllLists(ArrayList<ArrayList<Task>> taskLists, String oldTaskName, String newTaskName) {
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
	private void updateAllLists(ArrayList<ArrayList<Task>> taskLists, String oldTaskName, Task changedTask) {
		Task toRemove = new Task(oldTaskName);
		removeFromAllLists(taskLists, toRemove);

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
	private void saveAllTasks(ArrayList<ArrayList<Task>> taskLists) throws Exception {
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