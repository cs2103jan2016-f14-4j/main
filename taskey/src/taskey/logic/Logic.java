package taskey.logic;

import taskey.parser.AutoComplete;
import taskey.parser.Parser;
import taskey.logic.Task;

import java.util.ArrayList;

import taskey.constants.UiConstants.ContentBox;
import taskey.logic.ProcessedObject;

import static taskey.constants.ParserConstants.DISPLAY_COMMAND;
import static taskey.constants.ParserConstants.FINISHED_COMMAND;
import static taskey.constants.ParserConstants.NO_SUCH_COMMAND;

/**
 * @@author A0134177E
 * The Logic class provides a simplified interface to the Logic component and hides the more complex aspects, 
 * such as memory management and the internal details of the execution of each specific command. As a result, from the 
 * perspective of UI, all UI needs to do is call the executeCommand() method of Logic whenever the user enters a 
 * command.
 */
public class Logic {
	
    //================================================================================
    // Fields
    //================================================================================
	
	private Parser parser;
	private History history;
	private CommandExecutor cmdExecutor;
	private LogicMemory logicMemory;
	
    //================================================================================
    // Constructors
    //================================================================================
	
	public Logic() {
		parser = new Parser();
		history = new History();
		cmdExecutor = new CommandExecutor();
		logicMemory = new LogicMemory();
		updateHistory();
	}
	
    //================================================================================
    // Accessors
    //================================================================================
	
	/**
	 * Returns a deep copy of all task lists.
	 */
	public ArrayList<ArrayList<Task>> getAllTaskLists() {
		return ListCloner.cloneTaskLists(logicMemory.getTaskLists());
	}
	
	/**
	 * Returns a deep copy of the current tag category list.
	 */
	public ArrayList<TagCategory> getTagCategoryList() {
		return ListCloner.cloneTagCategoryList(logicMemory.getTagCategoryList());
	}
	
    //================================================================================
    // Interface Methods
    //================================================================================
	
	/**
	 * Executes the user supplied command.
	 *
	 * @param currentContent specifies the current tab that user is in.
	 * @param input			 the input String entered by the user
	 * @return               an object encapsulating the information required to update UI display
	 */
	public LogicFeedback executeCommand(ContentBox currentContent, String input) {
    	ProcessedObject po = parser.parseInput(input);
    	String command = po.getCommand();
    	Command cmd;
    	
    	if (input.equalsIgnoreCase("clear")) { // "clear" command is for developer testing only
    		cmd = new Clear();
			return executeClear(cmd);
    	}
    	
    	switch (command) {
			case "ADD_FLOATING":
				cmd = new AddFloating(po.getTask());
				return executeAdd(po, cmd);
				
			case "ADD_DEADLINE":
				cmd = new AddDeadline(po.getTask());
				return executeAdd(po, cmd);

			case "ADD_EVENT":
				cmd = new AddEvent(po.getTask());
				return executeAdd(po, cmd);

			case "DELETE_BY_INDEX":
				cmd = new DeleteByIndex(currentContent, po.getIndex());
				return executeDelete(po, cmd);
						
			case "DELETE_BY_CATEGORY":
				cmd = new DeleteByTagName(po.getCategory());
				return executeDelete(po, cmd);
			
			case "DONE_BY_INDEX":
				cmd = new DoneByIndex(currentContent, po.getIndex());
				return executeDone(po, cmd);			
				
			case "UPDATE_BY_INDEX_CHANGE_NAME":
				cmd = new UpdateByIndexChangeName(currentContent, po.getIndex(), po.getNewTaskName());
				return executeUpdate(po, cmd);
				
			case "UPDATE_BY_INDEX_CHANGE_DATE":
				cmd = new UpdateByIndexChangeDate(currentContent, po.getIndex(), po.getTask());
				return executeUpdate(po, cmd);
			
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				cmd = new UpdateByIndexChangeBoth(currentContent, po.getIndex(), po.getNewTaskName(), po.getTask());
				return executeUpdate(po, cmd);
			
			case "VIEW_BASIC":
				cmd = new ViewBasic(po.getViewType().get(0));
				return executeView(po, cmd);
			
			case "VIEW_TAGS":
				cmd = new ViewTags(po.getViewType());
				return executeView(po, cmd);

			case "UNDO":
				return executeUndo(po);
				
			case "ERROR":
				return new LogicFeedback(getAllTaskLists(), po, new LogicException(po.getErrorType()));
				
			case "SEARCH":
				cmd = new Search(po.getSearchPhrase());
				return executeSearch(po, cmd);

			default:
				break;
		}

		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_ERROR_COMMAND_EXECUTION));
	}
	
    //================================================================================
    // Command Methods
    //================================================================================

	private LogicFeedback executeClear(Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), new ProcessedObject("CLEAR"), le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), new ProcessedObject("CLEAR"), 
				                 new LogicException(LogicException.MSG_SUCCESS_CLEAR));
	}
	
	private LogicFeedback executeAdd(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_ADD));
	}
	
	private LogicFeedback executeDelete(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_DELETE));
	}
	
	private LogicFeedback executeDone(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_DONE));
	}
	
	private LogicFeedback executeUpdate(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_UPDATE));
	}
	
	private LogicFeedback executeView(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		return new LogicFeedback(getAllTaskLists(), po, null);
	}
	
	private LogicFeedback executeSearch(ProcessedObject po, Command cmd) {
		return executeView(po, cmd);
	}
	
	private LogicFeedback executeUndo(ProcessedObject po) {
		// History stacks must always have at least one item, which is inserted at startup
		assert(!history.listStackIsEmpty());
		assert(!history.tagStackIsEmpty()); 
		ArrayList<ArrayList<Task>> currentTaskLists = history.pop();
		ArrayList<ArrayList<Task>> previousTaskLists = history.peek();
		ArrayList<TagCategory> currentTagCategoryList = history.popTags();
		ArrayList<TagCategory> previousTagCategoryList = history.peekTags();
		
		if (previousTaskLists == null) {
			history.add(currentTaskLists);
			history.addTagList(currentTagCategoryList);
			return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_ERROR_UNDO));
		}
		
		logicMemory.setTaskLists(ListCloner.cloneTaskLists(previousTaskLists));
		logicMemory.setTagCategoryList(ListCloner.cloneTagCategoryList(previousTagCategoryList));
		
		return new LogicFeedback(getAllTaskLists(), po, null);
	}
	
    //================================================================================
    // Miscellaneous
    //================================================================================
	
	// Push the latest task lists and tag category list to history.
	private void updateHistory() {
		history.add(getAllTaskLists());
		history.addTagList(getTagCategoryList());
	}
	
	/*
	// Save all task lists to Storage. If the save failed, the task lists will be reverted to the states
	// they were in before they were modified.
	private void saveAllTasks(ArrayList<ArrayList<Task>> taskLists) throws LogicException {
		try {
			storage.saveAllTasklists(taskLists);
		} catch (StorageException se) {
			throw new LogicException (se.getMessage());
		}
	}*/
	
	
	public ArrayList<String> autoCompleteLine(String line, ContentBox currentContent) {
		AutoComplete auto = new AutoComplete();
		ProcessedAC pac = auto.completeCommand(line); 
		String pacCommand = pac.getCommand(); 
		
		if ( pacCommand.compareTo(DISPLAY_COMMAND) == 0) { // to complete a command
			ArrayList<String> suggestions = pac.getAvailCommands(); 
			return suggestions;
		} else if (pacCommand.compareTo(FINISHED_COMMAND) == 0) {
			return null;
		} else if (pacCommand.compareTo(NO_SUCH_COMMAND) == 0) {
			return null;  
		} else { // valid command
			ProcessedObject po = parser.parseInput(line);
			switch ( po.getCommand() ) {
			case "ADD_FLOATING":
			case "ADD_DEADLINE":
			case "ADD_EVENT":
				return new ArrayList<String>(); // valid
			case "DELETE_BY_INDEX":
			case "DELETE_BY_NAME":
			case "VIEW":
			case "SEARCH":
			case "DONE_BY_INDEX":
			case "DONE_BY_NAME":
			case "UPDATE_BY_INDEX_CHANGE_NAME":
			case "UPDATE_BY_INDEX_CHANGE_DATE":
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
			case "UPDATE_BY_NAME_CHANGE_NAME":
			case "UPDATE_BY_NAME_CHANGE_DATE":
			case "UPDATE_BY_NAME_CHANGE_BOTH":

			case "UNDO":
				return new ArrayList<String>();
				
			case "ERROR":
				return null;
			default:
				return null;
			}
		}
	}
}