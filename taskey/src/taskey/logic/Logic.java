package taskey.logic;

import taskey.parser.Parser;
import taskey.storage.Storage;
import taskey.ui.UiManager;
import taskey.logic.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import taskey.logic.ProcessedObject;

/**
 * TODO: class description
 * 
 * @author Hubert Wong
 */
public class Logic {
	/* List of status codes. Other components like Ui can use Logic.statusCode to access this list. */
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
	
	private static Logic instance = null;
	private Parser parser;
	private Storage storage;
	private UiManager uiManager;
	//The most recent processed object whose command is not VIEW, UNDO, SEARCH or ERROR
	private ProcessedObject mostRecentProcessedObject = null;
	//The current view type that Ui is displaying, e.g. deadline, events
	private String uiCurrentViewType = null;
	//Task lists retrieved from Storage at startup 
	private ArrayList<ArrayList<Task>> listsFromStorage = null;
	
	//HashMaps containing Task data for each category
	private HashMap<String, Task> allMap = null;
	private HashMap<String, Task> floatingMap = null;
	private HashMap<String, Task> deadlineMap = null;
	private HashMap<String, Task> eventMap = null;
	private HashMap<String, Task> doneMap = null;
	private HashMap<String, Task> expiredMap = null;
	
	//Collections of Task objects backed by the above HashMaps 
	private ArrayList<Task> allCollection = null; 
	private ArrayList<Task> floatingCollection = null;
	private ArrayList<Task> deadlineCollection = null;
	private ArrayList<Task> eventCollection = null;
	private ArrayList<Task> doneCollection = null;
	private ArrayList<Task> expiredCollection = null;
	
	//Constructor
	public static Logic getInstance() {
		if (instance == null) {
    		instance = new Logic();
    		instance.parser = Parser.getInstance();
    		instance.storage = Storage.getInstance();
    		instance.uiManager = UiManager.getInstance();
    	}
    	return instance;
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
    	String taskName = task.getTaskName();
    	int statusCode = 0;
    	int index; //Only used for commands that specify the index of a task
    	switch (command) {
    		case "VIEW":
    			String viewType = po.getViewType();
    			statusCode = view(viewType);
    			break;
    			
    		case "ADD_FLOATING":
    		case "ADD_DEADLINE":
    		case "ADD_EVENT":
    			statusCode = add(task, command);
    			break;
    			
    		case "DELETE_BY_INDEX":
    			index = po.getIndex() - 1; //Convert to 0-based index
    			if (uiCurrentViewType == "ALL") {
    				if (index >= allCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				Task toDelete = allCollection.get(index);
    				String toDeleteType = toDelete.getTaskType();
    				String toDeleteName = toDelete.getTaskName();
    				allCollection.remove(index);
    				storage.saveTaskList(allCollection, "all tasks"); //What if this fails? Then we have to put back the removed
    				                                  				  //task into allCollection. Either that or make a copy b4
    				                                  				  //passing to Storage
    				if (toDeleteType == "FLOATING") {
    					floatingMap.remove(toDeleteName);
        				storage.saveTaskList(floatingCollection, "floating tasks");
    				} else if (toDeleteType == "DEADLINE") {
    					deadlineMap.remove(toDeleteName);
        				storage.saveTaskList(deadlineCollection, "deadline tasks");
    				} else if (toDeleteType == "EVENT") {
    					eventMap.remove(toDeleteName);
        				storage.saveTaskList(eventCollection, "event tasks");
    				}
    			} else if (uiCurrentViewType == "FLOATING") {
    				if (index >= floatingCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				Task toDelete = floatingCollection.get(index);
    				String toDeleteName = toDelete.getTaskName();
    				floatingCollection.remove(index);
    				storage.saveTaskList(floatingCollection, "floating tasks");
    				allMap.remove(toDeleteName);
    				storage.saveTaskList(allCollection, "all tasks");
    			} else if (uiCurrentViewType == "EVENT") {
    				if (index >= eventCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				Task toDelete = eventCollection.get(index);
    				String toDeleteName = toDelete.getTaskName();
    				eventCollection.remove(index);
    				storage.saveTaskList(eventCollection, "event tasks");
    				allMap.remove(toDeleteName);
    				storage.saveTaskList(allCollection, "all tasks");
    			} else if (uiCurrentViewType == "DEADLINE") {
    				if (index >= deadlineCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				Task toDelete = deadlineCollection.get(index);
    				String toDeleteName = toDelete.getTaskName();
    				deadlineCollection.remove(index);
    				storage.saveTaskList(eventCollection, "deadline tasks");
    				allMap.remove(toDeleteName);
    				storage.saveTaskList(allCollection, "all tasks");
    			}
    			break;
    		
    		case "DELETE_BY_NAME":
    			if (allMap.containsKey(taskName)) {
    				Task toDelete = allMap.get(taskName);
    				String taskType = toDelete.getTaskType();
    				if (taskType == "FLOATING") {
    					floatingMap.remove(taskName);
        				storage.saveTaskList(floatingCollection, "floating tasks");
    				} else if (taskType == "EVENT") {
    					eventMap.remove(taskName);
        				storage.saveTaskList(eventCollection, "event tasks");
    				} else { //Deadline task
    					deadlineMap.remove(taskName);
        				storage.saveTaskList(deadlineCollection, "deadline tasks");
    				}
    				allMap.remove(taskName);
    				storage.saveTaskList(allCollection, "all tasks");
    			} else { //Task to delete does not exist
    				statusCode = ERROR_DELETE;
    			}
    			break;
    		
    		case "UPDATE_BY_INDEX":
    			//TODO: update indexed task in storage
    			break;
    		
    		case "UPDATE_BY_NAME":
    			if (allMap.containsKey(taskName)) {
    				Task toUpdate = allMap.get(taskName);
    				//TODO: ask Storage to update task
    				String taskType = toUpdate.getTaskType();
    				if (taskType == "FLOATING") {
    					floatingMap.remove(taskName);
    					storage.saveTaskList(floatingCollection, "floating tasks");
    				} else if (taskType == "EVENT") {
    					eventMap.remove(taskName);
    					storage.saveTaskList(eventCollection, "event tasks");
    				} else { //Deadline task
    					deadlineMap.remove(taskName);
    					storage.saveTaskList(deadlineCollection, "deadline tasks");
    				}
    				allMap.remove(taskName);
    				storage.saveTaskList(allCollection, "all tasks");
    				//TODO: add updated Task to relevant HashMaps
    			} else {
    				statusCode = ERROR_UPDATE;
    			}
    			break;
    			
    		case "DONE_BY_INDEX":
    			index = po.getIndex() - 1; //Convert to 0-based index
    			Task toMarkAsDone = null;
    			String toMarkAsDoneType = null;
    			String toMarkAsDoneName = null;
    			if (uiCurrentViewType == "ALL") {
    				if (index >= allCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				toMarkAsDone = allCollection.get(index);
    				toMarkAsDoneType = toMarkAsDone.getTaskType();
    				toMarkAsDoneName = toMarkAsDone.getTaskName();
    				allCollection.remove(index);
    				storage.saveTaskList(allCollection, "all tasks"); 
    				if (toMarkAsDoneType == "FLOATING") {
    					floatingMap.remove(toMarkAsDoneName);
        				storage.saveTaskList(floatingCollection, "floating tasks");
    				} else if (toMarkAsDoneType == "DEADLINE") {
    					deadlineMap.remove(toMarkAsDoneName);
        				storage.saveTaskList(deadlineCollection, "deadline tasks");
    				} else if (toMarkAsDoneType == "EVENT") {
    					eventMap.remove(toMarkAsDoneName);
        				storage.saveTaskList(eventCollection, "event tasks");
    				}
    			} else if (uiCurrentViewType == "FLOATING") {
    				if (index >= floatingCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				toMarkAsDone = floatingCollection.get(index);
    				toMarkAsDoneName = toMarkAsDone.getTaskName();
    				floatingCollection.remove(index);
    				storage.saveTaskList(floatingCollection, "floating tasks");
    				allMap.remove(toMarkAsDoneName);
    				storage.saveTaskList(allCollection, "all tasks");
    			} else if (uiCurrentViewType == "EVENT") {
    				if (index >= eventCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				toMarkAsDone = eventCollection.get(index);
    				toMarkAsDoneName = toMarkAsDone.getTaskName();
    				eventCollection.remove(index);
    				storage.saveTaskList(eventCollection, "event tasks");
    				allMap.remove(toMarkAsDoneName);
    				storage.saveTaskList(allCollection, "all tasks");
    			} else if (uiCurrentViewType == "DEADLINE") {
    				if (index >= deadlineCollection.size()) { //Out-of bound
    					return -1; //Stub
    				}
    				toMarkAsDone = deadlineCollection.get(index);
    				toMarkAsDoneName = toMarkAsDone.getTaskName();
    				deadlineCollection.remove(index);
    				storage.saveTaskList(eventCollection, "deadline tasks");
    				allMap.remove(toMarkAsDoneName);
    				storage.saveTaskList(allCollection, "all tasks");
    			}
    			doneMap.put(toMarkAsDoneName, toMarkAsDone);
    			storage.saveTaskList(doneCollection, "done tasks");
    			break;
    		
    		case "DONE_BY_NAME":
    			if (allMap.containsKey(taskName)) {
    				Task toComplete = allMap.get(taskName);
    				String taskType = toComplete.getTaskType();
    				if (taskType == "FLOATING") {
    					floatingMap.remove(taskName);
    					storage.saveTaskList(floatingCollection, "floating tasks");
    				} else if (taskType == "EVENT") {
    					eventMap.remove(taskName);
    					storage.saveTaskList(eventCollection, "event tasks");
    				} else { //Deadline task
    					deadlineMap.remove(taskName);
    					storage.saveTaskList(deadlineCollection, "deadline tasks");
    				}
    				allMap.remove(taskName);
    				storage.saveTaskList(allCollection, "all tasks");
    				doneMap.put(taskName, toComplete);
    				storage.saveTaskList(doneCollection, "done tasks");
    			} else {
    				statusCode = ERROR_DONE;
    			}
    			break;
    			
    		case "SEARCH":
    			String searchPhrase = po.getSearchPhrae();
    			if (allMap.containsKey(searchPhrase)) { //Only works if searchPhrase matches taskName
    				Task t = allMap.get(searchPhrase);
    				//TODO: return Task to Ui
    			} else {
    				statusCode = ERROR_SEARCH;
    			}
    			break;
    		
    		case "UNDO":
    			statusCode = undo();
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
    
    //Get Task lists from Storage at startup and populate the HashMaps and their corresponding collections.
    //Returns a status code representing outcome of action.
    private int getListsFromStorage() throws IOException, ClassNotFoundException {
    	listsFromStorage = new ArrayList<ArrayList<Task>>(6);
    	
    	//Get ALL list from Storage
    	listsFromStorage.set(0, storage.getTaskList("all tasks"));
    	allMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(0)) {
    		allMap.put(t.getTaskName(), t);
    	}
    	allCollection = (ArrayList<Task>) allMap.values();
    	
    	//Get FLOATING list from Storage
    	listsFromStorage.set(1, storage.getTaskList("floating tasks"));
    	floatingMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(1)) {
    		floatingMap.put(t.getTaskName(), t);
    	}
    	floatingCollection = (ArrayList<Task>) floatingMap.values();
    	
    	//Get DEADLINE list from Storage
    	listsFromStorage.set(2, storage.getTaskList("deadline tasks"));
    	deadlineMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(2)) {
    		deadlineMap.put(t.getTaskName(), t);
    	}
    	deadlineCollection = (ArrayList<Task>) deadlineMap.values();
    	
    	//Get EVENT list from Storage
    	listsFromStorage.set(3, storage.getTaskList("event tasks"));
    	eventMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(3)) {
    		eventMap.put(t.getTaskName(), t);
    	}
    	eventCollection = (ArrayList<Task>) eventMap.values();
    	
    	//Get DONE list from Storage
    	listsFromStorage.set(4, storage.getTaskList("done tasks"));
    	doneMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(4)) {
    		doneMap.put(t.getTaskName(), t);
    	}
    	doneCollection = (ArrayList<Task>) doneMap.values();
    	
    	//Get EXPIRED list from Storage
    	listsFromStorage.set(5, storage.getTaskList("expired tasks"));
    	expiredMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(5)) {
    		expiredMap.put(t.getTaskName(), t);
    	}
    	expiredCollection = (ArrayList<Task>) expiredMap.values();
    	
    	return -1; //Stub
    }
    
    //Updates Ui with a list of Tasks sorted by date, corresponding to the view type.
    //Returns a status code representing outcome of action.
    private int view(String viewType) throws ClassNotFoundException, IOException {
    	int statusCode = -1; //Stub 	
    	if (allMap == null) { //HashMap not initialized at startup, must get Tasks from Storage
			statusCode = getListsFromStorage();
			uiCurrentViewType = "ALL";
		} else if (viewType == "ALL") {
			Collections.sort(allCollection);
			//TODO: update Ui with allCollection
		} else if (viewType == "GENERAL") {
			Collections.sort(floatingCollection);
			//TODO: update Ui with floatingCollection
		} else if (viewType == "DEADLINES") {
			Collections.sort(deadlineCollection);
			//TODO: update Ui with deadlineCollection
		} else if (viewType == "EVENTS") {
			Collections.sort(eventCollection);
			//TODO: update Ui with eventCollection
		} else if (viewType == "DONE") {
			Collections.sort(doneCollection);
			//TODO: update Ui with doneCollection
		} else { //Expired tasks
			Collections.sort(expiredCollection);
			//TODO: update Ui with expiredCollection
		}
    	uiCurrentViewType = viewType;
    	
    	return statusCode; 
    }
    
    //Add the Task to Storage. Returns a status code representing outcome of action.
    private int add(Task task, String command) throws IOException {
    	if (command == "ADD_FLOATING") {
    		floatingMap.put(task.getTaskName(), task); 
    		storage.saveTaskList(floatingCollection, "floating tasks");		
    	} else if (command == "ADD_DEADLINE") {
    		deadlineMap.put(task.getTaskName(), task);
    		storage.saveTaskList(deadlineCollection, "deadline tasks");
    	} else { //Event tasks
    		eventMap.put(task.getTaskName(), task);
    		storage.saveTaskList(eventCollection, "event tasks");
    	}
		allMap.put(task.getTaskName(), task);
		storage.saveTaskList(allCollection, "all tasks");
		
		return -1; //Stub
    }
    
    //Undo the most recent action that was not view, undo, search or error.
    //Returns a status code representing outcome of action.
	private int undo() throws IOException {
		if (mostRecentProcessedObject == null) { //No undoable tasks since startup
			return ERROR_UNDO; //Stub
		}
		String mostRecentCommand = mostRecentProcessedObject.getCommand();
		Task mostRecentTask = mostRecentProcessedObject.getTask();
		String mostRecentTaskType = mostRecentTask.getTaskType();
		String mostRecentTaskName = mostRecentTask.getTaskName();
		switch (mostRecentCommand) {
			case "ADD_FLOATING":
				floatingMap.remove(mostRecentTaskName); 
				storage.saveTaskList(floatingCollection, "floating tasks");
				allMap.remove(mostRecentTaskName);
				storage.saveTaskList(allCollection, "all tasks");
				break;
				
			case "ADD_DEADLINE":
				deadlineMap.remove(mostRecentTaskName);
				storage.saveTaskList(deadlineCollection, "deadline tasks");
				allMap.remove(mostRecentTaskName);
				storage.saveTaskList(allCollection, "all tasks");
				break;
				
			case "ADD_EVENT":
				eventMap.remove(mostRecentTaskName);
				storage.saveTaskList(eventCollection, "event tasks");
				allMap.remove(mostRecentTaskName);
				storage.saveTaskList(allCollection, "all tasks");
				break;
			
			case "DELETE_BY_INDEX":
			case "DELETE_BY_NAME":
				if (mostRecentTaskType == "FLOATING") {
					floatingMap.put(mostRecentTaskName, mostRecentTask);
					storage.saveTaskList(floatingCollection, "floating tasks");
				} else if (mostRecentTaskType == "EVENT") {
					eventMap.put(mostRecentTaskName, mostRecentTask);
					storage.saveTaskList(eventCollection, "event tasks");
				} else { //Deadline tasks
					deadlineMap.put(mostRecentTaskName, mostRecentTask);
					storage.saveTaskList(deadlineCollection, "deadline tasks");
				}
				allMap.put(mostRecentTaskName, mostRecentTask);
				storage.saveTaskList(allCollection, "all tasks");
				break;
			
			case "UPDATE_BY_INDEX":
			case "UPDATE_BY_NAME":
				//TODO: revert most recently updated task in storage
				break;
				
			case "DONE_BY_INDEX":
			case "DONE_BY_NAME":
				if (mostRecentTaskType == "FLOATING") {
					floatingMap.put(mostRecentTaskName, mostRecentTask);
					storage.saveTaskList(floatingCollection, "floating tasks");
				} else if (mostRecentTaskType == "EVENT") {
					eventMap.put(mostRecentTaskName, mostRecentTask);
					storage.saveTaskList(eventCollection, "event tasks");
				} else { //Deadline tasks
					deadlineMap.put(mostRecentTaskName, mostRecentTask);
					storage.saveTaskList(deadlineCollection, "deadline tasks");
				}				
				doneMap.remove(mostRecentTaskName);
				storage.saveTaskList(doneCollection, "done tasks");				
				allMap.put(mostRecentTaskName, mostRecentTask);
				storage.saveTaskList(allCollection, "all tasks");				
				break;
			
			default:
		}
		
		return -1; //Stub
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