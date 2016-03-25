package taskey.logic;

import taskey.parser.AutoComplete;
import taskey.parser.Parser;
import taskey.logic.Task;

import java.util.ArrayList;

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
 */
public class Logic {
	private Parser parser;
	private History history;
	private CommandExecutor cmdExecutor;
	private LogicMemory logicMemory;
	
	public Logic() {
		parser = new Parser();
		history = new History();
		cmdExecutor = new CommandExecutor();
		logicMemory = new LogicMemory();
		updateHistory();
	}
	
	/**
	 * Returns a deep copy of all task lists.
	 */
	public ArrayList<ArrayList<Task>> getAllTaskLists() {
		return ListCloner.cloneTaskLists(logicMemory.getTaskLists());
	}
	
	/**
	 * Returns a deep copy of the current tag category list.
	 */
	public ArrayList<TagCategory> getTagCategoryList() {
		return ListCloner.cloneTagCategoryList(logicMemory.getTagCategoryList());
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
    	ProcessedObject po = parser.parseInput(input);
    	String command = po.getCommand();
    	Command cmd;
    	
    	if (input.equalsIgnoreCase("clear")) { // "clear" command is for developer testing only
			return executeClear();
    	}
    	
    	switch (command) {
			case "ADD_FLOATING":
				cmd = new AddFloating(po.getTask());
				return executeAdd(po, cmd);
				
			case "ADD_DEADLINE":
				cmd = new AddDeadline(po.getTask());
				return executeAdd(po, cmd);

			case "ADD_EVENT":
				cmd = new AddEvent(po.getTask());
				return executeAdd(po, cmd);

			/*case "DELETE_BY_INDEX":
				return deleteByIndex(currentContent, originalCopy, modifiedCopy, po);
				
			case "DELETE_BY_CATEGORY":
				return deleteByCategory(originalCopy, modifiedCopy, po);

			case "VIEW_BASIC":
				return viewBasic(originalCopy, po);
			
			case "VIEW_TAGS":
				return viewTags(originalCopy, modifiedCopy, po);

			case "SEARCH":
				return search(originalCopy, modifiedCopy, po);

			case "DONE_BY_INDEX":
				return doneByIndex(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_INDEX_CHANGE_NAME":
				return updateByIndexChangeName(currentContent, originalCopy, modifiedCopy, po);

			case "UPDATE_BY_INDEX_CHANGE_DATE":
				return updateByIndexChangeDate(currentContent, originalCopy, modifiedCopy, po);
				
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				return updateByIndexChangeBoth(currentContent, originalCopy, modifiedCopy, po);

			case "UNDO":
				return undo(po);
				
			case "ERROR":
				return new LogicFeedback(originalCopy, po, new Exception(po.getErrorType()));*/

			default:
				break;
		}

		return null; // Stub
	}

	private LogicFeedback executeClear() {
		Command cmd;
		cmd = new Clear();
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (Exception e) {
			return new LogicFeedback(getAllTaskLists(), new ProcessedObject("CLEAR"), e);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), new ProcessedObject("CLEAR"), 
				                 new Exception(LogicConstants.MSG_CLEAR_SUCCESSFUL));
	}

	private LogicFeedback executeAdd(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (Exception e) {
			return new LogicFeedback(getAllTaskLists(), po, e);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new Exception(LogicConstants.MSG_ADD_SUCCESSFUL));
	}
	
	// Push the latest task lists and tag category list to history.
	private void updateHistory() {
		history.add(getAllTaskLists());
		history.addTagList(getTagCategoryList());
	}
	
	/*
	// Searches for all expired and pending tasks that are tagged with at least one of the tag categories that the user 
	// wants to view.
	LogicFeedback viewTags(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy, 
			               ProcessedObject po) {
		ArrayList<String> tagsToView = po.getViewType();
		ArrayList<Task> pendingList = modifiedCopy.get(ListID.PENDING.getIndex());
		ArrayList<Task> expiredList = modifiedCopy.get(ListID.EXPIRED.getIndex());
		ArrayList<Task> actionList = modifiedCopy.get(ListID.ACTION.getIndex());
		actionList.clear();
		
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
		if (actionList.isEmpty()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_TAG_NOT_FOUND));
		}
			
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Views one of the task categories.
	LogicFeedback viewBasic(ArrayList<ArrayList<Task>> originalCopy, ProcessedObject po) {
		String viewType = po.getViewType().get(0);
		switch (viewType) {
			case "general":
				ArrayList<Task> generalList = originalCopy.get(ListID.GENERAL.getIndex());
				originalCopy.set(ListID.ACTION.getIndex(), generalList);
				break;
			
			case "deadlines":
				ArrayList<Task> deadlineList = originalCopy.get(ListID.DEADLINE.getIndex());
				originalCopy.set(ListID.ACTION.getIndex(), deadlineList);
				break;
				
			case "events":
				ArrayList<Task> eventList = originalCopy.get(ListID.EVENT.getIndex());
				originalCopy.set(ListID.ACTION.getIndex(), eventList);
				break;
				
			case "archive":
				ArrayList<Task> completedList = originalCopy.get(ListID.COMPLETED.getIndex());
				originalCopy.set(ListID.ACTION.getIndex(), completedList);
				break;
				
			case "expired":
				ArrayList<Task> expiredList = originalCopy.get(ListID.EXPIRED.getIndex());
				originalCopy.set(ListID.ACTION.getIndex(), expiredList);
				break;
			
			default:
				break;
				
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
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_DELETE_TAGS_SUCCESSFUL));
	}

	// Clears all task lists and the tag database, and saves the updated lists to disk.
	public LogicFeedback clear(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy) {
		ProcessedObject po = new ProcessedObject("CLEAR"); // Stub
		
		utd.deleteAllTags();
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
		}
		
		clearAllLists(modifiedCopy);	
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
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
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
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
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_ADD_SUCCESSFUL));
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
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
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
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_ADD_SUCCESSFUL));
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
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
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
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_ADD_SUCCESSFUL));
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
		
		// If the Task is not archived, remove its task tags, if any, from the tag database.
		ArrayList<String> taskTags = toDelete.getTaskTags();
		if (!(modifiedCopy.get(ListID.COMPLETED.getIndex()).contains(toDelete)) && taskTags != null) {
			for (String s : taskTags) {
				utd.removeTag(s);
			}
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
		}
		
		removeFromAllLists(modifiedCopy, toDelete);
		
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
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_DELETE_SUCCESSFUL));
	}

	// Search for all expired and pending Tasks whose names contain searchPhrase. searchPhrase is not case sensitive.
	// TODO: search list includes completed tasks as well?
	// TODO: improved search
	public LogicFeedback search(ArrayList<ArrayList<Task>> originalCopy, ArrayList<ArrayList<Task>> modifiedCopy, 
			                    ProcessedObject po) {
		String searchPhrase = po.getSearchPhrase(); // Validity of searchPhrase is already checked in ParseSearch

		ArrayList<Task> expiredList = modifiedCopy.get(ListID.EXPIRED.getIndex());
		ArrayList<Task> actionList = modifiedCopy.get(ListID.ACTION.getIndex());
		actionList.clear();
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
		
		if (actionList.isEmpty()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SEARCH_NOT_FOUND));
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
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
		if (modifiedCopy.get(ListID.COMPLETED.getIndex()).contains(toComplete)) {
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
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_DONE_SUCCESSFUL));
	}

	// Updates an indexed task's name on the current tab and saves the updated lists to disk.
	public LogicFeedback updateByIndexChangeName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			                                     ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		int taskIndex = po.getIndex();
		String newTaskName = po.getNewTaskName();
		String exceptionMsg;

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		Task toUpdate;

		try {
			toUpdate = targetList.get(taskIndex).getDuplicate();
		} catch (IndexOutOfBoundsException e) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, taskIndex + 1);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		// Cannot update expired or completed tasks
		if (modifiedCopy.get(ListID.COMPLETED.getIndex()).contains(toUpdate)
			|| modifiedCopy.get(ListID.EXPIRED.getIndex()).contains(toUpdate)) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		updateAllLists(modifiedCopy, toUpdate, newTaskName);
		
		// Check if user used "set" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
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
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_UPDATE_SUCCESSFUL));
	}

	// Updates an indexed task's date on the current tab and saves the updated lists to disk.
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
		
		// Cannot update expired or completed tasks
		if (modifiedCopy.get(ListID.COMPLETED.getIndex()).contains(toUpdate)
			|| modifiedCopy.get(ListID.EXPIRED.getIndex()).contains(toUpdate)) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID;
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
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);	
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_UPDATE_SUCCESSFUL));
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
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, new Exception(LogicConstants.MSG_UPDATE_SUCCESSFUL));
	}

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
		
		utd.setTags(previousTagList); 
		if (!utd.saveTagDatabase()) { // This tries to adds another copy of previousTagList to history, be mindful
			history.add(currentSuperList);
			history.addTagList(currentTagList);
			utd.setTags(currentTagList);
			return new LogicFeedback(currentSuperList, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
		}
		history.popTags(); // To remove the extra copy mentioned above
		
		try {
			saveAllTasks(previousSuperList); // Same as above, this tries to add another copy of previousSuperList to 
			                                 // history.
		} catch (Exception e) {
			utd.setTags(currentTagList);
			history.add(currentSuperList);
			history.addTagList(currentTagList);
			return new LogicFeedback(currentSuperList, po, e);
		}
		history.pop();
		
		taskLists = cloneLists(previousSuperList);
		return new LogicFeedback(previousSuperList, po, null);
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
	// Lists that previously contained oldTask but should not contain changedTask are updated.
	// Lists that should contain changedTask are updated as well.
	// This method may mess up the order of lists.
	private void updateAllLists(ArrayList<ArrayList<Task>> taskLists, Task oldTask, Task changedTask) {
		removeFromAllLists(taskLists, oldTask);
		
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
		} else if (listIndex == ListID.PENDING.getIndex() || listIndex == ListID.ACTION.getIndex()) {
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
	}*/
	
	
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
}