package taskey.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import taskey.ui.utility.UiGridSettings;

/**
 * 
 * Temporary file to be merged with Constants .java
 * @author JunWei
 *
 */
public class UiConstants {

	public enum ContentBox{
		WEEKlY(0), PENDING(1), EXPIRED(2), COMPLETED(3), ACTION(4);
		private final int value;
	    private ContentBox(int value) {
	        this.value = value;
	    }
	    public int getValue() {
	        return value;
	    }
	    public static ContentBox fromInteger(int x) {
	        switch(x) {
	        case 0:
	            return WEEKlY;
	        case 1:
	            return PENDING;
	        case 2:
	        	return EXPIRED;
	        case 3:
	        	return COMPLETED;
	        case 4:
	        	return ACTION;
	        }
	        return null;
	    }
	}
	
	public static final UiGridSettings normSettings = new UiGridSettings(10,10,10,90);
	public static final UiGridSettings weeklySettings = new UiGridSettings(10,5,100);
	
	public static final ArrayList<String> UI_STYLE_SHEETS = new ArrayList<String>(
			Arrays.asList("style.css","tabStyle.css","textStyles.css"));
	
	public static final int WORD_LIMIT_WEEKLIST = 10;
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
	
	public static final int UPDATE_INTERVAL = 1000; // in milliseconds
	public static final String PM_SUFFIX = "PM";
	public static final String AM_SUFFIX = "AM";
	
	public static final int DEFAULT_FADE_TIME = 1000; // How long to play the animation
	
}
