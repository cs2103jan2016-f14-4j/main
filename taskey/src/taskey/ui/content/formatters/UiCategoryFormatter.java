package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import taskey.constants.Triplet;
import taskey.constants.UiConstants;
import taskey.logic.Task;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiTextBuilder;

/**
 * This class is responsible for formatting the Category box
 * 
 * @author junwei
 */
public class UiCategoryFormatter extends UiFormatter {

	public UiCategoryFormatter(ScrollPane thePane) {
		super(thePane);
		addGrid(gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_CATEGORY),true);
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
			
			gridHelper.createStyledCell(2, i, UiConstants.STYLE_CATEGORY_BOX, currentGrid);
			// add tag numbers
			gridHelper.addTextFlowToCell(2,i,myBuilder.build(""+ categoryList.get(i).getC()),TextAlignment.CENTER,currentGrid);
		}
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
	}

	@Override
	public void processArrowKey(KeyEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int processDeleteKey() {
		// TODO Auto-generated method stub
		return -1;
	}

	@Override
	public int processEnterKey() {
		// TODO Auto-generated method stub
		return 0;
	}
}
