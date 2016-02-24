package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.ProcessedObject;

public class Parser {
	
	private HashMap<String,String> commandList = new HashMap<String,String>(); 
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
		//String task; 
		String command = getCommand(stringInput); 
		
		//don't need to check date, just get "task"
		//view, delete by index, delete by name, 
		switch(command) {
			case "view":
				processView(); 
				break; 
			case "delete":
				// task = getTaskName(command, stringInput);
				processDelete(); 
				break;
				
				
			//need to check date: 
			//add floating, add deadline, add event,
			//update by index, update by name 
			case "add":
				processAdd(); 
				break;
				
			case "set":
				processSet(); 
				break;
				
			default:
				//error goes here 
				break; 
		}

 
		//ArrayList<String> dateRange = getDate(stringInput); 
		//task = getTaskName(command, stringInput); 
		
		return new ProcessedObject();   
	}
	
	
	public String getCommand(String stringInput) {
		String[] splitString = stringInput.split(" ");

		return splitString[0].toLowerCase(); 
	}
	
	public void processView() {
		
	}
	
	public void processDelete() {
		
	}
	
	public void processAdd() {
		
	}
	
	public void processSet() {
		
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
