package taskey.logic;

import taskey.parser.Parser;
import taskey.storage.Storage;
import taskey.ui.UiManager;
import taskey.logic.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import taskey.logic.ProcessedObject;

/**
 * TODO: class description
 * 
 * @author Hubert Wong
 */
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
	
	private Parser parser;
	private Storage storage;
	private UiManager uiManager;
	/** The most recent processed object whose command is not VIEW, UNDO, SEARCH or ERROR */
	private ProcessedObject mostRecentProcessedObject = null;
	
	//TODO: constructors
	public Logic() {
		parser = new Parser();
		storage = Storage.getInstance();
		uiManager = UiManager.getInstance();
	}

    /**
     * Attempts to execute a command specified by the input string.
     * 
     * @param input       the input string
     * @return statusCode status code reflecting the outcome of command execution
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public int executeCommand(String input) throws ClassNotFoundException, IOException {
    	ProcessedObject po = parser.parseInput(input);
    	String command = po.getCommand();
    	Task task = po.getTask();
    	ArrayList<Task> tasksToView = new ArrayList<Task>();
    	ArrayList<Task> tasksToAdd = new ArrayList<Task>();
    	int statusCode = 0;
    	switch (command) {
    		case "VIEW":
    			String viewType = po.getViewType();
    			statusCode = view(viewType, tasksToView);
    			break;
    			
    		case "ADD_FLOATING":
    		case "ADD_DEADLINE":
    		/*case ADD_RECURRING:*/
    		case "ADD_EVENT":
    			statusCode = add(tasksToAdd, task, command);
    			break;
    			
    		case "DELETE_BY_INDEX":
    			//TODO: delete indexed task from storage
    			break;
    		
    		case "DELETE_BY_NAME":
    			//TODO: delete named task from storage
    			break;
    		
    		case "UPDATE_BY_INDEX":
    			//TODO: update indexed task in storage
    			break;
    		
    		case "UPDATE_BY_NAME":
    			//TODO: update named task in storage
    			break;
    			
    		case "DONE_BY_INDEX":
    			//TODO: mark indexed task as done in storage
    			break;
    		
    		case "DONE_BY_NAME":
    			//TODO: mark named task as done in storage
    			break;
    			
    		case "SEARCH":
    			String searchPhrase = po.getSearchPhrae();
    			//TODO: search for task in storage
    			break;
    		
    		case "UNDO":
    			statusCode = undo(tasksToAdd);
    			break;
    		
    		case "ERROR":
    			String errorType = po.getErrorType();
    			switch (errorType) {
    				case "ERROR_COMMAND":
    					//TODO: pass message to Ui
    					break;
    				
    				case "ERROR_VIEW_TYPE":
    					//TODO: pass message to Ui
    					break;
    				
    				case "ERROR_DATE_FORMAT":
    					//TODO: pass message to Ui
    					break;
    				
    				default:
    			}
    			
    		default:
    	}
    	
    	if (isUndoableCommand(command)) {
    		mostRecentProcessedObject = po;
    	}
    	uiManager.updateDisplay();
    	return statusCode; 
    }
    
    //Update the list of Tasks to view from Storage. Returns a status code representing outcome of action.
    private int view(String viewType, ArrayList<Task> tasksToView) throws ClassNotFoundException, IOException {
		ArrayList<Task> fullTaskList = storage.loadTasks();
		if (viewType == "GENERAL") {
			for (Task t : fullTaskList) {
	    		if (t.getTaskType() == "FLOATING") {
	    			tasksToView.add(t);
	    		}
	    	}
		} else if (viewType == "DEADLINE") {
			for (Task t : fullTaskList) {
	    		if (t.getTaskType() == "DEADLINE") {
	    			tasksToView.add(t);
	    		}
	    	}
		} else if (viewType == "EVENTS") {
			for (Task t : fullTaskList) {
	    		if (t.getTaskType() == "EVENT") {
	    			tasksToView.add(t);
	    		}
	    	}		    
		} else { //No restriction on viewType
			for (Task t : fullTaskList) {
				tasksToView.add(t);
			}
		}
		
		Collections.sort(tasksToView);
		return SUCCESS_VIEW; //Stub
    }
    
    //Add the Task to Storage. Returns a status code representing outcome of action.
    private int add(ArrayList<Task> tasksToAdd, Task task, String command) throws IOException {
    	if (command == "ADD_FLOATING") {
    		storage.setFilename("floating tasks");
    	} else if (command == "ADD_DEADLINE") {
    		storage.setFilename("deadline tasks");
    	} else { //Event tasks
    		storage.setFilename("event tasks");
    	}
    	
    	tasksToAdd.add(task);
		storage.saveTasks(tasksToAdd);
		
		return SUCCESS_ADD; //Stub
    }
    
    //Undo the most recent action that was not view, undo, search or error.
    //Returns a status code representing outcome of action.
	private int undo(ArrayList<Task> tasksToAdd) throws IOException {
		if (mostRecentProcessedObject == null) {
			return ERROR_UNDO; //Stub
		}
		String mostRecentCommand = mostRecentProcessedObject.getCommand();
		switch (mostRecentCommand) {
			case "ADD_FLOATING":
			case "ADD_DEADLINE":
			case "ADD_EVENT":
			case "ADD_RECURRING":
				//TODO: delete most recently added task from storage
				break;
			
			case "DELETE_BY_INDEX":
			case "DELETE_BY_NAME":
				Task mostRecentTask = mostRecentProcessedObject.getTask();
				String mostRecentTaskType = mostRecentTask.getTaskType();
				tasksToAdd.add(mostRecentTask);
				if (mostRecentTaskType == "FLOATING") {
					storage.setFilename("floating tasks");
				} else if (mostRecentTaskType == "EVENT") {
					storage.setFilename("event tasks");
				} else { //Deadline tasks
					storage.setFilename("deadline tasks");
				}
				storage.saveTasks(tasksToAdd);;
				break;
			
			case "UPDATE_BY_INDEX":
			case "UPDATE_BY_NAME":
				//TODO: revert most recently updated task in storage
				break;
				
			case "DONE_BY_INDEX":
			case "DONE_BY_NAME":
				//TODO: mark most recently "done" task as "undone" in storage
				break;
			
			default:
		}
		
		return SUCCESS_UNDO; //Stub
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
}