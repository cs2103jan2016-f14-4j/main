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
 * Job of this class is to parse "add" commands. 
 * @author Xue Hui
 *
 */
public class ParseAdd extends ParseCommand { 
	private DateTimePatternMatcher timeChecker = new DateTimePatternMatcher(); 
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
	protected ProcessedObject processAdd(String command, String stringInput) {
		String onlyPriorityPattern = "(!|!!|!!!|!!!!|!!!!!|!!!!!!)"; 
		ProcessedObject processed = null;
		Task task = new Task(); 
		//simpString: basically string without the command
		String simpString = stringNoCommand(stringInput);
		simpString = simpString.replace("tmr", "tomorrow"); //bug fix for time handling
		String simpString2 = simpString.split("#")[0].trim(); 
		
		if (isEmptyAdd(simpString2)) {
			processed = super.processError(ParserConstants.ERROR_ADD_EMPTY);
			return processed;
		}
		
		if(isOnlyNumbers(simpString2)) {
			processed = super.processError(ParserConstants.ERROR_ONLY_NUMS);
			return processed; 
		} 
		
		if (simpString2.matches(onlyPriorityPattern)) {
			processed = super.processError(ParserConstants.ERROR_ADD_EMPTY);
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
			if (!isValidTagList(tags)) {
				return super.processError(ParserConstants.ERROR_ADD_INVALID_TAG); 
			}
			processed.getTask().setTaskTags(tags);
		}
		return processed; 
	}
	
	/**
	 * Check that the taglist does not contain default category words (eg. event)
	 * or that the categories repeat multiple times 
	 * @param tagList
	 * @return true if none of the tags in tagList repeats. 
	 */
	private boolean isValidTagList(ArrayList<String> tagList) {
		String pattern = "(deadline|deadlines|event|events|general|all)";
		for(int i = 0; i < tagList.size(); i++) {
			String tag = tagList.get(i); 
			if (tag.matches(pattern)) {
				return false; 
			}
			//compare with every other tag in the list to ensure no repeats
			for (int j = i+1; j < tagList.size(); j++) {
				String otherTag = tagList.get(j); 
				if (tag.equals(otherTag)) {
					return false; 
				}
			}
		}
		return true; 
	}
	
	
	/**
	 * Processes the string normally as either a 
	 * floating, deadline or event task.
	 * If there is any formatting error, return error 
	 * @param command
	 * @param processed
	 * @param task
	 * @param simpString
	 * @return ProcessedObject 
	 */
	private ProcessedObject processNormally(String command, 
			ProcessedObject processed, Task task, String simpString) {
		String taskNameRaw; 
		try {
			taskNameRaw = removeTimeFromName(simpString); 
		} catch (Exception e) {
			return super.processError(ParserConstants.ERROR_DATE_KEYWORD); 
		}
		String taskName = taskNameRaw.substring(0, 1).toUpperCase() + taskNameRaw.substring(1);
		String rawDate = simpString.replace(taskNameRaw, "").trim();
		int priority = getPriority(rawDate); 
		
		//invalid priority given
		if (priority == -1) {
			return super.processError(ParserConstants.ERROR_SET_NEW_PRIORITY);
		}
		
		rawDate = rawDate.split("!")[0].trim(); //remove priority
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
		} else if (rawDate.indexOf("by") == 0) {
			if (simpString.split("by").length != 1) {
				//deadline 
				processed = handleDeadline(task, taskName, rawDate);
			} 
		} else if (rawDate.indexOf("on") == 0) {
			if (simpString.split("on").length != 1) {
				//deadline
				processed = handleDeadline(task, taskName, rawDate);	
			}
		} else if (rawDate.indexOf("at") == 0) {
			if (simpString.split("at").length != 1) {
				//handle as deadline 
				processed = handleDeadline(task, taskName, rawDate);
			}
		} else {
			//set as floating task, remove priority first  
			priority = getPriority(taskName); 
			//invalid priority given
			if (priority == -1) {
				return super.processError(ParserConstants.ERROR_SET_NEW_PRIORITY);
			}
			if (priority != 0) {
				taskName = taskName.split("!")[0].trim();
			}
			processed = handleFloating(command, taskName);
		}
		//set the priority
		if (priority != 0) {
			processed.getTask().setPriority(priority);
		}
		return processed;
	}
	
	/**
	 * If the task description is empty, it is considered an 
	 * empty add. So, return true to indicate error. 
	 * @param simpString
	 * @return ProcessedObject
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
	 * @return true if taskname only consists of numbers
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
	 * @return ProcessedObject
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
		long epochTimeStart = -1;
		long epochTimeEnd = -1; 
		
		//if date contains am or pm or morning or night, 
		//call pretty parser to process the time and return. 
		try {
			long[] epochTimeEvent = getPrettyTimeEvent(dateForPrettyParser);
			epochTimeStart = epochTimeEvent[0]; 
			epochTimeEnd = epochTimeEvent[1]; 
			task.setStartDate(epochTimeStart);
			task.setEndDate(epochTimeEnd);
			task.setTaskName(taskName);
			task.setTaskType("EVENT");
			processed = new ProcessedObject("ADD_EVENT",task);
			
			//make sure start time < end Time 
			if (!isValidEvent(epochTimeStart, epochTimeEnd)) {
				return super.processError(ParserConstants.ERROR_EVENT_TIME_INVALID); 
			}
			
			return processed;
		} catch (Error e) {
			//do nothing, continue to code below
			//ie. date format wrong or has no time in the date
		}
		
		//process start date/time
		if (!specialDays.containsKey(rawStartDate.toLowerCase())) {
			try {
				epochTime = timeConverter.toEpochTime(rawStartDate);
				epochTimeStart = epochTime; 
				task.setStartDate(epochTime);
			} catch (ParseException error) {
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, rawStartDate)); 
				return processed; 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawStartDate.toLowerCase());
			epochTimeStart = epochTime; 
			task.setStartDate(epochTime);
		}
		
		//process end date/time 
		if (!specialDays.containsKey(rawEndDate.toLowerCase())) {
			try {
				epochTime = timeConverter.toEpochTime(rawEndDate);
				epochTimeEnd = epochTime; 
				task.setEndDate(epochTime); 	
			} catch (ParseException error) {
				processed = super.processError(String.format(
						ParserConstants.ERROR_DATE_FORMAT, rawEndDate)); 
				return processed; 
			}
		} else {
			//process the special day
			epochTime = specialDays.get(rawEndDate.toLowerCase());
			epochTimeEnd = epochTime; 
			task.setEndDate(epochTime);
		}
		
		task.setTaskName(taskName);
		task.setTaskType("EVENT");
		processed = new ProcessedObject("ADD_EVENT",task);
		
		//check to make sure startDate < end date 
		if (!isValidEvent(epochTimeStart, epochTimeEnd)) {
			return super.processError(ParserConstants.ERROR_EVENT_TIME_INVALID); 
		}
		
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
	 * ADD: process deadlines with the keyword "by", "on" and "at" 
	 * @param task
	 * @param simpString
	 * @return ProcessedObject
	 */
	private ProcessedObject handleDeadline(Task task, String taskName, String rawDate) {
		long epochTime;
		ProcessedObject processed;
		String dateForPrettyParser = rawDate;
		String[] splitDate = rawDate.split(" ");
		rawDate = ""; 
		for(int i=1; i < splitDate.length; i++) { 
			rawDate += splitDate[i] + " "; 
		}
		rawDate = rawDate.trim(); 
		
		
		//if time contains am or pm or morning or night, 
		//call pretty parser to process the time.
		epochTime = getPrettyTime(dateForPrettyParser);
		if (epochTime != -1) {
			task.setDeadline(epochTime); 
		} else if (!specialDays.containsKey(rawDate.toLowerCase())) {
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
			epochTime = specialDays.get(rawDate.toLowerCase());
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
	 * @return ProcessedObject
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
	 * Given a stringInput, remove the command from the string
	 * @param command
	 * @param stringInput
	 * @return taskName without command
	 */
	private String stringNoCommand(String stringInput) {
		String command = stringInput.split(" ")[0]; //so we don't need to worry about case
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
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
	
	/**
	 * If the rawDate contains time field for an event, 
	 * use PrettyTimeParser to parse the date
	 * @param rawDate
	 * @return epochTime (long array) of rawDate (FOR EVENTS) 
	 */
	private long[] getPrettyTimeEvent(String rawDate) throws Error {
		//use regex to check for time format
		if (timeChecker.hasTimeEdit(rawDate)) {
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
		throw new Error(); //no time indicated, or time is in the wrong format
	}
	
	/**
	 * Get tag list for a task
	 * Assumptions: all tags must be added to the back of the userInput
	 * @param rawInput
	 * @return ArrayList of Tags
	 */
	private ArrayList<String> getTagList(String rawInput) {
		ArrayList<String> tagList = new ArrayList<String>();
		String[] splitString = rawInput.split("#");
		for(int i = 1; i < splitString.length; i++) {
			String tag = splitString[i].replace("!", "").trim(); 
			tagList.add(tag);
		}
		return tagList; 
	}
	
	/**
	 * Return the priority for a task 
	 * @param rawDate: date with priority still stuck there... 
	 * @return priority for the task
	 */
	private int getPriority(String rawDate) {
		//rawDate = rawDate.trim(); 
		int count = 0; 
		int dateLen = rawDate.length(); 
		boolean canContinue = false; 
		
		for(int i = dateLen-1; i >= 0; i--) {
			char k = rawDate.charAt(i); 
			//check that the end of the string has !, 
			//else there's no priority to check 
			if (i == dateLen-1 && k == '!') {
				canContinue = true; 
				count += 1;
			} else if (canContinue == true) {
				if (k == '!') {
					count += 1; 
				}
			} else {
				return 0; //user did not indicate a priority 
			}
		}
		
		if (count <= 3) {
			return count;
		}
		return -1; //error with priority 
	}
	
	/**
	 * Depending on where the user keyed in his dates, the task name
	 * might still contain the time in it. So this function will remove
	 * the time from the task name (if it is there) 
	 * @param taskName
	 * @return task name without time. 
	 */
	private String removeTimeFromName(String taskName) throws Exception {
		String keyword = "(at|from|on|by)";
		String combinedTime = "\\d{1,2}(:|.)?\\d{0,2}(am|pm|h)";
		String combinedTime2 = "(tonight|night|morning)"; //not in special days converter
		String timeSpecifier = "(am|pm|h)";
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
					if (time.matches(combinedTime) || specialDays.containsKey(time) ||
							time.matches(combinedTime2)) {
						//eg. 3pm or 3h or today/tomorrow/fri
						i += 2; 
						break;
					} else if (time.matches(timePattern) || time.matches(specialDaysKeywords)) {
						//eg. only a number or contains "this/next" 
						if (i+2 < size) {
							String time2 = splitName[i+2];
							String time3 = time + " " + time2; 
							if (time2.matches(timeSpecifier) || time2.length() == 3) {
								//eg. 3 pm or 2300 h or a month like feb 
								//(assume v. few words len(3) after num) 
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
						} else {
							stringRep += time + " "; 
							i += 1; 
						}
					} else {
						//probably a place and not time,
						//so add it to the task name 
						stringRep += word + " " + time + " ";
						i += 2; 
						continue; 
					}
				} else {
					//i+1 doesnt exist 
					throw new Exception(); 
				}
			} else {
				stringRep += word + " "; 
				i += 1; 
			}
		}	
		return stringRep.trim(); 
	}
}
