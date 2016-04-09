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
		for (ImageID helpImageID : ImageID.helpImages_All) {
			myImageContainer.put(helpImageID, 
					new Image(getClass().getResourceAsStream(helpFolder + helpImageID.getFilename())));
		}
	}
}
