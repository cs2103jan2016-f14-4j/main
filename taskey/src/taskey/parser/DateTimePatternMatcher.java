package taskey.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will check the date/time pattern of the user's input
 * and decide if the format falls into its blacklist, as specified
 * by a regex (regular expression). If it does,
 * return an Error object to tell the user to key in the date in some
 * other format. 
 * @author Xue Hui
 *
 */
public class DateTimePatternMatcher {
	private String pattern1 = "by \\d{1,2}(:|.)?\\d{0,2} ?(am|pm) on"; 
	
	public DateTimePatternMatcher() {
		
	}
	
	
	public boolean hasPattern(String input) {
		Pattern p = Pattern.compile(pattern1);
		Matcher m = p.matcher(input);
		
		while (m.find()) {
			System.out.println("Found a match.");
			return true;
		}
		return false; 
	}
	
	public static void main(String[] args) {
		DateTimePatternMatcher pm = new DateTimePatternMatcher(); 
		String myString = "add project meeting by 20 pm on 17 feb";
		
		System.out.println(pm.hasPattern(myString));
	}
	
	
}
