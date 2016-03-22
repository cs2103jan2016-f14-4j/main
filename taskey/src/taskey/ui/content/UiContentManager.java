package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import taskey.constants.Triplet;
import taskey.constants.UiConstants.ActionMode;
import taskey.constants.UiConstants.ContentBox;
import taskey.logic.Task;
import taskey.ui.content.formatters.UiActionFormatter;
import taskey.ui.content.formatters.UiCategoryFormatter;
import taskey.ui.content.formatters.UiDefaultFormatter;

/**
 * @@author A0125419H
 * This class acts as the interface for all content display related operations
 * It does the main job of setting up the UiFormatters, and redirecting input from UiController.
 *
 * @author JunWei
 */

public class UiContentManager {
	private ArrayList<Triplet<Color,String,Integer>> myCategoryList; // for using to set colors
	private ArrayList<UiFormatter> myFormatters; // for each content box

	public UiContentManager() {
		myFormatters = new ArrayList<UiFormatter>();
	}

	/**
	 * Sets up the UiFormatters for each pane
	 *
	 * @param pane - the pane
	 * @param contentID - the content id
	 */
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
	 * Used if the Content Box only has a single grid .
	 *
	 * @param myTaskList - list of tasks
	 * @param contentID - id of content box
	 */
	public void updateContentBox(ArrayList<Task> myTaskList, ContentBox contentID) {
		assert(myTaskList != null);
		UiFormatter myFormatter = myFormatters.get(contentID.getValue());
		myFormatter.setCategories(myCategoryList);
		myFormatter.format(myTaskList);
	}

	/**
	 * Update contents of action content box, depending on mode given.
	 *
	 * @param myTaskList - which is the list of tasks if applicable
	 * @param mode - Content LIST, HELP
	 */
	public void updateActionContentBox(ArrayList<Task> myTaskList, ActionMode mode) {
		int arrayIndex = ContentBox.ACTION.getValue();
		UiActionFormatter myFormatter = (UiActionFormatter) myFormatters.get(arrayIndex);
		myFormatter.setCategories(myCategoryList);
		myFormatter.updateContents(myTaskList,mode);
	}
	
	/**
	 * Update category content box.
	 *
	 * @param myCategoryList - the category list
	 * @param categoryNums - the category nums
	 * @param categoryColors - the category colors
	 */
	public void updateCategoryContentBox(ArrayList<Triplet<Color,String,Integer>> categoryList) {
		assert(categoryList != null);
		myCategoryList = categoryList;
		int arrayIndex = ContentBox.CATEGORY.getValue();
		UiCategoryFormatter myFormatter = (UiCategoryFormatter) myFormatters.get(arrayIndex);
		myFormatter.updateCategories(categoryList);
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

	public void processEnter(ContentBox currentContent) {
		int arrayIndex = currentContent.getValue();
		UiFormatter myFormatter = myFormatters.get(arrayIndex);
		myFormatter.processEnterKey();
	}
	
	public void processPageUpAndDown(KeyEvent event, ContentBox currentContent) {
		int arrayIndex = currentContent.getValue();
		UiFormatter myFormatter = myFormatters.get(arrayIndex);
		myFormatter.processPageUpAndDown(event);
	}
}
