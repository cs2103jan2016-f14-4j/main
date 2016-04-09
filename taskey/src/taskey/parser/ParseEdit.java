package taskey.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import taskey.constants.ParserConstants;
import taskey.messenger.ProcessedObject;
import taskey.messenger.Task; 

/**
 * @@author A0107345L 
 * Purpose is to parse the "set" command. 
 * @author Xue Hui
 *
 */
public class ParseEdit extends ParseCommand {
	private DateTimePatternMatcher timeChecker = new DateTimePatternMatcher(); 
	private HashMap<String,String> keywordsList = new HashMap<String,String>(); 
	private HashMap<String,Long> specialDays = new SpecialDaysConverter().getSpecialDays();
	
	private TimeConverter timeConverter = new TimeConverter(); 
	private PrettyTimeParser prettyParser = new PrettyTimeParser();
	
	public ParseEdit() {
		super(); 
		keywordsList.put("every", "every");
		keywordsList.put("by", "by");
		keywordsList.put("on", "on");
		keywordsList.put("from", "from");
		keywordsList.put("to", "to");
	}
	
	/**
	 * If command is SET, categorise into:
	 * 1. UPDATE_BY_INDEX_CHANGE_NAME
	 * 2. UPDATE_BY_INDEX_CHANGE_DATE
	 * 3. UPDATE_BY_INDEX_CHANGE_BOTH
	 * 4. UPDATE_BY_INDEX_CHANGE_PRIORITY
	 * 5. UPDATE_BY_NAME_CHANGE_NAME
	 * 6. UPDATE_BY_NAME_CHANGE_DATE
	 * 7. UPDATE_BY_NAME_CHANGE_BOTH
	 * 8. UPDATE_BY_NAME_CHANGE_PRIORITY 
	 * @param command
	 * @param stringInput
	 * @return appropriate ProcessedObject
	 */
	protected ProcessedObject processSet(String stringInput) {
		String strNoCommand = removeCommand(stringInput);
		
		if (stringInput.split(" ").length > 1) {
			String rawIndex = getTaskName(strNoCommand);
			String newTaskName = getNewName(strNoCommand);
			String newDate = getNewDate(strNoCommand); 
			int newPriority = getNewPriority(strNoCommand); 
			
			if (rawIndex.compareTo("") == 0) {
				return super.processError(ParserConstants.ERROR_NAME_EMPTY);
			}
			
			if (newPriority == -1) {
				return super.processError(ParserConstants.ERROR_SET_NEW_PRIORITY); 
			}
			
			try {
				int index = Integer.parseInt(rawIndex);	
				return updateByIndex(index-1, newTaskName, newDate, newPriority); 
			} catch (Exception e) {
				//if the update is not by index, then it's by task name. 
				//here rawIndex == old Task Name 
				return updateByName(rawIndex, newTaskName, newDate, newPriority); 
			}
		}
		//empty input, return error 
		return super.processError(ParserConstants.ERROR_INPUT_EMPTY);
	}
	
	/**
	 * Called by processSet(). Updates based on index the user has keyed in 
	 * @param index
	 * @param newTaskName
	 * @param newDateRaw
	 * @param newPriority
	 * @return ProcessedObject
	 */
	private ProcessedObject updateByIndex(int index, String newTaskName, String newDateRaw,
			int newPriority) {
		ProcessedObject processed = null; 
		
		//changing priority only 
		if (newPriority != 0) { 
			return updatePriorityByIndex(index, newPriority); 
		}
		
		//changing name only
		if (newTaskName != null && newDateRaw == null) {
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_INDEX_CHANGE_NAME, index); 
			return updateChangeName(processed, newTaskName); 
		} else if (newDateRaw != null && newTaskName == null) {
			//changing date only 
			newDateRaw = newDateRaw.replace("tmr", "tomorrow"); //prettytime cannot accept tmr
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_INDEX_CHANGE_DATE, index);
			return updateDate(newDateRaw, processed);
		} else if (newDateRaw != null && newTaskName != null) {
			//change both name and date 
			newDateRaw = newDateRaw.replace("tmr", "tomorrow"); 
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_INDEX_CHANGE_BOTH, index);
			processed = updateChangeName(processed, newTaskName); 
			return updateDate(newDateRaw, processed);
		} else {
			//error: wrong format
			processed = super.processError(ParserConstants.ERROR_STRING_FORMAT);
			return processed; 
		}
	}

	/**
	 * Update a date of a task by its index
	 * @param newDateRaw
	 * @param processed
	 * @return ProcessedObject
	 */
	private ProcessedObject updateDate(String newDateRaw, ProcessedObject processed) {
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
	}

	/**
	 * Change the priority of a task
	 * @param index
	 * @param newPriority
	 * @return ProcessedObject
	 */
	private ProcessedObject updatePriorityByIndex(int index, int newPriority) {
		ProcessedObject processed;
		processed = new ProcessedObject(ParserConstants.UPDATE_BY_INDEX_CHANGE_PRIORITY,
				index); 
		processed.setNewPriority(newPriority);
		return processed;
	}
	
	/**
	 * Called by processSet(). Updates based on task name that user has keyed in 
	 * @param oldTaskName
	 * @param newTaskName
	 * @param newDateRaw
	 * @param newPriority
	 * @return ProcessedObject
	 */
	private ProcessedObject updateByName(String oldTaskName, String newTaskName,
			String newDateRaw, int newPriority) {
		ProcessedObject processed = null; 
		
		//changing priority only 
		if (newPriority != 0) { 
			return updatePriorityByName(oldTaskName, newPriority); 
		}
		
		//if changing name only
		if (newTaskName != null && newDateRaw == null) {
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_NAME_CHANGE_NAME, 
					new Task(oldTaskName)); 
			return updateChangeName(processed, newTaskName); 
		} else if (newDateRaw != null && newTaskName == null) {
			//changing date only
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_NAME_CHANGE_DATE, 
					new Task(oldTaskName)); 
			
			return updateDate(newDateRaw, processed);
		} else if (newTaskName != null && newDateRaw != null) {
			//changing both name and date 
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_NAME_CHANGE_BOTH,
					new Task(oldTaskName));
			processed = updateChangeName(processed, newTaskName); 
			return updateDate(newDateRaw, processed);
		} else {
			//error: wrong format
			processed = super.processError(ParserConstants.ERROR_STRING_FORMAT);
			return processed; 
		}
	}

	/**
	 * Update priority of a task by its task name
	 * @param oldTaskName
	 * @param newPriority
	 * @return ProcessedObject
	 */
	private ProcessedObject updatePriorityByName(String oldTaskName, int newPriority) {
		ProcessedObject processed;
		processed = new ProcessedObject(ParserConstants.UPDATE_BY_NAME_CHANGE_PRIORITY,
						new Task(oldTaskName)); 
		processed.setNewPriority(newPriority);
		return processed;
	}
	
	/**
	 * Given a task name, we want to change that task's name 
	 * @param processed
	 * @param taskName
	 * @return ProcessedObject
	 */
	private ProcessedObject updateChangeName(ProcessedObject processed,	String newTaskName) {
		processed.setNewTaskName(newTaskName);
		return processed; 
	}
	
	
	/**
	 * Given a task we want to we want to change the task type to event. 
	 * @param processed
	 * @param newDateRaw
	 * @return ProcessedObject
	 */
	private ProcessedObject updateToEvent(ProcessedObject processed, String newDateRaw) {
		long epochTime; 
		long startTime = -1;
		long endTime = -1; 
		String[] dateList = newDateRaw.split(","); 
		String startDate = dateList[0].trim().toLowerCase();
		String endDate = dateList[1].trim().toLowerCase();
		Task changedTask = null; 
		
		if (processed.getCommand().compareTo(ParserConstants.UPDATE_BY_NAME_CHANGE_BOTH) == 0) {
			changedTask = processed.getTask(); 
		} else if (processed.getCommand().compareTo(ParserConstants.UPDATE_BY_NAME_CHANGE_DATE) == 0) {
			changedTask = processed.getTask();
		} else {
			changedTask = new Task(); 
		}
		changedTask.setTaskType("EVENT");
		//if time contains am or pm or morning or night, 
		//call pretty parser to process the time.
		epochTime = getPrettyTime(startDate);
		if (epochTime != -1) {
			startTime = epochTime; 
			changedTask.setStartDate(epochTime); 
		} else if (!specialDays.containsKey(startDate)) {
			try {
				epochTime = timeConverter.toEpochTime(startDate);
				startTime = epochTime; 
				changedTask.setStartDate(epochTime);
			} catch (ParseException error) {
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, startDate)); 
				return processed; 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(startDate);
			startTime = epochTime; 
			changedTask.setStartDate(epochTime);
		}
		
		epochTime = getPrettyTime(endDate);
		if (epochTime != -1) {
			endTime = epochTime; 
			changedTask.setEndDate(epochTime); 
		} else if (!specialDays.containsKey(endDate)) {
			try {
				epochTime = timeConverter.toEpochTime(endDate);
				endTime = epochTime; 
				changedTask.setEndDate(epochTime);
			} catch (ParseException error) {
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, endDate)); 
				return processed; 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(endDate);
			endTime = epochTime; 
			changedTask.setEndDate(epochTime);
		}
		//check to make sure valid event time 
		if (!isValidEvent(startTime, endTime)) {
			return super.processError(ParserConstants.ERROR_EVENT_TIME_INVALID);
		}
		
		processed.setTask(changedTask);
		return processed;
	}
	
	/**
	 * Checks if an event time is valid (ie, start time < end time) 
	 * @param eventStartTime
	 * @param eventEndTime
	 * @return true if start time < end time 
	 */
	private boolean isValidEvent(long eventStartTime, long eventEndTime) {
		if (eventStartTime == -1 || eventEndTime == -1) {
			return false; 
		}
		
		if (eventStartTime >= eventEndTime) {
			return false; 
		}
		return true; 
	}
	
	/**
	 * Given a task we want to update the task type to deadline
	 * @param processed
	 * @param newDateRaw
	 * @return ProcessedObject
	 */
	private ProcessedObject updateToDeadline(ProcessedObject processed, String newDateRaw) {
		long epochTime; 
		Task changedTask = null; 
		
		if (processed.getCommand().compareTo(ParserConstants.UPDATE_BY_NAME_CHANGE_BOTH) == 0) {
			changedTask = processed.getTask(); 
		} else if (processed.getCommand().compareTo(ParserConstants.UPDATE_BY_NAME_CHANGE_DATE) == 0) {
			changedTask = processed.getTask();
		} else {
			changedTask = new Task(); 
		}
		changedTask.setTaskType("DEADLINE");
		
		epochTime = getPrettyTime(newDateRaw);
		if (epochTime != -1) {
			changedTask.setDeadline(epochTime); 
		} else if (!specialDays.containsKey(newDateRaw.toLowerCase())) {
			try {
				epochTime = timeConverter.toEpochTime(newDateRaw.toLowerCase());
				changedTask.setDeadline(epochTime);
			} catch (ParseException error){
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, newDateRaw.toLowerCase())); 
				return processed; 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(newDateRaw);
			changedTask.setDeadline(epochTime);
		}
		
		processed.setTask(changedTask);
		return processed; 
	}
	
	/**
	 * Given a task we want to update task type to floating
	 * @param processed
	 * @return ProcessedObject
	 */
	private ProcessedObject updateToFloating(ProcessedObject processed) {
		Task changedTask = null; 
		
		if (processed.getCommand().compareTo(ParserConstants.UPDATE_BY_NAME_CHANGE_BOTH) == 0) {
			changedTask = processed.getTask(); 
		} else if (processed.getCommand().compareTo(ParserConstants.UPDATE_BY_NAME_CHANGE_DATE) == 0) {
			changedTask = processed.getTask();
		} else {
			changedTask = new Task(); 
		}
		changedTask.setTaskType("FLOATING");
		processed.setTask(changedTask);
		return processed;
	}
	
	/**
	 * Given a stringInput, remove the command from the string
	 * @param command
	 * @param stringInput
	 * @return taskName without command
	 */
	private String removeCommand(String stringInput) {
		String command = stringInput.split(" ")[0]; 
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
	}
	
	/**
	 * Remove new dates and new task name from the stringInput
	 * ie, just retrieve either the old task name or index of the old task
	 * @param stringInput
	 * @return
	 */
	private String getTaskName(String stringInput) {
		int strLen = stringInput.length(); 
		boolean canAdd = true;
		String taskName = ""; 
		
		for(int i = 0; i < strLen; i++) {
			char k = stringInput.charAt(i); 
			if (k == '"') {
				//specifier for change task name
				canAdd = false;
				break; 
			} else if (k == '[') {
				//specifier for change date
				canAdd = false;
				break;
			} else if (k == '!') {
				//specifier for change priority
				canAdd = false;
				break; 
			} else if (canAdd == true) {
				taskName += k; 
			}
		}
		return taskName.trim(); 
	}
	
	/**
	 * Get new priority for the task
	 * @param stringInput
	 * @return priority level 1,2,3 or -1 if error
	 */
	private int getNewPriority(String stringInput) {
		int strLen = stringInput.length();
		int count = 0; 
		
		for(int i = 0; i < strLen; i++) {
			char k = stringInput.charAt(i);
			//specifier for change priority
			if (k == '!') {
				count += 1; 
			} 
		}
		if (count <= 3) {
			return count; 
		} 
		return -1; 	
	}
	
	/**
	 * If stringInput has a new date entered, look for the []
	 * and get the dates 
	 * @param stringInput
	 * @return newDate if there is any, else return null 
	 */
	private String getNewDate(String stringInput) {
		int strLen = stringInput.length(); 
		boolean canAdd = false; 
		String date = ""; 
		
		for(int i = 0; i < strLen; i++) {
			char k = stringInput.charAt(i); 
			if (k == '[') {
				canAdd = true; 
			} else if (k == ']') {
				canAdd = false;
				break;
			} else if (canAdd == true) {
				date += k; 
			}
		}	
		
		if (date.compareTo("") == 0) {
			return null; 
		}
		return date.trim().toLowerCase(); 
	}
	
	/**
	 * Given a raw input string, look for the new name of
	 * the task if there is any
	 * @param stringInput
	 * @return new task name if there is, else return null. 
	 */
	private String getNewName(String stringInput) {
		String newName = null;
		String[] splitString = stringInput.split("\""); 
		
		if (splitString.length > 1) {
			newName = splitString[1].trim(); 
		}
		if (newName != null) { 
			if (newName.compareTo("") == 0) {
				return null; 
			}
		}
		return newName; 
	}
	
	/**
	 * If the rawDate contains a time field, use PrettyTimeParser to
	 * parse the date
	 * @param rawDate
	 * @return epochTime (long) of rawDate
	 */
	private long getPrettyTime(String rawDate) {
		//use regex to check for time format
		if (timeChecker.hasTimeEdit(rawDate)) {
			//if the date contains any of the time words, call PrettyParser
			List<Date> processedTime = prettyParser.parse(rawDate); 
			if (!processedTime.isEmpty()) {
				return processedTime.get(0).getTime() / 1000; 
			} else {
				return -1; //unable to process 
			}
		}
		return -1; //no time indicated, or time is in the wrong format
	}

}
