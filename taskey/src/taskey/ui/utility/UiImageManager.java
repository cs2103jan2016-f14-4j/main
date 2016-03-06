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
		} catch ( NullPointerException e ) {
			System.out.println("Images cant be loaded for some reason, check constants");
		}
	}
	
	public Image getImage(IMAGE_ID id) {
		Image theImage = myImageContainer.get(id);
		if ( theImage == null ) {
			System.out.println("Image not found by" + id);
		}
		return theImage;
	}

	public void cleanUp() {
		myImageContainer.clear();
	}
}
