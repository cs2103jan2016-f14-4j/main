package taskey.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.text.Font;
import taskey.ui.utility.UiGridSettings;

/**
 * 
 * Temporary file to be merged with Constants package
 * 
 * @author JunWei
 *
 */
public class UiConstants {

	// Messages
	public static final ArrayList<String> STATUS_MESSAGES = new ArrayList<String>(
			Arrays.asList("dummy","View Changed Successfully", "Added Task Successfully", "Delete Task Successfully",
						  "Updated Task Successfully", "Task marked as done", "Search returns a list of tasks",
						  "Error changing view", "Error adding task", "Error delete task", "Error updating task",
						  "Error marking task as done", "Error searching", "Error with undo"));
	
	// Content Boxes
	public enum ContentBox {
		THIS_WEEK(0), PENDING(1), EXPIRED(2), ACTION(3), CATEGORY(4);
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
				return THIS_WEEK;
			case 1:
				return PENDING;
			case 2:
				return EXPIRED;
			case 3:
				return ACTION;
			case 4:
				return CATEGORY;
			}
			return null;
		}
	}
	
	public enum IMAGE_ID {
		WINDOW_ICON, CROSS_DEFAULT, CROSS_SELECT, INBOX,
		ADD_FLOAT, ADD_DEADLINE, ADD_DEADLINE_DATE, ADD_EVENT, ADD_LAST,
		DELETE_ID, DELETE_NAME, DELETE_LAST,
		SET_ID_DATE, SET_ID_EVENT, SET_LAST,
		DONE_ID, DONE_NAME, DONE_LAST;
	}
	
	// For action 
	public enum ActionMode {
		LIST, HELP;
	}

	public static final UiGridSettings GRID_SETTINGS_DEFAULT = new UiGridSettings(5, 5, 0, 2, 98);
	public static final UiGridSettings GRID_SETTINGS_ACTION_LIST = new UiGridSettings(10, 15, 7, 7, 63, 30);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP = new UiGridSettings(0, 30, 0, 20, 80);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP_ADD = new UiGridSettings(0, 20, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_CATEGORY = new UiGridSettings(0, 10,0, 20,55,25);
	public static final UiGridSettings GRID_SETTINGS_DEFAULT_STACKPANE = new UiGridSettings(0, 0, 0, 80, 20);
	
	// Resources 
	public static final String UI_IMAGE_PATH_OFFSET = "../images/";
	public static final String UI_CSS_PATH_OFFSET = "css/";
	
	// Use of TextFlow for text, setting style of textflow only affects the container
	// every Text node has to have its own style (with other options besides Color)
	public static final String STYLE_TEXT_ALL = "textAll";
	public static final String STYLE_TEXT_RED = "textRed";
	public static final String STYLE_TEXT_BLACK = "textBlack";
	public static final String STYLE_TEXT_BLUE = "textBlue";
	public static final String STYLE_TEXT_GREEN = "textGreen";
	public static final String STYLE_TEXT_BLACK_TO_PURPLE = "textBlackToPurple";
	
	public static final String STYLE_NUMBER_ICON = "numberIcon";
	public static final String STYLE_GRAY_BOX = "grayBox";
	public static final String STYLE_WHITE_BOX = "whiteBox";
	public static final String STYLE_RED_BOX = "redBox";
	public static final String STYLE_PROMPT = "prompt";
	public static final String STYLE_PROMPT_SELECTED = "prompt-select";

	public static final ArrayList<String> STYLE_UI_DEFAULT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "defaultStyle.css", "defaultTab.css" ));
	public static final ArrayList<String> STYLE_UI_LIGHT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "lightStyle.css", "lightTab.css" ));
	
	// Others
	public static final int CHAR_LIMIT_WEEKLIST = 10;
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

	public static final int ClOCK_UPDATE_INTERVAL = 1000; // in milliseconds
	public static final String PM_SUFFIX = "PM";
	public static final String AM_SUFFIX = "AM";

	public static final int DEFAULT_FADE_TIME = 1000; // How long to play the animation

	
	
}
