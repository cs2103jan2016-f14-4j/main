package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.logic.ProcessedObject;
import taskey.logic.Task;



public class Parser {
	public static final String DELETE_BY_INDEX = "DELETE_BY_INDEX"; 
	public static final String DELETE_BY_NAME = "DELETE_BY_NAME"; 
	public static final String DONE_BY_INDEX = "DONE_BY_INDEX"; 
	public static final String DONE_BY_NAME = "DONE_BY_NAME"; 
	public static final String DAY_END = "23:59:59"; 
	public static final String ERROR_DATE_FORMAT = "Wrong date format"; 
	public static final String ERROR_VIEW_TYPE = "No such category"; 
	public static final String ERROR_COMMAND = "No such command"; 
	
	private HashMap<String,String> commandList = new HashMap<String,String>(); 
	private HashMap<String,String> viewList = new HashMap<String,String>();
	private HashMap<String,String> keywordsList = new HashMap<String,String>(); 
	private HashMap<String,Long> specialDays = new HashMap<String,Long>();
	
	private TimeConverter timeConverter = new TimeConverter(); 
	 
	
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
		
		specialDays.put("tomorrow", 
				timeConverter.getCurrTime() + TimeConverter.ONE_DAY); 
		specialDays.put("today", timeConverter.getCurrTime()); 
		specialDays.put("next sun", new Long(1)); 
		specialDays.put("next mon", new Long(1)); 
		specialDays.put("next tues", new Long(1)); 
		specialDays.put("next wed", new Long(1)); 
		specialDays.put("next thurs", new Long(1)); 
		specialDays.put("next fri", new Long(1)); 
		specialDays.put("next sat", new Long(1)); 
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
			
			case "search":
				break;
			
			case "undo":
				break; 
				
			case "done":
				processed = processDone(command, stringInput);
				break; 
				
			default:
				//error goes here
				processed = processError(ERROR_COMMAND); 
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
		return processError(ERROR_VIEW_TYPE); 
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
	
	/**
	 * If command is done, check if the done is by 
	 * 1. NAME, or
	 * 2. INDEX 
	 * and return the appropriate ProcessedObject
	 * @param command
	 * @param stringInput
	 * @return appropriate ProcessedObject 
	 */
	public ProcessedObject processDone(String command, String stringInput) {
		ProcessedObject processed; 
		String taskName = getTaskName(command, stringInput);
		
		try {
			int index = Integer.parseInt(taskName);
			processed = new ProcessedObject(DONE_BY_INDEX, index); 
			
		} catch (Exception e) {
			//if the delete is not by index, then it's by task name. 
			processed = new ProcessedObject(DONE_BY_NAME, new Task(taskName));
		}
		
		return processed; 
	}
	
	public ProcessedObject processAdd(String command, String stringInput) {
		long epochTime; 
		ProcessedObject processed = null;
		Task task = new Task(); 
		String taskName = null; 
		//simpString: basically string without the command
		String simpString = getTaskName(command, stringInput); 
		
		if (simpString.split("on").length != 1) {
			//deadline
			processed = handleDeadlineOn(task, simpString);
			
		} else if (simpString.split("by").length != 1) {
			//deadline 
			processed = handleDeadlineBy(task, simpString);
			
		} else if (simpString.split("from").length != 1) {
			//event
			processed = handleEvent(task, simpString);
			
		} else {
			//floating task 
			processed = handleFloating(command, simpString);
		}
		
		return processed; 
	}

	private ProcessedObject handleEvent(Task task, String simpString) {
		long epochTime;
		ProcessedObject processed;
		String taskName;
		String[] inputList = simpString.split("from");
		String[] dateList = inputList[1].split("to"); 
		taskName = inputList[0]; 
		String rawStartDate = dateList[0].trim();
		String rawEndDate = dateList[1].trim(); 
		
		if (!specialDays.containsKey(rawStartDate)) {
			if (rawStartDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(rawStartDate + DAY_END);
				task.setStartDate(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawStartDate);
			task.setStartDate(epochTime);
		}
		
		if (!specialDays.containsKey(rawEndDate)) {
			if (rawEndDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(rawEndDate + DAY_END);
				task.setEndDate(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawEndDate);
			task.setEndDate(epochTime);
		}
		
		task.setTaskName(taskName);
		task.setTaskType("EVENT");
		processed = new ProcessedObject("ADD_EVENT",task);
		return processed;
	}

	private ProcessedObject handleDeadlineBy(Task task, String simpString) {
		long epochTime;
		ProcessedObject processed;
		String taskName;
		String[] inputList = simpString.split("by");
		taskName = inputList[0].trim(); 
		String rawDate = inputList[1].trim(); 
		
		if (!specialDays.containsKey(rawDate)) {
			if (rawDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(rawDate + DAY_END);
				task.setDeadline(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawDate);
			task.setDeadline(epochTime);
		}
		
		task.setTaskName(taskName);
		task.setTaskType("DEADLINE");
		processed = new ProcessedObject("ADD_DEADLINE",task);
		return processed;
	}

	private ProcessedObject handleDeadlineOn(Task task, String simpString) {
		long epochTime;
		ProcessedObject processed;
		String taskName;
		String[] inputList = simpString.split("on"); 
		taskName = inputList[0].trim(); 
		String rawDate = inputList[1].trim();
		
		if (!specialDays.containsKey(rawDate)) {
			if (rawDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(rawDate + DAY_END);
				task.setDeadline(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawDate);
			task.setDeadline(epochTime);
		}
		
		task.setTaskName(taskName);
		task.setTaskType("DEADLINE");
		processed = new ProcessedObject("ADD_DEADLINE",task);
		return processed;
	}

	private ProcessedObject handleFloating(String command, String simpString) {
		ProcessedObject processed;
		String taskName;
		taskName = simpString;
		Task newTask = new Task(taskName); 
		
		newTask.setTaskType("FLOATING");
		processed = new ProcessedObject(command,newTask);
		
		return processed;
	}
	
	public void processSet(String command, String stringInput) {
		
	}
	
	public ProcessedObject processError(String errorType) {
		ProcessedObject processed = new ProcessedObject("ERROR");
		processed.setErrorType(errorType); 
		
		return processed;
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
