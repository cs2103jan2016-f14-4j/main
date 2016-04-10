package taskey.ui.utility;

import java.util.EnumSet;
import java.util.HashMap;

import javafx.scene.image.Image;
import taskey.constants.UiConstants;

/**
 * @@author A0125419H
 * This class handles loading of image resources 
 * which are likely to be permanent throughout the application life
 * 
 * @author Junwei
 *
 */
public class UiImageManager {
	
	/**
	 * @@author A0121618M
	 * This class enumerates all the images used in the GUI.
	 * It encapsulates the filenames of each image, and in particular, the captions associated with each help image.
	 * For convenience, static EnumSets for each category of help image are provided;
	 * these can be accessed through public getter methods.
	 */
	public enum ImageID {
		WINDOW_ICON		("windowIcon.png"),
		CROSS_DEFAULT	("crossDefault.png"),
		CROSS_SELECT	("crossSelect.png"),
		MINUS_DEFAULT	("minusDefault.png"),
		MINUS_SELECT	("minusSelect.png"),
		URGENT_MARK		("urgentMark.png"),
		FLOATING		("floating.png"),
		DEADLINE		("deadline.png"),
		EVENT			("event.png"),
		
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
		
		private static EnumSet<ImageID> helpImages = EnumSet.range(ADD1_FLOAT, VIEW_EVENT);
		private static EnumSet<ImageID> helpImages_Add = EnumSet.range(ADD1_FLOAT, ADD4_EVENT);
		private static EnumSet<ImageID> helpImages_Del = EnumSet.range(DELETE1_ID, DELETE2);
		private static EnumSet<ImageID> helpImages_Edit = EnumSet.range(SET1_NONE, SET7_BOTH);
		private static EnumSet<ImageID> helpImages_Done  = EnumSet.range(DONE1, DONE2);
		private static EnumSet<ImageID> helpImages_Search = EnumSet.range(SEARCH1, SEARCH2);
		private static EnumSet<ImageID> helpImages_Undo = EnumSet.range(UNDO1, UNDO2);
		private static EnumSet<ImageID> helpImages_Tag = EnumSet.range(TAG1, TAG3);
		private static EnumSet<ImageID> helpImages_View = EnumSet.range(VIEW_BASIC, VIEW_EVENT);
		
		private final String filename;
		private final String caption;
		
		// Constructor for the UI element images
		private ImageID(String filename) {
			this.filename = filename;
			this.caption = "";
		}
		
		// Constructor for the help images
		private ImageID(String filename, String helpText) {
			this.filename = filename;
			this.caption = helpText;
		}
		
		public String getCaption() {
			return caption;
		}
		
		public static EnumSet<ImageID> getHelpImageSet_Add() {
			return helpImages_Add;
		}
		public static EnumSet<ImageID> getHelpImageSet_Del() {
			return helpImages_Del;
		}
		public static EnumSet<ImageID> getHelpImageSet_Edit() {
			return helpImages_Edit;
		}
		public static EnumSet<ImageID> getHelpImageSet_Done() {
			return helpImages_Done;
		}
		public static EnumSet<ImageID> getHelpImageSet_Search() {
			return helpImages_Search;
		}
		public static EnumSet<ImageID> getHelpImageSet_Undo() {
			return helpImages_Undo;
		}
		public static EnumSet<ImageID> getHelpImageSet_Tag() {
			return helpImages_Tag;
		}
		public static EnumSet<ImageID> getHelpImageSet_View() {
			return helpImages_View;
		}
	}

	//@@author A0125419H
	private String helpFolder = UiConstants.UI_IMAGE_PATH_OFFSET + UiConstants.UI_IMAGE_HELP_PATH_OFFSET; // path
	private HashMap<ImageID, Image> myImageContainer = new HashMap<ImageID, Image>();
	
	private static UiImageManager instance = null;
	private UiImageManager() {
	}
	public static UiImageManager getInstance() {
		if ( instance == null ) {
			instance = new UiImageManager();
		}
		return instance;
	}
	
	public void loadImages() {
		try {
			myImageContainer.put(ImageID.WINDOW_ICON, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.WINDOW_ICON.filename)));
			myImageContainer.put(ImageID.CROSS_DEFAULT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.CROSS_DEFAULT.filename)));
			myImageContainer.put(ImageID.CROSS_SELECT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.CROSS_SELECT.filename)));
			myImageContainer.put(ImageID.MINUS_DEFAULT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.MINUS_DEFAULT.filename)));
			myImageContainer.put(ImageID.MINUS_SELECT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.MINUS_SELECT.filename)));
			myImageContainer.put(ImageID.URGENT_MARK, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.URGENT_MARK.filename)));
			myImageContainer.put(ImageID.FLOATING, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.FLOATING.filename)));
			myImageContainer.put(ImageID.DEADLINE, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.DEADLINE.filename)));
			myImageContainer.put(ImageID.EVENT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + ImageID.EVENT.filename)));
			
			loadHelpMenuImages();
			
		} catch ( NullPointerException e ) {
			System.out.println("Images cant be loaded for some reason, please refresh the taskey.ui.images package");
		}
	}
	
	/**
	 * @@author A0121618M
	 */
	private void loadHelpMenuImages() {
		for (ImageID helpImageID : ImageID.helpImages) {
			myImageContainer.put(helpImageID, 
					new Image(getClass().getResourceAsStream(helpFolder + helpImageID.filename)));
		}
	}
	
	/**
	 * @@author A0125419H
	 */
	public Image getImage(ImageID id) {
		Image theImage = myImageContainer.get(id);
		if ( theImage == null ) {
			System.out.println("Image not found by " + id);
		}
		return theImage;
	}

	public void cleanUp() {
		myImageContainer.clear();
	}
}
