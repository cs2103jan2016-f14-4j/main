package taskey.logic;

import taskey.parser.Parser;
import taskey.parser.TimeConverter;
import taskey.storage.Storage;
import taskey.ui.UiMain;
import taskey.ui.UiController;
import taskey.ui.UiConstants.ActionListMode;
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
	private TimeConverter timeConverter;
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
		timeConverter = new TimeConverter();
		uiController = UiMain.getInstance().getController();
		fileNames = new ArrayList<String>(Arrays.asList("PENDING", "EXPIRED", "GENERAL", "DEADLINE", "EVENT",
				                                        "COMPLETED"));
		lists = new ArrayList<ArrayList<Task>>();
		ArrayList<Task> thisWeek = new ArrayList<Task>();
		lists.add(thisWeek);
		
		for (int i = 0; i < fileNames.size(); i++) {
			ArrayList<Task> list = Storage.getInstance().getTaskList(fileNames.get(i));
			lists.add(list);
			
			if (i < 2) { //Only "PENDING" and "EXPIRED" have tabs
				uiController.updateDisplay(list, ContentBox.fromInteger(i + 1));
			}
		}
		
		//Update THIS_WEEK tab
		ArrayList<Task> pendingList = lists.get(ListID.PENDING.getValue());
		for (Task t : pendingList) {
			if (timeConverter.isSameWeek(t.getDeadlineEpoch(), timeConverter.getCurrTime()) ||
				timeConverter.isSameWeek(t.getStartDateEpoch(), timeConverter.getCurrTime())) {
				thisWeek.add(t);
			}
		}
		uiController.updateDisplay(thisWeek, ContentBox.THIS_WEEK);
		
		categoryList = new ArrayList<String>(Arrays.asList("General", "Deadline", "Event", "Completed"));
		//Values will be updated with refreshUiCategoryDisplay();
		categorySizes = new ArrayList<Integer>(Arrays.asList(0, 0, 0, 0)); 
		colorList = new ArrayList<Color>(Arrays.asList(Color.INDIGO, Color.BISQUE, Color.HOTPINK, Color.LIME));
		refreshUiCategoryDisplay();
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
			case THIS_WEEK:
				targetList = lists.get(ListID.THIS_WEEK.getValue());
				break;
			//case ACTION:
			//	targetList = lists.get(ListsID.ACTION.getValue());
			//	break;
			default:
				System.out.println("ContentBox invalid");
		}
		return targetList;
	}
	
	//Returns the first Task whose name matches the given name, or null otherwise.
	private Task getTaskByName(ArrayList<Task> targetList, String name ) {
		for ( int i = 0; i < targetList.size(); i ++ ) {
			Task theTask = targetList.get(i);
			if ( theTask.getTaskName().equals(name)) {
				return theTask;
			}
		}
		
		return null;
	}
	
	private int clearAllLists(ContentBox currentContent) {
		if (!currentContent.equals(ContentBox.PENDING)) { // "clear" only allowed in pending tab
			return -1;
		}
		
		for (int i = 0; i < fileNames.size() + 1; i++) {
			lists.get(i).clear();
		}
		
		return 0;
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
		
		//System.out.println(input);
		
    	if (input.equalsIgnoreCase("clear")) { // "clear" command is for developer testing only
			statusCode = clearAllLists(currentContent);
			saveAllTasks();
			UiMain.getInstance().getController().updateDisplay(lists.get(0), ContentBox.THIS_WEEK);
			UiMain.getInstance().getController().updateDisplay(lists.get(1), ContentBox.PENDING);
			UiMain.getInstance().getController().updateDisplay(lists.get(2), ContentBox.EXPIRED);
			//Clear the action tab
			UiMain.getInstance().getController().updateDisplay(new ArrayList<Task>(), ContentBox.ACTION);
			refreshUiCategoryDisplay();
			
			return statusCode;
    	}
    	
    	ProcessedObject po = parser.parseInput(input); 	
    	String command = po.getCommand();
    	Task task = po.getTask();
    	int taskIndex = po.getIndex(); //Only used for commands that specify the index of a task
    	String viewType = po.getViewType(); //Only used for view commands
    	String errorType = po.getErrorType(); //Only used for invalid commands
    	String searchPhrase = po.getSearchPhrase(); //Only used for search commands
    	String newTaskName = po.getNewTaskName(); //Only used for commands that change the name of a task
    	Task done;
    	Task toUpdate;
    	ArrayList<Task> doneList;
    
    	ArrayList<Task> targetList = getListFromContentBox(currentContent);
    	System.out.println("Command: " + command);
    	
    	switch (command) {		
			case "ADD_FLOATING":
				statusCode = addFloating(task);
				break;
				
			case "ADD_DEADLINE":
				statusCode = addDeadline(task);
				break;
				
			case "ADD_EVENT":
				statusCode = addEvent(task);
				break;
				
			case "DELETE_BY_INDEX":
				statusCode = deleteByIndex(currentContent, taskIndex, targetList);
				break;
				
			case "DELETE_BY_NAME":
				statusCode = deleteByName(currentContent, task.getTaskName(), targetList);
				break;
				
			case "VIEW":
				statusCode = view(viewType); 
				break;
				
			case "SEARCH":
				statusCode = search(searchPhrase);
				break;
				
			case "DONE_BY_INDEX":
				statusCode = doneByIndex(currentContent, taskIndex); 
				break;
				
			/*case "DONE_BY_NAME":
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
				break; */
				
			default:
				statusCode = -1; //Stub
				break;
		}
	
		saveAllTasks();
		
		return statusCode; 
	}
	
	private void refreshUiCategoryDisplay() {
		categorySizes.set(CategoryID.GENERAL.getValue(), lists.get(ListID.GENERAL.getValue()).size());
		categorySizes.set(CategoryID.DEADLINE.getValue(), lists.get(ListID.DEADLINE.getValue()).size());
		categorySizes.set(CategoryID.EVENT.getValue(), lists.get(ListID.EVENT.getValue()).size());
		categorySizes.set(CategoryID.COMPLETED.getValue(), lists.get(ListID.COMPLETED.getValue()).size());
		uiController.updateCategoryDisplay(categoryList, categorySizes, colorList);
	}
	
	//Mark an indexed task as done by adding it to the "completed" list and removing it from all the
	//lists of incomplete tasks. Also updates the UI display to reflect the updated lists.
	private int doneByIndex(ContentBox currentContent, int taskIndex) {
		Task toMarkAsDone = null;
		if (currentContent.equals(ContentBox.THIS_WEEK)) {
			try {
				toMarkAsDone = lists.get(ListID.THIS_WEEK.getValue()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return -1;
			}
		} else if (currentContent.equals(ContentBox.PENDING)) {
			try {
				toMarkAsDone = lists.get(ListID.PENDING.getValue()).remove(taskIndex);
			} catch (IndexOutOfBoundsException e) {
				return -1;
			}
		} else { //"done" command is not allowed in tabs other than "this week" or "pending"
			return -1;
		}
		
		lists.get(ListID.COMPLETED.getValue()).add(toMarkAsDone);
		
		//Remove the completed task from any other lists it may be included in.
		//If duplicate task names are allowed, the intended task may not be removed.
		lists.get(ListID.THIS_WEEK.getValue()).remove(toMarkAsDone);
		lists.get(ListID.PENDING.getValue()).remove(toMarkAsDone);
		lists.get(ListID.GENERAL.getValue()).remove(toMarkAsDone);
		lists.get(ListID.DEADLINE.getValue()).remove(toMarkAsDone);
		lists.get(ListID.EVENT.getValue()).remove(toMarkAsDone);

		//Update UI display
		UiMain.getInstance().getController().updateDisplay(lists.get(ListID.THIS_WEEK.getValue()), ContentBox.THIS_WEEK);
		UiMain.getInstance().getController().updateDisplay(lists.get(ListID.PENDING.getValue()), ContentBox.PENDING);
		refreshUiCategoryDisplay();
		
		return 0;
	}

	//View the type of task specified by viewType.
	private int view(String viewType) {
		if (viewType.equals("GENERAL")) {
			uiController.updateActionDisplay(lists.get(ListID.GENERAL.getValue()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		} else if (viewType.equals("DEADLINES")) {
			uiController.updateActionDisplay(lists.get(ListID.DEADLINE.getValue()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		} else if (viewType.equals("EVENTS")) {
			uiController.updateActionDisplay(lists.get(ListID.EVENT.getValue()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		} else if (viewType.equals("ARCHIVE")) {
			uiController.updateActionDisplay(lists.get(ListID.COMPLETED.getValue()), ActionListMode.TASKLIST);
			uiController.displayTabContents(ContentBox.ACTION.getValue());
		}
		
		return 0; //Stub
	}
	
	//Removes an indexed task from the Ui tab specified by currentContent.
	private int deleteByIndex(ContentBox currentContent, int taskIndex, ArrayList<Task> targetList) {
		if (!currentContent.equals(ContentBox.PENDING)) { //Delete command only allowed in pending tab
			return -1; //Stub
		}
		
		if (taskIndex >= targetList.size()) { //Out of bounds
			return -1; //Stub
		}
		
		Task t = targetList.get(taskIndex);
		String taskType = t.getTaskType();
		targetList.remove(taskIndex);
		
		if (taskType.equals("FLOATING")) {
			ArrayList<Task> floatingList = lists.get(ListID.GENERAL.getValue());
			floatingList.remove(t);
		} else if (taskType.equals("DEADLINE")) {
			ArrayList<Task> deadlineList = lists.get(ListID.DEADLINE.getValue());
			deadlineList.remove(t);
			lists.get(ListID.THIS_WEEK.getValue()).remove(t);
		} else if (taskType.equals("EVENT")) {
			ArrayList<Task> eventList = lists.get(ListID.EVENT.getValue());
			eventList.remove(t);
			lists.get(ListID.THIS_WEEK.getValue()).remove(t);
		}
		
		uiController.updateDisplay(targetList, currentContent);
		uiController.updateDisplay(lists.get(ListID.THIS_WEEK.getValue()), ContentBox.THIS_WEEK);
		refreshUiCategoryDisplay();
		
		return 0; //Stub
	}
	
	//Removes an named task from the Ui tab specified by currentContent.
	private int deleteByName(ContentBox currentContent, String taskName, ArrayList<Task> targetList) {
		if (!currentContent.equals(ContentBox.PENDING)) { //Delete command only allowed in pending tab
			return -1; //Stub
		}
		
		Task t = getTaskByName(targetList, taskName);
		
		if (t == null) { //Named task does not exist in the list
			return -1; //Stub
		}
		
		String taskType = t.getTaskType();
		targetList.remove(t);
		
		if (taskType.equals("FLOATING")) {
			ArrayList<Task> floatingList = lists.get(ListID.GENERAL.getValue());
			floatingList.remove(t);
		} else if (taskType.equals("DEADLINE")) {
			ArrayList<Task> deadlineList = lists.get(ListID.DEADLINE.getValue());
			deadlineList.remove(t);
			lists.get(ListID.THIS_WEEK.getValue()).remove(t);
		} else if (taskType.equals("EVENT")) {
			ArrayList<Task> eventList = lists.get(ListID.EVENT.getValue());
			eventList.remove(t);
			lists.get(ListID.THIS_WEEK.getValue()).remove(t);
		}
		
		uiController.updateDisplay(targetList, currentContent);
		uiController.updateDisplay(lists.get(ListID.THIS_WEEK.getValue()), ContentBox.THIS_WEEK);
		refreshUiCategoryDisplay();
		
		return 0; //Stub
	}
	
	//Search for all tasks that contain searchPhrase. searchPhrase is not case sensitive.
	private int search(String searchPhrase) {
		ArrayList<Task> matches = new ArrayList<Task>();
		ArrayList<Task> pendingList = lists.get(ListID.PENDING.getValue());
		
		for (Task t : pendingList) {
			if (t.getTaskName().toLowerCase().contains(searchPhrase.toLowerCase())) {
				matches.add(t);
			}
		}
		
		uiController.updateActionDisplay(matches, ActionListMode.TASKLIST);
		uiController.displayTabContents(ContentBox.ACTION.getValue());
		
		return 0; //Stub
	}
	
	//Updates Ui with a new floating task. Returns a status code reflecting outcome of command execution.
	private int addFloating(Task task) {
		ArrayList<Task> pendingList = lists.get(ListID.PENDING.getValue());
		ArrayList<Task> floatingList = lists.get(ListID.GENERAL.getValue());
		
		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return -1; //Stub
		}
		
		pendingList.add(task);
		floatingList.add(task);
		updateUiContentDisplay(pendingList, ContentBox.PENDING);
		uiController.displayTabContents(ContentBox.PENDING.getValue()); //Automatically switch to pending tab
		refreshUiCategoryDisplay();
		
		return 0; //Stub
	}
	
	//Updates Ui with a new deadline task. Returns a status code reflecting outcome of command execution.
	private int addDeadline(Task task) {
		ArrayList<Task> pendingList = lists.get(ListID.PENDING.getValue());
		ArrayList<Task> deadlineList = lists.get(ListID.DEADLINE.getValue());
		
		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return -1; //Stub
		}
		
		pendingList.add(task);
		deadlineList.add(task);
		
		if (timeConverter.isSameWeek(task.getDeadlineEpoch(), timeConverter.getCurrTime())) {
			lists.get(ListID.THIS_WEEK.getValue()).add(task);
			updateUiContentDisplay(lists.get(ListID.THIS_WEEK.getValue()), ContentBox.THIS_WEEK);
		}
		
		updateUiContentDisplay(pendingList, ContentBox.PENDING);
		uiController.displayTabContents(ContentBox.PENDING.getValue()); //Automatically switch to pending tab
		refreshUiCategoryDisplay();
		
		return 0; //Stub
	}
	
	//Updates Ui with a new event task. Returns a status code reflecting outcome of command execution.
	private int addEvent(Task task) {
		ArrayList<Task> pendingList = lists.get(ListID.PENDING.getValue());
		ArrayList<Task> eventList = lists.get(ListID.EVENT.getValue());
		
		if (pendingList.contains(task)) { //Duplicate task name not allowed
			return -1; //Stub
		}
		
		pendingList.add(task);
		eventList.add(task);
		
		if (timeConverter.isSameWeek(task.getStartDateEpoch(), timeConverter.getCurrTime())) {
			lists.get(ListID.THIS_WEEK.getValue()).add(task);
			updateUiContentDisplay(lists.get(ListID.THIS_WEEK.getValue()), ContentBox.THIS_WEEK);
		}
		
		updateUiContentDisplay(pendingList, ContentBox.PENDING);
		uiController.displayTabContents(ContentBox.PENDING.getValue()); //Automatically switch to pending tab
		refreshUiCategoryDisplay();
		
		return 0; //Stub
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
				Storage.getInstance().saveTaskList(lists.get(i + 1), fileNames.get(i));
				System.out.println("List: " + fileNames.get(i) + " saved");
			}
		} catch (Exception e) {
			System.out.println("Error in saving tasks");
		}
	}
}