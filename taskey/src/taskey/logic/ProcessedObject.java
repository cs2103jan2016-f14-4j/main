package taskey.logic;

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
 * 7. UPDATE_BY_INDEX_CHANGE_NAME
 * 8. UPDATE_BY_INDEX_CHANGE_DATE
 * 9. UPDATE_BY_INDEX_CHANGE_BOTH
 * 10. UPDATE_BY_NAME_CHANGE_NAME
 * 11. UPDATE_BY_NAME_CHANGE_DATE
 * 12. UPDATE_BY_NAME_CHANGE_BOTH
 * 13. VIEW 
 * 14. ERROR 
 * 15. DONE_BY_INDEX
 * 16. DONE_BY_NAME
 * 17. SEARCH
 * 18. UNDO 
 * 19. CHANGE_FILE_LOC
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
	private String viewType = null; 
	private String errorType = null;
	private String searchPhrase = null; 
	private String newTaskName = null; 
	private String newFileLoc = null; 
	
	
	//CONSTRUCTORS ====================================================
	
	/**
	 * Empty Constructor for testing, shouldn't be used if possible 
	 */
	public ProcessedObject() {
		
	}
	
	/**
	 * Constructor for ERROR/UNDO/SEARCH/CHANGE_FILE_LOC
	 * @param command
	 */
	public ProcessedObject(String command) {
		this.command = command; 
	}
	
	/**
	 * Constructor for VIEW 
	 * @param command
	 */
	public ProcessedObject(String command, String viewType) {
		this.command = command; 
		this.viewType = viewType; 
	}
	
	/**
	 * Constructor for ADD_FLOATING, ADD_DEADLINE, ADD_EVENT, ADD_RECURRING,
	 * DELETE_BY_NAME, UPDATE_BY_NAME_CHANGE_NAME, UPDATE_BY_NAME_CHANGE_DATE
	 * @param command
	 * @param task
	 */
	public ProcessedObject(String command, Task task) {
		this.command = command;
		this.task = task; 
	}
	
	/**
	 * Constructor for DELETE_BY_INDEX, UPDATE_BY_INDEX_CHANGE_NAME, 
	 * UPDATE_BY_INDEX_CHANGE_DATE 
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
	
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command; 
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		this.task = task; 
	}
	
	public int getIndex() {
		return index; 
	}
	
	public void setIndex(int index) {
		this.index = index; 
	}
	
	public String getViewType() {
		return viewType; 
	}
	
	public void setViewType(String viewType) {
		this.viewType = viewType; 
	}
	
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
	public String getErrorType() {
		return errorType; 
	}
	
	public void setSearchPhrase(String searchPhrase) {
		this.searchPhrase = searchPhrase; 
	}
	
	public String getSearchPhrase() {
		return searchPhrase; 
	}
	
	public void setNewTaskName(String taskName) {
		newTaskName = taskName; 
	}
	
	public String getNewTaskName() {
		return newTaskName; 
	}
	
	public String getNewFileLoc() {
		return newFileLoc; 
	}
	
	public void setNewFileLoc(String newFileLoc) {
		this.newFileLoc = newFileLoc; 
	}
	
	@Override
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
			stringRep += "view type: " + viewType + "\n"; 
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
		
		return stringRep; 
	}
}
