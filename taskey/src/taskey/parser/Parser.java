package taskey.parser;

import taskey.constants.ParserConstants;
import taskey.messenger.ProcessedObject; 


/**
 * @@author A0107345L
 * Logic can only call this class parse inputs from the user.
 * This Parser is a high level interface that acts as a facade
 * between the other parts of Parser. Commands parsed here make
 * use of the Command pattern as well. 
 * @author Xue Hui
 *
 */
public class Parser {	
	private ParseError parseError = new ParseError(); 
	private ParseAdd parseAdd = new ParseAdd(); 
	private ParseEdit parseEdit = new ParseEdit(); 
	private ParseUndo parseUndo = new ParseUndo();  
	private ParseSearch parseSearch = new ParseSearch(); 
	private ParseDelete parseDelete = new ParseDelete();
	private ParseDone parseDone = new ParseDone(); 
	private ParseFileLocation parseDir = new ParseFileLocation(); 
	private ParseSave parseSave = new ParseSave(); 
	private ParseClear parseClear = new ParseClear(); 
	
	private ParseView parseView = new ParseView();
	
	/**
	 * Constructor 
	 */
	public Parser() {
	
	}
	
	/**
	 * Process the user's command and execute it accordingly 
	 * @param command: string of command keyed in by user
	 * @return message to be displayed
	 */
	public ProcessedObject parseInput(String stringInput) {
		ProcessedObject processed = null;  
		//stringInput = stringInput.toLowerCase(); 
		String command = getCommand(stringInput); 
		
		//don't need to check date, just get "task"
		//view, delete by index, delete by name, save, clear
		switch(command) {
			case "view":
				processed = parseView.processView(stringInput); 
				break; 
			case "del":
				processed = parseDelete.processDelete(stringInput); 
				break;
			case "setdir":
				processed = parseDir.processLoc(stringInput);
				break; 	
			case "save":
				processed = parseSave.processSave(command); 
				break;
			case "clear":
				processed = parseClear.processClear(command);
				break; 
				
			//need to check date: 
			//add floating, add deadline, add event,
			//update by index, update by name ,etc 
			case "add":
				processed = parseAdd.processAdd(command, stringInput); 
				break;
			case "set":
				processed = parseEdit.processSet(stringInput); 
				break;
			case "search":
				processed = parseSearch.processSearch(command, stringInput); 
				break;
			case "undo":
				processed = parseUndo.processUndo(command); 
				break; 
			case "done":
				processed = parseDone.processDone(stringInput);
				break; 
			default:
				//error goes here
				processed = parseError.processError(String.format(
						ParserConstants.ERROR_COMMAND, command)); 
				break; 
		}
		
		return processed;   
	}
	
	
	/**
	 * Returns command (first word) from a string that the user entered 
	 * @param stringInput
	 * @return command
	 */
	private String getCommand(String stringInput) {
		String[] splitString = stringInput.split(" ");
		String command = splitString[0].toLowerCase();
		
		return command;
	}
}
