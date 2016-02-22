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
	private Parser parser;
	private Storage storage;
	private UiManager uiManager;
	/** The most recent processed object whose command is not VIEW or UNDO */
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
     * @param input the input string
     * @return      status code reflecting the outcome of command execution
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    public int executeCommand(String input) throws ClassNotFoundException, IOException {
    	ProcessedObject po = parser.parseInput(input);
    	String command = po.getCommand();
    	Task task = po.getTask();
    	
    	//TODO: handle invalid commands
    	
    	String taskName = task.getTaskName();
    	ArrayList<Task> tasksToView = new ArrayList<Task>();
    	ArrayList<Task> tasksToAdd = new ArrayList<Task>();
    	switch (command) {
    		case "VIEW":
    			String viewType = po.getViewType();
    			view(viewType, tasksToView);
    			break;
    			
    		case "ADD_FLOATING":
    		case "ADD_DEADLINE":
    		/*case ADD_RECURRING:*/
    		case "ADD_EVENT":
    			add(tasksToAdd, task, command);
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
    		
    		case "UNDO":
    			undo(tasksToAdd);
    			break;
    		
    		case "ERROR":
    			//TODO
    			
    		default:
    	}
    	
    	uiManager.updateDisplay();
    	return -1; //stub
    }
    
    //Update the list of Tasks to view from Storage.
    private void view(String viewType, ArrayList<Task> tasksToView) throws ClassNotFoundException, IOException {
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
		}
		
		Collections.sort(tasksToView);
    }
    
    //Add the Task to Storage.
    private void add(ArrayList<Task> tasksToAdd, Task task, String command) throws IOException {
    	if (command == "ADD_FLOATING") {
    		storage.setFilename("floating tasks");
    	} else if (command == "ADD_DEADLINE") {
    		storage.setFilename("deadline tasks");
    	} else { //Event tasks
    		storage.setFilename("event tasks");
    	}
    	
    	tasksToAdd.add(task);
		storage.saveTasks(tasksToAdd);
    }
    
    //Undo the most recent action, unless the action was view or undo.
	private void undo(ArrayList<Task> tasksToAdd) throws IOException {
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
			
			default:
		}
	}
}