package taskey.logic;

import taskey.parser.Parser;
import taskey.storage.Storage;
import taskey.ui.UiMain;
import taskey.ui.UiController;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ContentBox;
import taskey.logic.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import taskey.logic.LogicConstants.ListsID;
import taskey.logic.ProcessedObject;

/**
 * TODO: class description
 * 
 * @author Hubert Wong
 */


public class Logic {
	
	private static final ArrayList<String> fileNames = new ArrayList<String>(Arrays.asList("PENDING","COMPLETED","EXPIRED"));
	private static Parser myParser;
	private static Logic instance = null;
	
	private ArrayList<ArrayList<Task>> myLists = new ArrayList<ArrayList<Task>>();
	public static Logic getInstance() {
		if ( instance == null ) {
			instance = new Logic();
		}
		return instance;
	}

	public void initialize() {
		myParser = new Parser();
		for ( int i = 0; i < fileNames.size(); i++ ) {
			ArrayList<Task> theList = Storage.getInstance().getTaskList(fileNames.get(i));
			myLists.add(theList);
			//System.out.println(theList.size());
			UiMain.getInstance().getController().updateDisplay(theList, ContentBox.fromInteger(i+1));
		}
		myLists.add(new ArrayList<Task>()); // for action list exclusively
	}
	
	public ArrayList<Task> getListFromContentBox( ContentBox currentContent ) {
		ArrayList<Task> targetList = null;
		switch ( currentContent ) {
			case PENDING:
				targetList = myLists.get(ListsID.PENDING.getValue());
				break;
			case EXPIRED:
				targetList = myLists.get(ListsID.EXPIRED.getValue());
				break;
			case COMPLETED:
				targetList = myLists.get(ListsID.COMPLETED.getValue());
				break;
			case ACTION:
				targetList = myLists.get(ListsID.ACTION.getValue());
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
    	ProcessedObject po = myParser.parseInput(input);
    	
    	// important objects
    	String command = po.getCommand();
    	Task task = po.getTask();	
    	
    	ArrayList<Task> targetList = getListFromContentBox(currentContent);
    	System.out.println("Command: " + command);
    	switch (command) {
			case "ADD_FLOATING":
			case "ADD_DEADLINE":
			case "ADD_EVENT":
				targetList.add(task);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
			case "DELETE_BY_INDEX":
				int taskIndex = po.getIndex(); //Only used for commands that specify the index of a task
				targetList.remove(taskIndex);
				UiMain.getInstance().getController().updateDisplay(targetList, currentContent);
				break;
			case "DELETE_BY_NAME":
				targetList.remove(getTaskByName(targetList, task.getTaskName()));
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
		try{
			for ( int i = 0; i < fileNames.size(); i ++ ) {
				Storage.getInstance().saveTaskList(myLists.get(i), fileNames.get(i));
				System.out.println("List: " + fileNames.get(i) + " saved");
			}
		} catch ( Exception e ) {
			System.out.println("Error calling storage to save tasks");
		}
	}
}