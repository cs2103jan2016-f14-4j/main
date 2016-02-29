package taskey.parser;

import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedObject; 



public class Parser {
	
	private HashMap<String,String> commandList = new HashMap<String,String>(); 
	
	private ParseError parseError = new ParseError(); 
	private ParseAdd parseAdd = new ParseAdd(); 
	private ParseEdit parseEdit = new ParseEdit(); 
	private ParseUndo parseUndo = new ParseUndo(); 
	private ParseView parseView = new ParseView(); 
	private ParseSearch parseSearch = new ParseSearch(); 
	private ParseDelete parseDelete = new ParseDelete();
	private ParseDone parseDone = new ParseDone(); 
	 
	
	public Parser() {
		commandList.put("add", "add"); 
		commandList.put("view", "view"); 
		commandList.put("del", "del"); 
		commandList.put("set", "set"); 
		commandList.put("search", "search"); 
		commandList.put("done", "done"); 
		commandList.put("undo", "undo"); 
	}
	
	/**
	 * Process the user's command and execute it accordingly 
	 * @param command: string of command keyed in by user
	 * @return message to be displayed
	 */
	public ProcessedObject parseInput(String stringInput) {
		ProcessedObject processed = null;  
		String command = getCommand(stringInput); 
		
		//don't need to check date, just get "task"
		//view, delete by index, delete by name, 
		switch(command) {
			case "view":
				processed = parseView.processView(command, stringInput); 
				break; 
			case "del":
				processed = parseDelete.processDelete(command, stringInput); 
				break;
				
			//need to check date: 
			//add floating, add deadline, add event,
			//update by index, update by name 
			case "add":
				processed = parseAdd.processAdd(command, stringInput); 
				break;
			case "set":
				processed = parseEdit.processSet(command, stringInput); 
				break;
			case "search":
				processed = parseSearch.processSearch(command, stringInput); 
				break;
			case "undo":
				processed = parseUndo.processUndo(command); 
				break; 
			case "done":
				processed = parseDone.processDone(command, stringInput);
				break; 
			default:
				//error goes here
				processed = parseError.processError(ParserConstants.ERROR_COMMAND); 
				break; 
		}
		
		return processed;   
	}
	
	
	/**
	 * Returns command (first word) from a string that the user entered 
	 * @param stringInput
	 * @return command
	 */
	public String getCommand(String stringInput) {
		String[] splitString = stringInput.split(" ");
		String command = splitString[0].toLowerCase();
		
		if (commandList.containsKey(command)) {
			return command;
		} 
		return "error"; 
	}
	
	/**
	 * FOR FLOATING TASK: 
	 * Given a stringInput, remove the command from the string
	 * @param command
	 * @param stringInput
	 * @return taskName without command
	 */
	public String getTaskName(String command, String stringInput) {
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
	}
}
