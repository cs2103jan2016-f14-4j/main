package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import taskey.constants.Triplet;
import taskey.constants.UiConstants;
import taskey.logic.Task;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiTextBuilder;

/**
 * @@author A0125419H
 * This class is responsible for formatting the Category box
 * 
 * @author junwei
 */

public class UiCategoryFormatter extends UiFormatter {

	public UiCategoryFormatter(ScrollPane thePane) {
		super(thePane);
		setGrid(gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_CATEGORY));
		mainPane.setContent(currentGrid);
	}

	public void updateCategories(ArrayList<Triplet<Color, String, Integer>> categoryList) {
		clearCurrentGridContents();
		UiTextBuilder myBuilder = new UiTextBuilder();
		myBuilder.addMarker(0,UiConstants.STYLE_TEXT_CATEGORY);
		
		for ( int i = 0; i < categoryList.size(); i ++ ) {
			// add Rect
			gridHelper.createStyledCell(0, i, "", currentGrid);
			gridHelper.createScaledRectInCell(0, i, categoryList.get(i).getA(), currentGrid);
			// add tag name
			gridHelper.createStyledCell(1, i, UiConstants.STYLE_CATEGORY_BOX, currentGrid);
			gridHelper.addTextFlowToCell(1,i,myBuilder.build(categoryList.get(i).getB()),TextAlignment.CENTER,currentGrid);
			// add tag numbers
			gridHelper.createStyledCell(2, i, UiConstants.STYLE_CATEGORY_BOX, currentGrid);
			gridHelper.addTextFlowToCell(2,i,myBuilder.build(""+ categoryList.get(i).getC()),TextAlignment.CENTER,currentGrid);
		}
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
	}

	@Override
	public void processArrowKey(KeyEvent event) {
	}

	@Override
	public int processDeleteKey() {
		return -1;
	}

	@Override
	public int processEnterKey() {
		return 0;
	}

	@Override
	public void cleanUp() {
		clearCurrentGridContents();
	}

	@Override
	public void processPageUpAndDown(KeyEvent event) {	
	}
}
