package taskey.logic;

import java.util.ArrayList;

/**
 * @@author A0134177E
 * This class serves as the memory component for the Logic class. It holds references to all the Task and TagCategory
 * objects in use for the session. Each time a command is executed, the data in this component will be modified. 
 * Note that the data in this component will not be saved to disk unless the user enters the "save" command.  
 */
class LogicMemory {
	private ArrayList<ArrayList<Task>> taskLists;
	private ArrayList<TagCategory> tagCategoryList;
	
	LogicMemory() {
		// TODO: get lists from Storage
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
}
