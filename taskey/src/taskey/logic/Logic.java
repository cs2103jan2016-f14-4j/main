package taskey.logic;

import taskey.parser.AutoComplete;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;
import taskey.parser.UserTagDatabase;
import taskey.storage.History;
import taskey.storage.Storage;
import taskey.storage.StorageException;
import taskey.logic.Task;

import java.util.ArrayList;
import java.util.Iterator;

import taskey.logic.LogicConstants.ListID;
import taskey.constants.UiConstants.ContentBox;
import taskey.logic.ProcessedObject;

import static taskey.constants.ParserConstants.DISPLAY_COMMAND;
import static taskey.constants.ParserConstants.FINISHED_COMMAND;
import static taskey.constants.ParserConstants.NO_SUCH_COMMAND;

/**
 * The Logic class handles the execution of user commands. It contains an internal memory of task lists which facilitate 
 * the addition, deletion and updating of tasks. Each time a command is executed, these lists are modified and then saved 
 * to disk accordingly.
 *
 * @author Hubert Wong
 */
public class Logic {
	private Parser parser;
	private TimeConverter timeConverter;
	private UserTagDatabase utd;
	private Storage storage;
	private History history;
	private ArrayList<ArrayList<Task>> taskLists; // Can be moved to a LogicMemory component next time
	
	public Logic() {
		parser = new Parser();
		timeConverter = new TimeConverter();
		utd = new UserTagDatabase();
		storage = new Storage();
		history = storage.getHistory();

		// Get lists from Storage
		ArrayList<ArrayList<Task>> listsFromStorage = storage.loadAllTasklists();
		listsFromStorage.add(ListID.THIS_WEEK.getIndex(), new ArrayList<Task>()); // Reserve first slot in taskLists for 
		                                                                          // this week's task list
		taskLists = cloneLists(listsFromStorage);

		// Update EXPIRED and THIS_WEEK lists based on the current date and time
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		long currTime = timeConverter.getCurrTime();
		for (Iterator<Task> it = pendingList.iterator(); it.hasNext();) { // Iterator is used for safe removal of
			                                                              // elements while iterating
			Task t = it.next();
			if (t.getTaskType().equals("DEADLINE")) { // TODO: remove magic strings
				long deadline = t.getDeadlineEpoch();
				
				if (deadline < currTime) {
					it.remove();
					removeFromAllLists(taskLists, t); // May throw ConcurrentModificationException if duplicate names 
					                                  // are allowed
					taskLists.get(ListID.EXPIRED.getIndex()).add(t);
					
					ArrayList<String> taskTags = t.getTaskTags();
					if (taskTags != null) {
						for (String s : taskTags) {
							utd.removeTag(s);
						}
					}	
				} else if (timeConverter.isSameWeek(deadline, currTime)) {
					taskLists.get(ListID.THIS_WEEK.getIndex()).add(t);
				}
			}
			
			if (t.getTaskType().equals("EVENT")) {
				long startDate = t.getStartDateEpoch();
				long endDate = t.getEndDateEpoch();
				
				if (endDate < currTime) {
					it.remove();
					removeFromAllLists(taskLists, t); // Same issue as above
					taskLists.get(ListID.EXPIRED.getIndex()).add(t);
					
					ArrayList<String> taskTags = t.getTaskTags();
					if (taskTags != null) {
						for (String s : taskTags) {
							utd.removeTag(s);
						}
					}	
				} else if (timeConverter.isSameWeek(startDate, currTime)) {
					taskLists.get(ListID.THIS_WEEK.getIndex()).add(t);
				}
			}
		}
		
		history.add(cloneLists(taskLists));
		history.addTagList(utd.getTagList());
	}
	
	/**
	 * Returns a deep copy of all task lists.
	 */
	public ArrayList<ArrayList<Task>> getAllTaskLists() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		assert (!taskLists.contains(null));
		
		return cloneLists(taskLists);
	}
	
	/**
	 * Returns a deep copy of THIS_WEEK list.
	 */
	public ArrayList<Task> getThisWeekList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		ArrayList<Task> thisWeekList = taskLists.get(ListID.THIS_WEEK.getIndex());
		assert (thisWeekList != null);
		
		return cloneList(thisWeekList);
	}
	
	/**
	 * Returns a deep copy of PENDING list.
	 */
	public ArrayList<Task> getPendingList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		assert (pendingList != null);

		return cloneList(pendingList);
	}
	
	/**
	 * Returns a deep copy of EXPIRED list.
	 */
	public ArrayList<Task> getExpiredList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		ArrayList<Task> expiredList = taskLists.get(ListID.EXPIRED.getIndex());
		assert (expiredList != null);

		return cloneList(expiredList);
	}
	
	/**
	 * Returns a deep copy of GENERAL list.
	 */
	public ArrayList<Task> getGeneralList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		ArrayList<Task> generalList = taskLists.get(ListID.GENERAL.getIndex());
		assert (generalList != null);

		return cloneList(generalList);
	}
	
	/**
	 * Returns a deep copy of DEADLINE list.
	 */
	public ArrayList<Task> getDeadlineList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		ArrayList<Task> deadlineList = taskLists.get(ListID.DEADLINE.getIndex());
		assert (deadlineList != null);

		return cloneList(deadlineList);
	}

	/**
	 * Returns a deep copy of EVENT list.
	 */
	public ArrayList<Task> getEventList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		ArrayList<Task> eventList = taskLists.get(ListID.EVENT.getIndex());
		assert (eventList != null);

		return cloneList(eventList);
	}

	/**
	 * Returns a deep copy of COMPLETED list.
	 */
	public ArrayList<Task> getCompletedList() {
		assert (taskLists != null);
		assert (taskLists.size() == 7);
		ArrayList<Task> completedList = taskLists.get(ListID.COMPLETED.getIndex());
		assert (completedList != null);

		return cloneList(completedList);
	}
	
	public ArrayList<TagCategory> getTagList() {
		return utd.getTagList();
	}
	

	/**
	 * Executes the user supplied command by performing list operations on a copy of the existing task lists, and saving
	 * the copy to disk. If save errors occurred, the existing task lists remain intact. If no save errors occurred, the 
	 * existing task lists are updated with the modified copy. Adding tasks with the same name is currently not supported.
	 *
	 * @param currentContent specifies the current tab that user is in.
	 * @param input			 the input String entered by the user
	 * @return               an object encapsulating the information required to update UI display
	 */
	public LogicFeedback executeCommand(ContentBox currentContent, String input) {
		ArrayList<ArrayList<Task>> originalCopy = cloneLists(taskLists); // A deep copy of the existing task lists to 
		                                                                 // remember the state before list operations
		                                                                 // were applied.
		ArrayList<ArrayList<Task>> modifiedCopy = cloneLists(taskLists); // A deep copy of the existing task lists to 
		                                                                 // perform list operations on.
    	ProcessedObject po = parser.parseInput(input);
    	String command = po.getCommand();
    	
    	if (input.equalsIgnoreCase("clear")) { // "clear" command is for developer testing only
			return clear(originalCopy, modifiedCopy);
    	}

    	switch (command) {
			case "ADD_FLOATING":
				return addFloating(originalCopy, modifiedCopy, po);
				
			case "ADD_DEADLINE":
				return addDeadline(originalCopy, modifiedCopy, po);

			case "ADD_EVENT":
				return addEvent(originalCopy, modifiedCopy, po);

			case "DELETE_BY_INDEX":
				return deleteByIndex(currentContent, originalCopy, modifiedCopy, po);

			case "DELETE_BY_NAME":
				return deleteByName(currentContent, originalCopy, modifiedCopy, po);

			case "VIEW":
				return new LogicFeedback(originalCopy, po, null);

			case "SEARCH":
				return search(originalCopy, po);

			case "DONE_BY_INDEX":
				return doneByIndex(currentContent, originalCopy, modifiedCopy, po);

			case "DONE_BY_NAME":
				return doneByName(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_INDEX_CHANGE_NAME":
				return updateByIndexChangeName(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_INDEX_CHANGE_DATE":
				return updateByIndexChangeDate(currentContent, originalCopy, modifiedCopy, po);
				
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				return updateByIndexChangeBoth(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_NAME_CHANGE_NAME":
				return updateByNameChangeName(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_NAME_CHANGE_DATE":
				return updateByNameChangeDate(currentContent, originalCopy, modifiedCopy, po);
				
			case "UPDATE_BY_NAME_CHANGE_BOTH":
				return updateByNameChangeBoth(currentContent, originalCopy, modifiedCopy, po);

			case "UNDO":
				return undo(po);
				
			case "ERROR":
				return new LogicFeedback(originalCopy, po, new Exception(po.getErrorType()));

			default:
				break;
		}

		return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_COMMAND_EXECUTION));
	}
	
	// Clears all task lists, and saves the updated lists to disk.
	public LogicFeedback clear(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy) {
		ProcessedObject po = new ProcessedObject("CLEAR"); // Stub
		clearAllLists(modifiedCopy);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		utd.deleteAllTags();
			
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}


	// Adds a floating task to the relevant lists, and saves the updated lists to disk.
	public LogicFeedback addFloating(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy, 
			                  ProcessedObject po) {
		Task task = po.getTask();
		ArrayList<Task> pendingList = modifiedCopy.get(ListID.PENDING.getIndex());

		if (pendingList.contains(task)) {
			String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS, task.getTaskName());
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		pendingList.add(task);
		modifiedCopy.get(ListID.GENERAL.getIndex()).add(task);

		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		ArrayList<String> taskTags = task.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.addTag(s);
			}
			
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}


	// Adds a deadline task to the relevant lists, and saves the updated lists to disk.
	public LogicFeedback addDeadline(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy, 
			                  ProcessedObject po) {
		Task task = po.getTask();
		long deadline = task.getDeadlineEpoch();
		long currTime = timeConverter.getCurrTime();
		ArrayList<Task> pendingList = modifiedCopy.get(ListID.PENDING.getIndex());
		String exceptionMsg;

		if (pendingList.contains(task)) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS, task.getTaskName());
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (deadline < currTime) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, timeConverter.getDate(deadline));
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else {
			pendingList.add(task);
			modifiedCopy.get(ListID.DEADLINE.getIndex()).add(task);

			if (timeConverter.isSameWeek(deadline, currTime)) {
				modifiedCopy.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		ArrayList<String> taskTags = task.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.addTag(s);
			}
			
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Adds an event task to the relevant lists, and saves the updated lists to disk.
	public LogicFeedback addEvent(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy, 
            	           ProcessedObject po) {
		Task task = po.getTask();
		long startDate = task.getStartDateEpoch();
		long endDate = task.getEndDateEpoch();
		long currTime = timeConverter.getCurrTime();
		ArrayList<Task> pendingList = modifiedCopy.get(ListID.PENDING.getIndex());
		String exceptionMsg;

		if (pendingList.contains(task)) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS, task.getTaskName());
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}


		if (endDate < currTime) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, timeConverter.getDate(endDate));
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else {
			pendingList.add(task);
			modifiedCopy.get(ListID.EVENT.getIndex()).add(task);

			if (timeConverter.isSameWeek(startDate, currTime)) {
				modifiedCopy.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}

		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		ArrayList<String> taskTags = task.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.addTag(s);
			}
			
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}
	

	// Removes an indexed task from the current tab and saves the updated lists to disk.
	// TODO: support removal from the "ACTION" tab.
	public LogicFeedback deleteByIndex(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			                    ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
		
		if (currentContent.equals(ContentBox.ACTION)) { 
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_DELETE_INVALID_TAB));
		}

    	ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
    	Task toDelete;
    	String exceptionMsg;

		try {
			toDelete = targetList.remove(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (!currentContent.equals(ContentBox.EXPIRED)) {
			removeFromAllLists(modifiedCopy, toDelete);
			ArrayList<String> taskTags = toDelete.getTaskTags();
			
			if (taskTags != null) {
				for (String s : taskTags) {
					utd.removeTag(s);
				}
				
				if (!utd.saveTagDatabase()) {
					return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
				}
			}
		} else {
			targetList.remove(toDelete);
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}

	

	// Removes an indexed task from the current tab and saves the updated lists to disk.
	// TODO: support removal from the "ACTION" tab.
	public LogicFeedback deleteByName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
            				   ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		if (currentContent.equals(ContentBox.ACTION)) { 
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_DELETE_INVALID_TAB));
		}
		
    	ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
    	String taskName = po.getTask().getTaskName();
		Task toDelete = getTaskByName(targetList, taskName);
		String exceptionMsg;

		if (toDelete == null) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, taskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (!currentContent.equals(ContentBox.EXPIRED)) {
			removeFromAllLists(modifiedCopy, toDelete);
			ArrayList<String> taskTags = toDelete.getTaskTags();
			
			if (taskTags != null) {
				for (String s : taskTags) {
					utd.removeTag(s);
				}
				
				if (!utd.saveTagDatabase()) {
					return new LogicFeedback(cloneLists(taskLists), po, 
							                 new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
				}
			}
				
		} else {
			targetList.remove(toDelete);
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	

	// Search for all pending Tasks whose names contain searchPhrase. searchPhrase is not case sensitive.
	// TODO: search list includes expired and completed tasks as well
	public LogicFeedback search(ArrayList<ArrayList<Task>> originalCopy, ProcessedObject po) {
		String searchPhrase = po.getSearchPhrase();
		
		if (searchPhrase == null || searchPhrase.trim().isEmpty()) { // Consider moving the check to Parser
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SEARCH_PHRASE_EMPTY));
		}
		
		ArrayList<ArrayList<Task>> matches = new ArrayList<ArrayList<Task>>();
		while (matches.size() < 7) {
			matches.add(new ArrayList<Task>());
		}
		
		ArrayList<Task> pendingList = originalCopy.get(ListID.PENDING.getIndex());
		for (Task t : pendingList) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				matches.get(ListID.SEARCH.getIndex()).add(t);
			}
		}

		return new LogicFeedback(matches, po, null);
	}
	

	// Marks an indexed task from the current tab as done and saves the updated lists to disk.
	// TODO: support "done" from the "ACTION" tab. 
	public LogicFeedback doneByIndex(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			                  ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		Task toMarkAsDone;
		int taskIndex = po.getIndex();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DONE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));

		}
		
		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		try {
			toMarkAsDone = targetList.remove(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		removeFromAllLists(modifiedCopy, toMarkAsDone);
		modifiedCopy.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		
		try {
			saveAllTasks(cloneLists(modifiedCopy));
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}

	

	// Marks an named task from the current tab as done and saves the updated lists to disk.
	// TODO: support "done" from the "ACTION" tab. 
	public LogicFeedback doneByName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
    						 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		String taskName = po.getTask().getTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DONE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		Task toMarkAsDone = getTaskByName(targetList, taskName);
		
		if (toMarkAsDone == null) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		removeFromAllLists(modifiedCopy, toMarkAsDone);
		modifiedCopy.get(ListID.COMPLETED.getIndex()).add(toMarkAsDone);
		
		try {
			saveAllTasks(cloneLists(modifiedCopy));
		} catch (Exception e) {
			return new LogicFeedback(cloneLists(taskLists), po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	

	// Updates an indexed task's name on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByIndexChangeName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			                              ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
		String newTaskName = po.getNewTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		updateAllLists(modifiedCopy, toUpdate.getTaskName(), newTaskName);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}


	// Updates an indexed task's date on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByIndexChangeDate(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
            							  ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
		Task changedTask = po.getTask();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (changedTask.getTaskType().equals("DEADLINE")) {
			if (changedTask.getDeadlineEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getDeadline());
				return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
			}
		}
		
		if (changedTask.getTaskType().equals("EVENT")) {
			if (changedTask.getEndDateEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getEndDate());
				return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
			}
		}

		changedTask.setTaskName(toUpdate.getTaskName());
		updateAllLists(modifiedCopy, toUpdate.getTaskName(), changedTask);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Updates an indexed task's name and date on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByIndexChangeBoth(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			  							  ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
		String newTaskName = po.getNewTaskName();
		Task changedTask = po.getTask();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (changedTask.getTaskType().equals("DEADLINE")) {
			if (changedTask.getDeadlineEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getDeadline());
				return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
			}
		}
		
		if (changedTask.getTaskType().equals("EVENT")) {
			if (changedTask.getEndDateEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getEndDate());
				return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
			}
		}

		changedTask.setTaskName(newTaskName);
		updateAllLists(modifiedCopy, toUpdate.getTaskName(), changedTask);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Updates an named task's name on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByNameChangeName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
										 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		String oldTaskName = po.getTask().getTaskName();
		String newTaskName = po.getNewTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		if (getTaskByName(targetList, oldTaskName) == null) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, oldTaskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		updateAllLists(modifiedCopy, oldTaskName, newTaskName);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Updates an named task's date on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByNameChangeDate(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			 							 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		Task changedTask = po.getTask();
		String taskName = changedTask.getTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		if (getTaskByName(targetList, taskName) == null) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, taskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		updateAllLists(modifiedCopy, taskName, changedTask);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	

	// Updates an named task's name and date on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByNameChangeBoth(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			 							 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		Task changedTask = po.getTask();
		String oldTaskName = changedTask.getTaskName();
		String newTaskName = po.getNewTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		if (getTaskByName(targetList, oldTaskName) == null) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, oldTaskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		changedTask.setTaskName(newTaskName);
		updateAllLists(modifiedCopy, oldTaskName, changedTask);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}


	// Undo the last change to the task lists.
	// TODO: support undoing of task tags.
	public LogicFeedback undo(ProcessedObject po) {
		assert(!history.isEmpty()); // History must always have at least one item, which is the current superlist
		ArrayList<ArrayList<Task>> currentSuperList = history.pop();
		ArrayList<ArrayList<Task>> previousSuperList = history.peek();
		ArrayList<TagCategory> currentTagList = history.popTags();
		ArrayList<TagCategory> previousTagList = history.peekTags();
		
		System.out.println(currentTagList == null);
		System.out.println(previousTagList == null);
		
		if (previousSuperList == null) {
			history.add(currentSuperList);
			history.addTagList(currentTagList);
			return new LogicFeedback(taskLists, po, new Exception(LogicConstants.MSG_EXCEPTION_UNDO));
		}
		
		try {
			saveAllTasks(previousSuperList);
		} catch (Exception e) {
			history.add(currentSuperList);
			return new LogicFeedback(currentSuperList, po, e);
		}
		
		utd.setTags(previousTagList);
		if (!utd.saveTagDatabase()) {
			history.addTagList(currentTagList);
			utd.setTags(currentTagList);
			return new LogicFeedback(currentSuperList, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
		}
		
		taskLists = cloneLists(previousSuperList);
		
		return new LogicFeedback(previousSuperList, po, null);
	}
	
	// Creates a deep copy of the original list.
	private ArrayList<Task> cloneList(ArrayList<Task> list) {
		ArrayList<Task> copy = new ArrayList<Task>();
		for (Task t : list) {
			copy.add(t.getDuplicate());
		}
		
		return copy;
	}
	
	// Creates a deep copy of the original task lists.
	ArrayList<ArrayList<Task>> cloneLists(ArrayList<ArrayList<Task>> lists) {
		//assert(lists.size() == 7);
		ArrayList<ArrayList<Task>> copy = new ArrayList<ArrayList<Task>>();
		
		for (int i = 0; i < lists.size(); i++) {
			copy.add(cloneList(lists.get(i)));
		}
		
		return copy;
	}
	
	// Gets the list corresponding to the given ContentBox.
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

			default:
				System.out.println("ContentBox invalid");
		}

		return targetList;
	}

	// Removes the given Task from all existing lists except the "EXPIRED" and "COMPLETED" lists.
	// The intended Task may not be removed if duplicate Task names are allowed.
	private void removeFromAllLists(ArrayList<ArrayList<Task>> taskLists, Task toRemove) {
		taskLists.get(ListID.PENDING.getIndex()).remove(toRemove);
		taskLists.get(ListID.THIS_WEEK.getIndex()).remove(toRemove);
		taskLists.get(ListID.GENERAL.getIndex()).remove(toRemove);
		taskLists.get(ListID.DEADLINE.getIndex()).remove(toRemove);
		taskLists.get(ListID.EVENT.getIndex()).remove(toRemove);
	}

	private void clearAllLists(ArrayList<ArrayList<Task>> taskLists) {
		for (int i = 0; i < taskLists.size(); i++) {
			taskLists.get(i).clear();
		}
	}

	// Change the Task whose name is oldTaskName, if any, to have a new name newTaskName.
	// Also updates all lists containing the updated Task.
	private void updateAllLists(ArrayList<ArrayList<Task>> taskLists, String oldTaskName, String newTaskName) {
		Task t = new Task(oldTaskName);

		for (int i = 0; i < taskLists.size(); i++) {
			int taskIndex = taskLists.get(i).indexOf(t);

			if (taskIndex != -1) { // List contains the Task
				taskLists.get(i).get(taskIndex).setTaskName(newTaskName);
			}
		}
	}

	// Replace the Task whose name is oldTaskName with another task changedTask.
	// All lists containing the replaced task are updated as well.
	private void updateAllLists(ArrayList<ArrayList<Task>> taskLists, String oldTaskName, Task changedTask) {
		Task toRemove = new Task(oldTaskName);
		removeFromAllLists(taskLists, toRemove);

		for (int i = 0; i < taskLists.size(); i++) {
			if (belongsToList(changedTask, i)) {
				taskLists.get(i).add(changedTask);
			}
		}
	}

	// Returns true if and only if a given pending task should be classified under the list specified by listIndex.
	private boolean belongsToList(Task task, int listIndex) {
		String taskType = task.getTaskType();

		if (listIndex == ListID.THIS_WEEK.getIndex()) {
			return (timeConverter.isSameWeek(task.getDeadlineEpoch(), timeConverter.getCurrTime())
					|| timeConverter.isSameWeek(task.getStartDateEpoch(), timeConverter.getCurrTime()));
		} else if (listIndex == ListID.PENDING.getIndex()) {
			return true;
		} else if (listIndex == ListID.GENERAL.getIndex()) {
			return (taskType.equals("FLOATING"));
		} else if (listIndex == ListID.DEADLINE.getIndex()) {
			return (taskType.equals("DEADLINE"));
		} else if (listIndex == ListID.EVENT.getIndex()) {
			return (taskType.equals("EVENT"));
		}

		return false; // Stub
	}
	
	// Returns the first Task whose name matches taskName. If no such Task is found, this method
	// returns null.
	private Task getTaskByName(ArrayList<Task> list, String taskName) {
		for (Task t : list) {
			if (t.getTaskName().equals(taskName)) {
				return t;
			}
		}
		
		return null;
	}

	// Save all task lists to Storage. If the save failed, the task lists will be reverted to the states
	// they were in before they were modified.
	private void saveAllTasks(ArrayList<ArrayList<Task>> taskLists) throws Exception {
		try {
			storage.saveAllTasklists(taskLists);
			System.out.println("All tasklists saved.");
		} catch (StorageException se) {
			System.out.println(se.getMessage());
			taskLists = se.getLastModifiedTasklists(); // Dylan: this hasn't been tested. Will test next time.
			throw new Exception (se.getMessage());
		}
	}
	
	
	public ArrayList<String> autoCompleteLine(String line, ContentBox currentContent) {
		AutoComplete auto = new AutoComplete();
		ProcessedAC pac = auto.completeCommand(line); 
		String pacCommand = pac.getCommand(); 
		
		if ( pacCommand.compareTo(DISPLAY_COMMAND) == 0) { // to complete a command
			ArrayList<String> suggestions = pac.getAvailCommands(); 
			return suggestions;
		} else if (pacCommand.compareTo(FINISHED_COMMAND) == 0) {
			return null;
		} else if (pacCommand.compareTo(NO_SUCH_COMMAND) == 0) {
			return null;  
		} else { // valid command
			ProcessedObject po = parser.parseInput(line);
			switch ( po.getCommand() ) {
			case "ADD_FLOATING":
			case "ADD_DEADLINE":
			case "ADD_EVENT":
				return new ArrayList<String>(); // valid
			case "DELETE_BY_INDEX":
			case "DELETE_BY_NAME":
			case "VIEW":
			case "SEARCH":
			case "DONE_BY_INDEX":
			case "DONE_BY_NAME":
			case "UPDATE_BY_INDEX_CHANGE_NAME":
			case "UPDATE_BY_INDEX_CHANGE_DATE":
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
			case "UPDATE_BY_NAME_CHANGE_NAME":
			case "UPDATE_BY_NAME_CHANGE_DATE":
			case "UPDATE_BY_NAME_CHANGE_BOTH":

			case "UNDO":
				return new ArrayList<String>();
				
			case "ERROR":
				return null;
			default:
				return null;
			}
		}
	}
	
	
	/**
	 * This function takes in a ProcessedObject, checks whether there are
	 * tags that can be added to the UserTagDatabase, else do nothing. 
	 * @param po
	 */
	public void trackTags(ProcessedObject po) {
		Task task = po.getTask();
		if (task != null) {
			ArrayList<String> tags = task.getTaskTags(); 
			
			if (tags != null) {
				for(int i = 0; i < tags.size(); i++) {
					if (!utd.containsTagName(tags.get(i))) {
						utd.addTag(tags.get(i));
					}
				}
			}	 
		}
	} 
}