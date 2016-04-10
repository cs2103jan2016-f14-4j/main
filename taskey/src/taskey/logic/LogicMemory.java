package taskey.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import taskey.constants.UiConstants.ContentBox;
import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.parser.TimeConverter;
import taskey.storage.Storage;

/**
 * @@author A0134177E
 * This class serves as the memory component for the Logic class. It holds references to all the Task and TagCategory
 * objects in use for the session. Each time a command is executed, the data in this component will be modified. 
 * Note that the data in this component will not be saved to disk unless the user enters the "save" command.  
 */
public class LogicMemory {
	
    //================================================================================
    // Constants
    //================================================================================
	
	public static final int NUM_TASK_LISTS = 8;
	public static final int NUM_SECONDS_1_DAY = 86400;
	
	// Indices of each list
	public static final int INDEX_THIS_WEEK = 0;
	public static final int INDEX_PENDING = 1;
	public static final int INDEX_EXPIRED = 2;
	public static final int INDEX_FLOATING = 3;
	public static final int INDEX_DEADLINE = 4;
	public static final int INDEX_EVENT = 5;
	public static final int INDEX_COMPLETED = 6;
	public static final int INDEX_ACTION = 7;
	
    //================================================================================
    // Fields
    //================================================================================
	
	private Storage storage;
	private ArrayList<ArrayList<Task>> taskLists;
	private ArrayList<TagCategory> tagCategoryList;
	
    //================================================================================
    // Constructors
    //================================================================================
	
	LogicMemory() {
		storage = new Storage();
		initializeTaskLists();
		initializeTagCategoryList();
	}
	
    //================================================================================
    // Accessors
    //================================================================================

	ArrayList<ArrayList<Task>> getTaskLists() {
		assert(taskLists != null);
		assert(!taskLists.contains(null));
		assert(taskLists.size() == NUM_TASK_LISTS);
		sortTaskLists();
		return taskLists;
	}

	void setTaskLists(ArrayList<ArrayList<Task>> taskLists) {
		assert(taskLists != null);
		assert(!taskLists.contains(null));
		assert(taskLists.size() == NUM_TASK_LISTS);
		this.taskLists = taskLists;
	}

	ArrayList<TagCategory> getTagCategoryList() {
		assert(tagCategoryList != null);
		assert(!tagCategoryList.contains(null));
		sortTagCategoryList();
		return tagCategoryList;
	}

	void setTagCategoryList(ArrayList<TagCategory> tagCategoryList) {
		assert(tagCategoryList != null);
		assert(!tagCategoryList.contains(null));
		this.tagCategoryList = tagCategoryList;
	}
	
    //================================================================================
    // Command Methods
    //================================================================================
	
	/**
	 * Adds a floating task to the task lists.
	 * @param taskToAdd
	 * @throws LogicException if the task to add is a duplicate
	 */
	void addFloating(Task taskToAdd) throws LogicException {
		if (taskAlreadyExists(taskToAdd)) {
			throw new LogicException(LogicException.MSG_ERROR_DUPLICATE_TASKS);
		}
		
		taskLists.get(INDEX_PENDING).add(taskToAdd);
		taskLists.get(INDEX_FLOATING).add(taskToAdd);
		clearActionList(); // Action list not relevant for "add" command.
	}
	
	/**
	 * Adds a deadline task to the task lists.
	 * @param taskToAdd
	 * @throws LogicException if the task to add is a duplicate, or is already expired
	 */
	void addDeadline(Task taskToAdd) throws LogicException {
		if (taskAlreadyExists(taskToAdd)) {
			throw new LogicException(LogicException.MSG_ERROR_DUPLICATE_TASKS);
		}
		
		clearActionList(); // Action list not relevant for "add" command.
		
		if (taskToAdd.isExpired()) {
			taskLists.get(INDEX_EXPIRED).add(taskToAdd);
			addTags(taskToAdd.getTaskTags());
			throw new LogicException(LogicException.MSG_SUCCESS_ADD_EXPIRED);
		}
		
		taskLists.get(INDEX_PENDING).add(taskToAdd);
		taskLists.get(INDEX_DEADLINE).add(taskToAdd);
	
		if (taskToAdd.isThisWeek()) {
			taskLists.get(INDEX_THIS_WEEK).add(taskToAdd);
		}
	}
	
	/**
	 * Adds an event task to the task lists.
	 * @param taskToAdd
	 * @throws LogicException if the task to add is a duplicate, or is already expired
	 */
	void addEvent(Task taskToAdd) throws LogicException {
		if (taskAlreadyExists(taskToAdd)) {
			throw new LogicException(LogicException.MSG_ERROR_DUPLICATE_TASKS);
		}
		
		clearActionList(); // Action list not relevant for "add" command.
		
		if (taskToAdd.isExpired()) {
			taskLists.get(INDEX_EXPIRED).add(taskToAdd);
			addTags(taskToAdd.getTaskTags());
			throw new LogicException(LogicException.MSG_SUCCESS_ADD_EXPIRED);
		}
		
		taskLists.get(INDEX_PENDING).add(taskToAdd);
		taskLists.get(INDEX_EVENT).add(taskToAdd);

		if (taskToAdd.isThisWeek()) {
			taskLists.get(INDEX_THIS_WEEK).add(taskToAdd);
		}
	}
	
	/**
	 * Change the directory where task and tag category data are saved. If this method succeeds, all saved files are 
	 * moved from the current save directory to the new directory.
	 * @param pathName        the new directory pathname
	 * @@author A0121618M
	 * @throws LogicException if the directory could not be created or is invalid; an error occurred while moving files; or
	 * 						  savefiles already exist in the given directory (this is a signal for Logic to load these files)
	 */
	void changeSaveDirectory(String pathName) throws LogicException {
		try {
			storage.setDirectory(pathName, true); //true to move files
		} catch (FileAlreadyExistsException fae) { //new directory contains existing tasks data files
			try {
				storage.setDirectory(pathName, false); //prepare Storage to load from the new directory
			} catch (Exception e) { //pathName has already been verified at this point, so nothing should be thrown here
				throw new LogicException(LogicException.MSG_ERROR_SETDIR);
			}
			initializeTaskLists(); //load from the new directory
			initializeTagCategoryList();
			
			// The following exception message signals that LogicMemory has loaded from the new directory.
			// This is to tell Logic to clear History's stacks and add the newly loaded lists to History.
			throw new LogicException(LogicException.MSG_SUCCESS_SETDIR_LOAD);
			
		} catch (InvalidPathException e) { //path contains invalid characters
			throw new LogicException(e.getMessage());
			
		} catch (NotDirectoryException e) { //path goes to a file and not a directory
			throw new LogicException("The given path is not a directory: " + e.getMessage());
			
		} catch (AccessDeniedException e) { //access denied when creating a directory or writing a file
			throw new LogicException("Access denied to " +
									Paths.get((e.getOtherFile() == null) ? e.getFile() : e.getOtherFile()).getParent());
			
		} catch (FileSystemException e) { //reserved/illegal filename or nonexistent root drive letter
			throw new LogicException(e.getMessage());
			
		} catch (IOException ioe) { //any other IO error while creating a directory or writing a file
			throw new LogicException(ioe.getMessage());
			
		} catch (Exception e) { //unknown exception
			throw new LogicException(LogicException.MSG_ERROR_SETDIR);
		}
	}
	
	/**
	 * @@author A0134177E
	 * Removes an indexed task from the specified task list, and deletes all its tags from the tag category list.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be deleted
	 * @return           the Task that was deleted
	 * @throws LogicException if the index is invalid
	 */
	Task deleteByIndex(ContentBox contentBox, int taskIndex) throws LogicException {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new LogicException(LogicException.MSG_ERROR_INVALID_INDEX);
		}
		
		Task toDelete = targetList.get(taskIndex);

		if (!taskLists.get(INDEX_COMPLETED).contains(toDelete)) { // Completed tasks already have their tags removed.
			removeTaskTags(toDelete.getTaskTags());
		}
		
		removeFromAllLists(toDelete);
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			clearActionList();
		}
		
		return toDelete;
	}
	
	/**
	 * Deletes all tasks with the given tag name from the expired and pending lists, and updates the tag category list 
	 * accordingly.
	 * @param tagName
	 * @throws LogicException if the tag name was not found in the expired and pending lists
	 */
	void deleteByTagName(String tagName) throws LogicException {
		boolean tagFound = removeTaggedTasks(taskLists.get(INDEX_EXPIRED), tagName);
		tagFound = tagFound || removeTaggedTasks(taskLists.get(INDEX_PENDING), tagName);
		
		if (!tagFound) {
			throw new LogicException(LogicException.MSG_ERROR_TAG_NOT_FOUND);
		}
		
		clearActionList();
	}
	
	/**
	 * Marks an indexed task from the specified task list as done, and deletes all its tags from the tag category list.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be completed
	 * @throws LogicException if index is invalid or the user is trying to mark an archived task as done
	 */
	void doneByIndex(ContentBox contentBox, int taskIndex) throws LogicException {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new LogicException(LogicException.MSG_ERROR_INVALID_INDEX);
		}
		
		Task toComplete = targetList.get(taskIndex);
		
		if (taskLists.get(INDEX_COMPLETED).contains(toComplete)) {
			throw new LogicException(LogicException.MSG_ERROR_DONE_INVALID);
		}
		
		removeFromAllLists(toComplete);
		taskLists.get(INDEX_COMPLETED).add(toComplete);
		removeTaskTags(toComplete.getTaskTags());
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			clearActionList();
		}
	}
	
	/**
	 * Updates an indexed task from the specified task list, and also updates all lists that contained the updated task.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be updated
	 * @param newName    the new name to update the task to
	 * @param newTask    the task with the new date
	 * @throws LogicException if the index is invalid, or the updated task is a duplicate or expired
	 */
	void updateByIndexChangeBoth(ContentBox contentBox, int taskIndex, String newName, Task newTask) throws LogicException {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new LogicException(LogicException.MSG_ERROR_INVALID_INDEX);
		}
		
		Task toUpdate = targetList.get(taskIndex);
		
		if (taskLists.get(INDEX_COMPLETED).contains(toUpdate)) {
			throw new LogicException(LogicException.MSG_ERROR_UPDATE_INVALID);
		}
		
		newTask.setTaskName(newName);
		if (toUpdate.getTaskTags() != null) {
			newTask.setTaskTags(new ArrayList<String>(toUpdate.getTaskTags()));
		}
		
		if (taskAlreadyExists(newTask)) {
			throw new LogicException(LogicException.MSG_ERROR_DUPLICATE_TASKS);
		}
		
		removeFromAllLists(toUpdate);
		addTaskToLists(contentBox, newTask);
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			clearActionList();
		}
		
		if (newTask.isExpired()) {
			throw new LogicException(LogicException.MSG_SUCCESS_UPDATE_EXPIRED);
		}
	}
	
	/**
	 * Updates an indexed task from the specified task list, and also updates all lists that contained the updated task.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be updated
	 * @param newTask    the task with the new date
	 * @throws LogicException if the index is invalid, or the updated task is a duplicate or expired
	 */
	void updateByIndexChangeDate(ContentBox contentBox, int taskIndex, Task newTask) throws LogicException {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new LogicException(LogicException.MSG_ERROR_INVALID_INDEX);
		}
		
		Task toUpdate = targetList.get(taskIndex);
		updateByIndexChangeBoth(contentBox, taskIndex, toUpdate.getTaskName(), newTask);
	}
	
	/**
	 * Updates an indexed task from the specified task list, and also updates all lists that contained the updated task.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be updated
	 * @param newName    the new name to update the task to
	 * @throws LogicException if the index is invalid, or the updated task is a duplicate or expired
	 */
	void updateByIndexChangeName(ContentBox contentBox, int taskIndex, String newName) throws LogicException {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new LogicException(LogicException.MSG_ERROR_INVALID_INDEX);
		}
		
		Task toUpdate = targetList.get(taskIndex);
		Task toUpdateCopy = new Task(toUpdate);
		updateByIndexChangeBoth(contentBox, taskIndex, newName, toUpdateCopy);
	}
	
	void updateByIndexChangePriority(ContentBox contentBox, int taskIndex, int newPriority) throws LogicException {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new LogicException(LogicException.MSG_ERROR_INVALID_INDEX);
		}
		
		Task toUpdate = targetList.get(taskIndex);
		
		if (taskLists.get(INDEX_COMPLETED).contains(toUpdate)) {
			throw new LogicException(LogicException.MSG_ERROR_UPDATE_INVALID);
		}
		
		removeFromAllLists(toUpdate);
		toUpdate.setPriority(newPriority);
		addTaskToLists(contentBox, toUpdate);
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			clearActionList();
		}
	}
	
	/**
	 * Saves the current task lists and tag category list in memory to disk.
	 * @throws LogicException if error occurred during save
	 */
	void save() throws LogicException {
		try {
			storage.saveAllTasklists(ListCloner.cloneTaskLists(taskLists));
			storage.saveTaglist(ListCloner.cloneTagCategoryList(tagCategoryList));
		} catch (Exception e) {
			throw new LogicException(LogicException.MSG_ERROR_SAVE);
		}
	}
	
	/**
	 * Search for all expired and pending tasks via the given search phrase (not case sensitive).
	 * @param searchPhrase
	 * @throws LogicException if no matches were found
	 */
	void search(String searchPhrase) throws LogicException {
		ArrayList<Task> actionList = taskLists.get(INDEX_ACTION);
		clearActionList();
		//actionList.addAll(getSearchResults(taskLists.get(INDEX_EXPIRED), searchPhrase));
		actionList.addAll(getSearchResults(taskLists.get(INDEX_PENDING), searchPhrase));
		String exceptionMsg = String.format(LogicException.MSG_SUCCESS_SEARCH, searchPhrase);
		throw new LogicException(exceptionMsg);
	}
	
	/**
	 * Updates the action list based on the view type. When the user wants to view tasks by priority i.e. "high", "medium"
	 * or "low", only expired and pending tasks will be displayed.
	 * @param viewType
	 * @throws LogicException 
	 */
	void viewBasic(String viewType) throws LogicException {
		String exceptionMsg;
		
		switch (viewType) {
			case "general":
				taskLists.set(INDEX_ACTION, new ArrayList<Task>(taskLists.get(INDEX_FLOATING)));
				exceptionMsg = String.format(LogicException.MSG_SUCCESS_VIEW, viewType) + " tasks.";
				throw new LogicException(exceptionMsg);
			
			case "deadlines":
				taskLists.set(INDEX_ACTION, new ArrayList<Task>(taskLists.get(INDEX_DEADLINE)));
				break;
				
			case "events":
				taskLists.set(INDEX_ACTION, new ArrayList<Task>(taskLists.get(INDEX_EVENT)));
				break;
				
			case "archive":
				taskLists.set(INDEX_ACTION, new ArrayList<Task>(taskLists.get(INDEX_COMPLETED)));
				break;
				
			case "high":
			case "medium":
			case "low":
				clearActionList();
				// viewPriority(taskLists.get(INDEX_EXPIRED), viewType);
				viewPriority(taskLists.get(INDEX_PENDING), viewType);
				exceptionMsg = String.format(LogicException.MSG_SUCCESS_VIEW_PRIORITY, viewType);
				throw new LogicException(exceptionMsg);
							
			case "help": // Display of help will be handled by UI. UI should disallow any commands while in help mode.
				clearActionList();
				break;
			
			case "today":
				clearActionList();
				viewToday(taskLists.get(INDEX_PENDING));
				throw new LogicException(LogicException.MSG_SUCCESS_VIEW_TODAY);
			
			case "tomorrow":
				clearActionList();
				viewTomorrow(taskLists.get(INDEX_PENDING));
				throw new LogicException(LogicException.MSG_SUCCESS_VIEW_TOMORROW);
			
			default: // Should not reach this point
				exceptionMsg = String.format(LogicException.MSG_ERROR_VIEWTYPE, viewType);
				throw new LogicException(exceptionMsg);
		}
		
		exceptionMsg = String.format(LogicException.MSG_SUCCESS_VIEW, viewType) + ".";
		throw new LogicException(exceptionMsg);
	}

	/**
	 * Updates the action list with all the expired and pending tasks that contain at least one of the tag categories 
	 * that the user wants to view.
	 * @param tagNames
	 * @throws LogicException if the tag categories do not exist
	 */
	void viewTags(ArrayList<String> tagNames) throws LogicException {
		clearActionList();
		//boolean tagFound = viewTaggedTasks(taskLists.get(INDEX_EXPIRED), tagNames);
		boolean tagFound = viewTaggedTasks(taskLists.get(INDEX_PENDING), tagNames);
		
		throw new LogicException(LogicException.MSG_SUCCESS_VIEW_TAGS);
	}
	
    //================================================================================
    // Miscellaneous
    //================================================================================
	
	private void initializeTaskLists() {
		taskLists = storage.loadAllTasklists();
		assert(taskLists != null);
		
		// THIS_WEEK and ACTION lists are not loaded from storage and hence must be manually added.
		taskLists.add(INDEX_THIS_WEEK, new ArrayList<Task>());
		taskLists.add(INDEX_ACTION, new ArrayList<Task>());
		assert(taskLists.size() == NUM_TASK_LISTS);
		
		synchroniseTaskLists();
	}
	
	private void initializeTagCategoryList() {
		tagCategoryList = storage.loadTaglist();
	}
	
	/** Updates all the lists based on the current time on the user's computer clock. For example, deadline tasks that
	 *  are newly expired will be removed from the DEADLINE list and PENDING lists and added to the EXPIRED list. The 
	 *  tag category list is not affected.
	 */
	private void synchroniseTaskLists() {
		ArrayList<Task> thisWeekList = taskLists.get(INDEX_THIS_WEEK);
		ArrayList<Task> expiredList = taskLists.get(INDEX_EXPIRED);
		ArrayList<Task> pendingList = taskLists.get(INDEX_PENDING);

		for (Iterator<Task> it = pendingList.iterator(); it.hasNext();) { // Iterator is used for safe removal of
			                                                              // elements while iterating
			Task task = it.next();
			
			if (task.isExpired()) {
				it.remove();
				removeFromAllLists(task);
				expiredList.add(task);
			} else if (task.isThisWeek()) {
				thisWeekList.add(task);
			}
		}
	}
	
	void clearAllTaskLists() {
		for (int i = 0; i < taskLists.size(); i++) {
			taskLists.get(i).clear();
		}
	}
	
	private void clearActionList() {
		taskLists.get(INDEX_ACTION).clear();
	}
	
	void clearTagCategoryList() {
		tagCategoryList.clear();
	}
	
	private void sortTaskLists() {
		for (ArrayList<Task> list : taskLists) {
			Collections.sort(list, Collections.reverseOrder()); // Sort in reverse order because compareTo logic in
			                                                    // Task.java is reversed
		}
	}
	
	private void sortTagCategoryList() {
		Collections.sort(tagCategoryList); 
	}
	
	/** 
	 * Returns the index of the list corresponding to the current tab user is in.
	 * @param currentContent the current tab user is in
	 * @return
	 */
	private int getListIndex(ContentBox currentContent) {
		switch (currentContent) {
			case THIS_WEEK:
				return INDEX_THIS_WEEK;
			
			case PENDING:
				return INDEX_PENDING;
			
			case EXPIRED:
				return INDEX_EXPIRED;
				
			case ACTION:
				return INDEX_ACTION;
			
			default:
				return -1; // Stub
		}
	}
	
	/** 
	 * Adds the new Task to all the lists it should belong to, depending on the current tab the user is in.
	 * @param contentBox the current tab user is in
	 * @param newTask
	 */
	private void addTaskToLists(ContentBox contentBox, Task newTask) {
		for (int i = 0; i < taskLists.size(); i++) {
			if (belongsToList(contentBox, i, newTask)) {
				taskLists.get(i).add(newTask);
			}
		}
	}
	
	/** 
	 * Returns true if and only if the new Task belongs to the list specified by listIndex, depending on the current
	 * tab the user is in 
	 * @param contentBox the current tab user is in
	 * @param listIndex
	 * @param newTask
	 * @return
	 */
	private boolean belongsToList(ContentBox contentBox, int listIndex, Task newTask) {
		String taskType = newTask.getTaskType();
		boolean isExpired = newTask.isExpired();

		if (listIndex == INDEX_THIS_WEEK) {
			return (!isExpired && newTask.isThisWeek());
		} else if (listIndex == INDEX_PENDING) {
			return !isExpired;
		} else if (listIndex == INDEX_EXPIRED) {
			return isExpired;
		} else if (listIndex == INDEX_FLOATING) {
			return (taskType.equals("FLOATING")); // TODO: remove magic Strings
		} else if (listIndex == INDEX_DEADLINE) {
			return (!isExpired && taskType.equals("DEADLINE"));
		} else if (listIndex == INDEX_EVENT) {
			return (!isExpired && taskType.equals("EVENT"));
		} else if (listIndex == INDEX_ACTION) {
			return (!isExpired && contentBox.equals(ContentBox.ACTION));
		} else {
			return false;
		}
	}
	
	private void removeFromAllLists(Task toRemove) {
		taskLists.get(INDEX_THIS_WEEK).remove(toRemove);
		taskLists.get(INDEX_PENDING).remove(toRemove);
		taskLists.get(INDEX_EXPIRED).remove(toRemove);
		taskLists.get(INDEX_FLOATING).remove(toRemove);
		taskLists.get(INDEX_DEADLINE).remove(toRemove);
		taskLists.get(INDEX_EVENT).remove(toRemove);
		taskLists.get(INDEX_COMPLETED).remove(toRemove);
		taskLists.get(INDEX_ACTION).remove(toRemove);
	}
	
	/** 
	 * Removes all tasks from the given list which contain a tag with the given name. The tasks will be removed from
	 * other lists that contain them as well. For each task that is removed, the tag category list will be updated 
	 * accordingly.
	 * 
	 * @param list
	 * @param tagName
	 * @return true if and only if at least one task was removed from the given list
	 */
	private boolean removeTaggedTasks(ArrayList<Task> list, String tagName) {
		boolean taskRemoved = false;
		
		for (Iterator<Task> it = list.iterator(); it.hasNext();) { // Iterator is for save removal of elements while 
			                                                       // iterating
			Task task = it.next();
			ArrayList<String> taskTags = task.getTaskTags();
			if (taskTags != null && taskTags.contains(tagName)) {
				it.remove();
				removeFromAllLists(task); // This is safe because the task has already been removed from the current list.
				removeTaskTags(task.getTaskTags());
				taskRemoved = true;
			}
		}
		
		return taskRemoved;
	}
	
	/** 
	 * Views all tasks from the given list which contain at least one of the tags specified in tagNames. 
	 * 
	 * @param list
	 * @param tagName
	 * @return true if and only if at least one task was removed from the given list
	 */
	private boolean viewTaggedTasks(ArrayList<Task> list, ArrayList<String> tagNames) {
		boolean taskFound = false;
		
		for (Iterator<Task> it = list.iterator(); it.hasNext();) { 
			Task task = it.next();
			
			for (String s : tagNames) {
				ArrayList<String> taskTags = task.getTaskTags();
				if (taskTags != null && taskTags.contains(s)) {
					taskLists.get(INDEX_ACTION).add(task);
					taskFound = true;
					break;
				}
			}
		}
		
		return taskFound;
	}
	
	/** 
	 * Views all tasks from the given list which are of the specified priority. 
	 * 
	 * @param list
	 * @param priority
	 * @return true if and only if at least one task was found with the specified priority
	 */
	private boolean viewPriority(ArrayList<Task> list, String priority) {
		boolean priorityFound = false;
		int priorityNumber;
		
		if (priority.equals("high")) {
			priorityNumber = 3;
		} else if (priority.equals("medium")) {
			priorityNumber = 2;
		} else { // "low"
			priorityNumber = 1;
		}
		
		for (Iterator<Task> it = list.iterator(); it.hasNext();) { 
			Task task = it.next();
			
			if (task.getPriority() == priorityNumber) {
				taskLists.get(INDEX_ACTION).add(task);
				priorityFound = true;
			}
		}
		
		return priorityFound;
	}
	
	/** Views all tasks from the given list which are happening today. 
	 * 
	 * @param list
	 */
	private void viewToday(ArrayList<Task> list) {
		TimeConverter tc = new TimeConverter();
		
		for (Task t : list) {
			if (t.getTaskType().equals("DEADLINE") && tc.isToday(t.getDeadlineEpoch())) {
				taskLists.get(INDEX_ACTION).add(t);
			} else if (t.getTaskType().equals("EVENT")) {
				long currTime = tc.getCurrTime();
				long startDate = t.getStartDateEpoch();
				long endDate = t.getEndDateEpoch();
				
				if (tc.isToday(startDate) || tc.isToday(endDate)
				    || (startDate <= currTime && currTime <= endDate)) {
					taskLists.get(INDEX_ACTION).add(t);
				}
			}
		}
	}
	
	/** Views all tasks from the given list which are happening tomorrow. 
	 * 
	 * @param list
	 */
	private void viewTomorrow(ArrayList<Task> list) {
		TimeConverter tc = new TimeConverter();
		
		for (Task t : list) {
			if (t.getTaskType().equals("DEADLINE") && tc.isTmr(t.getDeadlineEpoch())) {
				taskLists.get(INDEX_ACTION).add(t);
			} else if (t.getTaskType().equals("EVENT")) {
				long tmrTime = tc.getCurrTime() + NUM_SECONDS_1_DAY;
				long startDate = t.getStartDateEpoch();
				long endDate = t.getEndDateEpoch();
				
				if (tc.isTmr(startDate) || tc.isTmr(endDate)
				    || (startDate <= tmrTime && tmrTime <= endDate)) {
					taskLists.get(INDEX_ACTION).add(t);
				}
			}
		}
	}
	
	/** 
	 * Adds all task tags in the given list into the tag category list.
	 * 
	 * @param taskTags
	 */
	void addTags(ArrayList<String> taskTags) {
		if (taskTags != null) {
			for (String s : taskTags) {
				addTag(s);
			}
		}
	}
	
	/** 
	 * Removes all task tags in the given list from the tag category list.
	 * 
	 * @param taskTags
	 */
	private void removeTaskTags(ArrayList<String> taskTags) {
		if (taskTags != null) {
			for (String s : taskTags) {
				removeTag(s);
			}
		}
	}
	
	/**
	 *  Returns true if and only if the given task already exists in any of the task lists.
	 * @param task
	 * @return
	 */
	private boolean taskAlreadyExists(Task task) {
		return (taskLists.get(INDEX_PENDING).contains(task) || taskLists.get(INDEX_COMPLETED).contains(task)
				|| taskLists.get(INDEX_EXPIRED).contains(task));
	}
	
	/**
	 * Add a new tag to the tag category list
	 * @param tagToAdd
	 */
	void addTag(String tagToAdd) { 
		int tagIndex = getTagIndex(tagToAdd);
		
		if (tagIndex == -1) { // Tag category list does not contain the tag to be added; add a new category for that tag.
			tagCategoryList.add(new TagCategory(tagToAdd)); 
		} else { // Tag category list already contains the tag to be added; increase the number of tags in that 
			     // category by one.
			tagCategoryList.get(tagIndex).increaseCount();
		}
	}
	
	/**
	 * Remove a tag from the tag category list. The tag should currently exist in the tag category list.
	 * @param tagToRemove
	 */
	private void removeTag(String tagToRemove) {
		int tagIndex = getTagIndex(tagToRemove);
		assert(tagIndex != -1);
		
		if (tagCategoryList.get(tagIndex).getNumTags() == 1) {
			tagCategoryList.remove(tagIndex);
		} else {
			tagCategoryList.get(tagIndex).decreaseCount();
		}
	}
	
	/**
	 * Returns the index of the specified tag name in the tag category list. If the tag name is not found, 
	 * -1 is returned.
	 * @param tagName
	 * @return
	 */
	private int getTagIndex(String tagName) {
		for (int i = 0; i < tagCategoryList.size(); i++) {
			if (tagCategoryList.get(i).getTagName().equals(tagName)) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Performs a search on the given list using the given search phrase, and returns a list of search results.
	 * The search algorithm uses the Levenshtein distance metric.
	 * @param list
	 * @param searchPhrase
	 * @return
	 */
	private ArrayList<Task> getSearchResults(ArrayList<Task> list, String searchPhrase) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		String[] searchTokens = searchPhrase.toLowerCase().split(" ");
		
		for (Task task : list) {
			double sumOfLevenshteinRatios = 0;
			boolean foundMatch = false;
			String[] taskNameTokens = task.getTaskName().toLowerCase().split(" ");
			
			if (searchTokens.length == 1) {
				if (searchPhrase.length() <= 2) { // For search phrases that are only one word with <= 2 characters,
					                              // perform whole word search only, for more precision.
					if (searchWholeWord(searchPhrase, taskNameTokens)) {
						searchResults.add(task);
					}
					continue;
				} else if (searchPhrase.length() >= 3 && searchSubstring(searchPhrase, taskNameTokens)) {
					searchResults.add(task);
					foundMatch = true;
				}
			}
			
			if (foundMatch) {
				continue;
			}
			
			for (String searchToken : searchTokens) {
				sumOfLevenshteinRatios += getMaxLevenshteinRatio(searchToken, taskNameTokens);
			}
			
			if ((sumOfLevenshteinRatios / searchTokens.length) >= 0.65) {
				searchResults.add(task);
			}
		}
		
		return searchResults;
	}
	
	/**
	 * Returns true if and only if the entire search phrase matches at least one of the task name tokens exactly.
	 * 
	 * @param searchToken
	 * @param taskNameTokens
	 * @return
	 */
	private static boolean searchWholeWord(String searchPhrase, String[] taskNameTokens) {
		for (String s : taskNameTokens) {
			if (searchPhrase.equals(s)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true if and only if the entire search phrase is a substring of at least one of the task name tokens.
	 * @param searchPhrase
	 * @param taskNameTokens
	 * @return
	 */
	private static boolean searchSubstring(String searchPhrase, String[] taskNameTokens) {
		for (String s : taskNameTokens) {
			if (s.contains(searchPhrase)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns the maximum Levenshtein ratio when the search token is compared to each of the task name tokens.
	 * <p>The Levenshtein ratio between two Strings is calculated as follows: 
	 * <br>First, find the Levenshtein distance between the two Strings.
	 * <br>Then, divide the distance by the length of the longest String.
	 * <br>Finally, subtract the result from the number 1.
	 * 
	 * @param searchToken
	 * @param taskNameTokens
	 * @return
	 */
	private static double getMaxLevenshteinRatio(String searchToken, String[] taskNameTokens) {
		double maxRatio = 0;
		
		for (String s : taskNameTokens) {
			int currDist = getLevenshteinDist(searchToken, s);
			double currRatio = 1 - ((double) currDist / Math.max(searchToken.length(), s.length()));
			
			if (currRatio > maxRatio) {
				maxRatio = currRatio;
			}
		}
		
		return maxRatio;
	}

	/**
	 * Returns the Levenshtein distance between two Strings. The Levenshtein distance between two Strings is the minimum 
	 * number of single-character edits (i.e. insertions, deletions or substitutions) required to change one String into 
	 * the other.
	 *  
	 * @param source
	 * @param target
	 * @return
	 */
	private static int getLevenshteinDist(String source, String target) {
		// For all i and j, d[i][j] will hold the Levenshtein distance between the first i characters of source and 
		// the first j characters of target
		int[][] d = new int[source.length() + 1][target.length() + 1];
		
		// If target is an empty string, then the first i characters of source can be converted to target by deleting
		// each of these i characters, yielding a Levenshtein distance of i.
		for (int i = 1; i <= source.length(); i++) {
		      d[i][0] = i;
		}
		
		// If source is an empty string, then the first j characters of target can be converted to source by deleting
		// each of these j characters, yielding a Levenshtein distance of j.
		for (int j = 1; j <= target.length(); j++) {
		      d[0][j] = j;
		}
		
		for (int j = 1; j <= target.length(); j++) {
			for (int i = 1; i <= source.length(); i++) {
				int substitutionCost = 0;
				
				if (source.charAt(i - 1) != target.charAt(j - 1)) { // i-th character from source and j-th character
		        	                                                // from target do not match (1-based indices)
					substitutionCost = 1;
				}
				
				d[i][j] = Math.min(d[i-1][j] + 1, // Deleting i-th character from source yields a cost of 1
		                      	   Math.min(d[i][j-1] + 1, // Deleting j-th character from target yields a cost of 1
		                                    d[i-1][j-1] + substitutionCost)); // Substituting the i-th character from 
				                                                              // source to match the j-th character in
				                                                              // target
			}
	
		}
		
		return d[source.length()][target.length()];
	}
	
	// For testing
	public static void main(String[] args) {
		assert(getLevenshteinDist("kitten", "sitting") == 3);
		assert(getLevenshteinDist("sitting", "kitten") == 3);
		assert(getLevenshteinDist("", "abcd") == 4);
		assert(getLevenshteinDist("abcd", "abcd") == 0);
	}
}
