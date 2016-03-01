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
		myImageContainer.put(IMAGE_ID.WINDOW_ICON, 
				new Image(getClass().getResourceAsStream(UiConstants.UI_IMAGE_PATH_OFFSET + "windowIcon.png")));
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
