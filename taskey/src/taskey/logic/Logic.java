package taskey.logic;

import taskey.parser.Parser;
import taskey.storage.Storage;

enum StatusCode {
	COMMAND_INVALID, COMMAND_SUCCESS, ADD_FLOATING, ADD_DEADLINED, ADD_EVENT, ADD_RECURRING, DELETE, UPDATE,
	UNDO, VIEW
}

public class Logic {

	private static Logic instance = null;
	private static Task mostRecent = null;

    public static Logic getInstance() { 
    	if ( instance == null ) {
    		instance = new Logic();
    	}
    	return instance;
    }
    
    /*
     * Attemps to execute a command specified by line. 
     */
    public int executeCommand(String line) {
    	/*Parser parser = new Parser(); 
    	Storage storage = new Storage();
    	Task task = parser.parseInput(line); 
    	
    	if (task == null) { //Command is invalid
    		return COMMAND_INVALID;
    	}
    	
    	else {
    		StatusCode status = task.getStatusCode();
    		String taskName = task.getName();
    		
    		switch (status) {
    			case ADD_FLOATING:
    				//addTask(name, deadline, start date, end date, recurring date)
    				storage.addTask(taskName, null, null, null, null);
    				break;
    			
    			case ADD_DEADLINED:
    				Date deadline = task.getDeadline();
    				storage.addTask(taskName, deadline, null, null, null);
    				break;
    				
    			case ADD_EVENT:
    				Date startDate = task.getStartDate();
    				Date endDate = task.getEndDate();
    				storage.addTask(taskName, null, startDate, endDate, null);
    				break;
    				
    			case ADD_RECURRING:
    				Date recurringDate = task.getRecurringDate();
    				storage.addTask(taskName, null, null, null, recurringDate);
    				break;
    			
    			case DELETE:
    				storage.deleteTask(taskName);
    				break;
    			
    			case UPDATE:
    				???
    			
    			case UNDO:
    				StatusCode mostRecentStatus = mostRecent.getStatusCode();
    				if (mostRecentStatus == ADD_FLOATING || mostRecentStatus == ADD_DEADLINED
    					|| mostRecentStatus == ADD_EVENT || mostRecentStatus == ADD_RECURRING) {
    					storage.deleteTask(mostRecent.getName());
    				} else if (mostRecentStatus == DELETE) {
    					???
    				}
    				break;
    			
    			default:
    		}
    	}*/
    	return 0;
    	// Update UI 
    	// UiController.getInstance().update();
    }
}
