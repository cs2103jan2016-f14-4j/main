package taskey.constants;

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

	public static final String PROGRAM_NAME = "Taskey";
	public static final String FXML_PATH = "layout.fxml";
	public static final String FXML_LOAD_FAIL = "Fxml file not found";
	
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
	public enum IMAGE_ID {
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
	
	// For action 
	public enum ActionMode {
		LIST, HELP;
	}

	public static final UiGridSettings GRID_SETTINGS_DEFAULT = new UiGridSettings(1, 1, 1, 2, 98);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP = new UiGridSettings(0, 24, 0, 20, 80);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP_MENU = new UiGridSettings(0, 20, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_CATEGORY = new UiGridSettings(0, 1,0, 15,65,25);
	public static final UiGridSettings GRID_SETTINGS_SINGLE = new UiGridSettings(0, 0, 0, 100);
	// Resources 
	// Not that getResourceByStream uses relative Package directory. 
	// In this case, images has to be a sub package of utility for a runnable JAR to work
	public static final String UI_IMAGE_PATH_OFFSET = "images/";
	public static final String UI_IMAGE_HELP_PATH_OFFSET = "help/";
	public static final String UI_CSS_PATH_OFFSET = "css/";
	
	// Use of TextFlow for text, setting style of TextFlow only affects the container
	// every Text node has to have its own style (with other options besides Color)
	public static final String STYLE_TEXT_ALL = "textAll";
	public static final String STYLE_TEXT_DEFAULT = "textDefault";
	public static final String STYLE_TEXT_RED = "textRed";
	public static final String STYLE_TEXT_BLACK = "textBlack";
	public static final String STYLE_TEXT_BLUE = "textBlue";
	public static final String STYLE_TEXT_CATEGORY = "textCategory";
	public static final String STYLE_CATEGORY_BOX = "categoryBox";
	public static final String STYLE_DEFAULT_BOX = "defaultBox";
	public static final String STYLE_HIGHLIGHT_BOX = "highlightBox";
	public static final String STYLE_ELLIPSE = "ellipse";
	public static final String STYLE_PROMPT = "prompt";
	public static final String STYLE_PROMPT_SELECTED = "promptSelect";
	public static final String STYLE_INPUT_NORMAL = "inputNormal";
	public static final String STYLE_INPUT_ERROR = "inputError";
	
	public static final ArrayList<String> STYLE_UI_DEFAULT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "defaultStyle.css", "defaultTab.css" ));
	public static final ArrayList<String> STYLE_UI_LIGHT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "lightStyle.css", "lightTab.css" ));
	
	// Others
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
	public static final int ClOCK_UPDATE_INTERVAL = 1000; // in milliseconds
	public static final String PM_SUFFIX = "PM";
	public static final String AM_SUFFIX = "AM";
	public static final int DEFAULT_FADE_TIME = 1000; // How long to play the animation (mostly for feedback pop up)
}
