package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedObject;
import taskey.logic.Task; 



public class Parser {
	
	private HashMap<String,String> commandList = new HashMap<String,String>(); 
	
	private ParseError parseError = new ParseError(); 
	private ParseAdd parseAdd = new ParseAdd(); 
	private ParseEdit parseEdit = new ParseEdit(); 
	private ParseUndo parseUndo = new ParseUndo();  
	private ParseSearch parseSearch = new ParseSearch(); 
	private ParseDelete parseDelete = new ParseDelete();
	private ParseDone parseDone = new ParseDone(); 
	private ParseFileLocation parseDir = new ParseFileLocation(); 
	
	private UserTagDatabase tagDB = new UserTagDatabase(); 
	private ParseView parseView = new ParseView(tagDB);
	
	public Parser() {
		commandList.put("add", "add"); 
		commandList.put("view", "view"); 
		commandList.put("del", "del"); 
		commandList.put("set", "set"); 
		commandList.put("search", "search"); 
		commandList.put("done", "done"); 
		commandList.put("undo", "undo"); 
		commandList.put("file_loc", "file_loc");
	}
	
	/**
	 * Process the user's command and execute it accordingly 
	 * @param command: string of command keyed in by user
	 * @return message to be displayed
	 */
	public ProcessedObject parseInput(String stringInput) {
		ProcessedObject processed = null;  
		stringInput = stringInput.toLowerCase(); 
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
			case "file_loc":
				processed = parseDir.processLoc(command, stringInput);
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
		//update tag categories if needed
		trackTags(processed); 
		
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
	
	/**
	 * This function takes in a ProcessedObject, checks whether there are
	 * tags that can be added to the UserTagDatabase, else do nothing. 
	 * @param po
	 */
	public void trackTags(ProcessedObject po) {
		Task task = po.getTask();
		if (task != null) {
			ArrayList<String> tags = task.getTaskTags(); 
			
			if (tags != null) {
				for(int i = 0; i < tags.size(); i++) {
					if (!tagDB.hasTag(tags.get(i))) {
						tagDB.addTag(tags.get(i));
					}
				}
			}	 
		}
	}
	
	/**
	 * For Logic: you can get this database to update the 
	 * available categories to display, or even update
	 * if all tasks do not have that category. 
	 * @return
	 */
	public UserTagDatabase getUserTagDB() {
		return tagDB; 
	}
}
