package taskey.logic;

import taskey.parser.Parser;
import taskey.storage.Storage;
import taskey.ui.UiManager;
import taskey.logic.Task;

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
     */
    public int executeCommand(String input) {
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
    			ArrayList<Task> fullTaskList = storage.loadTasks();
    			switch (viewType) {
    			    case "ALL":
    			    	break;
    			    
    			    case "GENERAL":
    			    	for (Task t : fullTaskList) {
    			    		if (t.getTaskType() == "FLOATING") {
    			    			tasksToView.add(t);
    			    		}
    			    	}
    			    	break;
    			    
    			    case "DEADLINE":
    			    	for (Task t : fullTaskList) {
    			    		if (t.getTaskType() == "DEADLINE") {
    			    			tasksToView.add(t);
    			    		}
    			    	}
    			    	break;
    			    
    			    case "EVENTS":
    			    	for (Task t : fullTaskList) {
    			    		if (t.getTaskType() == "EVENT") {
    			    			tasksToView.add(t);
    			    		}
    			    	}		    	
    			    	break;
    			    
    			    default:
    			}
    			Collections.sort(tasksToView);
    			break;
    			
    		case "ADD_FLOATING":
    			storage.setFilename("floating tasks");
    			tasksToAdd.add(task);
    			storage.saveTasks(tasksToAdd);;
    			break;
    			
    		case "ADD_DEADLINE":
    			storage.setFilename("deadline tasks");
    			/*long deadlineEpoch = task.getDeadlineEpoch();*/
    			tasksToAdd.add(task);
    			break;
    				
    		case "ADD_EVENT":
    			/*long startDateEpoch = task.getStartDateEpoch();
    			long endDateEpoch = task.getEndDateEpoch();*/
    			storage.setFilename("event tasks");
    			tasksToAdd.add(task);
    			break;
    				
    		/*case ADD_RECURRING:
    			//TODO: add recurring task to storage
    			break;*/
    			
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
    			break;
    		
    		case "ERROR":
    			//TODO
    			
    		default:
    	}
    	
    	// Update UI 
    	uiManager.updateDisplay(tasksToView);
    	
    	return -1; //stub
    }
}