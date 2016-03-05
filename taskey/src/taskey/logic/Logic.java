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

import javafx.scene.paint.Color;
import taskey.logic.LogicConstants.CategoryID;
import taskey.logic.LogicConstants.ListID;
import taskey.logic.ProcessedObject;

/**
 * TODO: class description
 * 
 * @author Hubert Wong
 */
public class Logic {
	private static Logic instance = null;
	private Parser parser;
	private UiController uiController;
	private ArrayList<String> fileNames;
	private ArrayList<ArrayList<Task>> lists;
	private ArrayList<String> categoryList;
	private ArrayList<Integer> categorySizes;
	private ArrayList<Color> colorList;
	
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
		fileNames = new ArrayList<String>(Arrays.asList("PENDING", "EXPIRED", "GENERAL", "DEADLINE", "EVENT",
				                                        "COMPLETED"));
		lists = new ArrayList<ArrayList<Task>>();
		
		for (int i = 0; i < fileNames.size(); i++) {
			ArrayList<Task> list = Storage.getInstance().getTaskList(fileNames.get(i));
			lists.add(list);
			
			if (i < 2) { //Only "PENDING" and "EXPIRED" have tabs
				uiController.updateDisplay(list, ContentBox.fromInteger(i + 1));
			}
		}
		
		//TODO: update the "THIS_WEEK" tab
		categoryList = new ArrayList<String>(Arrays.asList("General", "Deadline", "Event", "Completed"));
		categorySizes = new ArrayList<Integer>(Arrays.asList(lists.get(ListID.GENERAL.getValue()).size(),
															 lists.get(ListID.DEADLINE.getValue()).size(),
															 lists.get(ListID.EVENT.getValue()).size(),
															 lists.get(ListID.COMPLETED.getValue()).size()));
		colorList = new ArrayList<Color>(Arrays.asList(Color.INDIGO, Color.BISQUE, Color.HOTPINK, Color.LIME));
		uiController.updateCategoryDisplay(categoryList, categorySizes, colorList);
	}
	
	private ArrayList<Task> getListFromContentBox(ContentBox currentContent) {
		ArrayList<Task> targetList = null;
		switch (currentContent) {
			case PENDING:
				targetList = lists.get(ListID.PENDING.getValue());
				break;
			case EXPIRED:
				targetList = lists.get(ListID.EXPIRED.getValue());
				break;
			//case COMPLETED:
			//	targetList = myLists.get(ListsID.COMPLETED.getValue());
			//	break;
			//case ACTION:
			//	targetList = lists.get(ListsID.ACTION.getValue());
			//	break;
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
	
	/** 
	 * Executes the user supplied command.
	 * 
	 * @param currentContent specifies the current tab that user is in.
	 * @param input			 the input String entered by the user
	 * @return               status code representing outcome of command execution
	 */
	public int executeCommand(ContentBox currentContent, String input) {
		int statusCode = 0; //Stub
    	ProcessedObject po = parser.parseInput(input);
    	
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
				addFloating(currentContent, task, targetList);
				break;
				
			case "ADD_DEADLINE":
				break;
				
			case "ADD_EVENT":
				break;
				
			case "DELETE_BY_INDEX":
				statusCode = deleteByIndex(currentContent, taskIndex, targetList);
				break;
				
			case "DELETE_BY_NAME":
				targetList.remove(getTaskByName(targetList, task.getTaskName()));
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
				
			case "DONE_BY_INDEX":
				//System.out.println(taskIndex);
				done = targetList.remove(taskIndex - 1); //Temporary fix 
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				doneList = lists.get(ListID.COMPLETED.getValue());
				doneList.add(done);
			//	UiMain.getInstance().getController().updateDisplay(doneList, ContentBox.COMPLETED);
				break;
				
			case "DONE_BY_NAME":
				done = getTaskByName(targetList, task.getTaskName());
				targetList.remove(done);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				doneList = lists.get(ListID.COMPLETED.getValue());
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
	
		saveAllTasks();
		
		return statusCode; 
	}
	
	//Removes an indexed task from the Ui tab specified by currentContent.
	private int deleteByIndex(ContentBox currentContent, int taskIndex, ArrayList<Task> targetList) {
		if (taskIndex >= targetList.size()) { //Out of bounds
			return -1; //Stub
		}
		
		Task t = targetList.get(taskIndex);
		String taskType = t.getTaskType();
		targetList.remove(taskIndex);
		
		if (taskType.equals("FLOATING")) {
			lists.get(ListID.GENERAL.getValue()).remove(t);
			int currentSize = categorySizes.get(CategoryID.GENERAL.getValue());
			categorySizes.set(CategoryID.GENERAL.getValue(), currentSize - 1);
		}
		
		uiController.updateDisplay(targetList, currentContent);
		uiController.updateCategoryDisplay(categoryList, categorySizes, colorList);
		
		return 0; //Stub
	}
	
	//Updates Ui with a new floating task.
	private void addFloating(ContentBox currentContent, Task task, ArrayList<Task> targetList) {
		ArrayList<Task> floatingList = lists.get(ListID.GENERAL.getValue());
		floatingList.add(task);
		targetList.add(task);
		updateUiContentDisplay(targetList, currentContent);
		int currentSize = categorySizes.get(CategoryID.GENERAL.getValue());
		categorySizes.set(CategoryID.GENERAL.getValue(), currentSize + 1);
		uiController.updateCategoryDisplay(categoryList, categorySizes, colorList);
	}
	
	//Sorts targetList and updates the Ui tab corresponding to currentContent with the sorted list.
	private void updateUiContentDisplay(ArrayList<Task> targetList, ContentBox currentContent) {
		Collections.sort(targetList); //Right now doesn't seem to do anything
		uiController.updateDisplay(targetList, currentContent);
	}
	
	//Save all task lists to Storage.
	private void saveAllTasks() {
		try {
			for (int i = 0; i < fileNames.size(); i++) {
				Storage.getInstance().saveTaskList(lists.get(i), fileNames.get(i));
				System.out.println("List: " + fileNames.get(i) + " saved");
			}
		} catch (Exception e) {
			System.out.println("Error in saving tasks");
		}
	}
}