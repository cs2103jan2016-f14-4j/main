package taskey.parser;

import java.text.ParseException;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedObject;
import taskey.logic.Task; 

/**
 * Purpose is to parse the "set" command. 
 * @author Xue Hui
 *
 */
public class ParseEdit {
	private HashMap<String,String> keywordsList = new HashMap<String,String>(); 
	private HashMap<String,Long> specialDays = new SpecialDaysConverter().getSpecialDays();
	
	private TimeConverter timeConverter = new TimeConverter(); 
	
	private ParseError parseError = new ParseError(); 
	
	public ParseEdit() {
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
	 * 3. UPDATE_BY_NAME_CHANGE_NAME
	 * 4. UPDATE_BY_NAME_CHANGE_DATE 
	 * @param command
	 * @param stringInput
	 * @return appropriate ProcessedObject
	 */
	public ProcessedObject processSet(String command, String stringInput) {
		String taskName = getTaskName(command, stringInput).trim();
		
		if (stringInput.split(" ").length > 1) {
			String rawIndex = stringInput.split(" ")[1];
			
			try {
				int index = Integer.parseInt(rawIndex);
				
				return updateByIndex(taskName, index); 
			} catch (Exception e) {
				//if the update is not by index, then it's by task name. 
				return updateByName(taskName); 
			}
		}
		return parseError.processError("invalid input");
	}
	
	/**
	 * Called by processSet(). Updates based on index the user has keyed in 
	 * @param processed
	 * @param taskName
	 * @param index
	 * @return
	 */
	private ProcessedObject updateByIndex(String taskName, int index) {
		ProcessedObject processed = null; 
		
		//if changing name, check for " " 
		if (taskName.split("\"").length != 1) {
			return updateChangeName(processed, taskName, index); 
		} else if (taskName.split("\\[").length != 1) {
			//if changing date, check for < >
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_INDEX_CHANGE_DATE, index);
			taskName = taskName.replace("]", ""); 
			String[] taskParts = taskName.split("\\["); 
			
			if (taskParts.length == 1) {
				processed = parseError.processError("invalid input");
				return processed; 
			}
			
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
			processed = parseError.processError("Wrong format for changing task name/date");
			return processed; 
		}
	}
	
	/**
	 * Called by processSet(). Updates based on task name that user has keyed in 
	 * @param processed
	 * @param taskName
	 * @return
	 */
	private ProcessedObject updateByName(String taskName) {
		ProcessedObject processed = null; 
		
		//if changing name, check for " " 
		if (taskName.split("\"").length != 1) {
			return updateChangeName(processed, taskName); 
		} else if (taskName.split("\\[").length != 1) {
			//if changing date, check for < >
			processed = new ProcessedObject(ParserConstants.UPDATE_BY_NAME_CHANGE_DATE, new Task(taskName)); 
			taskName = taskName.replace("]", ""); 
			String[] taskParts = taskName.split("\\["); 
			String oldTaskName = taskParts[0].trim(); 
			
			if (taskParts.length == 1) {
				processed = parseError.processError("invalid input");
				return processed; 
			}
			
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
			processed = parseError.processError("Wrong format for changing task name/date");
			return processed; 
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
		processed = new ProcessedObject(ParserConstants.UPDATE_BY_NAME_CHANGE_NAME, new Task(oldName)); 
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
		processed = new ProcessedObject(ParserConstants.UPDATE_BY_INDEX_CHANGE_NAME); 
		processed.setNewTaskName(newName);
		processed.setIndex(index);
		
		return processed; 
	}
	
	/**
	 * Given a task index, we want to we want to change the task type to event. 
	 * @param processed
	 * @param newDateRaw
	 * @return
	 */
	private ProcessedObject updateToEvent(ProcessedObject processed, String newDateRaw) {
		long epochTime; 
		String[] dateList = newDateRaw.split(","); 
		String startDate = dateList[0].trim().toLowerCase();
		String endDate = dateList[1].trim().toLowerCase(); 
		Task changedTask = new Task(); 
		changedTask.setTaskType("EVENT");
		
		if (!specialDays.containsKey(startDate)) {
			try {
				epochTime = timeConverter.toEpochTime(startDate);
				changedTask.setStartDate(epochTime);
			} catch (ParseException error) {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		
		if (!specialDays.containsKey(endDate)) {
			try {
				epochTime = timeConverter.toEpochTime(endDate);
				changedTask.setEndDate(epochTime);
			} catch (ParseException error) {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed;
	}
	
	/**
	 * Given a task name, we want to change the task type to event 
	 * @param processed
	 * @param newDateRaw
	 * @param newTaskName
	 * @return
	 */
	private ProcessedObject updateToEvent(ProcessedObject processed, String newDateRaw,
			String newTaskName) {
		long epochTime; 
		String[] dateList = newDateRaw.split(","); 
		String startDate = dateList[0].trim().toLowerCase();
		String endDate = dateList[1].trim().toLowerCase(); 
		Task changedTask = new Task(newTaskName); 
		changedTask.setTaskType("EVENT");
		
		if (!specialDays.containsKey(startDate)) {
			try {
				epochTime = timeConverter.toEpochTime(startDate);
				changedTask.setStartDate(epochTime);
			} catch (ParseException error) {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		
		if (!specialDays.containsKey(endDate)) {
			try {
				epochTime = timeConverter.toEpochTime(endDate);
				changedTask.setEndDate(epochTime);
			} catch (ParseException error) {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed;
	}
	
	/**
	 * Given a task index, we want to update the task type to deadline
	 * @param processed
	 * @param newDateRaw
	 * @return
	 */
	private ProcessedObject updateToDeadline(ProcessedObject processed, String newDateRaw) {
		long epochTime; 
		Task changedTask = new Task(); 
		changedTask.setTaskType("DEADLINE");
		
		if (!specialDays.containsKey(newDateRaw.toLowerCase())) {
			try {
				epochTime = timeConverter.toEpochTime(newDateRaw.toLowerCase());
				changedTask.setDeadline(epochTime);
			} catch (ParseException error){
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed; 
	}
	
	/**
	 * Give a task name, we want to update the task type to deadline
	 * @param processed
	 * @param newDateRaw
	 * @param newTaskName
	 * @return
	 */
	private ProcessedObject updateToDeadline(ProcessedObject processed, String newDateRaw,
			String newTaskName) {
		long epochTime; 
		Task changedTask = new Task(newTaskName); 
		changedTask.setTaskType("DEADLINE");
		
		if (!specialDays.containsKey(newDateRaw.toLowerCase())) {
			try {
				epochTime = timeConverter.toEpochTime(newDateRaw.toLowerCase());
				changedTask.setDeadline(epochTime);
			} catch (ParseException error) {
				processed = parseError.processError(ParserConstants.ERROR_DATE_FORMAT); 
				return processed; 
			}
		}
		processed.setTask(changedTask);
		return processed; 
	}
	
	/**
	 * Given a task index, we want to update task type to floating
	 * @param processed
	 * @return
	 */
	private ProcessedObject updateToFloating(ProcessedObject processed) {
		Task changedTask = new Task();
		changedTask.setTaskType("FLOATING");
		processed.setTask(changedTask);
		return processed;
	}
	
	/**
	 * Given a task name, we want to update task type to floating 
	 * @param processed
	 * @param newTaskName
	 * @return
	 */
	private ProcessedObject updateToFloating(ProcessedObject processed,
			String newTaskName) {
		Task changedTask = new Task(newTaskName);
		changedTask.setTaskType("FLOATING");
		processed.setTask(changedTask);
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
