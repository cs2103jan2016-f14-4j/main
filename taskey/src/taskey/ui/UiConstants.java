package taskey.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.text.Font;
import taskey.ui.utility.UiGridSettings;

/**
 * 
 * Temporary file to be merged with Constants .java
 * 
 * @author JunWei
 *
 */
public class UiConstants {

	// Content Boxes
	public enum ContentBox {
		WEEKLY(0), PENDING(1), EXPIRED(2), ACTION(3);
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
				return ACTION;
			}
			return null;
		}
	}
	
	public enum IMAGE_ID {
		WINDOW_ICON;
	}
	
	// For action List
	public enum ActionContentMode {
		TASKLIST, HELP_MAIN, HELP_ADD, HELP_DEL;
	}

	public static final UiGridSettings GRID_SETTINGS_WEEKLY = new UiGridSettings(0, 10, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_PENDING = new UiGridSettings(10, 15, 7, 7, 63, 30);
	public static final UiGridSettings GRID_SETTINGS_ACTION_LISTVIEW = new UiGridSettings(10, 15, 7, 7, 93);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELPVIEW = new UiGridSettings(0, 10, 0, 100);

	// Resources 
	public static final String UI_IMAGE_PATH_OFFSET = "../images/";
	public static final String UI_CSS_PATH_OFFSET = "css/";
	
	public static final Font UI_DEFAULT_FONT = new Font("Serif",13);
	// Use of TextFlow for text, setting style of textflow only affects the container
	// every Text node has to have its own style (with other options besides Color)
	public static final String STYLE_TEXT_RED = "textRed";
	public static final String STYLE_TEXT_BLACK = "textBlack";
	public static final String STYLE_TEXT_BLUE = "textBlue";
	public static final String STYLE_TEXT_GREEN = "textGreen";
	public static final String STYLE_TEXT_BLACK_TO_PURPLE = "textBlackToPurple";
	
	public static final String STYLE_NUMBER_ICON = "numberIcon";
	public static final String STYLE_WHITE_BOX = "whiteBox";
	public static final String STYLE_RED_BOX = "redBox";
	public static final String STYLE_PROMPT = "prompt";
	public static final String STYLE_PROMPT_SELECTED = "prompt-select";

	public static final ArrayList<String> STYLE_UI_DEFAULT = new ArrayList<String>(
			Arrays.asList("defaultStyle.css", "defaultTab.css", "sharedStyles.css"));
	public static final ArrayList<String> STYLE_UI_LIGHT = new ArrayList<String>(
			Arrays.asList("lightStyle.css", "lightTab.css", "sharedStyles.css"));
	
	// Others
	public static final int CHAR_LIMIT_WEEKLIST = 10;
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("EE: d MMMMM");

	public static final int ClOCK_UPDATE_INTERVAL = 1000; // in milliseconds
	public static final String PM_SUFFIX = "PM";
	public static final String AM_SUFFIX = "AM";

	public static final int DEFAULT_FADE_TIME = 1000; // How long to play the animation

	
	
}
