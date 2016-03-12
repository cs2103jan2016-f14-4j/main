package taskey.ui.content;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.util.Pair;
import taskey.logic.Task;	
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.content.formatters.UiActionFormatter;
import taskey.ui.content.formatters.UiCategoryFormatter;
import taskey.ui.content.formatters.UiDefaultFormatter;
import taskey.ui.UiConstants.ActionListMode;
import taskey.ui.utility.UiAnimationManager;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiGridSettings;

/**
 * This class acts as the interface for all content display related operations
 * It does the main job of setting up the UiFormatters
 * 
 * @author JunWei
 *
 */
public class UiContentManager {
	private ArrayList<UiFormatter> myFormatters; // for each content box

	public UiContentManager() {
		myFormatters = new ArrayList<UiFormatter>();
	}

	public void setUpContentBox(ScrollPane pane, ContentBox contentID) {
		assert(pane != null);
		UiFormatter myFormatter;
		switch (contentID) {
		case ACTION:
			myFormatter = new UiActionFormatter(pane);
			break;
		case CATEGORY:
			myFormatter = new UiCategoryFormatter(pane);
			break;
		default:
			myFormatter = new UiDefaultFormatter(pane);
			break;
		}
		myFormatters.add(myFormatter);
	}

	/**
	 * Generic update Content Box method, which would just call format on the formatters, regardless of grid type
	 * Used if the Content Box only has a single grid 
	 * @param myTaskList - list of tasks
	 * @param contentID - id of content box
	 */
	public void updateContentBox(ArrayList<Task> myTaskList, ContentBox contentID) {
		assert(myTaskList != null);
		UiFormatter myFormatter = myFormatters.get(contentID.getValue());
		myFormatter.format(myTaskList);
	}

	/**
	 * Update contents of action content box, depending on mode given
	 * @param myTaskList - which is the list of tasks
	 * @param mode - Content LIST, HELP
	 */
	public void updateActionContentBox(ArrayList<Task> myTaskList, ActionListMode mode) {
		assert(myTaskList != null);
		int arrayIndex = ContentBox.ACTION.getValue();
		UiActionFormatter myFormatter = (UiActionFormatter) myFormatters.get(arrayIndex);
		myFormatter.updateContents(myTaskList,mode);
	}
	
	public void updateCategoryContentBox(ArrayList<String> myCategoryList, ArrayList<Integer> categoryNums, ArrayList<Color> categoryColors) {
		assert(myCategoryList != null);
		assert(categoryNums != null);
		int arrayIndex = ContentBox.CATEGORY.getValue();
		UiCategoryFormatter myFormatter = (UiCategoryFormatter) myFormatters.get(arrayIndex);
		myFormatter.updateCategories(myCategoryList,categoryNums,categoryColors);
	}

	public void cleanUp() {
		for ( int i = 0; i < myFormatters.size(); i ++ ) {
			myFormatters.get(i).cleanUp();
		}
		myFormatters.clear();
	}
	
	public void processArrowKey(KeyEvent event, ContentBox currentContent) {
		assert(event != null);
		int arrayIndex = currentContent.getValue();
		UiFormatter myFormatter = myFormatters.get(arrayIndex);
		myFormatter.processArrowKey(event);
	}

	public int processDelete(ContentBox currentContent) {
		int arrayIndex = currentContent.getValue();
		UiFormatter myFormatter = myFormatters.get(arrayIndex);
		return myFormatter.processDeleteKey();
	}
}
