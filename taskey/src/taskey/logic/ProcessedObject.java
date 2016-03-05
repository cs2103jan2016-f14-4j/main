package taskey.logic;

/**
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
 * 9. UPDATE_BY_NAME_CHANGE_NAME
 * 10. UPDATE_BY_NAME_CHANGE_DATE
 * 11. VIEW 
 * 12. ERROR 
 * 13. DONE_BY_INDEX
 * 14. DONE_BY_NAME
 * 15. SEARCH
 * 16. UNDO 
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
	 */
	private String command = null;
	private Task task = null; 
	private int index = -1; 
	private String viewType = null; 
	private String errorType = null;
	private String searchPhrase = null; 
	private String newTaskName = null; 
	
	
	//CONSTRUCTORS ====================================================
	
	/**
	 * Empty Constructor for testing, shouldn't be used if possible 
	 */
	public ProcessedObject() {
		
	}
	
	/**
	 * Constructor for ERROR/UNDO/SEARCH 
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
			stringRep += "at index: " + String.valueOf(index); 
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
		
		return stringRep; 
	}
}
