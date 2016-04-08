package taskey.constants;

import java.awt.Dimension;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import taskey.ui.utility.UiGridSettings;

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
	// @@author A0121618M
	public enum ImageID {
		WINDOW_ICON, CROSS_DEFAULT, CROSS_SELECT, MINUS_DEFAULT, MINUS_SELECT, URGENT_MARK, 
		FLOATING, DEADLINE, EVENT,
		
		// Help images
		ADD1_FLOAT			("add1_Float.png", "Type: add <task name> to add a general task"),
		ADD2_DEADLINE_TMR	("add2_DeadlineTmr.png", "Type: add <task name> on/by <date> to add a deadline."),
		ADD3_DEADLINE_DATE	("add3_DeadlineDate.png", "<date> can also be a calendar date in (D)D MMM (YYYY) format"),
		ADD4_EVENT			("add4_Event.png", "Type: add <task name> from <date> to <date> to add an event.\n Press Enter to return"),
		
		DELETE1_ID	("delete1_ID.png", "Type: del <ID> to delete a task; ID is shown on the left."),
		DELETE2		("delete2.png", "That's it! Press Enter to return"),
		
		SET1_NONE		("set1_none.png", "Type: set <ID> [none] to remove a task's date(s)"),
		SET2_NONE		("set2_none.png", "The task has been changed to a general task"),
		SET3_DEADLINE	("set3_deadline.png", "Type: set <ID> [date] to set a task to have one date"),
		SET4_DEADLINE	("set4_deadline.png", "The task has been changed to a deadline"),
		SET5_NAME		("set5_name.png", "Type: set \"Name\" to change a task's name"),
		SET6_EVENT		("set6_event.png", "Type: set [start date, end date] to set an event"),
		SET7_BOTH		("set7_both.png", "Type: set \"Name\" [date] to change both the name and date at the same time. Press Enter to return"),
		
		DONE1	("done1.png", "Type: done <ID> to move a task to the archive"),
		DONE2	("done2.png", "That's it! Press Enter to return"),
		
		SEARCH1	("search1.png", "Type: search <phrase> to search for a task"),
		SEARCH2	("search2.png",  "Search results will be shown in the Action tab.\n Press Enter to return"),
		
		UNDO1	("undo1.png", "Type: undo to revert any changes made to your tasks"),
		UNDO2	("undo2.png", "That's it! Press Enter to return"),
		
		TAG1	("tag1.png", "When adding a task, type #<tag> at the end to tag it"),
		TAG2	("tag2.png", "The number of tags will be shown in the left pane"),
		TAG3	("tag3.png", "Type: view #<tag> to view all tasks with the given tag.\n Press Enter to return"),
		
		VIEW_BASIC		("view1_basic.png", "You can filter the pending tab by task type: general, deadlines and events"),
		VIEW_GENERAL	("view2_general.png", "Type: view general, to view pending general tasks"),
		VIEW_DEADLINE	("view3_deadlines.png", "Type: view deadlines, to view pending deadlines"),
		VIEW_EVENT		("view4_events.png", "Type: view events to view pending events.\n Press Enter to return");
		
		public static EnumSet<ImageID> helpImages_All = EnumSet.range(ADD1_FLOAT, VIEW_EVENT);
		
		public static EnumSet<ImageID> helpImages_Add = EnumSet.range(ADD1_FLOAT, ADD4_EVENT);
		public static EnumSet<ImageID> helpImages_Del = EnumSet.range(DELETE1_ID, DELETE2);
		public static EnumSet<ImageID> helpImages_Set = EnumSet.range(SET1_NONE, SET7_BOTH);
		public static EnumSet<ImageID> helpImages_Done  = EnumSet.range(DONE1, DONE2);
		public static EnumSet<ImageID> helpImages_Search = EnumSet.range(SEARCH1, SEARCH2);
		public static EnumSet<ImageID> helpImages_Undo = EnumSet.range(UNDO1, UNDO2);
		public static EnumSet<ImageID> helpImages_Tag = EnumSet.range(TAG1, TAG3);
		public static EnumSet<ImageID> helpImages_View = EnumSet.range(VIEW_BASIC, VIEW_EVENT);
		
		private String filename;
		private String caption;
		
		private ImageID(String filename, String helpText) {
			this.filename = filename;
			this.caption = helpText;
		}
		
		private ImageID() {
		}
		
		public String getFilename() {
			return filename;
		}
		
		public String getCaption() {
			return caption;
		}
	}
	
	// For action tab
	public enum ActionMode {
		LIST, HELP;
	}
	public static final String ACTION_MODE_INVALID = "Mode invalid For ActionFormatter";
	
	// Grid settings
	public static final UiGridSettings GRID_SETTINGS_DEFAULT = new UiGridSettings(1, 1, 3, 5, 98);
	public static final UiGridSettings GRID_SETTINGS_ENTRY_PANE = new UiGridSettings(0, 0, 0, 80, 20);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP = new UiGridSettings(0, 20, 10, 25, 75);
	public static final UiGridSettings GRID_SETTINGS_ACTION_HELP_MENU = new UiGridSettings(0, 20, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_CATEGORY = new UiGridSettings(0, 1,0, 15,65,25);
	public static final UiGridSettings GRID_SETTINGS_SINGLE_CELL = new UiGridSettings(0, 0, 0, 100);
	public static final UiGridSettings GRID_SETTINGS_ALERT = new UiGridSettings(0, 20, 0, 100);
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
	public static final String STYLE_INPUT_CORRECT = "inputCorrect";
	public static final String STYLE_ALERT_BOX = "alertBox";
	
	public static final ArrayList<String> STYLE_UI_DEFAULT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "defaultStyle.css", "defaultTab.css" ));
	public static final ArrayList<String> STYLE_UI_LIGHT = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "lightStyle.css", "lightTab.css" ));
	public static final ArrayList<String> STYLE_UI_ALERT_WINDOW = new ArrayList<String>(
			Arrays.asList("sharedStyles.css", "alertStyles.css"));
	public static final String STYLE_SHEETS_LOAD_FAIL = " loading style sheets";
	
	// Tray
	public static final String TRAY_SHOW_OPTION = "Show Taskey";
	public static final String TRAY_CLOSE_OPTION = "Close Program";
	public static final String TRAY_CLOSE_NO_SAVE_OPTION = "Close Without Saving";
	public static final String TRAY_IMAGE_PATH = "utility/images/windowIcon.png";
	public static final String TRAY_IMAGE_LOAD_FAIL = "Failed to load tray icon";
	public static final String MINIMIZE_MESSAGE_HEADER = "Taskey has been minimized.";
	public static final String MINIMIZE_MESSAGE_BODY = "Taskey will continue running in the background, click to resume planning your tasks.";
	
	// Alert Window
	public static final int MAX_ALERTS = 10; 
	public static final float ALERTS_OPACITY = 0.8f;
	public static final int[] HOUR_MARKS = { 1, 3, 5, 12 };
	
	// Others
	public static final DateFormat CLOCK_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
	public static final int UPDATE_SERVICE_INTERVAL = 300000; // in milliseconds (5 minutes)
	public static final int DEFAULT_ANIM_DURATION = 1000; // How long to play an animation
	public static final int DEFAULT_FADE_START_DELAY = 2000; 
	public static final int DEFAULT_SHAKE_DISTANCE = 10;
	public static final int DEFAULT_SHAKE_INTERVAL = 100;
	public static final int MAX_INPUT_HISTORY = 5; // number of inputs to remember for uicontroller
	public static final int ENTRIES_PER_PAGE_DEFAULT = 5;
	public static final int ENTRIES_PER_PAGE_HELP_MENU = 6;
	public static final int WINDOW_RESIZE_PICK_BOUNDS = 10;
}