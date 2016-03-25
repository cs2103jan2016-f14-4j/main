package taskey.logic;

import java.util.ArrayList;
import java.util.Iterator;

import taskey.constants.UiConstants.ContentBox;
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
	
	// Indices of each list
	public static final int INDEX_THIS_WEEK = 0;
	public static final int INDEX_PENDING = 1;
	public static final int INDEX_EXPIRED = 2;
	public static final int INDEX_GENERAL = 3;
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
	 */
	void addFloating(Task taskToAdd) throws Exception {
		if (taskAlreadyExists(taskToAdd)) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS);
		}
		
		taskLists.get(INDEX_PENDING).add(taskToAdd);
		taskLists.get(INDEX_GENERAL).add(taskToAdd);
		taskLists.get(INDEX_ACTION).clear(); // Action list is not relevant for this command, clear it to reduce clutter
	}
	
	/**
	 * Adds a deadline task to the task lists.
	 * @param taskToAdd
	 */
	void addDeadline(Task taskToAdd) throws Exception {
		if (taskAlreadyExists(taskToAdd)) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS);
		}
		
		if (taskToAdd.isExpired()) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED);
		}
		
		taskLists.get(INDEX_PENDING).add(taskToAdd);
		taskLists.get(INDEX_DEADLINE).add(taskToAdd);
		taskLists.get(INDEX_ACTION).clear(); // Action list is not relevant for this command, clear it to reduce clutter
		
		if (taskToAdd.isThisWeek()) {
			taskLists.get(INDEX_THIS_WEEK).add(taskToAdd);
		}
	}
	
	/**
	 * Adds an event task to the task lists.
	 * @param taskToAdd
	 */
	void addEvent(Task taskToAdd) throws Exception {
		if (taskAlreadyExists(taskToAdd)) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS);
		}
		
		if (taskToAdd.isExpired()) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED);
		}
		
		taskLists.get(INDEX_PENDING).add(taskToAdd);
		taskLists.get(INDEX_EVENT).add(taskToAdd);
		taskLists.get(INDEX_ACTION).clear(); // Action list is not relevant for this command, clear it to reduce clutter
		
		if (taskToAdd.isThisWeek()) {
			taskLists.get(INDEX_THIS_WEEK).add(taskToAdd);
		}
	}
	
	/**
	 * Removes an indexed task from the specified task list, and deletes all its tags from the tag category list.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be deleted
	 * @return           the Task that was deleted
	 */
	Task deleteByIndex(ContentBox contentBox, int taskIndex) throws Exception {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_INVALID_INDEX);
		}
		
		Task toDelete = targetList.get(taskIndex);
		removeFromAllLists(toDelete);
		removeTaskTags(toDelete.getTaskTags());
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			taskLists.get(INDEX_ACTION).clear();
		}
		
		return toDelete;
	}
	
	/**
	 * Deletes all tasks with the given tag name from the expired and pending lists, and updates the tag category list 
	 * accordingly.
	 * @param tagName
	 */
	void deleteByTagName(String tagName) {
		removeTaggedTasks(taskLists.get(INDEX_EXPIRED), tagName);
		removeTaggedTasks(taskLists.get(INDEX_PENDING), tagName);
		taskLists.get(INDEX_ACTION).clear(); // Action list is not relevant for this command, clear it to reduce clutter
	}
	
	/**
	 * Marks an indexed task from the specified task list as done, and deletes all its tags from the tag category list.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be completed
	 */
	void doneByIndex(ContentBox contentBox, int taskIndex) throws Exception {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_INVALID_INDEX);
		}
		
		Task toComplete = targetList.get(taskIndex);
		
		if (taskLists.get(INDEX_COMPLETED).contains(toComplete)) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DONE_INVALID);
		}
		
		removeFromAllLists(toComplete);
		taskLists.get(INDEX_COMPLETED).add(toComplete);
		removeTaskTags(toComplete.getTaskTags());
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			taskLists.get(INDEX_ACTION).clear();
		}
	}
	
	/**
	 * Updates an indexed task from the specified task list, and also updates all lists that contained the updated task.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be updated
	 * @param newName    the new name to update the task to
	 */
	void updateByIndexChangeName(ContentBox contentBox, int taskIndex, String newName) throws Exception {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_INVALID_INDEX);
		}
		
		Task toUpdate = targetList.get(taskIndex);
		
		if (taskLists.get(INDEX_COMPLETED).contains(toUpdate)) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_UPDATE_INVALID);
		}
		
		Task toUpdateCopy = new Task(toUpdate);
		toUpdateCopy.setTaskName(newName);
		
		if (taskAlreadyExists(toUpdateCopy)) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS);
		}
		
		removeFromAllLists(toUpdate);
		toUpdate.setTaskName(newName);
		addTaskToLists(contentBox, toUpdate);
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			taskLists.get(INDEX_ACTION).clear();
		}
	}
	
	/**
	 * Updates an indexed task from the specified task list, and also updates all lists that contained the updated task.
	 * @param contentBox specifies the current tab that user is in
	 * @param taskIndex  the index of task to be updated
	 * @param newTask    the task with the new date
	 */
	void updateByIndexChangeDate(ContentBox contentBox, int taskIndex, Task newTask) throws Exception {
		ArrayList<Task> targetList = taskLists.get(getListIndex(contentBox));
		
		if (taskIndex >= targetList.size() || taskIndex < 0) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_INVALID_INDEX);
		}
		
		Task toUpdate = targetList.get(taskIndex);
		
		if (taskLists.get(INDEX_COMPLETED).contains(toUpdate)) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_UPDATE_INVALID);
		}
		
		if (newTask.isExpired()) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED);
		}
		
		removeFromAllLists(toUpdate);
		newTask.setTaskName(toUpdate.getTaskName());
		addTaskToLists(contentBox, newTask);
		
		if (!contentBox.equals(ContentBox.ACTION)) { // User not in ACTION tab, clear it to remove clutter
			taskLists.get(INDEX_ACTION).clear();
		}
	}

	/**
	 * Search for all expired and pending tasks that contain the given search phrase in their name. The search phrase 
	 * is not case sensitive.
	 * TODO: improved search
	 * @param searchPhrase
	 */
	void search(String searchPhrase) throws Exception {
		ArrayList<Task> actionList = taskLists.get(INDEX_ACTION);
		actionList.clear();
		actionList.addAll(getSearchResults(taskLists.get(INDEX_EXPIRED), searchPhrase));
		actionList.addAll(getSearchResults(taskLists.get(INDEX_PENDING), searchPhrase));
		
		if (actionList.isEmpty()) {
			throw new Exception(LogicConstants.MSG_EXCEPTION_SEARCH_NOT_FOUND);
		}
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
	
	// Clears all task lists.
	void clearAllTaskLists() {
		for (int i = 0; i < taskLists.size(); i++) {
			taskLists.get(i).clear();
		}
	}
	
	// Clear the tag category list.
	void clearTagCategoryList() {
		tagCategoryList.clear();
	}
	
	// Returns the index of the list corresponding to the current ContentBox.
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
	
	// Adds the new Task to all the lists it should belong to, depending on the current tab the user is in.
	private void addTaskToLists(ContentBox contentBox, Task newTask) {
		for (int i = 0; i < taskLists.size(); i++) {
			if (belongsToList(contentBox, i, newTask)) {
				taskLists.get(i).add(newTask);
			}
		}
	}
	
	// Returns true if and only if the new Task belongs to the list specified by listIndex, depending on the current 
	// tab the user is in.
	private boolean belongsToList(ContentBox contentBox, int listIndex, Task newTask) {
		String taskType = newTask.getTaskType();
		boolean isExpired = newTask.isExpired();

		if (listIndex == INDEX_THIS_WEEK) {
			return (!isExpired && newTask.isThisWeek());
		} else if (listIndex == INDEX_PENDING) {
			return !isExpired;
		} else if (listIndex == INDEX_EXPIRED) {
			return isExpired;
		} else if (listIndex == INDEX_GENERAL) {
			return (taskType.equals("FLOATING")); // TODO: remove magic Strings
		} else if (listIndex == INDEX_DEADLINE) {
			return (!isExpired && taskType.equals("DEADLINE"));
		} else if (listIndex == INDEX_EVENT) {
			return (!isExpired && taskType.equals("EVENT"));
		} else if (listIndex == INDEX_ACTION) {
			return contentBox.equals(ContentBox.ACTION);
		} else {
			return false;
		}
	}
	
	// Removes the given Task from all task lists.
	private void removeFromAllLists(Task toRemove) {
		taskLists.get(INDEX_THIS_WEEK).remove(toRemove);
		taskLists.get(INDEX_PENDING).remove(toRemove);
		taskLists.get(INDEX_EXPIRED).remove(toRemove);
		taskLists.get(INDEX_GENERAL).remove(toRemove);
		taskLists.get(INDEX_DEADLINE).remove(toRemove);
		taskLists.get(INDEX_EVENT).remove(toRemove);
		taskLists.get(INDEX_COMPLETED).remove(toRemove);
		taskLists.get(INDEX_ACTION).remove(toRemove);
	}
	
	/** 
	 * Removes all tasks which contain a tag with the given name, from the given list. The tasks will be removed from
	 * other lists that contain them as well. For each task that is removed, the tag category list will be updated 
	 * accordingly.
	 * 
	 * @param list
	 * @param tagName
	 */
	private void removeTaggedTasks(ArrayList<Task> list, String tagName) {
		for (Iterator<Task> it = list.iterator(); it.hasNext();) { // Iterator is for save removal of elements while 
			                                                       // iterating
			Task task = it.next();
			
			if (task.getTaskTags().contains(tagName)) {
				it.remove();
				removeFromAllLists(task); // This is safe because the task has already been removed from the current list.
				removeTaskTags(task.getTaskTags());
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
	
	// Returns true if and only if the given task already exists in any of the task lists.
	private boolean taskAlreadyExists(Task task) {
		return (taskLists.get(INDEX_PENDING).contains(task) || taskLists.get(INDEX_COMPLETED).contains(task)
				|| taskLists.get(INDEX_EXPIRED).contains(task));
	}
	
	/**
	 * Add a new tag to the tag category list.
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
	
	// Remove a tag from the tag category list. The tag should currently exist in the tag category list.
	private void removeTag(String tagToRemove) {
		int tagIndex = getTagIndex(tagToRemove);
		assert(tagIndex != -1);
		
		if (tagCategoryList.get(tagIndex).getNumTags() == 1) {
			tagCategoryList.remove(tagIndex);
		} else {
			tagCategoryList.get(tagIndex).decreaseCount();
		}
	}
	
	// Returns the index of the specified tag name in the tag category list. If the tag name is not found, -1 is returned.
	private int getTagIndex(String tagName) {
		for (int i = 0; i < tagCategoryList.size(); i++) {
			if (tagCategoryList.get(i).getTagName().equals(tagName)) {
				return i;
			}
		}
		
		return -1;
	}
	
	private ArrayList<Task> getSearchResults(ArrayList<Task> list, String searchPhrase) {
		ArrayList<Task> searchResults = new ArrayList<Task>();
		
		for (Task t : list) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				searchResults.add(t);
			}
		}
		
		return searchResults;
	}
}
