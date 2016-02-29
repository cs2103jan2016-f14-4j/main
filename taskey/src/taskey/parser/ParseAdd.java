package taskey.parser;

import taskey.logic.ProcessedObject;
import taskey.logic.Task;

import java.util.HashMap;

import taskey.constants.ParserConstants; 

/**
 * Job of this class is to parse "add" commands. 
 * @author Xue Hui
 *
 */
public class ParseAdd { 
	private HashMap<String,String> keywordsList = new HashMap<String,String>(); 
	private HashMap<String,Long> specialDays = new HashMap<String,Long>();
	
	private TimeConverter timeConverter = new TimeConverter(); 
	
	private ParseError parseError = new ParseError(); 
	
	public ParseAdd() {		
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
				epochTime = timeConverter.toEpochTime(rawStartDate + " " + ParserConstants.DAY_END);
				task.setStartDate(epochTime);
			} else if (rawStartDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawStartDate + " " + String.valueOf(year) 
						+ " " + ParserConstants.DAY_END);
				task.setStartDate(epochTime); 
			}else {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
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
				epochTime = timeConverter.toEpochTime(rawEndDate + " " + ParserConstants.DAY_END);
				task.setEndDate(epochTime);
			} else if (rawEndDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawEndDate + " " + String.valueOf(year) 
						+ " " + ParserConstants.DAY_END);
				task.setEndDate(epochTime); 	
			} else {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
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
				epochTime = timeConverter.toEpochTime(rawDate + " " + ParserConstants.DAY_END);
				task.setDeadline(epochTime);
			} else if (rawDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawDate + " " + String.valueOf(year) 
						+ " " + ParserConstants.DAY_END);
				task.setDeadline(epochTime);
			}else {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
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
				epochTime = timeConverter.toEpochTime(rawDate + " " + ParserConstants.DAY_END);
				task.setDeadline(epochTime);
			} else if (rawDate.length() == 6) {
				//ie. format is DD MMM
				timeConverter.setCurrTime();
				int year = timeConverter.getYear(timeConverter.getCurrTime());
				epochTime = timeConverter.toEpochTime(rawDate + " " + String.valueOf(year) 
						+ " " + ParserConstants.DAY_END);
				task.setDeadline(epochTime);
			} else {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
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
