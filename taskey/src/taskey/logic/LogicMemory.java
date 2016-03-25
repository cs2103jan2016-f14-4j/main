package taskey.logic;

import java.util.ArrayList;
import java.util.Iterator;

import taskey.parser.TimeConverter;
import taskey.storage.Storage;

/**
 * @@author A0134177E
 * This class serves as the memory component for the Logic class. It holds references to all the Task and TagCategory
 * objects in use for the session. Each time a command is executed, the data in this component will be modified. 
 * Note that the data in this component will not be saved to disk unless the user enters the "save" command.  
 */
class LogicMemory {
	
	private static final int NUM_TASK_LISTS = 8;
	
	// Indices of each list
	private static final int INDEX_THIS_WEEK = 0;
	private static final int INDEX_PENDING = 1;
	private static final int INDEX_EXPIRED = 2;
	private static final int INDEX_GENERAL = 3;
	private static final int INDEX_DEADLINE = 4;
	private static final int INDEX_EVENT = 5;
	private static final int INDEX_COMPLETED = 6;
	private static final int INDEX_ACTION = 7;
	
	private Storage storage;
	private ArrayList<ArrayList<Task>> taskLists;
	private ArrayList<TagCategory> tagCategoryList;
	
	LogicMemory() {
		storage = new Storage();
		initializeTaskLists();
		initializeTagCategoryList();
	}

	ArrayList<ArrayList<Task>> getTaskLists() {
		return taskLists;
	}

	void setTaskLists(ArrayList<ArrayList<Task>> taskLists) {
		this.taskLists = taskLists;
	}

	ArrayList<TagCategory> getTagCategoryList() {
		return tagCategoryList;
	}

	void setTagCategoryList(ArrayList<TagCategory> tagCategoryList) {
		this.tagCategoryList = tagCategoryList;
	}
	
	private void initializeTaskLists() {
		taskLists = storage.loadAllTasklists();
		assert(taskLists != null);
		
		// THIS_WEEK and ACTION lists are not loaded from storage and hence must be manually added.
		taskLists.add(INDEX_THIS_WEEK, new ArrayList<Task>());
		taskLists.add(INDEX_ACTION, new ArrayList<Task>());
		assert(taskLists.size() == NUM_TASK_LISTS);
		
		synchroniseLists();
	}
	
	private void initializeTagCategoryList() {
		tagCategoryList = storage.loadTaglist();
	}
	
	/** Updates all the lists based on the current time on the user's computer clock. For example, deadline tasks that
	 *  are newly expired will be removed from the DEADLINE list and PENDING lists and added to the EXPIRED list. The 
	 *  tag category list is not affected.
	 */
	private void synchroniseLists() {
		TimeConverter timeConverter = new TimeConverter();
		long currTime = timeConverter.getCurrTime();
		ArrayList<Task> thisWeekList = taskLists.get(INDEX_THIS_WEEK);
		ArrayList<Task> expiredList = taskLists.get(INDEX_EXPIRED);
		ArrayList<Task> pendingList = taskLists.get(INDEX_PENDING);

		for (Iterator<Task> it = pendingList.iterator(); it.hasNext();) { // Iterator is used for safe removal of
			                                                              // elements while iterating
			Task task = it.next();
			
			if (task.getTaskType().equals("DEADLINE")) { // TODO: remove magic strings
				long deadline = task.getDeadlineEpoch();
				if (deadline < currTime) {
					it.remove();
					removeFromAllLists(task); 
					expiredList.add(task);
				} else if (timeConverter.isSameWeek(deadline, currTime)) {
					thisWeekList.add(task);
				}
			} else if (task.getTaskType().equals("EVENT")) {
				long startDate = task.getStartDateEpoch();
				long endDate = task.getEndDateEpoch();
				if (endDate < currTime) {
					it.remove();
					removeFromAllLists(task); 
					expiredList.add(task);
				} else if (timeConverter.isSameWeek(startDate, currTime)) {
					thisWeekList.add(task);
				}
			}
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
}
