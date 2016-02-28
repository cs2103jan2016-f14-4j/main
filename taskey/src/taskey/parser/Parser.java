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
	public static final String UPDATE_BY_INDEX_CHANGE_NAME = "UPDATE_BY_INDEX_CHANGE_NAME"; 
	public static final String UPDATE_BY_INDEX_CHANGE_DATE = "UPDATE_BY_INDEX_CHANGE_DATE"; 
	public static final String UPDATE_BY_NAME_CHANGE_NAME = "UPDATE_BY_NAME_CHANGE_NAME";
	public static final String UPDATE_BY_NAME_CHANGE_DATE = "UPDATE_BY_NAME_CHANGE_DATE";
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
		
		//TODO: put in correct times for special days. 
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
			case "del":
				processed = processDelete(command, stringInput); 
				break;
				
			//need to check date: 
			//add floating, add deadline, add event,
			//update by index, update by name 
			case "add":
				processed = processAdd(command, stringInput); 
				break;
				
			case "set":
				processSet(command, stringInput); 
				break;
			
			case "search":
				processed = processSearch(command, stringInput); 
				break;
			
			case "undo":
				processed = processUndo(command); 
				break; 
				
			case "done":
				processed = processDone(command, stringInput);
				break; 
				
			default:
				//error goes here
				processed = processError(ERROR_COMMAND); 
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
	 * Return ProcessedObject for Undo 
	 * @param command
	 * @return
	 */
	public ProcessedObject processUndo(String command) {
		return new ProcessedObject(command.toUpperCase()); 
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
			return new ProcessedObject("VIEW",viewType.toUpperCase());
		}
		return processError(ERROR_VIEW_TYPE); 
	}
	
	/**
	 * Return ProcessedObject for Search 
	 * @param command
	 * @param stringInput
	 * @return
	 */
	public ProcessedObject processSearch(String command, String stringInput) {
		ProcessedObject processed = new ProcessedObject(command.toUpperCase()); 
		String searchPhrase = getTaskName(command, stringInput);
		processed.setSearchPhrase(searchPhrase);
		
		return processed; 
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
			//if the done is not by index, then it's by task name. 
			processed = new ProcessedObject(DONE_BY_NAME, new Task(taskName));
		}
		
		return processed; 
	}
	
	/**
	 * If command is ADD, process and categorise into:
	 * 1. FLOATING
	 * 2. EVENT
	 * 3. DEADLINE 
	 * @param command
	 * @param stringInput
	 * @return appropriate ProcessedObject 
	 */
	public ProcessedObject processAdd(String command, String stringInput) {
		ProcessedObject processed = null;
		Task task = new Task(); 
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

	/**
	 * ADD: process event 
	 * @param task
	 * @param simpString
	 * @return
	 */
	private ProcessedObject handleEvent(Task task, String simpString) {
		long epochTime;
		ProcessedObject processed;
		String taskName;
		String[] inputList = simpString.split("from");
		String[] dateList = inputList[1].split("to"); 
		taskName = inputList[0].trim(); 
		String rawStartDate = dateList[0].trim();
		String rawEndDate = dateList[1].trim(); 
		
		if (!specialDays.containsKey(rawStartDate)) {
			if (rawStartDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(rawStartDate + " " + DAY_END);
				task.setStartDate(epochTime);
			} else if (rawStartDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawStartDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				task.setStartDate(epochTime); 
			}else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawStartDate);
			task.setStartDate(epochTime);
		}
		
		if (!specialDays.containsKey(rawEndDate)) {
			if (rawEndDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(rawEndDate + " " + DAY_END);
				task.setEndDate(epochTime);
			} else if (rawEndDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawEndDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				task.setEndDate(epochTime); 	
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
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
	
	/**
	 * ADD: process deadlines with the keyword "by"
	 * @param task
	 * @param simpString
	 * @return
	 */
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
				epochTime = timeConverter.toEpochTime(rawDate + " " + DAY_END);
				task.setDeadline(epochTime);
			} else if (rawDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				task.setDeadline(epochTime);
			}else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
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

	/**
	 * Add: Process deadlines with the keyword "on"
	 * @param task
	 * @param simpString
	 * @return
	 */
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
				epochTime = timeConverter.toEpochTime(rawDate + " " + DAY_END);
				task.setDeadline(epochTime);
			} else if (rawDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				task.setDeadline(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
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
	
	/**
	 * ADD: Process floating event 
	 * @param command
	 * @param simpString
	 * @return
	 */
	private ProcessedObject handleFloating(String command, String simpString) {
		ProcessedObject processed;
		String taskName;
		taskName = simpString;
		Task newTask = new Task(taskName); 
		
		newTask.setTaskType("FLOATING");
		processed = new ProcessedObject("ADD_FLOATING",newTask);
		
		return processed;
	}
	
	//TODO 
	public ProcessedObject processSet(String command, String stringInput) {
		ProcessedObject processed = null; 
		String taskName = getTaskName(command, stringInput).trim();
		long epochTime; 
		
		try {
			int index = Integer.parseInt(taskName);
			
			//if changing name, check for " " 
			if (taskName.split("\"").length != 1) {
				return updateChangeName(processed, taskName, index); 
			} else if (taskName.split("<").length != 1) {
				//if changing date, check for < >
				processed = new ProcessedObject(UPDATE_BY_INDEX_CHANGE_DATE, index); 
				taskName = taskName.replace(">", ""); 
				String[] taskParts = taskName.split("<"); 
				String newDateRaw = taskParts[1]; 
				
				if (newDateRaw.toLowerCase().compareTo("none") == 0) {
					//change the task to floating
					return updateToFloating(processed); 
				} else if (newDateRaw.split(",").length == 2) {
					//change the task to event
					return updateToEvent(processed, newDateRaw); 
				} else {
					// change the task to deadline
					return updateToDeadline(processed, newDateRaw);
				}
			} else {
				processed = processError("Wrong format for changing task name/date");
				return processed; 
			}
			
		} catch (Exception e) {
			//if the update is not by index, then it's by task name. 
			
			//if changing name, check for " " 
			if (taskName.split("\"").length != 1) {
				return updateChangeName(processed, taskName); 
			} else if (taskName.split("<").length != 1) {
				//if changing date, check for < >
				processed = new ProcessedObject(UPDATE_BY_NAME_CHANGE_DATE, new Task(taskName)); 
				taskName = taskName.replace(">", ""); 
				String[] taskParts = taskName.split("<"); 
				String oldTaskName = taskParts[1].trim(); 
				String newDateRaw = taskParts[1]; 
				
				if (newDateRaw.toLowerCase().compareTo("none") == 0) {
					//change the task to floating
					return updateToFloating(processed, oldTaskName); 
				} else if (newDateRaw.split(",").length == 2) {
					//change the task to event
					return updateToEvent(processed, newDateRaw, oldTaskName); 
				} else {
					// change the task to deadline
					return updateToDeadline(processed, newDateRaw, oldTaskName);
				}
			} else {
				processed = processError("Wrong format for changing task name/date");
				return processed; 
			}
		}
	}
	
	/**
	 * Given a task name, we want to change that task's name 
	 * @param processed
	 * @param taskName
	 * @return
	 */
	private ProcessedObject updateChangeName(ProcessedObject processed, String taskName) {
		String[] taskParts = taskName.split("\"");
		String oldName = taskParts[0].trim();  
		String newName = taskParts[1].trim(); 
		processed = new ProcessedObject(UPDATE_BY_NAME_CHANGE_NAME, new Task(oldName)); 
		processed.setNewTaskName(newName);
		
		return processed; 
	}
	
	/**
	 * Given task index, we want to change a task name. 
	 * @param processed
	 * @param taskName
	 * @param index
	 * @return
	 */
	private ProcessedObject updateChangeName(ProcessedObject processed, String taskName, 
			int index) {
		String[] taskParts = taskName.split("\"");
		String newName = taskParts[1].trim(); 
		processed = new ProcessedObject(UPDATE_BY_INDEX_CHANGE_NAME); 
		processed.setNewTaskName(newName);
		processed.setIndex(index);
		
		return processed; 
	}
	
	private ProcessedObject updateToEvent(ProcessedObject processed, String newDateRaw) {
		long epochTime; 
		String[] dateList = newDateRaw.split(","); 
		String startDate = dateList[0];
		String endDate = dateList[1]; 
		Task changedTask = new Task(); 
		changedTask.setTaskType("EVENT");
		
		if (!specialDays.containsKey(startDate)) {
			if (startDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(startDate + " " + DAY_END);
				changedTask.setStartDate(epochTime);
			} else if (startDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(startDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				changedTask.setStartDate(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		
		if (!specialDays.containsKey(endDate)) {
			if (endDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(endDate + " " + DAY_END);
				changedTask.setEndDate(epochTime);
			} else if (endDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(endDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				changedTask.setEndDate(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed;
	}
	
	private ProcessedObject updateToEvent(ProcessedObject processed, String newDateRaw,
			String newTaskName) {
		long epochTime; 
		String[] dateList = newDateRaw.split(","); 
		String startDate = dateList[0];
		String endDate = dateList[1]; 
		Task changedTask = new Task(newTaskName); 
		changedTask.setTaskType("EVENT");
		
		if (!specialDays.containsKey(startDate)) {
			if (startDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(startDate + " " + DAY_END);
				changedTask.setStartDate(epochTime);
			} else if (startDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(startDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				changedTask.setStartDate(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		
		if (!specialDays.containsKey(endDate)) {
			if (endDate.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(endDate + " " + DAY_END);
				changedTask.setEndDate(epochTime);
			} else if (endDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(endDate + " " + String.valueOf(year) 
						+ " " + DAY_END);
				changedTask.setEndDate(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed;
	}
	
	private ProcessedObject updateToDeadline(ProcessedObject processed, String newDateRaw) {
		long epochTime; 
		Task changedTask = new Task(); 
		changedTask.setTaskType("DEADLINE");
		
		if (!specialDays.containsKey(newDateRaw)) {
			if (newDateRaw.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(newDateRaw + " " + DAY_END);
				changedTask.setDeadline(epochTime);
			} else if (newDateRaw.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(newDateRaw + " " + String.valueOf(year) 
						+ " " + DAY_END);
				changedTask.setDeadline(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed; 
	}
	
	private ProcessedObject updateToDeadline(ProcessedObject processed, String newDateRaw,
			String newTaskName) {
		long epochTime; 
		Task changedTask = new Task(newTaskName); 
		changedTask.setTaskType("DEADLINE");
		
		if (!specialDays.containsKey(newDateRaw)) {
			if (newDateRaw.length() == 11) {
				//ie. format is DD MMM YYYY
				epochTime = timeConverter.toEpochTime(newDateRaw + " " + DAY_END);
				changedTask.setDeadline(epochTime);
			} else if (newDateRaw.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(newDateRaw + " " + String.valueOf(year) 
						+ " " + DAY_END);
				changedTask.setDeadline(epochTime);
			} else {
				processed = processError(ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed; 
	}

	private ProcessedObject updateToFloating(ProcessedObject processed) {
		Task changedTask = new Task();
		changedTask.setTaskType("FLOATING");
		processed.setTask(changedTask);
		return processed;
	}
	
	private ProcessedObject updateToFloating(ProcessedObject processed,
			String newTaskName) {
		Task changedTask = new Task(newTaskName);
		changedTask.setTaskType("FLOATING");
		processed.setTask(changedTask);
		return processed;
	}
	
	/**
	 * Process Errors for string formatting/commands/etc... 
	 * @param errorType
	 * @return
	 */
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
		stringInput = stringInput.toLowerCase(); 
		String viewType = stringInput.replaceFirst(command, "");
		viewType = viewType.toLowerCase();
		viewType = viewType.trim(); 
		
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
}
