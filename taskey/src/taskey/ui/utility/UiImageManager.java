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
			// help add images
			myImageContainer.put(IMAGE_ID.ADD_FLOAT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "addFloat.png")));
			myImageContainer.put(IMAGE_ID.ADD_DEADLINE, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "addDeadline.png")));
			myImageContainer.put(IMAGE_ID.ADD_DEADLINE_DATE, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "addDeadlineDate.png")));
			myImageContainer.put(IMAGE_ID.ADD_EVENT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "addEvent.png")));
			myImageContainer.put(IMAGE_ID.ADD_LAST, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "addLast.png")));
			// help delete images
			myImageContainer.put(IMAGE_ID.DELETE_ID, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "deleteID.png")));
			myImageContainer.put(IMAGE_ID.DELETE_NAME, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "deleteName.png")));
			myImageContainer.put(IMAGE_ID.DELETE_LAST, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "deleteLast.png")));
			// help set images
			myImageContainer.put(IMAGE_ID.SET_ID_DATE, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "setIDDate.png")));
			myImageContainer.put(IMAGE_ID.SET_ID_EVENT, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "setIDEvent.png")));
			myImageContainer.put(IMAGE_ID.SET_LAST, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "setLast.png")));
			// help done images
			myImageContainer.put(IMAGE_ID.DONE_ID, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "doneID.png")));
			myImageContainer.put(IMAGE_ID.DONE_NAME, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "doneName.png")));
			myImageContainer.put(IMAGE_ID.DONE_LAST, 
					new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "doneLast.png")));
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
}
