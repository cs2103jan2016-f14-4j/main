package taskey.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @@author A0107345L
 * This class will check the date/time pattern of the user's input
 * and decide if the format falls into its blacklist, as specified
 * by a regex (regular expression). If it does,
 * return an Error object to tell the user to key in the date in some
 * other format. 
 * This blacklist is to ensure that grammatically incorrect dates and times
 * do not get parsed by PrettyTimeParser, as they are potentially confusing
 * and get varied results from the parser. 
 * @author Xue Hui
 *
 */
public class DateTimePatternMatcher {
	/* Blacklist for Adding task with date/time */
	private String pattern1 = "by \\d{1,2}(:|.)?\\d{0,2} ?(am|pm) on"; 
	private String pattern2 = "on \\d{1,2} (jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec) "
			+ "from \\d{1,2}(:|.)?\\d{0,2} ?(am|pm) to";
	private String pattern3 = "on \\d{1,2} (jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec) "
			+ "\\d{4} from \\d{1,2}(:|.)?\\d{0,2} ?(am|pm) to";
	private String pattern4 = "on (this|next)? (mon|tue|wed|thu|fri|sat|sun) from \\d{1,2}(:|.)?\\d{0,2} ?(am|pm) to"; 
	private String pattern5 = "on (tmr|tomorrow|today) from \\d{1,2}(:|.)?\\d{0,2} ?(am|pm) to"; 
	private String pattern6 = "at \\d{1,2}(:|.)?\\d{0,2} ?(am|pm) by"; 
	
	/* White-list for AutoComplete to suggest date/time */
	private String pattern7 = "\\d{1,2}(:|.)?\\d{0,2} ?(a|p|h)?"; //for time
	private String pattern8 = "\\d{1,2}"; //for date
	private String checkTime = "(am|pm)";
	private String correctTimeFormat = "\\d{1,2}(:|.)?\\d{0,2} ?(am|pm)"; //for time
	private String dateFormat = "\\d{1,2} (jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)"; 
	private String dayFormat = "(mon|tue|wed|thu|fri|sat|sun|monday|tuesday|wednesday|thursday|"
			+ "friday|saturday|sunday|tues|thurs|tmr|tomorrow|today)";
	
	/* White-list for ParseEdit to check if the time has am, pm or h, or morning or night */ 
	private String pattern9 = "\\d{1,2}(:|.)?\\d{0,2} ?(am|pm|h)"; //for time
	private String pattern10= "(morning|night)";
	
	/* ParseAdd: Check if Event Time includes time for both start and End s */
	private String timeChecker = "\\d{1,2}(:|.)?\\d{0,2} ?(am|pm|h)"; 
	
	public DateTimePatternMatcher() {
		
	}
	
	/**
	 * Check if both start and end times of events have times included in them.
	 * @param startTime
	 * @param endTime
	 * @return true if both have times 
	 */
	public boolean isBothTime(String startTime, String endTime) {
		Pattern p = Pattern.compile(timeChecker);
		Matcher m = p.matcher(startTime);
		Matcher m2 = p.matcher(endTime);
		
		while (m.find()) {
			//System.out.println("Found a match.");
			while (m2.find()) {
				//System.out.println("Found a match.");
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if any 3letter input has am or pm in it,
	 * so that AutoComplete does not suggest a month correction
	 * @param input
	 * @return true if it has am or pm in it 
	 */
	public boolean hasAmPm(String input) {
		Pattern p = Pattern.compile(checkTime);
		Matcher m = p.matcher(input);
		
		while (m.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		return false; 
	}
	
	/**
	 * For ParseEdit class:
	 * Checks if the input contains something that looks like a time format
	 * @param input
	 * @return true if there seems to be some kind of time format
	 */
	public boolean hasTimeEdit(String input) {
		Pattern p = Pattern.compile(pattern9);
		Matcher m = p.matcher(input);
		
		Pattern p2 = Pattern.compile(pattern10);
		Matcher m2 = p2.matcher(input);
		
		while (m.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m2.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		return false; 
	}
	
	/**
	 * For AutoComplete class:
	 * Checks if the input contains an exact time format
	 * @param input
	 * @return true if there seems to be some kind of time format
	 */
	public boolean hasCorrectTimeFormat(String input) {
		Pattern p = Pattern.compile(correctTimeFormat);
		Matcher m = p.matcher(input);
		
		while (m.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		return false; 
	}
	
	/**
	 * For AutoComplete class:
	 * Checks if the input contains something that looks like a time format
	 * @param input
	 * @return true if there seems to be some kind of time format
	 */
	public boolean hasTimeAC(String input) {
		if (input.matches(pattern7)) {
			return true; 
		}
		return false; 
	}
	
	/**
	 * For AutoComplete class:
	 * Checks if the input contains something that looks like a date format
	 * @param input
	 * @return true if it matches pattern 
	 */
	public boolean hasDateAC(String input) {
		if (input.matches(pattern8)) {
			try {
				int num = Integer.parseInt(input); 
				if (num >= 1 && num <= 31) {
					return true; 
				}
			} catch (Exception e) {
				return false; 
			}
		}
		return false; 
	}
	
	/**
	 * For AutoComplete class:
	 * Checks if the input contains any date format in the string 
	 * @param input
	 * @return true if there is any date format 
	 */
	public boolean hasFullDateAC(String input) {
		Pattern p = Pattern.compile(dateFormat);
		Matcher m = p.matcher(input);
		
		Pattern p2 = Pattern.compile(dayFormat);
		Matcher m2 = p2.matcher(input);
		
		Pattern p3 = Pattern.compile(pattern8);
		Matcher m3 = p3.matcher(input);
		
		while (m.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m2.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m3.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		return false; 
	}
	
	
	/**
	 * Check if the input falls into any of the patterns specified in this class
	 * @param input
	 * @return true if it contains the pattern, false if it doesn't 
	 */
	public boolean hasPattern(String input) {
		Pattern p = Pattern.compile(pattern1);
		Matcher m = p.matcher(input);
		
		Pattern p2 = Pattern.compile(pattern2);
		Matcher m2 = p2.matcher(input);
		
		Pattern p3 = Pattern.compile(pattern3);
		Matcher m3 = p3.matcher(input);
		
		Pattern p4 = Pattern.compile(pattern4);
		Matcher m4 = p4.matcher(input);
		
		Pattern p5 = Pattern.compile(pattern5);
		Matcher m5 = p5.matcher(input);
		
		Pattern p6 = Pattern.compile(pattern6);
		Matcher m6 = p6.matcher(input);
		
		while (m.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m2.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m3.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m4.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m5.find()) {
			//System.out.println("Found a match.");
			return true;
		}
		
		while (m6.find()) {
			//System.out.println("Found a match.");
			return true;
		}	
		return false; 
	}
	
}
