package taskey.ui.utility;

import java.util.HashMap;

import javafx.scene.image.Image;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.IMAGE_ID;

/**
 * This class handles loading of image resources 
 * which are likely to be permanent throughout the application life
 * @author Junwei
 *
 */
public class UiImageManager {

	private HashMap<IMAGE_ID, Image> myImageContainer = new HashMap<IMAGE_ID, Image>();
	private static UiImageManager instance = null;
	
	public static UiImageManager getInstance() {
		if ( instance == null ) {
			instance = new UiImageManager();
		}
		return instance;
	}
	
	public void loadImages() {
		try {
			myImageContainer.put(IMAGE_ID.WINDOW_ICON, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "windowIcon.png")));
			myImageContainer.put(IMAGE_ID.CROSS_DEFAULT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "crossDefault.png")));
			myImageContainer.put(IMAGE_ID.CROSS_SELECT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "crossSelect.png")));
			myImageContainer.put(IMAGE_ID.INBOX, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "inbox.png")));
			loadHelpMenuImages();
		} catch ( NullPointerException e ) {
			System.out.println("Images cant be loaded for some reason, please refresh the taskey.ui.images package");
		}
	}
	
	public Image getImage(IMAGE_ID id) {
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
		String helpFolder = UiConstants.UI_IMAGE_PATH_OFFSET + UiConstants.UI_IMAGE_HELP_PATH_OFFSET;
		// help add images
		myImageContainer.put(IMAGE_ID.ADD_FLOAT, 
				new Image(getClass().getResourceAsStream(helpFolder + "addFloat.png")));
		myImageContainer.put(IMAGE_ID.ADD_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "addDeadline.png")));
		myImageContainer.put(IMAGE_ID.ADD_DEADLINE_DATE, 
				new Image(getClass().getResourceAsStream(helpFolder + "addDeadlineDate.png")));
		myImageContainer.put(IMAGE_ID.ADD_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "addEvent.png")));
		myImageContainer.put(IMAGE_ID.ADD_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "addLast.png")));
		// help delete images
		myImageContainer.put(IMAGE_ID.DELETE_ID, 
				new Image(getClass().getResourceAsStream(helpFolder + "deleteID.png")));
		myImageContainer.put(IMAGE_ID.DELETE_NAME, 
				new Image(getClass().getResourceAsStream(helpFolder + "deleteName.png")));
		myImageContainer.put(IMAGE_ID.DELETE_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "deleteLast.png")));
		// help set images
		myImageContainer.put(IMAGE_ID.SET_ID_DATE, 
				new Image(getClass().getResourceAsStream(helpFolder + "setIDDate.png")));
		myImageContainer.put(IMAGE_ID.SET_ID_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "setIDEvent.png")));
		myImageContainer.put(IMAGE_ID.SET_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "setLast.png")));
		// help done images
		myImageContainer.put(IMAGE_ID.DONE_ID, 
				new Image(getClass().getResourceAsStream(helpFolder + "doneID.png")));
		myImageContainer.put(IMAGE_ID.DONE_NAME, 
				new Image(getClass().getResourceAsStream(helpFolder + "doneName.png")));
		myImageContainer.put(IMAGE_ID.DONE_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "doneLast.png")));
		// help search images
		myImageContainer.put(IMAGE_ID.SEARCH_NAME, 
				new Image(getClass().getResourceAsStream(helpFolder + "undo.png")));
		myImageContainer.put(IMAGE_ID.SEARCH_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "undoLast.png")));
		// help undo images
		myImageContainer.put(IMAGE_ID.UNDO, 
				new Image(getClass().getResourceAsStream(helpFolder + "undo.png")));
		myImageContainer.put(IMAGE_ID.UNDO_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "undoLast.png")));
		// help tag
		myImageContainer.put(IMAGE_ID.TAG, 
				new Image(getClass().getResourceAsStream(helpFolder + "tag.png")));
		myImageContainer.put(IMAGE_ID.TAG_LAST, 
				new Image(getClass().getResourceAsStream(helpFolder + "tagLast.png")));
		// help view
		myImageContainer.put(IMAGE_ID.VIEW_GENERAL, 
				new Image(getClass().getResourceAsStream(helpFolder + "tag.png")));
		myImageContainer.put(IMAGE_ID.VIEW_DEADLINE, 
				new Image(getClass().getResourceAsStream(helpFolder + "tagLast.png")));
		myImageContainer.put(IMAGE_ID.VIEW_EVENT, 
				new Image(getClass().getResourceAsStream(helpFolder + "tagLast.png")));
	}
}
