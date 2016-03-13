package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiTextBuilder;

/**
 * This class is responsible for formatting the Category box
 * 
 * @author junwei
 */
public class UiCategoryFormatter extends UiFormatter {
	
	public static final int BULLET_RADIUS = 5;
	
	public UiCategoryFormatter(ScrollPane thePane) {
		super(thePane);
		addGrid(gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_CATEGORY),true);
		mainPane.setContent(currentGrid);
	}

	public void updateCategories(ArrayList<String> categoryNames, ArrayList<Integer> categoryNums, ArrayList<Color> categoryColors) {
		clearCurrentGridContents();
		UiTextBuilder myConfig = new UiTextBuilder();
		myConfig.addMarker(0,"textCategory");
		for ( int i = 0; i < categoryNames.size(); i ++ ) {
			// add bullet
			gridHelper.addCircleToCell(0,i,createBullet(BULLET_RADIUS,categoryColors.get(i)),currentGrid);
			
			// add tag name
			gridHelper.addTextFlowToCell(1,i,myConfig.build(categoryNames.get(i)),TextAlignment.CENTER,currentGrid);
			
			// add tag numbers
			gridHelper.addTextFlowToCell(2,i,myConfig.build(""+ categoryNums.get(i)),TextAlignment.CENTER,currentGrid);
		}
	}
	
	private Circle createBullet(int radius, Color theCenter) {
		assert(theCenter != null);
		Circle myCircle = new Circle(radius);
		Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(0.25f, theCenter),new Stop(0.75f, theCenter), new Stop(1, Color.BLACK)};
		LinearGradient myGradiant = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		myCircle.setFill(myGradiant);
		return myCircle;
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
