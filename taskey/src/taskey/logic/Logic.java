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
 * @@author A0134177E
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
		storage = new Storage();
		utd = new UserTagDatabase(storage);
		history = storage.getHistory();

		// Get lists from Storage
		ArrayList<ArrayList<Task>> listsFromStorage = storage.loadAllTasklists();
		listsFromStorage.add(ListID.THIS_WEEK.getIndex(), new ArrayList<Task>()); // Reserve first slot for 
		                                                                          // this week's task list
		listsFromStorage.add(ListID.ACTION.getIndex(), new ArrayList<Task>()); //Reserve last slot for action list
		taskLists = cloneLists(listsFromStorage);

		// Update EXPIRED and THIS_WEEK lists based on the current date and time.
		// Tags from newly expired tasks are not removed from the tag database.
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		long currTime = timeConverter.getCurrTime();
		for (Iterator<Task> it = pendingList.iterator(); it.hasNext();) { // Iterator is used for safe removal of
			                                                              // elements while iterating
			Task t = it.next();
			if (t.getTaskType().equals("DEADLINE")) { // TODO: remove magic strings
				long deadline = t.getDeadlineEpoch();
				if (deadline < currTime) {
					it.remove();
					removeFromAllLists(taskLists, t); 
					taskLists.get(ListID.EXPIRED.getIndex()).add(t);
				} else if (timeConverter.isSameWeek(deadline, currTime)) {
					taskLists.get(ListID.THIS_WEEK.getIndex()).add(t);
				}
			}
			
			if (t.getTaskType().equals("EVENT")) {
				long startDate = t.getStartDateEpoch();
				long endDate = t.getEndDateEpoch();
				if (endDate < currTime) {
					it.remove();
					removeFromAllLists(taskLists, t);
					taskLists.get(ListID.EXPIRED.getIndex()).add(t);
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
		assert (taskLists.size() == 8);
		assert (!taskLists.contains(null));
		
		return cloneLists(taskLists);
	}
	
	/**
	 * Returns a deep copy of THIS_WEEK list.
	 */
	public ArrayList<Task> getThisWeekList() {
		assert (taskLists != null);
		assert (taskLists.size() == 8);
		ArrayList<Task> thisWeekList = taskLists.get(ListID.THIS_WEEK.getIndex());
		assert (thisWeekList != null);
		
		return cloneList(thisWeekList);
	}
	
	/**
	 * Returns a deep copy of PENDING list.
	 */
	public ArrayList<Task> getPendingList() {
		assert (taskLists != null);
		assert (taskLists.size() == 8);
		ArrayList<Task> pendingList = taskLists.get(ListID.PENDING.getIndex());
		assert (pendingList != null);

		return cloneList(pendingList);
	}
	
	/**
	 * Returns a deep copy of EXPIRED list.
	 */
	public ArrayList<Task> getExpiredList() {
		assert (taskLists != null);
		assert (taskLists.size() == 8);
		ArrayList<Task> expiredList = taskLists.get(ListID.EXPIRED.getIndex());
		assert (expiredList != null);

		return cloneList(expiredList);
	}
	
	/**
	 * Returns a deep copy of GENERAL list.
	 */
	public ArrayList<Task> getGeneralList() {
		assert (taskLists != null);
		assert (taskLists.size() == 8);
		ArrayList<Task> generalList = taskLists.get(ListID.GENERAL.getIndex());
		assert (generalList != null);

		return cloneList(generalList);
	}
	
	/**
	 * Returns a deep copy of DEADLINE list.
	 */
	public ArrayList<Task> getDeadlineList() {
		assert (taskLists != null);
		assert (taskLists.size() == 8);
		ArrayList<Task> deadlineList = taskLists.get(ListID.DEADLINE.getIndex());
		assert (deadlineList != null);

		return cloneList(deadlineList);
	}

	/**
	 * Returns a deep copy of EVENT list.
	 */
	public ArrayList<Task> getEventList() {
		assert (taskLists != null);
		assert (taskLists.size() == 8);
		ArrayList<Task> eventList = taskLists.get(ListID.EVENT.getIndex());
		assert (eventList != null);

		return cloneList(eventList);
	}

	/**
	 * Returns a deep copy of COMPLETED list.
	 */
	public ArrayList<Task> getCompletedList() {
		assert (taskLists != null);
		assert (taskLists.size() == 8);
		ArrayList<Task> completedList = taskLists.get(ListID.COMPLETED.getIndex());
		assert (completedList != null);

		return cloneList(completedList);
	}
	
	/**
	 * Returns a deep copy of the current tag list.
	 */
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

			/*case "DELETE_BY_NAME":
				return deleteByName(currentContent, originalCopy, modifiedCopy, po);*/
				
			case "DELETE_BY_CATEGORY":
				return deleteByCategory(originalCopy, modifiedCopy, po);

			case "VIEW_BASIC":
				return new LogicFeedback(originalCopy, po, null);
			
			case "VIEW_TAGS":
				return viewTags(originalCopy, po);

			case "SEARCH":
				return search(originalCopy, po);

			case "DONE_BY_INDEX":
				return doneByIndex(currentContent, originalCopy, modifiedCopy, po);

			/*case "DONE_BY_NAME":
				return doneByName(currentContent, originalCopy, modifiedCopy, po);*/

			case "UPDATE_BY_INDEX_CHANGE_NAME":
				return updateByIndexChangeName(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_INDEX_CHANGE_DATE":
				return updateByIndexChangeDate(currentContent, originalCopy, modifiedCopy, po);
				
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				return updateByIndexChangeBoth(currentContent, originalCopy, modifiedCopy, po);

			/*case "UPDATE_BY_NAME_CHANGE_NAME":
				return updateByNameChangeName(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_NAME_CHANGE_DATE":
				return updateByNameChangeDate(currentContent, originalCopy, modifiedCopy, po);
				
			case "UPDATE_BY_NAME_CHANGE_BOTH":
				return updateByNameChangeBoth(currentContent, originalCopy, modifiedCopy, po);*/

			case "UNDO":
				return undo(po);
				
			case "ERROR":
				return new LogicFeedback(originalCopy, po, new Exception(po.getErrorType()));

			default:
				break;
		}

		return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_COMMAND_EXECUTION));
	}
	
	// Searches for all expired and pending tasks that are tagged with at least one of the tag categories that the user 
	// wants to view.
	LogicFeedback viewTags(ArrayList<ArrayList<Task>> originalCopy, ProcessedObject po) {
		ArrayList<String> tagsToView = po.getViewType();
		ArrayList<Task> pendingList = originalCopy.get(ListID.PENDING.getIndex());
		ArrayList<Task> expiredList = originalCopy.get(ListID.EXPIRED.getIndex());
		ArrayList<Task> actionList = originalCopy.get(ListID.ACTION.getIndex());
		
		for (Task t : expiredList) {
			ArrayList<String> tagList = t.getTaskTags();
			if (tagList != null) {
				for (String s : tagsToView) {
					if (tagList.contains(s)) {
						actionList.add(t);
						break;
					}
				}
			}
		}
		
		for (Task t : pendingList) {
			ArrayList<String> tagList = t.getTaskTags();
			if (tagList != null) {
				for (String s : tagsToView) {
					if (tagList.contains(s)) {
						actionList.add(t);
						break;
					}
				}
			}
		}
		
		taskLists = cloneLists(originalCopy);
		return new LogicFeedback(originalCopy, po, null);
	}
	
	// Delete all tasks with the tag category specified within the ProcessedObject po.
	LogicFeedback deleteByCategory(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy, 
								   ProcessedObject po) {
		String category = po.getCategory();
		ArrayList<Task> pendingList = modifiedCopy.get(ListID.PENDING.getIndex());
		ArrayList<Task> expiredList = modifiedCopy.get(ListID.EXPIRED.getIndex());
		boolean categoryExists = false;
		
		// Remove all tasks pending tasks tagged with the specified category
		for (Iterator<Task> it = pendingList.iterator(); it.hasNext();) {
			Task task = it.next();
			ArrayList<String> taskTags = task.getTaskTags();
			if (taskTags != null && taskTags.contains(category)) {
				categoryExists = true;
				it.remove();
				removeFromAllLists(modifiedCopy, task); 
				for (String s : taskTags) {
					utd.removeTag(s);
				}
			}
		}
		
		// Remove all expired tasks tagged with the specified category
		for (Iterator<Task> it = expiredList.iterator(); it.hasNext();) {
			Task task = it.next();
			ArrayList<String> taskTags = task.getTaskTags();
			if (taskTags != null && taskTags.contains(category)) {
				categoryExists = true;
				it.remove();
				removeFromAllLists(modifiedCopy, task); 
				for (String s : taskTags) {
					utd.removeTag(s);
				}
			}
		}
		
		if (!categoryExists) {
			String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_TAG_NOT_FOUND, category);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}

	// Clears all task lists and the tag database, and saves the updated lists to disk.
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
		ArrayList<Task> expiredList = modifiedCopy.get(ListID.EXPIRED.getIndex());
		ArrayList<Task> completedList = modifiedCopy.get(ListID.COMPLETED.getIndex());
		
		// Check that the Task does not exist in the pending, expired or completed lists.
		if (pendingList.contains(task) || expiredList.contains(task) || completedList.contains(task)) {
			String exceptionMsg = LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		// Add the Task to the PENDING and GENERAL lists.
		pendingList.add(task);
		modifiedCopy.get(ListID.GENERAL.getIndex()).add(task);
		
		// Add the Task tags, if any, to the tag database.
		ArrayList<String> taskTags = task.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.addTag(s);
			}
			
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}

		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		// At this point, all saves succeeded, so clear the action list because the "add" command is not relevant for
		// this list.
		modifiedCopy.get(ListID.ACTION.getIndex()).clear();
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
		ArrayList<Task> expiredList = modifiedCopy.get(ListID.EXPIRED.getIndex());
		ArrayList<Task> completedList = modifiedCopy.get(ListID.COMPLETED.getIndex());
		String exceptionMsg;
		
		// Check that the Task does not exist in the pending, expired or completed lists.
		if (pendingList.contains(task) || expiredList.contains(task) || completedList.contains(task)) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (deadline < currTime) { // Cannot add tasks whose deadline already passed.
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, timeConverter.getDate(deadline));
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else {
			pendingList.add(task);
			modifiedCopy.get(ListID.DEADLINE.getIndex()).add(task);

			if (timeConverter.isSameWeek(deadline, currTime)) {
				modifiedCopy.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}
		
		// Add the Task tags, if any, to the tag database.
		ArrayList<String> taskTags = task.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.addTag(s);
			}
			
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		// At this point, all saves succeeded, so clear the action list because the "add" command is not relevant for
		// this list.
		modifiedCopy.get(ListID.ACTION.getIndex()).clear();
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
		ArrayList<Task> expiredList = modifiedCopy.get(ListID.EXPIRED.getIndex());
		ArrayList<Task> completedList = modifiedCopy.get(ListID.COMPLETED.getIndex());
		String exceptionMsg;
		
		// Check that the Task does not exist in the pending, expired or completed lists.
		if (pendingList.contains(task) || expiredList.contains(task) || completedList.contains(task)) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		if (endDate < currTime) { // Cannot add events which are already over.
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, timeConverter.getDate(endDate));
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else {
			pendingList.add(task);
			modifiedCopy.get(ListID.EVENT.getIndex()).add(task);

			if (timeConverter.isSameWeek(startDate, currTime)) { // TODO: how to determine if events are in same week?
				modifiedCopy.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}
		
		// Add the Task tags, if any, to the tag database.
		ArrayList<String> taskTags = task.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.addTag(s);
			}
			
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		// At this point, all saves succeeded, so clear the action list because the "add" command is not relevant for
		// this list.
		modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Removes an indexed task from the current tab and saves the updated lists to disk.
	public LogicFeedback deleteByIndex(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			                    	   ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
    	ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
    	Task toDelete;
    	String exceptionMsg;

		try {
			toDelete = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		removeFromAllLists(modifiedCopy, toDelete);
		
		// Remove the Task tags, if any, from the tag database.
		ArrayList<String> taskTags = toDelete.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.removeTag(s);
			}
				
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}
		
		// Check if user deleted from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}

		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}

		taskLists = cloneLists(modifiedCopy);		
		return new LogicFeedback(modifiedCopy, po, null);
	}

	/* @@author A0134177E-unused
	 * Team decided to do away from deleting tasks by name because delete by index is sufficient and can handle
	 * duplicate task names.
	// Removes an indexed task from the current tab and saves the updated lists to disk.
	public LogicFeedback deleteByName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
            				   		  ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
    	
		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
    	String taskName = po.getTask().getTaskName();
		ArrayList<Task> tasksToDelete = getTasksByName(targetList, taskName);
		String exceptionMsg;

		if (tasksToDelete.isEmpty()) { // Name not found
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, taskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else if (tasksToDelete.size() > 1) { // Multiple tasks with the same name found, prompt user to choose which 
			                                   // one to delete
			ArrayList<ArrayList<Task>> lists = getEmptyLists();
			lists.set(ListID.ACTION.getIndex(), tasksToDelete);
			mostRecentActionList = tasksToDelete;
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DUPLICATE_TASK_NAMES;
			return new LogicFeedback(lists, po, new Exception(exceptionMsg));
		} else { // Only one task with the given name
			Task toDelete = tasksToDelete.get(0);
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
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}*/

	//@@author A0134177E
	// Search for all expired and pending Tasks whose names contain searchPhrase. searchPhrase is not case sensitive.
	// TODO: search list includes completed tasks as well?
	// TODO: improved search
	public LogicFeedback search(ArrayList<ArrayList<Task>> originalCopy, ProcessedObject po) {
		String searchPhrase = po.getSearchPhrase(); // Validity of searchPhrase is already checked in ParseSearch

		ArrayList<Task> expiredList = originalCopy.get(ListID.EXPIRED.getIndex());
		ArrayList<Task> actionList = originalCopy.get(ListID.ACTION.getIndex());
		for (Task t : expiredList) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				actionList.add(t);
			}
		}
		
		ArrayList<Task> pendingList = originalCopy.get(ListID.PENDING.getIndex());
		for (Task t : pendingList) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				actionList.add(t);
			}
		}
		
		taskLists = cloneLists(originalCopy);
		history.add(originalCopy);
		return new LogicFeedback(originalCopy, po, null);
	}

	// Marks an indexed task from the current tab as done and saves the updated lists to disk.
	public LogicFeedback doneByIndex(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			                  ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		Task toComplete;
		int taskIndex = po.getIndex();
		String exceptionMsg;
		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		try {
			toComplete = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		// User is trying to complete a task that is already done
		if (currentContent.equals(ContentBox.ACTION) && modifiedCopy.get(ListID.COMPLETED.getIndex()).contains(toComplete)) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DONE_INVALID;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		removeFromAllLists(modifiedCopy, toComplete);
		modifiedCopy.get(ListID.COMPLETED.getIndex()).add(toComplete);
		
		// Check if user used "done" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}
		
		ArrayList<String> taskTags = toComplete.getTaskTags();
		if (taskTags != null) {
			for (String s : taskTags) {
				utd.removeTag(s);
			}
			
			if (!utd.saveTagDatabase()) {
				return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
			}
		}	
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}

	/* @@author A0134177E-unused
	 * Team decided to do away from completing tasks by name because done by index is sufficient and can handle
	 * duplicate task names.
	// Marks an named task from the current tab as done and saves the updated lists to disk.
	// TODO: support "done" from the "ACTION" tab. 
	public LogicFeedback doneByName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
    						 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		String taskName = po.getTask().getTaskName();
		String exceptionMsg;	
		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		ArrayList<Task> tasksToComplete = getTasksByName(targetList, taskName);
		
		if (currentContent.equals(ContentBox.ACTION) && mostRecentActionCommand.equals("VIEW ARCHIVE")) { //Stub
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DONE_INVALID;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (tasksToComplete.isEmpty()) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, taskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else if (tasksToComplete.size() > 1) { // Multiple tasks with the same name found, prompt user to choose which 
			                                     // one to complete
			ArrayList<ArrayList<Task>> lists = getEmptyLists();
			lists.set(ListID.ACTION.getIndex(), tasksToComplete);
			mostRecentActionList = tasksToComplete;
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DUPLICATE_TASK_NAMES;
			return new LogicFeedback(lists, po, new Exception(exceptionMsg));
		} else { // Only one task with the given name
			Task toComplete = tasksToComplete.get(0);
			removeFromAllLists(modifiedCopy, toComplete);
			modifiedCopy.get(ListID.COMPLETED.getIndex()).add(toComplete);
			ArrayList<String> taskTags = toComplete.getTaskTags();
			
			if (taskTags != null) {
				for (String s : taskTags) {
					utd.removeTag(s);
				}
				
				if (!utd.saveTagDatabase()) {
					return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
				}
			}	
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}*/
	
	// @@author A0134177E
	// Updates an indexed task's name on the current tab and saves the updated lists to disk.
	public LogicFeedback updateByIndexChangeName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			                                     ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
		String newTaskName = po.getNewTaskName();
		String exceptionMsg;

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex);
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		updateAllLists(modifiedCopy, toUpdate, newTaskName);
		
		// Check if user used "set" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}
		
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
		updateAllLists(modifiedCopy, toUpdate, changedTask);
		
		// Check if user used "set" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);	
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Updates an indexed task's name and date on the current tab and saves the updated lists to disk. 
	public LogicFeedback updateByIndexChangeBoth(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
												 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
		String newTaskName = po.getNewTaskName();
		Task changedTask = po.getTask();
		String exceptionMsg;

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
		updateAllLists(modifiedCopy, toUpdate, changedTask);
		
		// Check if user used "set" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}
		
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	/* @@author A0134177E-unused
	 * Team decided to do away from updating tasks by name because done by index is sufficient and can handle
	 * duplicate task names.
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
	}*/

	//@@author A0134177E
	// Undo the last change to the task lists.
	public LogicFeedback undo(ProcessedObject po) {
		// History stacks must always have at least one item, which is inserted at startup
		assert(!history.listStackIsEmpty());
		assert(!history.tagStackIsEmpty()); 
		ArrayList<ArrayList<Task>> currentSuperList = history.pop();
		ArrayList<ArrayList<Task>> previousSuperList = history.peek();
		ArrayList<TagCategory> currentTagList = history.popTags();
		ArrayList<TagCategory> previousTagList = history.peekTags();
		
		if (previousSuperList == null) {
			history.add(currentSuperList);
			history.addTagList(currentTagList);
			return new LogicFeedback(currentSuperList, po, new Exception(LogicConstants.MSG_EXCEPTION_UNDO));
		}
		
		if (previousTagList == null) { //No tags to undo
			history.addTagList(currentTagList);
		} else {
			utd.setTags(previousTagList); 
			
			if (!utd.saveTagDatabase()) { //This tries to adds another copy of previousTagList to history, be mindful
				history.add(currentSuperList);
				history.addTagList(currentTagList);
				utd.setTags(currentTagList);
				return new LogicFeedback(currentSuperList, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
			}
		}
		
		try {
			saveAllTasks(previousSuperList);
		} catch (Exception e) {
			utd.setTags(currentTagList);
			history.add(currentSuperList);
			history.addTagList(currentTagList);
			return new LogicFeedback(currentSuperList, po, e);
		}
		
		taskLists = cloneLists(previousSuperList);
		return new LogicFeedback(previousSuperList, po, null);
	}
	
	// Creates a deep copy of the given task list.
	private ArrayList<Task> cloneList(ArrayList<Task> list) {
		ArrayList<Task> copy = new ArrayList<Task>();
		
		for (Task t : list) {
			copy.add(t.getDuplicate());
		}
		
		return copy;
	}
	
	// Creates a deep copy of the given task lists.
	ArrayList<ArrayList<Task>> cloneLists(ArrayList<ArrayList<Task>> lists) {
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
				
			case ACTION:
				targetList = taskLists.get(ListID.ACTION.getIndex());
				break;

			default:
				System.out.println("ContentBox invalid");
		}
		return targetList;
	}

	// Removes the given Task from all task lists.
	private void removeFromAllLists(ArrayList<ArrayList<Task>> taskLists, Task toRemove) {
		taskLists.get(ListID.THIS_WEEK.getIndex()).remove(toRemove);
		taskLists.get(ListID.PENDING.getIndex()).remove(toRemove);
		taskLists.get(ListID.EXPIRED.getIndex()).remove(toRemove);
		taskLists.get(ListID.GENERAL.getIndex()).remove(toRemove);
		taskLists.get(ListID.DEADLINE.getIndex()).remove(toRemove);
		taskLists.get(ListID.EVENT.getIndex()).remove(toRemove);
		taskLists.get(ListID.COMPLETED.getIndex()).remove(toRemove);
		taskLists.get(ListID.ACTION.getIndex()).remove(toRemove);
	}

	private void clearAllLists(ArrayList<ArrayList<Task>> taskLists) {
		for (int i = 0; i < taskLists.size(); i++) {
			taskLists.get(i).clear();
		}
	}
	
	private ArrayList<ArrayList<Task>> getEmptyLists() {
		ArrayList<ArrayList<Task>> emptyLists = new ArrayList<ArrayList<Task>>();
		while (emptyLists.size() < 8) {
			emptyLists.add(new ArrayList<Task>());
		}
		return emptyLists;
	}

	// Change the old Task, if any, to have a new name newTaskName.
	// Also updates all lists containing the updated Task.
	private void updateAllLists(ArrayList<ArrayList<Task>> taskLists, Task oldTask, String newTaskName) {
		for (int i = 0; i < taskLists.size(); i++) {
			int taskIndex = taskLists.get(i).indexOf(oldTask);

			if (taskIndex != -1) { // List contains the Task
				taskLists.get(i).get(taskIndex).setTaskName(newTaskName);
			}
		}
	}

	// Replace the old Task with another task changedTask.
	// All lists containing the replaced task are updated as well.
	private void updateAllLists(ArrayList<ArrayList<Task>> taskLists, Task oldTask, Task changedTask) {
		for (int i = 0; i < taskLists.size(); i++) {
			int index = taskLists.get(i).indexOf(oldTask);
			if (index != -1) { // The list contains the task
				taskLists.get(i).set(index, changedTask);
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
	
	// Returns all Tasks in the given list whose name matches taskName. If no such Task is found, an empty list is
	// returned.
	private ArrayList<Task> getTasksByName(ArrayList<Task> list, String taskName) {
		ArrayList<Task> matches = new ArrayList<Task>();
		for (Task t : list) {
			if (t.getTaskName().equals(taskName)) {
				matches.add(t);
			}
		}
		
		return matches;
	}

	// Save all task lists to Storage. If the save failed, the task lists will be reverted to the states
	// they were in before they were modified.
	private void saveAllTasks(ArrayList<ArrayList<Task>> taskLists) throws Exception {
		try {
			storage.saveAllTasklists(taskLists);
		} catch (StorageException se) {
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
	
	/* @@author A0134177E-unused
	 * This is the code I wrote before my team decided to switch to ArrayLists instead of using HashMaps.
	 * There are many other instances where previous code I wrote was deleted, but I only chose to include this here
	 * because this particular code segment took me many days of effort to write and refactor.
	 * It also highlights the difference between the way Logic was implemented then and now.
	 * Notice that, in this old version, there was significant coupling between Logic and UI. 
	 * We have since removed the coupling.
	 * TODO: class description
	 * 
	 * @author Hubert Wong
	public class Logic {
		//List of status codes. Other components like Ui can use Logic.statusCode to access this list.
		public static final int SUCCESS_VIEW = 1;
		public static final int SUCCESS_ADD = 2;
		public static final int SUCCESS_DELETE = 3;
		public static final int SUCCESS_UPDATE = 4;
		public static final int SUCCESS_DONE = 5;
		public static final int SUCCESS_SEARCH = 6;
		public static final int SUCCESS_UNDO = 7;
		public static final int ERROR_VIEW = -1;
		public static final int ERROR_ADD = -2;
		public static final int ERROR_DELETE = -3;
		public static final int ERROR_UPDATE = -4;
		public static final int ERROR_DONE = -5;
		public static final int ERROR_SEARCH = -6;
		public static final int ERROR_UNDO = -7;
		
		private static Logic instance = null;
		private Parser parser;
		private Storage storage;
		private UiController uiController;
		
		//The most recent command which is not VIEW, UNDO, SEARCH or ERROR
		private String mostRecentUndoableCommand = null;
		
		private Task mostRecentTask = null;
		private Task mostRecentUpdatedTask = null; //To facilitate the reversal of updates
		
		//The current view type that Ui is displaying, e.g. deadline, events
		private String uiCurrentViewType = null;
		
		//Task lists retrieved from Storage at startup 
		private ArrayList<ArrayList<Task>> listsFromStorage = null;
		
		//Number of task lists in listsFromStorage
		private static final int NUM_TASK_LISTS = 6;
		
		//Indices of each Task list in listsFromStorage
		private static final int INDEX_PENDING_LIST = 0;
		private static final int INDEX_FLOATING_LIST = 1;
		private static final int INDEX_DEADLINE_LIST = 2;
		private static final int INDEX_EVENT_LIST = 3;
		private static final int INDEX_DONE_LIST = 4;
		private static final int INDEX_EXPIRED_LIST = 5;
		
		//Names of save file for each Task list. Can be moved to Storage later on.
		private static final String NAME_PENDING_SAVE_FILE = "pending tasks";
		private static final String NAME_FLOATING_SAVE_FILE = "floating tasks";
		private static final String NAME_DEADLINE_SAVE_FILE = "deadline tasks";
		private static final String NAME_EVENT_SAVE_FILE = "event tasks";
		private static final String NAME_DONE_SAVE_FILE = "done tasks";
		private static final String NAME_EXPIRED_SAVE_FILE = "expired tasks";
		
		//HashMaps containing Task data for each Task category. 
		//The key String holds the name of Task, and the value Task is the corresponding Task object.
		private HashMap<String, Task> pendingMap = null;
		private HashMap<String, Task> floatingMap = null;
		private HashMap<String, Task> deadlineMap = null;
		private HashMap<String, Task> eventMap = null;
		private HashMap<String, Task> doneMap = null;
		private HashMap<String, Task> expiredMap = null;
		
		//Collections of Task objects backed by the above HashMaps 
		private ArrayList<Task> pendingCollection = null; 
		private ArrayList<Task> floatingCollection = null;
		private ArrayList<Task> deadlineCollection = null;
		private ArrayList<Task> eventCollection = null;
		private ArrayList<Task> doneCollection = null;
		private ArrayList<Task> expiredCollection = null;
		
		public static void main(String[] args) throws IOException {
			Logic logicTest = Logic.getInstance();
			Task t = new Task("a new test task");
			logicTest.floatingMap = new HashMap<String, Task>();
			logicTest.pendingMap = new HashMap<String, Task>();
			logicTest.addFloatingToStorage(t, "a new test task");
			logicTest.getListsFromStorage();
		}
		
		/**
		 * Gets an instance of the Logic class if an instance does not already exist.
		 * 
		 * @return an instance of the Logic class
		 * 
		public static Logic getInstance() {
			if (instance == null) {
	    		instance = new Logic();
	    		instance.parser = new Parser();
	    		instance.storage = Storage.getInstance();
	    		instance.uiController = UiMain.getInstance().getController();
	    	}
	    	return instance;
	    }
		
		/**
		 * Get the list of pending tasks. Note that this list may not be sorted.
		 * 
		 * @return list of pending tasks\
		 * 
		public ArrayList<Task> getPendingTasks() {
			return pendingCollection;
		}
		
		/**
		 * Get the list of floating tasks. Note that this list may not be sorted.
		 * 
		 * @return list of floating tasks
		 * 
		public ArrayList<Task> getFloatingTasks() {
			return floatingCollection;
		}
		
		/**
		 * Get the list of deadline tasks. Note that this list may not be sorted.
		 * 
		 * @return list of deadline tasks
		 * 
		public ArrayList<Task> getDeadlineTasks() {
			return deadlineCollection;
		}
		
		/**
		 * Get the list of event tasks. Note that this list may not be sorted.
		 * 
		 * @return list of event tasks
		 * 
		public ArrayList<Task> getEventTasks() {
			return eventCollection;
		}
		
		/**
		 * Get the list of done tasks. Note that this list may not be sorted.
		 * 
		 * @return list of done tasks
		 *
		public ArrayList<Task> getDoneTasks() {
			return doneCollection;
		}
		
		/**
		 * Get the list of expired tasks. Note that this list may not be sorted.
		 * 
		 * @return list of expired tasks
		 *
		public ArrayList<Task> getExpiredTasks() {
			return expiredCollection;
		}
		
		/** 
		 * Updates Logic with the view type that Ui is currently in.
		 * 
		 * @param viewType
		 *
		public void updateViewType(String viewType) {
			uiCurrentViewType = viewType;
		}
		
		/**
		 * Initializes Ui with lists of each task category.
		 * 
		 * @return status code reflecting the outcome of command execution
		 *
		public int initializeUi() {
			int statusCode = getListsFromStorage();
			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
			uiController.updateDisplay(expiredCollection, UiConstants.ContentBox.EXPIRED);
			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
			uiCurrentViewType = "ALL";
			
			return statusCode;
		}
		
	    /**
	     * Attempts to execute a command specified by the input string.
	     * 
	     * @param input the input string
	     * @return      status code reflecting the outcome of command execution
	     * @throws IOException 
	     *
	    public int executeCommand(String input) throws IOException {
	    	int statusCode = 0; //Stub
	    	ProcessedObject po = parser.parseInput(input);
	    	String command = po.getCommand();
	    	Task task = po.getTask();
	    	int taskIndex = po.getIndex() - 1; //Only used for commands that specify the index of a task
	    	String viewType = po.getViewType(); //Only used for view commands
	    	String errorType = po.getErrorType(); //Only used for invalid commands
	    	String searchPhrase = po.getSearchPhrase(); //Only used for search commands
	    	String newTaskName = po.getNewTaskName(); //Only used for commands that change the name of a task
	    	String taskName = task.getTaskName();
	   	
	    	switch (command) {
	    		case "VIEW":
	    			statusCode = view(viewType);
	    			break;
	    			
	    		case "ADD_FLOATING":
	    			statusCode = addFloatingToStorage(task, taskName);
	    			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "ADD_DEADLINE":
	    			statusCode = addDeadlineToStorage(task, taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "ADD_EVENT":
	    			statusCode = addEventToStorage(task, taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "DELETE_BY_INDEX":
	    			statusCode = deleteIndexedTaskFromStorage(taskIndex);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "DELETE_BY_NAME":
	    			statusCode = deleteNamedTaskFromStorage(taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "UPDATE_BY_INDEX_CHANGE_NAME":
	    			statusCode = updateIndexedTaskNameInStorage(taskIndex, newTaskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "UPDATE_BY_INDEX_CHANGE_DATE":
	    			statusCode = updateIndexedTaskDateInStorage(task, taskIndex);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "UPDATE_BY_NAME_CHANGE_NAME":
	    			statusCode = updateNamedTaskNameInStorage(taskName, newTaskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "UPDATE_BY_NAME_CHANGE_DATE":
	    			statusCode = updateNamedTaskDateInStorage(task, taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    				
	    		case "DONE_BY_INDEX":
	    			statusCode = markIndexedTaskAsDoneInStorage(taskIndex);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	       			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "DONE_BY_NAME":
	    			statusCode = markNamedTaskAsDoneInStorage(taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	       			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "SEARCH":
	    			Task t = search(searchPhrase);
	    			if (t == null) { //Task not found
	    				statusCode = -1; //Stub
	    			} else {
	    				ArrayList<Task> matches = new ArrayList<Task>();
	    				matches.add(t);
	    				uiController.updateActionDisplay(matches, UiConstants.ActionContentMode.TASKLIST);
	    				statusCode = -1; //Stub
	    			}
	    			break;
	    		
	    		case "UNDO":
	    			statusCode = undo();
	    			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
	    			uiController.updateDisplay(expiredCollection, UiConstants.ContentBox.EXPIRED);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "ERROR": //TODO
	    			statusCode = -1; //Stub
	    			break;
	    			
	    		default:
	    	}
	    	
	    	if (isUndoableCommand(command)) {
	    		mostRecentUndoableCommand = command;
	    	}
	    	
	    	return statusCode; 
	    }
	    
	    //Updates Ui with a list of Tasks sorted by date, corresponding to the view type.
	    //Assumes that the collections are not null.
	    //Returns a status code representing outcome of action.
	    private int view(String viewType) throws IOException {
	    	int statusCode = -1; //Stub 
	    	
	    	if (viewType.equals("ALL")) {
				Collections.sort(pendingCollection);
				uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("GENERAL")) {
				Collections.sort(floatingCollection);
				uiController.updateActionDisplay(floatingCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("DEADLINES")) {
				Collections.sort(deadlineCollection);
				uiController.updateActionDisplay(deadlineCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("EVENTS")) {
				Collections.sort(eventCollection);
				uiController.updateActionDisplay(eventCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("DONE")) {
				Collections.sort(doneCollection);
				uiController.updateActionDisplay(doneCollection, UiConstants.ActionContentMode.TASKLIST);
			} else { //Expired tasks
				Collections.sort(expiredCollection);
				uiController.updateActionDisplay(expiredCollection, UiConstants.ActionContentMode.TASKLIST);
			}
	    	uiCurrentViewType = viewType;
	    	
	    	return statusCode; 
	    }
	    
	    //Add the floating Task to Storage. Returns a status code representing outcome of action.
	    private int addFloatingToStorage(Task task, String taskName) throws IOException {
			floatingMap.put(taskName, task); 
			floatingCollection = new ArrayList<Task>(floatingMap.values());
			storage.saveTaskList(floatingCollection, NAME_FLOATING_SAVE_FILE);	
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			mostRecentTask = task;
			
			return -1; //Stub
	    }
	    
	    //Add the deadline Task to Storage. Returns a status code representing outcome of action.
	    private int addDeadlineToStorage(Task task, String taskName) throws IOException {
			deadlineMap.put(taskName, task); 
			deadlineCollection = new ArrayList<Task>(deadlineMap.values());
			storage.saveTaskList(deadlineCollection, NAME_DEADLINE_SAVE_FILE);	
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			mostRecentTask = task;
			
			return -1; //Stub
	    }
	    
	  //Add the event Task to Storage. Returns a status code representing outcome of action.
	    private int addEventToStorage(Task task, String taskName) throws IOException {
			eventMap.put(taskName, task); 
			eventCollection = new ArrayList<Task>(eventMap.values());
			storage.saveTaskList(eventCollection, NAME_EVENT_SAVE_FILE);	
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			mostRecentTask = task;
			
			return -1; //Stub
	    }
	    
	    //Undo the most recent action that was not view, undo, search or error.
	    //Returns a status code representing outcome of action.
		private int undo() throws IOException {
			if (mostRecentUndoableCommand == null) { //No undoable commands since startup
				return -1; //Stub
			}

			String mostRecentTaskType = mostRecentTask.getTaskType();
			String mostRecentTaskName = mostRecentTask.getTaskName();
			
			switch (mostRecentUndoableCommand) {
				case "ADD_FLOATING":
				case "ADD_DEADLINE":
				case "ADD_EVENT":
					return removeTaskFromMaps(mostRecentTaskName, mostRecentTaskType);
				
				case "DELETE_BY_INDEX":
				case "DELETE_BY_NAME":
					return putTaskInMaps(mostRecentTask, mostRecentTaskName, mostRecentTaskType);
				
				case "UPDATE_BY_INDEX":
				case "UPDATE_BY_NAME":
					String mostRecentUpdatedTaskName = mostRecentUpdatedTask.getTaskName();
					String mostRecentUpdatedTaskType = mostRecentUpdatedTask.getTaskType();
					removeTaskFromMaps(mostRecentUpdatedTaskName, mostRecentUpdatedTaskType);
					return putTaskInMaps(mostRecentTask, mostRecentTaskName, mostRecentTaskType);
					
				case "DONE_BY_INDEX":
				case "DONE_BY_NAME":
					doneMap.remove(mostRecentTaskName);
					doneCollection = new ArrayList<Task>(doneMap.values());
					storage.saveTaskList(doneCollection, NAME_DONE_SAVE_FILE);		
					return putTaskInMaps(mostRecentTask, mostRecentTaskName, mostRecentTaskType);			
				
				default:
			}
			
			return -1; //Stub
		}
		
		//Deletes the Task specified by taskIndex from Storage.
		//Returns a status code reflecting outcome of command execution.
		private int deleteIndexedTaskFromStorage(int taskIndex) throws IOException {
			Task toDelete = getIndexedTask(taskIndex);
			
			if (toDelete == null) { //Index is invalid
				return -1; //Stub
			}
			
			String toDeleteType = toDelete.getTaskType();
			String toDeleteName = toDelete.getTaskName();
			mostRecentTask = toDelete;
			
			return removeTaskFromMaps(toDeleteName, toDeleteType);
		}
		
		//Deletes the Task specified by taskName from Storage.
		//Returns a status code reflecting outcome of command execution.
		private int deleteNamedTaskFromStorage(String taskName) throws IOException {
			if (pendingMap.containsKey(taskName)) {
				Task toDelete = pendingMap.get(taskName);
				String taskType = toDelete.getTaskType();
				mostRecentTask = toDelete;
				return removeTaskFromMaps(taskName, taskType);
			} else { //Task to delete does not exist
				return -1; //Stub
			}
		}
		
		//Updates the Task specified by taskIndex in Storage with newTaskName.
		//Returns a status code reflecting outcome of command execution.
		private int updateIndexedTaskNameInStorage(int taskIndex, String newTaskName) throws IOException {
			Task toUpdate = getIndexedTask(taskIndex);
			
			if (toUpdate == null) {
				return -1; //Stub
			}
			
			String toUpdateType = toUpdate.getTaskType();
			String toUpdateName = toUpdate.getTaskName();
			removeTaskFromMaps(toUpdateName, toUpdateType);
			mostRecentTask = toUpdate;
			Task updated = toUpdate.getDuplicate();
			updated.setTaskName(newTaskName);
			mostRecentUpdatedTask = updated;
			
			return putTaskInMaps(toUpdate, newTaskName, toUpdateType);
		}
		
		//Updates the Task specified by taskIndex in Storage with task which contains the new date.
		//Returns a status code reflecting outcome of command execution.
		private int updateIndexedTaskDateInStorage(Task task, int taskIndex) throws IOException {
			Task toUpdate = getIndexedTask(taskIndex);
			
			if (toUpdate == null) {
				return -1; //Stub
			}
			
			String toUpdateType = toUpdate.getTaskType();
			String toUpdateName = toUpdate.getTaskName();
			String newTaskType = task.getTaskType();
			removeTaskFromMaps(toUpdateName, toUpdateType);
			mostRecentTask = toUpdate;
			task.setTaskName(toUpdateName);
			mostRecentUpdatedTask = task;
			
			return putTaskInMaps(task, toUpdateName, newTaskType);
		}
		
		//Updates the Task specified by taskName in Storage with newTaskName.
		//Returns a status code reflecting outcome of command execution.
		private int updateNamedTaskNameInStorage(String oldTaskName, String newTaskName) throws IOException {
			if (pendingMap.containsKey(oldTaskName)) {
				Task toUpdate = pendingMap.get(oldTaskName);
				String toUpdateType = toUpdate.getTaskType();
				removeTaskFromMaps(oldTaskName, toUpdateType);
				mostRecentTask = toUpdate;
				Task updated = toUpdate.getDuplicate();
				updated.setTaskName(newTaskName);
				mostRecentUpdatedTask = updated;
				return putTaskInMaps(toUpdate, newTaskName, toUpdateType);
			} else { //Task to update does not exist
				return -1; //Stub
			}
		}
		
		//Updates the Task specified by taskName in Storage with task which contains the new date.
		//Returns a status code reflecting outcome of command execution.
		private int updateNamedTaskDateInStorage(Task task, String taskName) throws IOException {
			if (pendingMap.containsKey(taskName)) {
				Task toUpdate = pendingMap.get(taskName);
				String toUpdateType = toUpdate.getTaskType();
				String newTaskType = task.getTaskType();
				removeTaskFromMaps(taskName, toUpdateType);
				mostRecentTask = toUpdate;
				mostRecentUpdatedTask = task;
				return putTaskInMaps(task, taskName, newTaskType);
			} else { //Task to update does not exist
				return -1; //Stub
			}
		}
		
		//Marks the Task specified by taskIndex as done in Storage.
		//Returns a status code reflecting outcome of command execution.
		private int markIndexedTaskAsDoneInStorage(int taskIndex) throws IOException {
			Task toMark = getIndexedTask(taskIndex);
				
			if (toMark == null) { //Index is invalid
				return -1; //Stub
			}
				
			String toMarkType = toMark.getTaskType();
			String toMarkName = toMark.getTaskName();
			removeTaskFromMaps(toMarkName, toMarkType);
			mostRecentTask = toMark;
			doneMap.put(toMarkName, toMark);
			doneCollection = new ArrayList<Task>(doneMap.values());
			storage.saveTaskList(doneCollection, NAME_DONE_SAVE_FILE);
				
			return -1; //Stub
		}
		
		//Marks the Task specified by taskName as done in Storage.
		//Returns a status code reflecting outcome of command execution.
		private int markNamedTaskAsDoneInStorage(String taskName) throws IOException {
			if (pendingMap.containsKey(taskName)) {
				Task toMark= pendingMap.get(taskName);
				String toMarkName = toMark.getTaskName();
				String toMarkType = toMark.getTaskType();
				removeTaskFromMaps(taskName, toMarkType);
				mostRecentTask = toMark;
				doneMap.put(toMarkName, toMark);
				doneCollection = new ArrayList<Task>(doneMap.values());
				storage.saveTaskList(doneCollection, NAME_DONE_SAVE_FILE);
				return -1; //Stub
			} else { //Task name does not exist
				return -1; //Stub
			}
		}
		
		//Returns an existing Task whose name matches searchPhrase, or null if no matches are found.
		private Task search(String searchPhrase) {
			if (pendingMap.containsKey(searchPhrase)) { //Only works if searchPhrase matches taskName
				return pendingMap.get(searchPhrase);
			} else {
				return null;
			}
		}
		
		//Adds the specified <taskName, task> mappings to the relevant HashMaps determined by taskType.
		//This method also saves the updated Task collections to Storage.
		//Returns a status code reflecting outcome of command execution.
		private int putTaskInMaps(Task task, String taskName, String taskType) throws IOException {
			if (taskType.equals("FLOATING")) {
				floatingMap.put(taskName, task);
				floatingCollection = new ArrayList<Task>(floatingMap.values());
				storage.saveTaskList(floatingCollection, NAME_FLOATING_SAVE_FILE);
			} else if (taskType.equals("DEADLINE")) {
				deadlineMap.put(taskName, task);
				deadlineCollection = new ArrayList<Task>(deadlineMap.values());
				storage.saveTaskList(deadlineCollection, NAME_DEADLINE_SAVE_FILE);
			} else if (taskType.equals("EVENT")) {
				eventMap.put(taskName, task);
				eventCollection = new ArrayList<Task>(eventMap.values());
				storage.saveTaskList(eventCollection, NAME_EVENT_SAVE_FILE);
			}
			
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			
			return -1; //stub
		}
		
		//Removes the mappings whose keys are specified by taskName from the relevant HashMaps.
		//The HashMaps to remove the mappings from are determined by taskType.
		//This method also saves the updated Task collections to Storage.
		//Returns a status code reflecting outcome of command execution.
		private int removeTaskFromMaps(String taskName, String taskType) throws IOException {
			if (taskType.equals("FLOATING")) {
				floatingMap.remove(taskName);
				floatingCollection = new ArrayList<Task>(floatingMap.values());
				storage.saveTaskList(floatingCollection, NAME_FLOATING_SAVE_FILE);
			} else if (taskType.equals("DEADLINE")) {
				deadlineMap.remove(taskName);
				deadlineCollection = new ArrayList<Task>(deadlineMap.values());
				storage.saveTaskList(deadlineCollection, NAME_DEADLINE_SAVE_FILE);
			} else if (taskType.equals("EVENT")) {
				eventMap.remove(taskName);
				eventCollection = new ArrayList<Task>(eventMap.values());
				storage.saveTaskList(eventCollection, NAME_EVENT_SAVE_FILE);
			}
			
			pendingMap.remove(taskName);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			
			return -1; //stub
		}
		
		//Returns a reference to the indexed Task based on the current Ui view type, or null if index is invalid.
		private Task getIndexedTask(int taskIndex) {
			if (outOfBounds(taskIndex)) {
				return null;
			}
			
			if (uiCurrentViewType.equals("ALL")) {
				return pendingCollection.get(taskIndex);
			} else if (uiCurrentViewType.equals("FLOATING")) {
				return floatingCollection.get(taskIndex);
			} else if (uiCurrentViewType.equals("DEADLINE")) {
				return deadlineCollection.get(taskIndex);
			} else if (uiCurrentViewType.equals("EVENT")) {
				return eventCollection.get(taskIndex);
			} else { //May need to add more cases like "EXPIRED", "DONE" etc.
				return null;
			}
		}
		
		//Determines if the given Task index is valid for the current Ui view type.
		//Returns true if the index is out of bounds (invalid).
		private boolean outOfBounds(int taskIndex) {
			if (uiCurrentViewType.equals("ALL")) {
				return (taskIndex < pendingCollection.size()) ? false : true;
			} else if (uiCurrentViewType.equals("FLOATING")) {
				return (taskIndex < floatingCollection.size()) ? false : true;
			} else if (uiCurrentViewType.equals("DEADLINE")) {
				return (taskIndex < deadlineCollection.size()) ? false : true;
			} else if (uiCurrentViewType.equals("EVENT")) {
				return (taskIndex < eventCollection.size()) ? false : true;
			} else { //May need to add more cases like "EXPIRED", "DONE" etc.
				return false;
			}
		}
		
		//Get Task lists from Storage at startup and populate the HashMaps and their corresponding collections.
	    //Returns a status code representing outcome of action.
	    private int getListsFromStorage() {
	    	listsFromStorage = new ArrayList<ArrayList<Task>>(NUM_TASK_LISTS);
	    	
	    	
	    	//Get PENDING list from Storage
	    	listsFromStorage.add(INDEX_PENDING_LIST, storage.getTaskList(NAME_PENDING_SAVE_FILE));
	    	pendingMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_PENDING_LIST)) {
	    		pendingMap.put(t.getTaskName(), t);
	    	}
	    	pendingCollection = new ArrayList<Task>(pendingMap.values());
	    	
	    	//Get FLOATING list from Storage
	    	listsFromStorage.add(INDEX_FLOATING_LIST, storage.getTaskList(NAME_FLOATING_SAVE_FILE));
	    	floatingMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_FLOATING_LIST)) {
	    		floatingMap.put(t.getTaskName(), t);
	    	}
	    	floatingCollection = new ArrayList<Task>(floatingMap.values());
	    	
	    	//Get DEADLINE list from Storage
	    	listsFromStorage.add(INDEX_DEADLINE_LIST, storage.getTaskList(NAME_DEADLINE_SAVE_FILE));
	    	deadlineMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_DEADLINE_LIST)) {
	    		deadlineMap.put(t.getTaskName(), t);
	    	}
	    	deadlineCollection = new ArrayList<Task>(deadlineMap.values());
	    	
	    	//Get EVENT list from Storage
	    	listsFromStorage.add(INDEX_EVENT_LIST, storage.getTaskList(NAME_EVENT_SAVE_FILE));
	    	eventMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_EVENT_LIST)) {
	    		eventMap.put(t.getTaskName(), t);
	    	}
	    	eventCollection = new ArrayList<Task>(eventMap.values());
	    	
	    	//Get DONE list from Storage
	    	listsFromStorage.add(INDEX_DONE_LIST, storage.getTaskList(NAME_DONE_SAVE_FILE));
	    	doneMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_DONE_LIST)) {
	    		doneMap.put(t.getTaskName(), t);
	    	}
	    	doneCollection = new ArrayList<Task>(doneMap.values());
	    	
	    	//Get EXPIRED list from Storage
	    	listsFromStorage.add(INDEX_EXPIRED_LIST, storage.getTaskList(NAME_EXPIRED_SAVE_FILE));
	    	expiredMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_EXPIRED_LIST)) {
	    		expiredMap.put(t.getTaskName(), t);
	    	}
	    	expiredCollection = new ArrayList<Task>(expiredMap.values());
	    	
	    	return -1; //Stub
	    }
		
	    //Returns true if the supplied command can be undone.
	    private boolean isUndoableCommand(String command) {
	    	switch (command) {
	    		case "VIEW":
	    		case "SEARCH":
	    		case "UNDO":
	    		case "ERROR":
	    			return false;
	    		
	    		default:
	    	}
	    	
	    	return true;
	    }
	}*/
}