package taskey.ui.utility;

import java.util.HashMap;

import javafx.scene.image.Image;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ImageID;

/**
 * @@author A0125419H
 * This class handles loading of image resources 
 * which are likely to be permanent throughout the application life
 * @author Junwei
 *
 */

public class UiImageManager {
	private String helpFolder = UiConstants.UI_IMAGE_PATH_OFFSET + UiConstants.UI_IMAGE_HELP_PATH_OFFSET;
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
	
	public void loadHelpMenuImages() {	
		loadAddMenuImages();
		loadDeleteMenuImages();
		loadSetMenuImages();
		loadDoneMenuImages();
		loadSearchMenuImages();
		loadUndoMenuImages();
		loadTagMenuImages();
		loadViewMenuImages();
	}
	
	private void loadAddMenuImages() {
		myImageContainer.put(ImageID.ADD_FLOAT, 
				new Image(getClass().getResourceAsStream(helpFolder + "addFloat.png")));
		myImageContainer.put(ImageID.ADD_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "addDeadline.png")));
		myImageContainer.put(ImageID.ADD_DEADLINE_DATE, 
				new Image(getClass().getResourceAsStream(helpFolder + "addDeadlineDate.png")));
		myImageContainer.put(ImageID.ADD_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "addEvent.png")));
		myImageContainer.put(ImageID.ADD_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "addLast.png")));
	}
	private void loadDeleteMenuImages() {
		myImageContainer.put(ImageID.DELETE_ID, 
				new Image(getClass().getResourceAsStream(helpFolder + "deleteID.png")));
		myImageContainer.put(ImageID.DELETE_NAME, 
				new Image(getClass().getResourceAsStream(helpFolder + "deleteName.png")));
		myImageContainer.put(ImageID.DELETE_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "deleteLast.png")));
	}
	private void loadSetMenuImages() {
		myImageContainer.put(ImageID.SET_ID_DATE, 
				new Image(getClass().getResourceAsStream(helpFolder + "setIDDate.png")));
		myImageContainer.put(ImageID.SET_ID_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "setIDEvent.png")));
		myImageContainer.put(ImageID.SET_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "setLast.png")));
	}
	private void loadDoneMenuImages() {
		myImageContainer.put(ImageID.DONE_ID, 
				new Image(getClass().getResourceAsStream(helpFolder + "doneID.png")));
		myImageContainer.put(ImageID.DONE_NAME, 
				new Image(getClass().getResourceAsStream(helpFolder + "doneName.png")));
		myImageContainer.put(ImageID.DONE_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "doneLast.png")));
	}
	private void loadSearchMenuImages() {
		myImageContainer.put(ImageID.SEARCH_NAME, 
				new Image(getClass().getResourceAsStream(helpFolder + "undo.png")));
		myImageContainer.put(ImageID.SEARCH_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "undoLast.png")));
	}
	private void loadUndoMenuImages() {
		myImageContainer.put(ImageID.UNDO, 
				new Image(getClass().getResourceAsStream(helpFolder + "undo.png")));
		myImageContainer.put(ImageID.UNDO_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "undoLast.png")));
	}
	private void loadTagMenuImages() {
		myImageContainer.put(ImageID.TAG, 
				new Image(getClass().getResourceAsStream(helpFolder + "tag.png")));
		myImageContainer.put(ImageID.TAG_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "tagLast.png")));
	}
	private void loadViewMenuImages() {
		myImageContainer.put(ImageID.VIEW_GENERAL, 
				new Image(getClass().getResourceAsStream(helpFolder + "tag.png")));
		myImageContainer.put(ImageID.VIEW_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "tagLast.png")));
		myImageContainer.put(ImageID.VIEW_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "tagLast.png")));
	}
}
