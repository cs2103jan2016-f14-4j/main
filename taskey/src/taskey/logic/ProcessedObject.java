package taskey.logic;

import java.util.ArrayList;

/**
 * @@author A0107345L
 * This class will be used for facilitating transfer of a processed task from 
 * Parser to Logic. 
 * 
 * command types:
 * 1. ADD_FLOATING
 * 2. ADD_DEADLINE
 * 3. ADD_EVENT
 * 4. ADD_RECURRING
 * 5. DELETE_BY_INDEX
 * 6. DELETE_BY_NAME 
 * 7. DELETE_BY_CATEGORY
 * 8. UPDATE_BY_INDEX_CHANGE_NAME
 * 9. UPDATE_BY_INDEX_CHANGE_DATE
 * 10. UPDATE_BY_INDEX_CHANGE_BOTH
 * 11. UPDATE_BY_INDEX_CHANGE_PRIORITY
 * 12. UPDATE_BY_NAME_CHANGE_NAME
 * 13. UPDATE_BY_NAME_CHANGE_DATE
 * 14. UPDATE_BY_NAME_CHANGE_BOTH
 * 15. UPDATE_BY_NAME_CHANGE_PRIORITY
 * 16. VIEW_TAGS
 * 17. VIEW_BASIC
 * 18. ERROR 
 * 19. DONE_BY_INDEX
 * 20. DONE_BY_NAME
 * 21. SEARCH
 * 22. UNDO 
 * 23. CHANGE_FILE_LOC
 * 
 * @author Xue Hui
 *
 */
public class ProcessedObject {
	/*
	 * index is only used if command type is DELETE_BY_INDEX or UPDATE_BY_INDEX
	 * viewType is only used if command type is VIEW. 
	 * possible values for viewType:
	 * 1. ALL
	 * 2. GENERAL
	 * 3. DEADLINES
	 * 4. EVENTS 
	 * 5. ARCHIVE 
	 * 6. HELP 
	 */
	private String command = null;
	private Task task = null; 
	private int index = -1; 
	private ArrayList<String> viewType = null; 
	private String errorType = null;
	private String searchPhrase = null; 
	private String newTaskName = null; 
	private int newPriority = -1; 
	private String newFileLoc = null; 
	private String category = null; 
	
	
	//CONSTRUCTORS ====================================================
	
	/**
	 * Empty Constructor for testing, shouldn't be used if possible 
	 */
	public ProcessedObject() {
		
	}
	
	/**
	 * Constructor for ERROR/UNDO/SEARCH/CHANGE_FILE_LOC/DELETE_BY_CATEGORY
	 * @param command
	 */
	public ProcessedObject(String command) {
		this.command = command; 
	}
	
	/**
	 * Constructor for VIEW_BASIC, VIEW_TAGS 
	 * @param command
	 */
	public ProcessedObject(String command, ArrayList<String> viewType) {
		this.command = command; 
		this.viewType = viewType; 
	}
	
	/**
	 * Constructor for ADD_FLOATING, ADD_DEADLINE, ADD_EVENT, ADD_RECURRING,
	 * DELETE_BY_NAME, UPDATE_BY_NAME_CHANGE_NAME, UPDATE_BY_NAME_CHANGE_DATE,
	 * UPDATE_BY_NAME_CHANGE_BOTH
	 * @param command
	 * @param task
	 */
	public ProcessedObject(String command, Task task) {
		this.command = command;
		this.task = task; 
	}
	
	/**
	 * Constructor for DELETE_BY_INDEX, UPDATE_BY_INDEX_CHANGE_NAME, 
	 * UPDATE_BY_INDEX_CHANGE_DATE, UPDATE_BY_INDEX_CHANGE_BOTH
	 * @param command
	 * @param task
	 * @param index
	 */
	public ProcessedObject(String command, int index) {
		this.command = command;
		this.index = index; 
	}
	
	//=================================================================
	
	//Corresponding GET/SET methods ===================================
	
	/**
	 * @return command that the user has keyed in, or error
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Set the command that the user has keyed in. 
	 * @param command
	 */
	public void setCommand(String command) {
		this.command = command; 
	}
	
	/**
	 * @return Task object attached to this ProcessedObject, 
	 * if there is any
	 */
	public Task getTask() {
		return task;
	}
	
	/**
	 * Set the task object if required. 
	 * @param task
	 */
	public void setTask(Task task) {
		this.task = task; 
	}
	
	/**
	 * @return index of the task to update or delete
	 */
	public int getIndex() {
		return index; 
	}
	
	/**
	 * set the index of the task to update or delete
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index; 
	}
	
	/**
	 * @return view type of a view command
	 */
	public ArrayList<String> getViewType() {
		return viewType; 
	}
	
	/**
	 * Set view type of a view command
	 * @param viewType
	 */
	public void setViewType(ArrayList<String> viewType) {
		this.viewType = viewType; 
	}
	
	/**
	 * set the details of an ERROR command
	 * @param errorType
	 */
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
	/**
	 * @return get details of an ERROR command
	 */
	public String getErrorType() {
		return errorType; 
	}
	
	/**
	 * Set search phrase, if the command is SEARCH
	 * @param searchPhrase
	 */
	public void setSearchPhrase(String searchPhrase) {
		this.searchPhrase = searchPhrase; 
	}
	
	/**
	 * @return search phrase, if the command is SEARCH
	 */
	public String getSearchPhrase() {
		return searchPhrase; 
	}
	
	/**
	 * Set new task name of a task,
	 * used with a "SET" command
	 * @param taskName
	 */
	public void setNewTaskName(String taskName) {
		newTaskName = taskName; 
	}
	
	/**
	 * @return new task name of a task; used with "SET" command
	 */
	public String getNewTaskName() {
		return newTaskName; 
	}
	
	/**
	 * @return directory of new file location to be used
	 */
	public String getNewFileLoc() {
		return newFileLoc; 
	}
	
	/**
	 * Set new directory of new file location to be used 
	 * @param newFileLoc
	 */
	public void setNewFileLoc(String newFileLoc) {
		this.newFileLoc = newFileLoc; 
	}
	
	/**
	 * Set category of tasks to delete
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	
	/**
	 * @return category of tasks to delete 
	 */
	public String getCategory() {
		return category; 
	}
	
	/**
	 * @return new priority of the task (for set) 
	 */
	public int getNewPriority() {
		return newPriority; 
	}
	
	/**
	 * @param newPriority set new priority of the task (for set) 
	 */
	public void setNewPriority(int newPriority) {
		this.newPriority = newPriority; 
	}
	
	@Override
	/**
	 * @@author A0134177E
	 * Compare if a ProcessedObject is equal to another ProcessedObject
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ProcessedObject)) {
		    return false;
		  }
		
		ProcessedObject other = (ProcessedObject) obj;
		
		if ((command == null && other.command != null) || (command != null 
			 && (other.command == null || !command.equals(other.command)))) {
			return false;
		}	
		
		if ((task == null && other.task != null) || (task != null 
			 && (other.task == null || !task.equals(other.task)))) {
			return false;
		}	
		
		if (index != other.index) {
			return false;
		}
		
		if ((viewType == null && other.viewType != null) || (viewType != null 
			 && (other.viewType == null || !viewType.equals(other.viewType)))) {
			return false;
		}	
		
		if ((errorType == null && other.errorType != null) || (errorType != null 
			 && (other.errorType == null || !errorType.equals(other.errorType)))) {
			return false;
		}	
		
		if ((searchPhrase == null && other.searchPhrase != null) || (searchPhrase != null 
			 && (other.searchPhrase == null || !searchPhrase.equals(other.searchPhrase)))) {
			return false;
		}	
		
		if ((newTaskName == null && other.newTaskName != null) || (newTaskName != null 
			 && (other.newTaskName == null || !newTaskName.equals(other.newTaskName)))) {
			return false;
		}	
		
		if ((newFileLoc == null && other.newFileLoc != null) || (newFileLoc != null 
			 && (other.newFileLoc == null || !newFileLoc.equals(other.newFileLoc)))) {
			return false;
		}	
		
		return true;
	}
	
	@Override 
	/**
	 * @@author A0107345L
	 * For debugging
	 */
	public String toString() {
		String stringRep = ""; 
		
		stringRep += "Command: " + command + "\n";
		
		if (task != null) {
			stringRep += task.toString(); 
		}
		
		if (index != -1) {
			stringRep += "at index: " + String.valueOf(index) + "\n"; 
		}
		
		if (viewType != null) {
			stringRep += "view type: "; 
			for(int i = 0; i < viewType.size(); i++) {
				stringRep += viewType.get(i) + ", "; 
			}
			stringRep += "\n"; 
		}
		
		if (errorType != null) {
			stringRep += "error type: " + errorType + "\n"; 
		}
		
		if (searchPhrase != null) {
			stringRep += "search phrase: " + searchPhrase + "\n"; 
		} 
		
		if (newTaskName != null) {
			stringRep += "new TaskName: " + newTaskName + "\n"; 		
		}
		
		if (category != null) {
			stringRep += "category: " + category + "\n";
		}
		
		if (newPriority != -1) {
			stringRep += "newPriority: " + newPriority + "\n"; 
		}
		
		return stringRep; 
	}
}
