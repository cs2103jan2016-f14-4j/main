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
    			if (allMap.containsKey(taskName)) {
    				Task toDelete = allMap.get(taskName);
    				//TODO: ask Storage to delete task
    				String taskType = toDelete.getTaskType();
    				if (taskType == "FLOATING") {
    					floatingMap.remove(taskName);
    				} else if (taskType == "EVENT") {
    					eventMap.remove(taskName);
    				} else { //Deadline task
    					deadlineMap.remove(taskName);
    				}
    				allMap.remove(taskName);
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
    				} else if (taskType == "EVENT") {
    					eventMap.remove(taskName);
    				} else { //Deadline task
    					deadlineMap.remove(taskName);
    				}
    				allMap.remove(taskName);
    				//TODO: add updated Task to relevant HashMaps
    			} else {
    				statusCode = ERROR_UPDATE;
    			}
    			break;
    			
    		case "DONE_BY_INDEX":
    			//TODO: mark indexed task as done in storage
    			break;
    		
    		case "DONE_BY_NAME":
    			if (allMap.containsKey(taskName)) {
    				Task toComplete = allMap.get(taskName);
    				//TODO: ask Storage to mark task as done
    				String taskType = toComplete.getTaskType();
    				if (taskType == "FLOATING") {
    					floatingMap.remove(taskName);
    				} else if (taskType == "EVENT") {
    					eventMap.remove(taskName);
    				} else { //Deadline task
    					deadlineMap.remove(taskName);
    				}
    				allMap.remove(taskName);
    				doneMap.put(taskName, toComplete);
    			} else {
    				statusCode = ERROR_DONE;
    			}
    			break;
    			
    		case "SEARCH":
    			String searchPhrase = po.getSearchPhrae();
    			if (allMap.containsKey(searchPhrase)) {
    				Task t = allMap.get(searchPhrase);
    				//TODO: return Task to Ui
    			} else {
    				statusCode = ERROR_SEARCH;
    			}
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
    
    private int getListsFromStorage() throws IOException, ClassNotFoundException {
    	listsFromStorage = new ArrayList<ArrayList<Task>>(6);
    	
    	//Get ALL list from Storage
    	storage.setFilename("all tasks");
    	listsFromStorage.set(0, storage.loadTasks());
    	allMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(0)) {
    		allMap.put(t.getTaskName(), t);
    	}
    	allCollection = (ArrayList<Task>) allMap.values();
    	
    	//Get FLOATING list from Storage
    	storage.setFilename("floating tasks");
    	listsFromStorage.set(1, storage.loadTasks());
    	floatingMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(1)) {
    		floatingMap.put(t.getTaskName(), t);
    	}
    	floatingCollection = (ArrayList<Task>) floatingMap.values();
    	
    	//Get DEADLINE list from Storage
    	storage.setFilename("deadline tasks");
    	listsFromStorage.set(2, storage.loadTasks());
    	deadlineMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(2)) {
    		deadlineMap.put(t.getTaskName(), t);
    	}
    	deadlineCollection = (ArrayList<Task>) deadlineMap.values();
    	
    	//Get EVENT list from Storage
    	storage.setFilename("event tasks");
    	listsFromStorage.set(3, storage.loadTasks());
    	eventMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(3)) {
    		eventMap.put(t.getTaskName(), t);
    	}
    	eventCollection = (ArrayList<Task>) eventMap.values();
    	
    	//Get DONE list from Storage
    	storage.setFilename("done tasks");
    	listsFromStorage.set(4, storage.loadTasks());
    	doneMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(4)) {
    		doneMap.put(t.getTaskName(), t);
    	}
    	doneCollection = (ArrayList<Task>) doneMap.values();
    	
    	//Get EXPIRED list from Storage
    	storage.setFilename("expired tasks");
    	listsFromStorage.set(5, storage.loadTasks());
    	expiredMap = new HashMap<String, Task>();
    	for (Task t : listsFromStorage.get(5)) {
    		expiredMap.put(t.getTaskName(), t);
    	}
    	expiredCollection = (ArrayList<Task>) expiredMap.values();
    	
    	return -1; //Stub
    }
    
    
    private int view(String viewType, ArrayList<Task> tasksToView) throws ClassNotFoundException, IOException {
    	int statusCode = -1; //Stub 	
    	if (allMap == null) { //HashMap not initialized at startup, must get Tasks from Storage
			statusCode = getListsFromStorage();
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
    	
    	return statusCode; 
    }
    
    //Add the Task to Storage. Returns a status code representing outcome of action.
    private int add(ArrayList<Task> tasksToAdd, Task task, String command) throws IOException {
    	if (command == "ADD_FLOATING") {
    		storage.setFilename("floating tasks");
    		floatingMap.put(task.getTaskName(), task); //Technically should be done only after storage 
    		                                           //add succeeded
    	} else if (command == "ADD_DEADLINE") {
    		storage.setFilename("deadline tasks");
    		deadlineMap.put(task.getTaskName(), task);
    	} else { //Event tasks
    		storage.setFilename("event tasks");
    		eventMap.put(task.getTaskName(), task);
    	}
    	
    	tasksToAdd.add(task);
		storage.saveTasks(tasksToAdd);
		allMap.put(task.getTaskName(), task);
		
		return -1; //Stub
    }
    
    //Undo the most recent action that was not view, undo, search or error.
    //Returns a status code representing outcome of action.
	private int undo(ArrayList<Task> tasksToAdd) throws IOException {
		if (mostRecentProcessedObject == null) {
			return ERROR_UNDO; //Stub
		}
		String mostRecentCommand = mostRecentProcessedObject.getCommand();
		Task mostRecentTask = mostRecentProcessedObject.getTask();
		String mostRecentTaskType = mostRecentTask.getTaskType();
		String mostRecentTaskName = mostRecentTask.getTaskName();
		switch (mostRecentCommand) {
			case "ADD_FLOATING":
				floatingMap.remove(mostRecentTaskName); //Technically should only be done after storage has
				                                        //successfully performed delete
				allMap.remove(mostRecentTaskName);
				storage.setFilename("floating tasks");
				//TODO: delete floating task from storage
				break;
				
			case "ADD_DEADLINE":
				deadlineMap.remove(mostRecentTaskName);
				allMap.remove(mostRecentTaskName);
				storage.setFilename("deadline tasks");
				//TODO: delete deadline task from storage
				break;
				
			case "ADD_EVENT":
				eventMap.remove(mostRecentTaskName);
				allMap.remove(mostRecentTaskName);
				storage.setFilename("event tasks");
				//TODO: delete event task from storage
				break;
				
			/*case "ADD_RECURRING":
				break;*/
			
			case "DELETE_BY_INDEX":
			case "DELETE_BY_NAME":
				tasksToAdd.add(mostRecentTask);
				if (mostRecentTaskType == "FLOATING") {
					storage.setFilename("floating tasks");
					floatingMap.put(mostRecentTaskName, mostRecentTask);
				} else if (mostRecentTaskType == "EVENT") {
					storage.setFilename("event tasks");
					eventMap.put(mostRecentTaskName, mostRecentTask);
				} else { //Deadline tasks
					storage.setFilename("deadline tasks");
					deadlineMap.put(mostRecentTaskName, mostRecentTask);
				}
				storage.saveTasks(tasksToAdd);
				allMap.put(mostRecentTaskName, mostRecentTask);
				break;
			
			case "UPDATE_BY_INDEX":
			case "UPDATE_BY_NAME":
				//TODO: revert most recently updated task in storage
				break;
				
			case "DONE_BY_INDEX":
			case "DONE_BY_NAME":
				//TODO: mark most recently "done" task as "undone" in storage
				if (mostRecentTaskType == "FLOATING") {
					floatingMap.put(mostRecentTaskName, mostRecentTask);
				} else if (mostRecentTaskType == "EVENT") {
					eventMap.put(mostRecentTaskName, mostRecentTask);
				} else { //Deadline tasks
					deadlineMap.put(mostRecentTaskName, mostRecentTask);
				}
				
				doneMap.remove(mostRecentTaskName);
				allMap.put(mostRecentTaskName, mostRecentTask);
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