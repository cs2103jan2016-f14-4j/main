package taskey.logic;

import static taskey.constants.ParserConstants.DISPLAY_COMMAND;
import static taskey.constants.ParserConstants.FINISHED_COMMAND;
import static taskey.constants.ParserConstants.NO_SUCH_COMMAND;

import java.util.ArrayList;

import taskey.constants.UiConstants.ContentBox;
import taskey.messenger.ProcessedAC;
import taskey.messenger.ProcessedObject;
import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.parser.AutoComplete;
import taskey.parser.Parser;

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
	private AutoComplete autoComplete;
	
    //================================================================================
    // Constructors
    //================================================================================
	
	public Logic() {
		parser = new Parser();
		history = new History();
		cmdExecutor = new CommandExecutor();
		logicMemory = new LogicMemory();
		autoComplete = new AutoComplete();
		updateHistory();
	}
	
    //================================================================================
    // Accessors
    //================================================================================
	
	/**
	 * Returns a (sorted) deep copy of all task lists.
	 */
	public ArrayList<ArrayList<Task>> getAllTaskLists() {
		return ListCloner.cloneTaskLists(logicMemory.getTaskLists());
	}
	
	/**
	 * Returns a (sorted) deep copy of the current tag category list.
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
				
			case "CHANGE_FILE_LOC":
				cmd = new ChangeSaveDirectory(po.getNewFileLoc());
				return executeChangeSaveDirectory(po, cmd);
				
			case "CLEAR":
	    		cmd = new Clear();
				return executeClear(cmd);
				
			case "DELETE_BY_CATEGORY":
				cmd = new DeleteByTagName(po.getCategory());
				return executeDelete(po, cmd);

			case "DELETE_BY_INDEX":
				cmd = new DeleteByIndex(currentContent, po.getIndex());
				return executeDelete(po, cmd);
			
			case "DONE_BY_INDEX":
				cmd = new DoneByIndex(currentContent, po.getIndex());
				return executeDone(po, cmd);	
				
			case "ERROR":
				return new LogicFeedback(getAllTaskLists(), po, new LogicException(po.getErrorType()));
				
			case "SAVE":
	    		cmd = new Save();
				return executeSave(cmd);
				
			case "SEARCH":
				cmd = new Search(po.getSearchPhrase());
				return executeSearch(po, cmd);
				
			case "UNDO":
				return executeUndo(po);
				
			case "UPDATE_BY_INDEX_CHANGE_BOTH":
				cmd = new UpdateByIndexChangeBoth(currentContent, po.getIndex(), po.getNewTaskName(), po.getTask());
				return executeUpdate(po, cmd);
				
			case "UPDATE_BY_INDEX_CHANGE_DATE":
				cmd = new UpdateByIndexChangeDate(currentContent, po.getIndex(), po.getTask());
				return executeUpdate(po, cmd);
				
			case "UPDATE_BY_INDEX_CHANGE_NAME":
				cmd = new UpdateByIndexChangeName(currentContent, po.getIndex(), po.getNewTaskName());
				return executeUpdate(po, cmd);		
				
			case "UPDATE_BY_INDEX_CHANGE_PRIORITY":
				cmd = new UpdateByIndexChangePriority(currentContent, po.getIndex(), po.getNewPriority());
				return executeUpdate(po, cmd);

			case "VIEW_BASIC":
				cmd = new ViewBasic(po.getViewType().get(0));
				return executeView(po, cmd);
			
			case "VIEW_TAGS":
				cmd = new ViewTags(po.getViewType());
				return executeView(po, cmd);

			default:
				return new LogicFeedback(getAllTaskLists(), po, 
						                 new LogicException(LogicException.MSG_ERROR_INVALID_COMMAND));
		}
	}
	
    //================================================================================
    // Command Methods
    //================================================================================
	
	private LogicFeedback executeAdd(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		updateHistory();
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_ADD));
	}
	
	private LogicFeedback executeChangeSaveDirectory(ProcessedObject po, Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), po, le);
		}
		return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_SUCCESS_CHANGE_DIR));
	}
	
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
	
	private LogicFeedback executeSave(Command cmd) {
		try {
			cmdExecutor.execute(cmd, logicMemory);
		} catch (LogicException le) {
			return new LogicFeedback(getAllTaskLists(), new ProcessedObject("SAVE"), le);
		}
		return new LogicFeedback(getAllTaskLists(), new ProcessedObject("SAVE"), 
				                 new LogicException(LogicException.MSG_SUCCESS_SAVE));
	}
	
	private LogicFeedback executeSearch(ProcessedObject po, Command cmd) {
		return executeView(po, cmd);
	}
	
	private LogicFeedback executeUndo(ProcessedObject po) {
		// History stacks must always have at least one item, which is inserted at startup
		assert(!history.taskStackIsEmpty());
		assert(!history.tagStackIsEmpty()); 
		ArrayList<ArrayList<Task>> currentTaskLists = history.popTaskStack();
		ArrayList<ArrayList<Task>> previousTaskLists = history.peekTaskStack();
		ArrayList<TagCategory> currentTagCategoryList = history.popTagStack();
		ArrayList<TagCategory> previousTagCategoryList = history.peekTagStack();
		
		if (previousTaskLists == null) {
			history.addTaskLists(currentTaskLists);
			history.addTagCategoryList(currentTagCategoryList);
			return new LogicFeedback(getAllTaskLists(), po, new LogicException(LogicException.MSG_ERROR_UNDO));
		}
		
		logicMemory.setTaskLists(ListCloner.cloneTaskLists(previousTaskLists));
		logicMemory.setTagCategoryList(ListCloner.cloneTagCategoryList(previousTagCategoryList));
		
		return new LogicFeedback(getAllTaskLists(), po, null);
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
	
    //================================================================================
    // Miscellaneous
    //================================================================================
	
	// Push the latest task lists and tag category list to history.
	private void updateHistory() {
		history.addTaskLists(getAllTaskLists());
		history.addTagCategoryList(getTagCategoryList());
	}
	
	public ArrayList<String> autoCompleteLine(String line, ContentBox currentContent) {
		/*AutoComplete auto = new AutoComplete();
		ProcessedAC pac = auto.completeCommand(line); 
		String pacCommand = pac.getCommand(); 
		
		if ( pacCommand.compareTo(DISPLAY_COMMAND) == 0) { // to complete a command
			ArrayList<String> suggestions = pac.getAvailCommands(); 
			return suggestions;
		} else if (pacCommand.compareTo(FINISHED_COMMAND) == 0) {
			return new ArrayList<String>();
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
		}*/
		ProcessedAC pac = autoComplete.getSuggestions(line, getTagCategoryList());
		String pacCommand = pac.getCommand();
		
		if (pacCommand.equals(DISPLAY_COMMAND)) { // to complete a command
			ArrayList<String> suggestions = pac.getAvailCommands(); 
			return suggestions;
		} else if (pacCommand.equals(FINISHED_COMMAND)) {
			return new ArrayList<String>();
		} else { // Command not recognized
			return null;  
		}
	}
}