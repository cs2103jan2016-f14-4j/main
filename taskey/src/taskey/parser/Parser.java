package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.ProcessedObject;
import taskey.logic.Task;



public class Parser {
	public static final String DELETE_BY_INDEX = "DELETE_BY_INDEX"; 
	public static final String DELETE_BY_NAME = "DELETE_BY_NAME"; 
	
	private HashMap<String,String> commandList = new HashMap<String,String>(); 
	private HashMap<String,String> viewList = new HashMap<String,String>();
	private HashMap<String,String> keywordsList = new HashMap<String,String>(); 
	private HashMap<String,String> specialDays = new HashMap<String,String>();
	 
	
	public Parser() {
		commandList.put("add", "add"); 
		commandList.put("view", "view"); 
		commandList.put("del", "del"); 
		commandList.put("set", "set"); 
		commandList.put("search", "search"); 
		commandList.put("done", "done"); 
		commandList.put("undo", "undo"); 
		
		viewList.put("all", "all"); 
		viewList.put("general", "general");
		viewList.put("deadlines", "deadlines");
		viewList.put("events", "events"); 
		
		keywordsList.put("every", "every");
		keywordsList.put("by", "by");
		keywordsList.put("on", "on");
		keywordsList.put("from", "from");
		keywordsList.put("to", "to");
		
		specialDays.put("tomorrow", "tomorrow"); 
		specialDays.put("today", "today"); 
		specialDays.put("next week", "next week"); 
		specialDays.put("tonight", "tonight"); 
		specialDays.put("this weekend", "this weekend"); 
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
				processed = processView(command, stringInput); 
				break; 
			case "delete":
				processed = processDelete(command, stringInput); 
				break;
				
			//need to check date: 
			//add floating, add deadline, add event,
			//update by index, update by name 
			case "add":
				processAdd(command, stringInput); 
				break;
				
			case "set":
				processSet(command, stringInput); 
				break;
				
			default:
				//error goes here
				processed = processError(); 
				break; 
		}

 
		//ArrayList<String> dateRange = getDate(stringInput); 
		//task = getTaskName(command, stringInput); 
		
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
	 * If the command is view, process what kind of view it is:
	 * 1. ALL
	 * 2. GENERAL
	 * 3. DEADLINES
	 * 4. EVENTS 
	 * @param command
	 * @param stringInput
	 * @return processedStuff
	 */
	public ProcessedObject processView(String command, String stringInput) {
		String viewType = getViewType(command, stringInput);
		
		if (viewType.compareTo("error") != 0) {
			return new ProcessedObject(command,viewType.toUpperCase());
		}
		return processError(); 
	}
	
	/**
	 * If command is delete, check if the deletion is by 
	 * 1. NAME, or
	 * 2. INDEX 
	 * and return the appropriate ProcessedObject
	 * @param command
	 * @param stringInput
	 * @return appropriate ProcessedObject 
	 */
	public ProcessedObject processDelete(String command, String stringInput) {
		ProcessedObject processed; 
		String taskName = getTaskName(command, stringInput);
		
		try {
			int index = Integer.parseInt(taskName);
			processed = new ProcessedObject(DELETE_BY_INDEX, index); 
			
		} catch (Exception e) {
			//if the delete is not by index, then it's by task name. 
			processed = new ProcessedObject(DELETE_BY_NAME, new Task(taskName));
		}
		
		return processed; 
	}
	
	public void processAdd(String command, String stringInput) {
		
	}
	
	public void processSet(String command, String stringInput) {
		
	}
	
	public ProcessedObject processError() {
		return new ProcessedObject("ERROR");
	}
	
	/**
	 * Get viewType all, general, events or deadlines, or returns error 
	 * @param command
	 * @param stringInput
	 * @return string view type 
	 */
	public String getViewType(String command, String stringInput) {
		String viewType = stringInput.replaceFirst(command, "").toLowerCase();
		
		if (viewList.containsKey(viewType)) {
			return viewType; 
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
	 * FOR TASK WITH DATES:
	 * Given a stringInput, remove the command and date from the string
	 * @param command
	 * @param date
	 * @param stringInput
	 * @return taskName without command and date 
	 */
	public String getTaskName(String command, String date, String stringInput) {
		String task = stringInput.replaceFirst(command, "");
		task = task.replaceFirst(date, ""); 
		//TODO: work on the logic: may not be entirely correct. 
		return task.trim(); 
	}
	
	//TODO: figure out when to remove keywords from commands 
	private String joinStringWithoutKeywords(ArrayList<String> dates) {
		 
		return ""; 
	}
	
	//TODO: Dummy for now  
	public ArrayList<String> getDate(String stringInput) {
		String[] splitArray = stringInput.split("on");
		return new ArrayList<String>(); 
	}
	
	
	public static void main(String[] args) {
		//String myString = getTaskName("add","add taskey to github");
		//System.out.println(myString);
	}
}
