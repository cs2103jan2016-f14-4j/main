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
	private String pattern2 = "on \\w{1,3} from \\d{1,2}(:|.)?\\d{0,2}(am|pm) to \\d{1,2}(:|.)?\\d{0,2}(am|pm)";
	private String pattern3 = "at \\d{1,2}(:|.)?\\d{0,2}(am|pm) by"; 
	
	public DateTimePatternMatcher() {
		
	}
	
	
	public boolean hasPattern(String input) {
		Pattern p = Pattern.compile(pattern1);
		Matcher m = p.matcher(input);
		
		Pattern p2 = Pattern.compile(pattern2);
		Matcher m2 = p2.matcher(input);
		
		Pattern p3 = Pattern.compile(pattern3);
		Matcher m3 = p3.matcher(input);
		
		while (m.find()) {
			System.out.println("Found a match.");
			return true;
		}
		
		while (m2.find()) {
			System.out.println("Found a match.");
			return true;
		}
		
		while (m3.find()) {
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
