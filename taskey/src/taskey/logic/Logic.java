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
 * The Logic class provides a simplified interface to the Logic component and hides the more complex aspects, 
 * such as memory management and the internal details of the execution of each specific command. As a result, from the 
 * perspective of UI, all UI needs to do is call the executeCommand() method of Logic whenever the user enters a 
 * command.
 */
public class Logic {
	
    //================================================================================
    // Fields
    //================================================================================
	
	private Parser parser;
	private History history;
	private CommandExecutor cmdExecutor;
	private LogicMemory logicMemory;
	
    //================================================================================
    // Constructors
    //================================================================================
	
	public Logic() {
		parser = new Parser();
		history = new History();
		cmdExecutor = new CommandExecutor();
		logicMemory = new LogicMemory();
		updateHistory();
	}
	
    //================================================================================
    // Accessors
    //================================================================================
	
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
	
    //================================================================================
    // Interface Methods
    //================================================================================
	
	/**
	 * Executes the user supplied command.
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
    		cmd = new Clear();
			return executeClear(cmd);
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

			case "DELETE_BY_INDEX":
				cmd = new DeleteByIndex(currentContent, po.getIndex());
				return executeDelete(po, cmd);
						
			case "DELETE_BY_CATEGORY":
				cmd = new DeleteByTagName(po.getCategory());
				return executeDelete(po, cmd);
			
			case "DONE_BY_INDEX":
				cmd = new DoneByIndex(currentContent, po.getIndex());
				return executeDone(po, cmd);			
				
			case "UPDATE_BY_INDEX_CHANGE_NAME":
				cmd = new UpdateByIndexChangeName(currentContent, po.getIndex(), po.getNewTaskName());
				return executeUpdate(po, cmd);
				
			case "UPDATE_BY_INDEX_CHANGE_DATE":
				cmd = new UpdateByIndexChangeDate(currentContent, po.getIndex(), po.getTask());
				return executeUpdate(po, cmd);
			
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				cmd = new UpdateByIndexChangeBoth(currentContent, po.getIndex(), po.getNewTaskName(), po.getTask());
				return executeUpdate(po, cmd);
			
			/*
			case "VIEW_BASIC":
				return viewBasic(originalCopy, po);
			
			case "VIEW_TAGS":
				return viewTags(originalCopy, modifiedCopy, po);

			case "UNDO":
				return undo(po);
				
			case "ERROR":
				return new LogicFeedback(originalCopy, po, new LogicException(po.getErrorType()));*/
				
			case "SEARCH":
				cmd = new Search(po.getSearchPhrase());
				return executeSearch(po, cmd);

			default:
				break;
		}

		return null; // Stub
	}
	
    //================================================================================
    // Command Methods
    //================================================================================

	private LogicFeedback executeClear(Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), new ProcessedObject("CLEAR"), le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), new ProcessedObject("CLEAR"), 
				                 new LogicException(LogicException.MSG_SUCCESS_CLEAR));
	}
	
	private LogicFeedback executeAdd(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_ADD));
	}
	
	private LogicFeedback executeDelete(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_DELETE));
	}
	
	private LogicFeedback executeDone(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_DONE));
	}
	
	private LogicFeedback executeUpdate(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_UPDATE));
	}
	
	private LogicFeedback executeSearch(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, null);
	}
	
    //================================================================================
    // Miscellaneous
    //================================================================================
	
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
			return new LogicFeedback(originalCopy, po, new LogicException(LogicConstants.MSG_EXCEPTION_TAG_NOT_FOUND));
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
			return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
		}
		
		// Cannot update expired or completed tasks
		if (modifiedCopy.get(ListID.COMPLETED.getIndex()).contains(toUpdate)
			|| modifiedCopy.get(ListID.EXPIRED.getIndex()).contains(toUpdate)) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID;
			return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
		}

		updateAllLists(modifiedCopy, toUpdate, newTaskName);
		
		// Check if user used "set" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new LogicException(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (LogicException le) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, new LogicException(LogicConstants.MSG_UPDATE_SUCCESSFUL));
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
			return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
		}
		
		// Cannot update expired or completed tasks
		if (modifiedCopy.get(ListID.COMPLETED.getIndex()).contains(toUpdate)
			|| modifiedCopy.get(ListID.EXPIRED.getIndex()).contains(toUpdate)) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID;
			return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
		}
		
		if (changedTask.getTaskType().equals("DEADLINE")) {
			if (changedTask.getDeadlineEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getDeadline());
				return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
			}
		}
		
		if (changedTask.getTaskType().equals("EVENT")) {
			if (changedTask.getEndDateEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getEndDate());
				return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
			}
		}

		changedTask.setTaskName(toUpdate.getTaskName());
		updateAllLists(modifiedCopy, toUpdate, changedTask);
		
		// Check if user used "set" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new LogicException(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (LogicException le) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);	
		return new LogicFeedback(modifiedCopy, po, new LogicException(LogicConstants.MSG_UPDATE_SUCCESSFUL));
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
			return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
		}
		
		if (changedTask.getTaskType().equals("DEADLINE")) {
			if (changedTask.getDeadlineEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getDeadline());
				return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
			}
		}
		
		if (changedTask.getTaskType().equals("EVENT")) {
			if (changedTask.getEndDateEpoch() < timeConverter.getCurrTime()) {
				exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, changedTask.getEndDate());
				return new LogicFeedback(originalCopy, po, new LogicException(exceptionMsg));
			}
		}

		changedTask.setTaskName(newTaskName);
		updateAllLists(modifiedCopy, toUpdate, changedTask);
		
		// Check if user used "set" from the ACTION tab. If so, don't clear the ACTION list.
		if (!currentContent.equals(ContentBox.ACTION)) {
			modifiedCopy.get(ListID.ACTION.getIndex()).clear();
		}
		
		if (!utd.saveTagDatabase()) {
			return new LogicFeedback(originalCopy, po, new LogicException(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (LogicException le) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, new LogicException(LogicConstants.MSG_UPDATE_SUCCESSFUL));
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
			return new LogicFeedback(currentSuperList, po, new LogicException(LogicConstants.MSG_EXCEPTION_UNDO));
		}
		
		utd.setTags(previousTagList); 
		if (!utd.saveTagDatabase()) { // This tries to adds another copy of previousTagList to history, be mindful
			history.add(currentSuperList);
			history.addTagList(currentTagList);
			utd.setTags(currentTagList);
			return new LogicFeedback(currentSuperList, po, new LogicException(LogicConstants.MSG_EXCEPTION_SAVING_TAGS));
		}
		history.popTags(); // To remove the extra copy mentioned above
		
		try {
			saveAllTasks(previousSuperList); // Same as above, this tries to add another copy of previousSuperList to 
			                                 // history.
		} catch (LogicException le) {
			utd.setTags(currentTagList);
			history.add(currentSuperList);
			history.addTagList(currentTagList);
			return new LogicFeedback(currentSuperList, po, e);
		}
		history.pop();
		
		taskLists = cloneLists(previousSuperList);
		return new LogicFeedback(previousSuperList, po, null);
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
	private void saveAllTasks(ArrayList<ArrayList<Task>> taskLists) throws LogicException {
		try {
			storage.saveAllTasklists(taskLists);
		} catch (StorageException se) {
			throw new LogicException (se.getMessage());
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