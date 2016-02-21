package taskey.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
	}
	
	public static final ArrayList<String> UI_STYLE_SHEETS = new ArrayList<String>(
			Arrays.asList("style.css"));
	
	public static final String STYLE_TAB_WINDOW = "stackpane";
	public static final int WORD_LIMIT_WEEKLIST = 10;
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
	
	public static final int UPDATE_INTERVAL = 1000; // in milliseconds
	public static final String PM_SUFFIX = "PM";
	public static final String AM_SUFFIX = "AM";
	
	public static final int DEFAULT_FADE_TIME = 1000; // How long to play the animation
	
}
