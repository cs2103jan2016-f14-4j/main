package taskey.logic;

import taskey.parser.Parser;
import taskey.storage.Storage;
import taskey.ui.UiMain;
import taskey.ui.UiController;
import taskey.ui.UiConstants.ContentBox;
import taskey.logic.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import taskey.logic.LogicConstants.ListsID;
import taskey.logic.ProcessedObject;

/**
 * TODO: class description
 * 
 * @author Hubert Wong
 */
public class Logic {
	private static Logic instance = null;
	private static Parser parser;
	private static UiController uiController;
	private static ArrayList<String> fileNames;
	private static ArrayList<ArrayList<Task>> lists;
	public static final int numTabs = 2;
	
	/** Get the Logic singleton */
	public static Logic getInstance() {
		if ( instance == null ) {
			instance = new Logic();
		}
		return instance;
	}
	
	/** Initializes the Logic singleton and updates Ui with the lists from Storage. */
	public void initialize() {
		instance = Logic.getInstance();
		parser = new Parser();
		uiController = UiMain.getInstance().getController();
		fileNames = new ArrayList<String>(Arrays.asList("PENDING", "EXPIRED", "COMPLETED", "GENERAL", "DEADLINE", 
                										"EVENT"));
		lists = new ArrayList<ArrayList<Task>>();
		
		for (int i = 0; i < numTabs; i++) {
			ArrayList<Task> list = Storage.getInstance().getTaskList(fileNames.get(i));
			lists.add(list);
			uiController.updateDisplay(list, ContentBox.fromInteger(i + 1));
		}
		
		//TODO: update the categories "THIS_WEEK", "COMPLETED", "GENERAL", "DEADLINE", "EVENT"
		ArrayList<String> categoryList = new ArrayList<String>(Arrays.asList("General", "Deadline", "Event", 
				                                                             "Completed"));
		ArrayList<Integer> categorySizes = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0));
		uiController.updateCategoryDisplay(categoryList, categorySizes);
	}
	
	public ArrayList<Task> getListFromContentBox(ContentBox currentContent) {
		ArrayList<Task> targetList = null;
		switch (currentContent) {
			case PENDING:
				targetList = lists.get(ListsID.PENDING.getValue());
				break;
			case EXPIRED:
				targetList = lists.get(ListsID.EXPIRED.getValue());
				break;
			//case COMPLETED:
			//	targetList = myLists.get(ListsID.COMPLETED.getValue());
			//	break;
			case ACTION:
				targetList = lists.get(ListsID.ACTION.getValue());
				break;
			default:
				System.out.println("ContentBox invalid");
		}
		return targetList;
	}
	
	public Task getTaskByName(ArrayList<Task> targetList, String name ) {
		for ( int i = 0; i < targetList.size(); i ++ ) {
			Task theTask = targetList.get(i);
			if ( theTask.getTaskName().equals(name)) {
				return theTask;
			}
		}
		return null;
	}
	
	public int executeCommand(ContentBox currentContent, String input) {
		int statusCode = 0; //Stub
    	ProcessedObject po = parser.parseInput(input);
    	
    	// important objects
    	String command = po.getCommand();
    	Task task = po.getTask();
    	int taskIndex = po.getIndex(); //Only used for commands that specify the index of a task
    	String viewType = po.getViewType(); //Only used for view commands
    	String errorType = po.getErrorType(); //Only used for invalid commands
    	String searchPhrase = po.getSearchPhrase(); //Only used for search commands
    	String newTaskName = po.getNewTaskName(); //Only used for commands that change the name of a task
    	//String taskName = task.getTaskName();
    	Task done;
    	Task toUpdate;
    	ArrayList<Task> doneList;
    
    	ArrayList<Task> targetList = getListFromContentBox(currentContent);
    	System.out.println("Command: " + command);
    	
    	switch (command) {
			case "ADD_FLOATING":
			case "ADD_DEADLINE":
			case "ADD_EVENT":
				targetList.add(task);
				Collections.sort(targetList); //Right now doesn't seem to do anything
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "DELETE_BY_INDEX":
				targetList.remove(taskIndex);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "DELETE_BY_NAME":
				targetList.remove(getTaskByName(targetList, task.getTaskName()));
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "DONE_BY_INDEX":
				//System.out.println(taskIndex);
				done = targetList.remove(taskIndex - 1); //Temporary fix 
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				doneList = lists.get(ListsID.COMPLETED.getValue());
				doneList.add(done);
			//	UiMain.getInstance().getController().updateDisplay(doneList, ContentBox.COMPLETED);
				break;
				
			case "DONE_BY_NAME":
				done = getTaskByName(targetList, task.getTaskName());
				targetList.remove(done);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				doneList = lists.get(ListsID.COMPLETED.getValue());
				doneList.add(done);
			//	UiMain.getInstance().getController().updateDisplay(doneList, ContentBox.COMPLETED);
				break;
				
			case "UPDATE_BY_INDEX_CHANGE_NAME":
				toUpdate = targetList.get(taskIndex - 1); //Temporary fix
				toUpdate.setTaskName(newTaskName);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "UPDATE_BY_INDEX_CHANGE_DATE":
				toUpdate = targetList.remove(taskIndex - 1); //Temporary fix
				task.setTaskName(toUpdate.getTaskName());
				targetList.add(task);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "UPDATE_BY_NAME_CHANGE_NAME":
				toUpdate = getTaskByName(targetList, task.getTaskName());
				toUpdate.setTaskName(newTaskName);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "UPDATE_BY_NAME_CHANGE_DATE":
				toUpdate = getTaskByName(targetList, task.getTaskName()); 
				targetList.remove(toUpdate);
				targetList.add(task);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "UNDO":
				break;
			default:
				statusCode = -1; //Stub
				break;
		}
	
    	// do saving
		saveAllTasks();
		return statusCode; // no issues
	}
	
	public void saveAllTasks() {
		try {
			for ( int i = 0; i < fileNames.size(); i ++ ) {
				Storage.getInstance().saveTaskList(lists.get(i), fileNames.get(i));
				System.out.println("List: " + fileNames.get(i) + " saved");
			}
		} catch ( Exception e ) {
			System.out.println("Error calling storage to save tasks");
		}
	}
}