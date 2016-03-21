package taskey.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ocpsoft.prettytime.nlp.PrettyTimeParser;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedObject;
import taskey.logic.Task; 

/**
 * @@author A0107345L
 * Job of this class is to parse "add" commands. 
 * @author Xue Hui
 *
 */
public class ParseAdd extends ParseCommand { 
	ArrayList<String> timeWords = new ArrayList<String>(); 
	private HashMap<String,String> keywordsList = new HashMap<String,String>(); 
	private HashMap<String,Long> specialDays = new SpecialDaysConverter().getSpecialDays();
	
	private TimeConverter timeConverter = new TimeConverter(); 
	private PrettyTimeParser prettyParser = new PrettyTimeParser();
	private DateTimePatternMatcher pm = new DateTimePatternMatcher();  
	
	public ParseAdd() {	
		super();
		
		keywordsList.put("every", "every");
		keywordsList.put("by", "by");
		keywordsList.put("on", "on");
		keywordsList.put("from", "from");
		keywordsList.put("to", "to");
		
		timeWords.add("am");
		timeWords.add("pm");
		//PrettyTime's default time for morning/night is 8am/8pm
		timeWords.add("morning"); 
		timeWords.add("night"); //can be tonight, tomorrow night, etc	
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
		String simpString = stringNoCommand(command, stringInput);
		simpString = simpString.replace("tmr", "tomorrow"); //bug fix for time handling
		
		if (isEmptyAdd(simpString)) {
			processed = super.processError(ParserConstants.ERROR_ADD_EMPTY);
			return processed;
		}
		
		if(isOnlyNumbers(simpString)) {
			processed = super.processError(ParserConstants.ERROR_ONLY_NUMS);
			return processed; 
		} 
		
		//process as floating, event, deadline, or error 
		processed = processNormally(command, processed, task, simpString);
		
		//if there's error, don't continue to process tags
		if (processed.getCommand().compareTo(ParserConstants.ERROR) == 0) {
			return processed;
		}
		
		//process tags now: if there are tags, add it in.
		if (simpString.split("#").length != 1) {
			ArrayList<String> tags = getTagList(simpString); 
			processed.getTask().setTaskTags(tags);
		}
		return processed; 
	}
	
	/**
	 * Processes the string normally as either a 
	 * floating, deadline or event task.
	 * If there is any formatting error, return error 
	 * @param command
	 * @param processed
	 * @param task
	 * @param simpString
	 * @return
	 */
	private ProcessedObject processNormally(String command, 
			ProcessedObject processed, Task task, String simpString) {
		String taskName = removeTimeFromName(simpString); 
		String rawDate = simpString.replace(taskName, "").trim();
		rawDate = rawDate.split("#")[0].trim();//remove tags
		
		//do pattern matching on raw date here. 
		if (pm.hasPattern(rawDate)) {
			processed = super.processError(String.format(
					ParserConstants.ERROR_DATE_GRAMMAR, rawDate));
			return processed; 
		}
		
		if (rawDate.contains("from")) {
			if (simpString.split("from").length != 1) {
				//event
				processed = handleEvent(task, taskName, rawDate);
			}
		} else if (rawDate.contains("by")) {
			if (simpString.split("by").length != 1) {
				//deadline 
				processed = handleDeadline(task, taskName, rawDate);
			} 
		} else if (rawDate.contains("on")) {
			if (simpString.split("on").length != 1) {
				//deadline
				processed = handleDeadline(task, taskName, rawDate);	
			}
		} else if (rawDate.contains("at")) {
			if (simpString.split("at").length != 1) {
				//handle as deadline 
				processed = handleDeadline(task, taskName, rawDate);
			}
		} else {
			//set as floating task 
			processed = handleFloating(command, simpString);
		}
		return processed;
	}
	
	/**
	 * If the task description is empty, it is considered an 
	 * empty add. So, return true to indicate error. 
	 * @param simpString
	 * @return
	 */
	private boolean isEmptyAdd(String simpString) {
		if (simpString.compareTo("") == 0) {
			return true; 
		}
		return false; 
	}
	
	/**
	 * if taskname consists entirely of numbers, return true
	 * to indicate error
	 * @param simpString
	 * @return
	 */
	private boolean isOnlyNumbers(String simpString) {
		try {
			int temp = Integer.parseInt(simpString); 
			return true; 
		} catch (Exception e) {
			return false;
		} 
	}

	/**
	 * ADD: process event 
	 * @param task
	 * @param simpString
	 * @return
	 */
	private ProcessedObject handleEvent(Task task, String taskName, String rawDate) {
		long epochTime;
		ProcessedObject processed;
	
		String simpString2 = rawDate.replace("today", "2day"); 
		simpString2 = simpString2.replace("tomorrow", "tmr"); 
		simpString2 = simpString2.replace("from", "").trim();
		String[] dateList = simpString2.split("to"); 
		String dateForPrettyParser = rawDate;
		
		String rawStartDate = dateList[0].trim().toLowerCase();
		String rawEndDate = dateList[1].trim().toLowerCase(); 
		
		//if date contains am or pm or morning or night, 
		//call pretty parser to process the time and return. 
		try {
			long[] epochTimeEvent = getPrettyTimeEvent(dateForPrettyParser);
			task.setStartDate(epochTimeEvent[0]);
			task.setEndDate(epochTimeEvent[1]);
			task.setTaskName(taskName);
			task.setTaskType("EVENT");
			processed = new ProcessedObject("ADD_EVENT",task);
			return processed;
		} catch (Error e) {
			//do nothing, continue to code below
			//ie. date format wrong or has no time in the date
		}
		
		if (!specialDays.containsKey(rawStartDate)) {
			try {
				epochTime = timeConverter.toEpochTime(rawStartDate);
				task.setStartDate(epochTime);
			} catch (ParseException error) {
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, rawStartDate)); 
				return processed; 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawStartDate);
			task.setStartDate(epochTime);
		}
		
		
		if (!specialDays.containsKey(rawEndDate)) {
			try {
				epochTime = timeConverter.toEpochTime(rawEndDate);
				task.setEndDate(epochTime); 	
			} catch (ParseException error) {
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, rawEndDate)); 
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
	 * ADD: process deadlines with the keyword "by", "on" and "at" 
	 * @param task
	 * @param simpString
	 * @return
	 */
	private ProcessedObject handleDeadline(Task task, String taskName, String rawDate) {
		long epochTime;
		ProcessedObject processed;
		String dateForPrettyParser = rawDate;
		rawDate = rawDate.replaceFirst("by", "").trim(); 
		rawDate = rawDate.replaceFirst("on", "").trim(); 
		rawDate = rawDate.replaceFirst("at", "").trim(); 
		
		//if time contains am or pm or morning or night, 
		//call pretty parser to process the time.
		epochTime = getPrettyTime(dateForPrettyParser);
		if (epochTime != -1) {
			task.setDeadline(epochTime); 
		} else if (!specialDays.containsKey(rawDate)) {
			//process standard calendar dates (eg. 17 Feb) 
			try {
				epochTime = timeConverter.toEpochTime(rawDate); 
				task.setDeadline(epochTime);
			} catch (ParseException error) {
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, rawDate)); 
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
		String taskName = simpString.split("#")[0].trim();
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
	public String stringNoCommand(String command, String stringInput) {
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
	}
	
	/**
	 * If the rawDate contains a time field, use PrettyTimeParser to
	 * parse the date
	 * @param rawDate
	 * @return epochTime (long) of rawDate
	 */
	public long getPrettyTime(String rawDate) {
		for(int i = 0; i < timeWords.size(); i++) {
			if (rawDate.contains(timeWords.get(i))) {
				//if the date contains any of the time words, call prettyParser
				List<Date> processedTime = prettyParser.parse(rawDate); 
				if (!processedTime.isEmpty()) { 
					return processedTime.get(0).getTime() / 1000; 
				} else {
					return -1; 
				}
			}
		}
		return -1; //no time indicated, or time is in the wrong format
	}
	
	/**
	 * If the rawDate contains time field for an event, 
	 * use PrettyTimeParser to parse the date
	 * @param rawDate
	 * @return epochTime (long array) of rawDate (FOR EVENTS) 
	 */
	public long[] getPrettyTimeEvent(String rawDate) throws Error {
		for(int i = 0; i < timeWords.size(); i++) {
			if (rawDate.contains(timeWords.get(i))) {
				//if the date contains any of the time words, call prettyParser
				List<Date> processedTime = prettyParser.parse(rawDate); 
				if (processedTime.size() >= 2) {
					long[] epochTimes = {processedTime.get(0).getTime() / 1000,
							processedTime.get(1).getTime() / 1000}; 
					
					return epochTimes; 
				} else {
					throw new Error(); 
				}
			}
		}
		throw new Error(); //no time indicated, or time is in the wrong format
	}
	
	/**
	 * Get tag list for a task
	 * Assumptions: all tags must be added to the back of the userInput
	 * @param rawInput
	 * @return
	 */
	public ArrayList<String> getTagList(String rawInput) {
		ArrayList<String> tagList = new ArrayList<String>();
		String[] splitString = rawInput.split("#");
		for(int i = 1; i < splitString.length; i++) {
			tagList.add(splitString[i].trim());
		}
		return tagList; 
	}
	
	/**
	 * Depending on where the user keyed in his dates, the task name
	 * might still contain the time in it. So this function will remove
	 * the time from the task name (if it is there) 
	 * @param taskName
	 * @return
	 */
	public String removeTimeFromName(String taskName) {
		String keyword = "(at|from|on|by)";
		String combinedTime = "\\d{1,2}(:|.)?\\d{0,2}(am|pm)";
		String timeSpecifier = "(am|pm)";
		String timePattern = "\\d{1,2}(:|.)?\\d{0,2}"; //regex
		String specialDaysKeywords = "(this|next)";
		String stringRep = ""; 
		String[] splitName = taskName.split(" "); 
		int size = splitName.length; 
		
		for(int i = 0; i < size; ) {
			String word = splitName[i]; 
			if (word.matches(keyword)) {
				if (i+1 < size) {
					String time = splitName[i+1];
					if (time.matches(combinedTime) || specialDays.containsKey(time)) {
						//eg. 3pm or today/tomorrow/fri
						i += 2; 
						break;
					} else if (time.matches(timePattern) || time.matches(specialDaysKeywords)) {
						//eg. only a number or contains "this/next" 
						if (i+2 < size) {
							String time2 = splitName[i+2];
							String time3 = time + " " + time2; 
							if (time2.matches(timeSpecifier) || time2.length() == 3) {
								//eg. 3 pm or a month like feb (assume v. few words len(3) after num) 
								i += 2;
								break;								
							} else if (specialDays.containsKey(time3)) {
								//eg. this fri or next fri
								i += 2;
								break; 							
							} else {
								//probably a place and not time
								stringRep += word + " " + time + " " + time2 + " ";
								i += 3; 
								continue; 
							}
						}
					} else {
						//probably a place and not time,
						//so add it to the task name 
						stringRep += word + " " + time + " ";
						i += 2; 
						continue; 
					}
				}
			} else {
				stringRep += word + " "; 
				i += 1; 
			}
		}	
		return stringRep.trim(); 
	}
}
