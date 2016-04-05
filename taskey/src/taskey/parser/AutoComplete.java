package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.messenger.ProcessedAC;
import taskey.messenger.TagCategory;

/**
 * @@author A0107345L
 * This class processes what words should be shown in the dropdown
 * menu if the user types into the CLI - it allows the user to auto-complete
 * his commands. 
 * @author Xue Hui
 *
 */
public class AutoComplete {
	private DateTimePatternMatcher pm = new DateTimePatternMatcher(); 
	private TimeConverter tc = new TimeConverter(); 
	
	private HashMap<String,String> commandList = new HashMap<String,String>();
	private ArrayList<String> specialDays = new ArrayList<String>(); 
	private ArrayList<String> specialDaysThis = new ArrayList<String>();
	private ArrayList<String> specialDaysNext = new ArrayList<String>(); 
	private ArrayList<String> commands = new ArrayList<String>();
	private ArrayList<String> viewList = new ArrayList<String>();
	private ArrayList<String> months = new ArrayList<String>(); 
	private HashMap<String, String> monthsMap = new HashMap<String,String>(); 
	private HashMap<String, String> daysOfWeek = new HashMap<String,String>(); 
	private HashMap<String, String> allDaysOfWeek = new HashMap<String, String>(); 
	
	public AutoComplete() {
		
		processCommands();
		processViewList();
		processSpecialDays();
		processMonths(); 
		processDaysOfWeek();
		processAllDaysOfWeek();
		
		
	}

	/**
	 * Process command database
	 */
	private void processCommands() {
		commands.add("add");
		commands.add("view");
		commands.add("del");
		commands.add("set");
		commands.add("search");
		commands.add("done");
		commands.add("undo");
		commands.add("setdir");
		commands.add("save");
		commands.add("clear");
		
		commandList.put("add","add");
		commandList.put("view","view");
		commandList.put("del", "del");
		commandList.put("set","set");
		commandList.put("search","search");
		commandList.put("done","done");
		commandList.put("undo","undo");
		commandList.put("setdir","setdir");
		commandList.put("save","save");
		commandList.put("clear","clear");
	}

	/**
	 * Process ViewList Database 
	 */
	private void processViewList() {
		viewList.add("all");
		viewList.add("today");
		viewList.add("tomorrow");
		viewList.add("general");
		viewList.add("deadlines");
		viewList.add("events");
		viewList.add("archive");
		viewList.add("help");
		viewList.add("high");
		viewList.add("medium");
		viewList.add("low");
	}

	/**
	 * Process Special days database 
	 */
	private void processSpecialDays() {
		specialDays.add("sun");
		specialDays.add("mon");
		specialDays.add("tue");
		specialDays.add("wed");
		specialDays.add("thu");
		specialDays.add("fri");
		specialDays.add("sat");
		
		specialDaysThis.add("this sun");
		specialDaysThis.add("this mon");
		specialDaysThis.add("this tue");
		specialDaysThis.add("this wed");
		specialDaysThis.add("this thu");
		specialDaysThis.add("this fri");
		specialDaysThis.add("this sat");
		
		specialDaysNext.add("next sun");
		specialDaysNext.add("next mon");
		specialDaysNext.add("next tue");
		specialDaysNext.add("next wed");
		specialDaysNext.add("next thu");
		specialDaysNext.add("next fri");
		specialDaysNext.add("next sat");
	}

	/**
	 * Process months database
	 */
	private void processMonths() {
		months.add("jan");
		months.add("feb");
		months.add("mar");
		months.add("apr");
		months.add("may");
		months.add("jun");
		months.add("jul");
		months.add("aug");
		months.add("sep");
		months.add("oct");
		months.add("nov");
		months.add("dec");
		
		monthsMap.put("jan", "jan"); 
		monthsMap.put("feb", "feb"); 
		monthsMap.put("mar", "mar"); 
		monthsMap.put("apr", "apr"); 
		monthsMap.put("may", "may"); 
		monthsMap.put("jun", "jun"); 
		monthsMap.put("jul", "jul"); 
		monthsMap.put("aug", "aug"); 
		monthsMap.put("sep", "sep"); 
		monthsMap.put("oct", "oct"); 
		monthsMap.put("nov", "nov"); 
		monthsMap.put("dec", "dec");
	}

	/**
	 * Process dayOfWeek database
	 */
	private void processDaysOfWeek() {
		daysOfWeek.put("mon", "mon");
		daysOfWeek.put("tue", "tue");
		daysOfWeek.put("wed", "wed");
		daysOfWeek.put("thu", "thu");
		daysOfWeek.put("fri", "fri");
		daysOfWeek.put("sat", "sat");
		daysOfWeek.put("sun", "sun");
		
		daysOfWeek.put("monday", "monday");
		daysOfWeek.put("tuesday", "tuesday");
		daysOfWeek.put("wednesday", "wednesday");
		daysOfWeek.put("thursday", "thursday");
		daysOfWeek.put("friday", "friday");
		daysOfWeek.put("saturday", "saturday");
		daysOfWeek.put("sunday", "sunday");
	}

	/**
	 * Process alldaysofweek database
	 */
	private void processAllDaysOfWeek() {
		allDaysOfWeek.put("mon", "mon");
		allDaysOfWeek.put("tue", "tue");
		allDaysOfWeek.put("wed", "wed");
		allDaysOfWeek.put("thu", "thu");
		allDaysOfWeek.put("fri", "fri");
		allDaysOfWeek.put("sat", "sat");
		allDaysOfWeek.put("sun", "sun");
		
		allDaysOfWeek.put("monday", "monday");
		allDaysOfWeek.put("tuesday", "tuesday");
		allDaysOfWeek.put("wednesday", "wednesday");
		allDaysOfWeek.put("thursday", "thursday");
		allDaysOfWeek.put("friday", "friday");
		allDaysOfWeek.put("saturday", "saturday");
		allDaysOfWeek.put("sunday", "sunday");
		
		allDaysOfWeek.put("this mon", "mon");
		allDaysOfWeek.put("this tue", "tue");
		allDaysOfWeek.put("this wed", "wed");
		allDaysOfWeek.put("this thu", "thu");
		allDaysOfWeek.put("this fri", "fri");
		allDaysOfWeek.put("this sat", "sat");
		allDaysOfWeek.put("this sun", "sun");
		
		allDaysOfWeek.put("this monday", "monday");
		allDaysOfWeek.put("this tuesday", "tuesday");
		allDaysOfWeek.put("this wednesday", "wednesday");
		allDaysOfWeek.put("this thursday", "thursday");
		allDaysOfWeek.put("this friday", "friday");
		allDaysOfWeek.put("this saturday", "saturday");
		allDaysOfWeek.put("this sunday", "sunday");
		
		allDaysOfWeek.put("next mon", "mon");
		allDaysOfWeek.put("next tue", "tue");
		allDaysOfWeek.put("next wed", "wed");
		allDaysOfWeek.put("next thu", "thu");
		allDaysOfWeek.put("next fri", "fri");
		allDaysOfWeek.put("next sat", "sat");
		allDaysOfWeek.put("next sun", "sun");
		
		allDaysOfWeek.put("next monday", "monday");
		allDaysOfWeek.put("next tuesday", "tuesday");
		allDaysOfWeek.put("next wednesday", "wednesday");
		allDaysOfWeek.put("nextthursday", "thursday");
		allDaysOfWeek.put("next friday", "friday");
		allDaysOfWeek.put("next saturday", "saturday");
		allDaysOfWeek.put("next sunday", "sunday");
	}
	
	public ProcessedAC getSuggestions(String rawPhrase, ArrayList<TagCategory> tagDB) {
		ProcessedAC suggestions = null;
		String phrase = rawPhrase.toLowerCase().trim();
		String[] split = phrase.split(" ");
		
		if (split.length == 1) {
			//likely only command 
			suggestions = completeCommand(phrase); 
			return suggestions; 
		}
		
		//look for the correct word to auto-suggest and return 
		switch (split[0].trim()) {
			case "view": 
				suggestions = completeView(phrase, tagDB); 
				break;
			
			case "add": 
				suggestions = completeAdd(phrase, tagDB);
				break;
				
			case "set":
				suggestions = completeEdit(phrase);
				break;
				
			case "del":
			case "search":
			case "done":
			case "undo":
			case "setdir":
			case "save":
			case "clear": 
				suggestions = new ProcessedAC(ParserConstants.FINISHED_COMMAND);
				break; 
				
			default:
				suggestions = new ProcessedAC(ParserConstants.NO_SUCH_COMMAND);
				break; 
		}		
		
		return suggestions; 
	}
	
	/**
	 * Given a partial input of the command, return a list of commands 
	 * that contain that input as a sub-string. 
	 * @param phrase
	 * @return If no such command exists, return null
	 */
	public ProcessedAC completeCommand(String phrase) {
		phrase = phrase.toLowerCase().trim(); 
		
		//if the command is completed, don't need to process
		if (commandList.containsKey(phrase)) {
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
		}
		
		ArrayList<String> availCommands = new ArrayList<String>();
		
		//find list normally  
		for(int i = 0; i < commands.size(); i++) {
			if (commands.get(i).indexOf(phrase) == 0) {
				availCommands.add(commands.get(i)); 
			}
		}
		
		if (!availCommands.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availCommands);
		} else {
			//try to check if command is misspelt
			availCommands = correctCommandError(phrase); 
			if (availCommands != null) {
				return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availCommands); 
			}
		}
		//no such command containing that sub-string
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND); 
	}
	
	/**
	 * Given a partial input that contains "view xxxx xxx",
	 * return a list of available view types that the user can access in his 
	 * latest word 
	 * @param phrase
	 * @return If no such list of views is available, return null 
	 */
	private ProcessedAC completeView(String phrase, ArrayList<TagCategory> tagDB) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("view", "").trim(); 
		String[] parts = phrase.split(" ");
		//only want to auto-complete the latest word
		String word = parts[parts.length-1].trim(); 
		
		//check if basic view exists 
		for(int i = 0; i < viewList.size(); i++) {
			if (viewList.get(i).indexOf(word) == 0) {
				availViews.add(viewList.get(i)); 
			}
		}
		
		//check if tag view exists 
		for(int i = 0; i < tagDB.size(); i++) {
			String tag = "#" + tagDB.get(i).getTagName(); 
			if (tag.contains(word)) {
				//if user types without #, 
				//then he should select the dropdown suggestion
				availViews.add(tag); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availViews); 
		}
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND); 
	}
	
	/**
	 * Help a user suggest dates/categories/priority if any 
	 * @param phrase
	 * @param utd 
	 * @return ProcessedAutoComplete Object 
	 */
	private ProcessedAC completeAdd(String phrase, ArrayList<TagCategory> tagDB) { 
		String keyWords = "(at|on|by|from)";
		
		ArrayList<String> availSuggestions = new ArrayList<String>(); 
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("add", "").trim();
		String[] splitString = phrase.split(" ");
		String latestWord = splitString[splitString.length - 1]; 
		
		/*
		if (splitString.length >= 3) { 
			String beforeLatestWord = splitString[splitString.length - 2]; 
		
			if (monthsMap.containsKey(beforeLatestWord)) {
				return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
			}
		} */
		
		if (latestWord.contains("#")) {
			latestWord = latestWord.replace("#", "").trim(); 
			//suggest categories to the user 
			//if empty tag, suggest anything
			if (latestWord.equals("")) {
				//if tagDB is empty, nth to suggest 
				if (tagDB.size() == 0) {
					return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
				}
				//else suggest something 
				for(int i = 0; i < tagDB.size(); i++) {
					if (i < 3) {
						availSuggestions.add("#" + tagDB.get(i).getTagName());
					}
				}
				return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availSuggestions);
			}
			//if typed halfway, can suggest 
			for(int i = 0; i < tagDB.size(); i++) {
				String tag = tagDB.get(i).getTagName(); 
				if (tag.contains(latestWord)) {
					availSuggestions.add(tag); 
				}
			}
			//if tag doesnt exist, maybe it's a new tag. don't highlight error
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
		} else if (latestWord.contains("!")) {
			//suggest priorities to the user 
			if (phrase.contains("!")) {
				availSuggestions.add("!"); 
				availSuggestions.add("!!");
				availSuggestions.add("!!!");
			} else if (phrase.contains("!!")) {
				availSuggestions.add("!!");
				availSuggestions.add("!!!");
			} else if (phrase.contains("!!!")) {
				//no need to suggest anything else 
				return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
			} else if (phrase.contains("!!!!")) {
				//anything more than 3 !s is an error
				return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND);
			}
		} else if (latestWord.matches(keyWords)) {
			//suggest some dates to the user, since keywords spotted 
			availSuggestions.add("tmr");
			availSuggestions.add("8pm");
			availSuggestions.add("next mon");
		} else if (!hasKeywords(splitString)) {
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
		} else {
			availSuggestions = suggestDates(latestWord, phrase); 
			if (availSuggestions == null) {
				//might be task name, don't indicate error 
				return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
			}
		}
		
		if (!availSuggestions.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availSuggestions); 
		}
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND);
	}

	/**
	 * Checks if any word in an input contains keywords
	 * @param phraseSplit
	 * @return true if the keywords at/on/by/from exists anywhere
	 */
	private boolean hasKeywords(String[] phraseSplit) {
		String keyWords = "(at|on|by|from)";
		
		for (int i = 0; i < phraseSplit.length; i++) {
			String word = phraseSplit[i];
			if (word.matches(keyWords)) {
				return true; 
			}	
		}
		return false; 
	}
	
	/**
	 * Help user to fill in !!! (priority) or date 
	 * Will not suggest anything for task name or new task name 
	 * @param phrase
	 * @return ProcessedAutoComplete Object 
	 */
	private ProcessedAC completeEdit(String phrase) {
		ArrayList<String> availSuggestions = new ArrayList<String>(); 
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("set", "").trim();
		
		if (phrase.contains("!!!!")) {
			//anything more than 3 !s is an error
			return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND);
		} else if (phrase.contains("!!!")) {
			//no need to suggest anything else 
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
		} else if (phrase.contains("!!")) {
			availSuggestions.add("!!");
			availSuggestions.add("!!!");
		} else if (phrase.contains("!")) {
			availSuggestions.add("!"); 
			availSuggestions.add("!!");
			availSuggestions.add("!!!"); 
		} else if (phrase.contains("[")) {
			//suggest a date to the user 
			String dates; 
			String[] rawDates = phrase.split("\\[");
			if (rawDates.length > 1) {
				dates = rawDates[1].trim();
				dates = dates.replace("]", "");
			} else {
				//probably an empty [] from autocomplete
				availSuggestions.add("tomorrow"); 
				return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availSuggestions); 
			}
			String date;
			if (dates.split(",").length == 2) {
				//only auto complete the 2nd date 
				date = dates.split(",")[1].trim(); 
			} else {
				//only 1 date, try to auto complete that 
				date = dates; 
			}
			ArrayList<String> suggestedDates = suggestDates(date); 
			if (suggestedDates != null) {
				availSuggestions = suggestDates(date); 	
			} else {
				return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
			}
		} else if (phrase.contains("\"")) {
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
		} else {
			//user has not typed any changes, so suggest format 
			availSuggestions.add("\"New Task Name\"");
			availSuggestions.add("[New Date]");
			availSuggestions.add("!!"); 
		}
		
		if (!availSuggestions.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availSuggestions); 
		}
		return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
	}
	
	/**
	 * FOR PARSING EDIT/SET 
	 * Suggest some dates and times that the user can type
	 * @param date
	 * @return a list of date/time that the user can type
	 */
	private ArrayList<String> suggestDates(String date) {
		ArrayList<String> availDates = new ArrayList<String>();
		
		availDates = checkIfSpecialDate(date, availDates);
		if (availDates == null) {
			return null; 
		}
		if (!availDates.isEmpty()) {
			return availDates; 
		}
		
		//eg. sun... mon... 
		for(int i = 0; i < specialDays.size(); i++) { 
			if (specialDays.get(i).indexOf(date) == 0) {
				availDates.add(specialDays.get(i));
			}
		}
		
		String[] tempArr = date.split(" ");
		String timeBehind = tempArr[tempArr.length-1]; 
		
		//check for possible time formats, and suggest times
		if (pm.hasTimeAC(date.trim())) {
			//String date2 = date.replace("pm", "");
			//date2 = date2.replace("am", "");
			String date2 = date.replace("h", "");
			date2 = date2.replace("a", "");
			date2 = date2.replace("p", "");
			try {
				int num = Integer.parseInt(date2.trim()); 
				if (num <= 12) {
					availDates.add(date2.trim()+"am");
					availDates.add(date2.trim()+"pm"); 
				}
				if (date2.trim().length() == 4 && num <= 2400 
						&& num != 2016 && num != 2017) { 
					availDates.add(date2.trim()+"h"); 
				}
			} catch (Exception e) {
				//do nth 
			}
		} else if (pm.hasCorrectTimeFormat(date.trim())) {
			//don't suggest any time, if there's no other date format,
			//then suggest "tomorrow"
			String[] temp = date.split(" ");
			String date2 = ""; 
			for (int i = 1; i < temp.length; i++) {
				date2 += temp[i]; 
			}
			if (!pm.hasFullDateAC(date2)) {
				availDates.add("tomorrow"); 
			} else if (pm.hasDateAC(date2)) {
				ArrayList<String> dateRaw = tc.get3MonthsFromNow(); 
				for(int i = 0; i < dateRaw.size(); i++) {
					availDates.add(date2.trim()+ " " + dateRaw.get(i)); //dd mmm
				}
			} else {
				checkIfContainsMonth(date2, availDates);
			}
		} else {
			//suggest a time? 
			//availDates.add("9pm");
		}
		
		//check if month 
		checkIfContainsMonth(date, availDates);
		
		//check for normal date
		checkIfContainsNormalDate(date, availDates);		
		
		if (!availDates.isEmpty()) {
			return availDates; 
		}
		return null; 
	}
	
	/**
	 * Checks if the input contains what looks like a special date
	 * @param date
	 * @param availDates
	 * @return list of avail special dates to autocomplete 
	 */
	private ArrayList<String> checkIfSpecialDate(String date, ArrayList<String> availDates) {
		if (allDaysOfWeek.containsKey(date.trim())) {
			return null; 
		}
		
		if ("this".indexOf(date) == 0) { //eg. this ... 
			availDates = specialDaysThis; 
			return availDates; 
		} else if ("next".indexOf(date) == 0) { //eg. next ...
			availDates = specialDaysNext; 
			return availDates; 
		}
		return availDates;
	}

	/**
	 * Checks if the input possibly contains some normal date,
	 * and attempts to autocomplete it 
	 * @param date
	 * @param availDates
	 */
	private void checkIfContainsNormalDate(String date, ArrayList<String> availDates) {
		if (pm.hasDateAC(date.trim())) {
			ArrayList<String> dateRaw = tc.get3MonthsFromNow(); 
			for(int i = 0; i < dateRaw.size(); i++) {
				availDates.add(date.trim()+ " " + dateRaw.get(i)); //dd mmm
			}
		}
	}
	
	/**
	 * If the date input looks like it contains a month, try to autocomplete the 
	 * month, or autocorrect it if it is spelled wrongly 
	 * @param date
	 * @param availDates
	 */
	private void checkIfContainsMonth(String date, ArrayList<String> availDates) {
		if (date.split(" ").length >= 2) { 
			String day = date.split(" ")[0]; 
			String mth = date.split(" ")[1]; //get the month
			if (mth.length() <= 3) {
				if (mth.length() == 3) {
					if (monthsMap.containsKey(mth)) {
						//do nth
					} else if (daysOfWeek.containsKey(mth)) {
						//do nth
					} else if (pm.hasAmPm(mth)) {
						//do nth 
					} else {
						ArrayList<String> suggestTemp = correctDateError(mth); 
						if (suggestTemp != null) { 
							for(int i = 0; i < suggestTemp.size(); i++) {
								availDates.add(suggestTemp.get(i)); 
							}
						}
					}
				} else {
					for(int i = 0; i < months.size(); i++) {
						String month = months.get(i);
						if (month.indexOf(mth) == 0) {
							availDates.add(day + " " + month); 
						}
					}
				}
			}
		}
	}
	
	/**
	 * OVERLOADED METHOD: FOR PARSING ADD 
	 * Suggest some dates and times that the user can type
	 * @param date
	 * @return a list of date/time that the user can type
	 */
	private ArrayList<String> suggestDates(String date, String phrase) {
		ArrayList<String> availDates = new ArrayList<String>();
		
		availDates = checkIfSpecialDate(date, availDates);
		if (availDates == null) {
			return null; 
		}
		if (!availDates.isEmpty()) {
			return availDates; 
		}
		
		//eg. sun... mon... 
		String parts[] = phrase.split(" "); 
		String number = parts[parts.length-2]; //get 2nd last word
		try {
			int num = Integer.parseInt(number); 
		} catch (Exception e) {
			//check if completed first : 
			if (daysOfWeek.containsKey(date)) {
				return null; 
			} else {
				//only add stuff like sun mon if the thing is not a number
				for(int i = 0; i < specialDays.size(); i++) { 
					if (specialDays.get(i).indexOf(date) == 0) {
						availDates.add(specialDays.get(i));
					}
				}
			}
		}
		
		//check for possible time formats, and suggest times
		if (pm.hasTimeAC(date.trim())) {
			//date = date.replace("pm", "");
			//date = date.replace("am", "");
			date = date.replace("h", "");
			date = date.replace("a", "");
			date = date.replace("p", "");
			try {
				int num = Integer.parseInt(date.trim()); 
				if (num <= 12) {
					availDates.add(date.trim()+"am");
					availDates.add(date.trim()+"pm"); 
				}
				if (date.trim().length() == 4 && num <= 2400 
						&& num != 2016 && num != 2017) { 
					availDates.add(date.trim()+"h"); 
				}
			} catch (Exception e) {
				//do nth 
			}
		}
		
		//check for normal date
		if (pm.hasDateAC(date.trim())) {
			String tempDateRaw = getDateRaw(phrase); 
			boolean hasMonth = false; 
			for(int i = 0; i < months.size(); i++) {
				if (tempDateRaw.contains(months.get(i)) && !tempDateRaw.contains("from")) {
					hasMonth = true; 
				}
			}
			if (hasMonth == false) { 
				ArrayList<String> dateRaw = tc.get3MonthsFromNow(); 
				for(int i = 0; i < dateRaw.size(); i++) {
					availDates.add(date.trim()+ " " + dateRaw.get(i)); //dd mmm
				}
			}
		}
		
		//check if month 
		if (date.length() <= 3) {
			if (date.length() == 3) {
				if (monthsMap.containsKey(date.trim())) {
					//do nth
				} else if (pm.hasAmPm(date.trim())) {
					//do nth 
				} else {
					ArrayList<String> suggestTemp = correctDateError(date.trim()); 
					if (suggestTemp != null) {
						for(int i = 0; i < suggestTemp.size(); i++) {
							availDates.add(suggestTemp.get(i)); 
						}
					}
				}
			} else {
				for(int i = 0; i < months.size(); i++) {
					String month = months.get(i);
					if (month.indexOf(date) == 0) {
						availDates.add(month); 
					}
				}
			}
		}
		if (!availDates.isEmpty()) {
			return availDates; 
		}
		return null; 
	}
	
	/**
	 * Get a rough estimate of date from an input phrase
	 * (doesn't matter if location is included) 
	 * @param phrase
	 * @return estimated date phrase
	 */
	private String getDateRaw(String phrase) {
		String dateRaw = ""; 
		String[] parts = phrase.split(" "); 
		boolean canAdd = false;
		
		for(int i = 0; i < parts.length; i++) {
			String temp = parts[i]; 
			if (temp.equalsIgnoreCase("by") || 
					temp.equalsIgnoreCase("from") ||
					temp.equalsIgnoreCase("at") ||
					temp.equalsIgnoreCase("on")) {
				canAdd = true;
			} else if (canAdd == true) {
				dateRaw += temp; 
			}
		}
		return dateRaw; 
	}
	
	/**
	 * If a command has been spelt wrongly, try to correct it
	 * @param misSpelled
	 * @return an array list of suggestions for the correct command spelling
	 */
	public ArrayList<String> correctCommandError(String misSpelled) {
		ArrayList<String> suggestions = new ArrayList<String>(); 
		
		for(int i = 0; i < commands.size(); i++) {
			String correctComm = commands.get(i); 
			int temp = levenshteinDist(misSpelled, correctComm); 
			if (temp == 0) {
				//exactly the same
				return null;  
			}
			if (temp <= 2) {
				suggestions.add(correctComm); 
			}
			
		}
		return suggestions; 
	}
	
	/**
	 * If the user has misspelt his date, suggest a correction for him 
	 * @param misSpelled
	 * @return list of possible correctly spelled dates 
	 */
	public ArrayList<String> correctDateError(String misSpelled) {
		ArrayList<String> suggestions = new ArrayList<String>(); 
		
		for(int i = 0; i < months.size(); i++) {
			String month = months.get(i); 
			int temp = levenshteinDist(misSpelled, month); 
			if (temp == 0) {
				//exactly the same
				return null;  
			}
			if (temp <= 2) {
				suggestions.add(month); 
			}
		}
		return suggestions; 
	}
	
	/**
	 * This algorithm measure the difference in distance between
	 * a source (src) string and a target (tar) string
	 * Algorithm taken from: https://en.wikipedia.org/wiki/Levenshtein_distance
	 * Upper and lower bounds of the algorithm: 
	 * 1) It is at least the difference in the size of the 2 strings
	 * 2) It is at most the length of the longer string
	 * 3) 0 iff the 2 strings are equal
	 * 4) If the 2 strings are the same size, upperBound of Levenstein distance is the Hamming Distance
	 * 5) Satisfies Triangle Inequality [LD(string1,string2) >= LD(string1) + LD(string2)]
	 * @param src
	 * @param tar
	 * @return distance between the 2 strings 
	 */
	private int levenshteinDist(String src, String tar) {
		//init to 0 automatically by java 
		int[][] d = new int[src.length()+1][tar.length()+1]; 
		
		for(int i = 1; i <= src.length(); i++) {
		      d[i][0] = i;
		}
		
		for(int j = 1; j <= tar.length(); j++) {
			d[0][j] = j; 
		}
		
		for(int j = 1; j <= tar.length(); j++) {
			for(int i = 1; i <= src.length(); i++) {
				int substitutionCost; 
				
				if (src.charAt(i-1) == tar.charAt(j-1)) {
					substitutionCost = 0; 
				} else {
					substitutionCost = 1; 
				}
				d[i][j] = Math.min(d[i-1][j] + 1, //deletion
							Math.min(d[i][j-1] + 1, //insertion
									d[i-1][j-1] + substitutionCost)); //substitution
				
			}
		}
		
		return d[src.length()][tar.length()]; 
	}
	
	
	/* for testing 
	public static void main(String[] args) {
		AutoComplete ac = new AutoComplete(); 
		ac.correctDateError("fbr"); 
	} */ 
	
	
	/* @@author A0107345L-unused
	 * Decided not to use the code below as we decided to change 
	 * what the AutoComplete should display 
	 */
	
	/**
	 * Given a partial input that contains "del xxxx",
	 * display a list of tasks that the user can delete 
	 * (he can delete by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	private ArrayList<String> completeDelete(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("del", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
	
	/**
	 * Given a partial input that contains "done xxxx",
	 * display a list of tasks that the user can set as done 
	 * (he can set done by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	private ArrayList<String> completeDone(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("done", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
}
