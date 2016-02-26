package taskey.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import taskey.ui.utility.UiGridSettings;

/**
 * 
 * Temporary file to be merged with Constants .java
 * 
 * @author JunWei
 *
 */
public class UiConstants {

	public enum ContentBox {
		WEEKLY(0), PENDING(1), EXPIRED(2), COMPLETED(3), ACTION(4);
		private final int value;

		private ContentBox(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static ContentBox fromInteger(int x) {
			switch (x) {
			case 0:
				return WEEKLY;
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

	public static final String UI_CSS_PATH_OFFSET = "css/";
	public static final UiGridSettings GRID_SETTINGS_WEEKLY = new UiGridSettings(0, 10, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_PENDING = new UiGridSettings(10, 15, 7, 7, 63, 30);
	public static final UiGridSettings GRID_SETTINGS_ACTION = new UiGridSettings(10, 15, 7, 7, 93);

	public static final ArrayList<String> UI_DEFAULT_STYLE = new ArrayList<String>(
			Arrays.asList("defaultStyle.css", "defaultTab.css", "defaultText.css"));
	public static final ArrayList<String> UI_LIGHT_STYLE = new ArrayList<String>(
			Arrays.asList("lightStyle.css"));

	public static final int WORD_LIMIT_WEEKLIST = 10;
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

	public static final int ClOCK_UPDATE_INTERVAL = 1000; // in milliseconds
	public static final String PM_SUFFIX = "PM";
	public static final String AM_SUFFIX = "AM";

	public static final int DEFAULT_FADE_TIME = 1000; // How long to play the animation
}
