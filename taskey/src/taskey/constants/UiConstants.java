package taskey.constants;

import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import taskey.ui.content.UiGridSettings;

/**
 * @@author A0125419H
 * This file contains all the constants for UI
 * 
 * @author JunWei
 *
 */

public class UiConstants {

	// Initialization in UiMain
	public static final String PROGRAM_NAME = "Taskey";
	public static final String FXML_PATH = "layout.fxml";
	public static final String FXML_ALERT_PATH = "alert.fxml";
	public static final String FXML_LOAD_FAIL = "Fxml file not found";
	
	public static final Dimension WINDOW_MIN_SIZE = new Dimension(370,285); // half of original window size
	
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
	
	// Images
	public enum ImageID {
		WINDOW_ICON, CROSS_DEFAULT, CROSS_SELECT, MINUS_DEFAULT, MINUS_SELECT, URGENT_MARK, 
		FLOATING, DEADLINE, EVENT,
		ADD_FLOAT, ADD_DEADLINE, ADD_DEADLINE_DATE, ADD_EVENT, ADD_LAST,
		DELETE_ID, DELETE_NAME, DELETE_LAST,
		SET_ID_DATE, SET_ID_EVENT, SET_LAST,
		DONE_ID, DONE_NAME, DONE_LAST,
		SEARCH_NAME, SEARCH_LAST,
		UNDO, UNDO_LAST,
		TAG, TAG_LAST,
		VIEW_GENERAL, VIEW_DEADLINE, VIEW_EVENT;
	}
	
	// For action tab
	public enum ActionMode {
		LIST, HELP;
	}

	public static final UiGridSettings GRID_SETTINGS_DEFAULT = new UiGridSettings(1, 1, 1, 2, 98);
	public static final UiGridSettings GRID_SETTINGS_ENTRY_PANE = new UiGridSettings(0, 0, 0, 80, 20);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP = new UiGridSettings(0, 24, 5, 20, 80);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP_MENU = new UiGridSettings(0, 20, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_CATEGORY = new UiGridSettings(0, 1,0, 15,65,25);
	public static final UiGridSettings GRID_SETTINGS_SINGLE_CELL = new UiGridSettings(0, 0, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_ALERT = new UiGridSettings(0, 20, 15, 100);
	public static final UiGridSettings GRID_SETTINGS_ALERT_ENTRY_PANE = new UiGridSettings(0, 0, 0, 10, 75,15);
	
	// Resources 
	// Note that getResourceByStream uses relative Package directory. 
	// In this case, images has to be a sub package of utility for a runnable JAR to work
	public static final String UI_IMAGE_PATH_OFFSET = "images/";
	public static final String UI_IMAGE_HELP_PATH_OFFSET = "help/";
	public static final String UI_CSS_PATH_OFFSET = "css/";
	
	// Use of TextFlow for text, setting style of TextFlow only affects the container
	// every Text node has to have its own style (with other options besides Color)
	public static final String STYLE_TEXT_ALL = "textAll";
	public static final String STYLE_TEXT_DEFAULT = "textDefault";
	public static final String STYLE_TEXT_BLACK = "textBlack";
	public static final String STYLE_TEXT_RED = "textRed";
	public static final String STYLE_TEXT_BLUE = "textBlue";
	public static final String STYLE_TEXT_CATEGORY = "textCategory";
	public static final String STYLE_CATEGORY_BOX = "categoryBox";
	public static final String STYLE_DEFAULT_BOX = "defaultBox";
	public static final String STYLE_HIGHLIGHT_BOX = "highlightBox";
	public static final String STYLE_HELP_MENU_SELECTOR = "helpSelector";
	public static final String STYLE_PROMPT_DEFAULT = "prompt";
	public static final String STYLE_PROMPT_SELECTED = "promptSelect";
	public static final String STYLE_INPUT_NORMAL = "inputNormal";
	public static final String STYLE_INPUT_ERROR = "inputError";
	public static final String STYLE_ALERT_BOX = "alertBox";
	
	public static final ArrayList<String> STYLE_UI_DEFAULT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "defaultStyle.css", "defaultTab.css" ));
	public static final ArrayList<String> STYLE_UI_LIGHT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "lightStyle.css", "lightTab.css" ));
	public static final ArrayList<String> STYLE_UI_ALERT_WINDOW = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "alertStyles.css"));
	
	public static String STYLE_SHEETS_LOAD_FAIL = " loading style sheets";
	
	// Tray
	public static String TRAY_SHOW_OPTION = "Show Taskey";
	public static String TRAY_CLOSE_OPTION = "Close Program";
	public static String TRAY_CLOSE_NO_SAVE_OPTION = "Close Without Saving";
	public static String TRAY_IMAGE_PATH = "utility/images/windowIcon.png";
	public static String TRAY_IMAGE_LOAD_FAIL = "Failed to load tray icon";
	public static String MINIMIZE_MESSAGE_HEADER = "Taskey has been minimized.";
	public static String MINIMIZE_MESSAGE_BODY = "Taskey will continue running in the background, click to resume planning your tasks.";
	
	// Alert Window
	public static final int MAX_ALERTS = 10; 
	public static final float ALERTS_OPACITY = 0.8f;
	
	// Others
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
	public static final int UPDATE_SERVICE_INTERVAL = 300000; // in milliseconds (5 minutes)
	public static final int DEFAULT_FADE_TIME = 1000; // How long to play the animation for Fade only
	public static final int DEFAULT_FADE_START_DELAY = 2000; 
	public static final int MAX_INPUT_HISTORY = 5; // number of inputs to remember for uicontroller
}