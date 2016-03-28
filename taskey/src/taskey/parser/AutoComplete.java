package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.messenger.ProcessedAC;
import taskey.messenger.TagCategory;
import taskey.messenger.UserTagDatabase;

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
	
	public AutoComplete() {
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
		
		viewList.add("all");
		viewList.add("general");
		viewList.add("deadlines");
		viewList.add("events");
		viewList.add("archive");
		viewList.add("help");
		viewList.add("high");
		viewList.add("medium");
		viewList.add("low");
		
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
	
	public ProcessedAC getSuggestions(String rawPhrase, UserTagDatabase utd) {
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
				suggestions = completeView(phrase, utd); 
				break;
			
			case "add": 
				suggestions = completeAdd(phrase, utd);
				break;
				
			case "set":
				suggestions = completeEdit(phrase);
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
		if (commandList.containsKey("phrase")) {
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
		}
		
		ArrayList<String> availCommands = new ArrayList<String>();
		
		//if only one letter, don't search the wrong part of the command
		if (phrase.length() == 1) {
			for(int i = 0; i < commands.size(); i++) {
				String tempCommand = commands.get(i); 
				int temp = tempCommand.indexOf(phrase);
				if (temp == 0) { //first pos 
					availCommands.add(tempCommand); 		
				}
			}
			if (!availCommands.isEmpty()) {
				return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availCommands);
			}
			return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND); 
		}
		
		//find list normally 
		for(int i = 0; i < commands.size(); i++) {
			if (commands.get(i).contains(phrase)) {
				availCommands.add(commands.get(i)); 
			}
		}
		
		if (!availCommands.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availCommands);
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
	private ProcessedAC completeView(String phrase, UserTagDatabase utd) {
		ArrayList<TagCategory> tagDB = utd.getTagList(); 
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("view", ""); 
		String[] parts = phrase.split(" ");
		//only want to auto-complete the latest word
		String word = parts[parts.length-1].trim(); 
		
		//check if basic view exists 
		for(int i = 0; i < viewList.size(); i++) {
			if (viewList.get(i).contains(word)) {
				availViews.add(viewList.get(i)); 
			}
		}
		
		//check if tag view exists 
		for(int i = 0; i < tagDB.size(); i++) {
			String tag = tagDB.get(i).getTagName(); 
			if (tag.contains(phrase)) {
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
	private ProcessedAC completeAdd(String phrase, UserTagDatabase utd) {
		ArrayList<TagCategory> tagDB = utd.getTagList(); 
		String keyWords = "(at|on|by|from)";
		ArrayList<String> availSuggestions = new ArrayList<String>(); 
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("add", "").trim();
		String[] splitString = phrase.split(" ");
		String latestWord = splitString[splitString.length - 1]; 
		
		if (latestWord.contains("#")) {
			latestWord = latestWord.replace("#", "").trim(); 
			//suggest categories to the user 
			//if empty tag, suggest anything
			if (latestWord.equals("")) {
				//if tagDB is empty, nth to suggest 
				if (tagDB.size() == 0) {
					return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
				}
				for(int i = 0; i < tagDB.size(); i++) {
					if (i < 3) {
						availSuggestions.add(tagDB.get(i).getTagName());
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
			}
		} else if (latestWord.matches(keyWords)) {
			//suggest some dates to the user, since keywords spotted 
			availSuggestions.add("tmr");
			availSuggestions.add("8pm");
			availSuggestions.add("next mon");
		} else {
			availSuggestions = suggestDates(latestWord); 
			if (availSuggestions == null) {
				return new ProcessedAC(ParserConstants.FINISHED_COMMAND); 
			}
		}
		
		if (!availSuggestions.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availSuggestions); 
		}
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND);
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
		} else if (phrase.contains("[")) {
			String phrase2 = phrase.replace("]", ""); 
			//suggest a date to the user 
			String dates = phrase2.split("\\[")[1].trim();
			String date;
			if (dates.split(",").length == 2) {
				//only auto complete the 2nd date 
				date = dates.split(",")[1].trim(); 
			} else {
				//only 1 date, try to autocomplete that 
				date = dates; 
			}
			availSuggestions = suggestDates(date); 	
		}
		
		if (!availSuggestions.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availSuggestions); 
		}
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND);
	}
	
	/**
	 * Suggest some dates and times that the user can type
	 * @param date
	 * @return a list of date/time that the user can type
	 */
	private ArrayList<String> suggestDates(String date) {
		ArrayList<String> availDates = new ArrayList<String>();
		
		if ("this".contains(date.trim())) { //eg. this ... 
			availDates = specialDaysThis; 
			return availDates; 
		} else if ("next".contains(date.trim())) { //eg. this ...
			availDates = specialDaysNext; 
			return availDates; 
		}
		
		//eg. sun... mon... 
		for(int i = 0; i < specialDays.size(); i++) { 
			if (specialDays.get(i).contains(date)) {
				availDates.add(specialDays.get(i));
			}
		}
		
		//check for time 
		if (pm.hasTimeAC(date.trim())) {
			availDates.add(date.trim()+"am");
			availDates.add(date.trim()+"pm"); 
		}
		
		//check for normal date
		if (pm.hasDateAC(date.trim())) {
			ArrayList<String> dateRaw = tc.get3MonthsFromNow(); 
			for(int i = 0; i < dateRaw.size(); i++) {
				availDates.add(date.trim()+ " " + dateRaw.get(i)); //dd mmm
			}
		}
		if (!availDates.isEmpty()) {
			return availDates; 
		}
		return null; 
	}
	
	
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
	public ArrayList<String> completeDelete(String phrase, ArrayList<String> tasks) {
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
	public ArrayList<String> completeDone(String phrase, ArrayList<String> tasks) {
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
