package taskey.ui.utility;

import java.util.HashMap;

import javafx.scene.image.Image;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ImageID;

/**
 * @@author A0125419H
 * This class handles loading of image resources 
 * which are likely to be permanent throughout the application life
 * 
 * @author Junwei
 *
 */

public class UiImageManager {
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
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "windowIcon.png")));
			myImageContainer.put(ImageID.CROSS_DEFAULT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "crossDefault.png")));
			myImageContainer.put(ImageID.CROSS_SELECT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "crossSelect.png")));
			myImageContainer.put(ImageID.MINUS_DEFAULT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "minusDefault.png")));
			myImageContainer.put(ImageID.MINUS_SELECT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "minusSelect.png")));
			myImageContainer.put(ImageID.URGENT_MARK, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "urgentMark.png")));
			myImageContainer.put(ImageID.FLOATING, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "floating.png")));
			myImageContainer.put(ImageID.DEADLINE, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "deadline.png")));
			myImageContainer.put(ImageID.EVENT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "event.png")));
			
			loadHelpMenuImages();
			
		} catch ( NullPointerException e ) {
			System.out.println("Images cant be loaded for some reason, please refresh the taskey.ui.images package");
		}
	}
	
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
	
	/**
	 * @@author A0121618M
	 */
	private void loadHelpMenuImages() {
/*		loadAddMenuImages();
		loadDeleteMenuImages();
		loadSetMenuImages();
		loadDoneMenuImages();
		loadSearchMenuImages();
		loadUndoMenuImages();
		loadTagMenuImages();
		loadViewMenuImages();*/
		
		for (ImageID helpImageID : ImageID.helpImages_All) {
			myImageContainer.put(helpImageID, 
					new Image(getClass().getResourceAsStream(helpFolder + helpImageID.getFilename())));
		}
	}
	
/*	private void loadAddMenuImages() {
		myImageContainer.put(ImageID.ADD1_FLOAT, 
				new Image(getClass().getResourceAsStream(helpFolder + "add1_Float.png")));
		myImageContainer.put(ImageID.ADD2_DEADLINE_TMR, 
				new Image(getClass().getResourceAsStream(helpFolder + "add2_DeadlineTmr.png")));
		myImageContainer.put(ImageID.ADD3_DEADLINE_DATE, 
				new Image(getClass().getResourceAsStream(helpFolder + "add3_DeadlineDate.png")));
		myImageContainer.put(ImageID.ADD4_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "add4_Event.png")));
	}
	private void loadDeleteMenuImages() {
		myImageContainer.put(ImageID.DELETE1_ID, 
				new Image(getClass().getResourceAsStream(helpFolder + "delete1_ID.png")));
		myImageContainer.put(ImageID.DELETE2, 
				new Image(getClass().getResourceAsStream(helpFolder + "delete2.png")));
	}
	private void loadSetMenuImages() {
		myImageContainer.put(ImageID.SET1_NONE, 
				new Image(getClass().getResourceAsStream(helpFolder + "set1_none.png")));
		myImageContainer.put(ImageID.SET2_NONE, 
				new Image(getClass().getResourceAsStream(helpFolder + "set2_none.png")));
		myImageContainer.put(ImageID.SET3_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "set3_deadline.png")));
		myImageContainer.put(ImageID.SET3_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "set4_deadline.png")));
		myImageContainer.put(ImageID.SET3_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "set5_name.png")));
		myImageContainer.put(ImageID.SET3_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "set6_event.png")));
		myImageContainer.put(ImageID.SET3_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "set7_both.png")));
	}
	private void loadDoneMenuImages() {
		myImageContainer.put(ImageID.DONE_ID, 
				new Image(getClass().getResourceAsStream(helpFolder + "done1.png")));
		myImageContainer.put(ImageID.DONE_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "done2.png")));
	}
	private void loadSearchMenuImages() {
		myImageContainer.put(ImageID.SEARCH_NAME, 
				new Image(getClass().getResourceAsStream(helpFolder + "search1.png")));
		myImageContainer.put(ImageID.SEARCH_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "search2.png")));
	}
	private void loadUndoMenuImages() {
		myImageContainer.put(ImageID.UNDO, 
				new Image(getClass().getResourceAsStream(helpFolder + "undo1.png")));
		myImageContainer.put(ImageID.UNDO_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "undo2.png")));
	}
	private void loadTagMenuImages() {
		myImageContainer.put(ImageID.TAG, 
				new Image(getClass().getResourceAsStream(helpFolder + "tag1.png")));
		myImageContainer.put(ImageID.TAG_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "tag2.png")));
	}
	private void loadViewMenuImages() {
		myImageContainer.put(ImageID.VIEW_BASIC, 
				new Image(getClass().getResourceAsStream(helpFolder + "view1_basic.png")));
		myImageContainer.put(ImageID.VIEW_GENERAL, 
				new Image(getClass().getResourceAsStream(helpFolder + "view2_general.png")));
		myImageContainer.put(ImageID.VIEW_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "view3_deadlines.png")));
		myImageContainer.put(ImageID.VIEW_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "view4_events.png")));
	}*/
}
