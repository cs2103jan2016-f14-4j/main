package taskey.logic;

import taskey.parser.Parser;
import taskey.storage.Storage;
import taskey.ui.UiManager;
import taskey.logic.Task;

import java.util.ArrayList;
import java.util.Collections;

import taskey.logic.ProcessedObject;

/**
 * //TODO: class description
 * 
 * @author Hubert Wong
 */
public class Logic {
	private Parser parser;
	private Storage storage;
	/** The most recent task whose command is not VIEW or UNDO */
	private ProcessedObject mostRecentProcessedObject = null;
	
	//TODO: constructors
	public Logic() {
		parser = new Parser();
		storage = new Storage();
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
    	switch (command) {
    		case VIEW:
    			String viewType = po.getViewType();
    			switch (viewType) {
    			    case ALL:
    			    	//TODO: send view all request to storage
    			    	break;
    			    
    			    case GENERAL:
    			    	//TODO: send view general request to storage
    			    	break;
    			    
    			    case DEADLINE:
    			    	//TODO: send view deadline request to storage
    			    	break;
    			    
    			    case EVENTS:
    			    	//TODO: send view events request to storage
    			    	break;
    			    
    			    default:
    			}
    			Collections.sort(tasksToView);
    			break;
    			
    		case ADD_FLOATING:
    			//TODO: add floating task to storage
    			break;
    			
    		case ADD_DEADLINE:
    			long deadlineEpoch = task.getDeadlineEpoch();
    			//TODO: add deadlined task to storage
    			break;
    				
    		case ADD_EVENT:
    			long startDateEpoch = task.getStartDateEpoch();
    			long endDateEpoch = task.getEndDateEpoch();
    			//TODO: add event task to storage
    			break;
    				
    		/*case ADD_RECURRING:
    			//TODO: add recurring task to storage
    			break;*/
    			
    		case DELETE_BY_INDEX:
    			//TODO: delete indexed task from storage
    			break;
    		
    		case DELETE_BY_NAME:
    			//TODO: delete named task from storage
    			break;
    		
    		case UPDATE_BY_INDEX:
    			//TODO: update indexed task in storage
    			break;
    		
    		case UPDATE_BY_NAME:
    			//TODO: update named task in storage
    			break;
    		
    		case UNDO:
    			String mostRecentCommand = mostRecentProcessedObject.getCommand();
    			switch (mostRecentCommand) {
    				case ADD_FLOATING:
    				case ADD_DEADLINE:
    				case ADD_EVENT:
    				case ADD_RECURRING:
    					//TODO: delete most recently added task from storage
    					break;
    				
    				case DELETE_BY_INDEX:
    				case DELETE_BY_NAME:
    					//TODO: add most recently deleted task to storage
    					break;
    				
    				case UPDATE_BY_INDEX:
    				case UPDATE_BY_NAME:
    					//TODO: revert most recently updated task in storage
    					break;
    				
    				default:
    			}
    			break;
    		
    		case ERROR:
    			//TODO
    			
    		default:
    	}
    	
    	// Update UI 
    	UiManager.getInstance().updateDisplay();
    	
    	return -1; //stub
    }
}