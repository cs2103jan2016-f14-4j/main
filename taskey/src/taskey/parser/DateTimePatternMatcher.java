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
	private String pattern7 = "\\d{1,2}(:|.)?\\d{0,2} ?(a|p|am|pm|h)?"; //for time
	private String pattern8 = "\\d{1,2} ?"; //for date
	
	/* White-list for ParseEdit to check if the time has am, pm or h, or morning or night */ 
	private String pattern9 = "\\d{1,2}(:|.)?\\d{0,2} ?(am|pm|h)"; //for time
	private String pattern10= "(morning|night)";
	
	public DateTimePatternMatcher() {
		
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
	 * @return
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
	
	
	public static void main(String[] args) {
		DateTimePatternMatcher pm = new DateTimePatternMatcher(); 
		String string1 = "add project meeting by 3 pm on 17 feb";
		String string2 = "add project meeting on 19 feb from 4pm to 5pm";
		String string3 = "add do homework at 3pm by tomorrow";
		
		System.out.println(pm.hasPattern(string1));
		System.out.println(pm.hasPattern(string2));
		System.out.println(pm.hasPattern(string3));
		
		String string4 = "add do homework by 23:00h";
		String string5 = "add do homework by tonight"; 
		System.out.println(pm.hasTimeEdit(string4));
		System.out.println(pm.hasTimeEdit(string5));
	} 
	
}
