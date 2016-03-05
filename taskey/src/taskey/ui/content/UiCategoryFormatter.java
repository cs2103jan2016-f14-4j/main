package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiTextConfig;

public class UiCategoryFormatter extends UiFormatter {
	public static final int BULLET_RADIUS = 5;
	
	public UiCategoryFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
	}

	public void updateCategories(ArrayList<String> myCategoryList, ArrayList<Integer> categoryNums) {
		UiTextConfig myConfig = new UiTextConfig();
		myConfig.addMarker(0,"textCategory");
		for ( int i = 0; i < myCategoryList.size(); i ++ ) {
			// add bullet
			createStyledCell(0, i,"", currentGrid);
			addCircleToCell(0,i,createBullet(BULLET_RADIUS,Color.YELLOW),currentGrid);
			
			// add tag name
			TextFlow element = new TextFlow();
			element.getChildren().addAll(myConfig.format(myCategoryList.get(i)));
			createStyledCell(1,i,"",currentGrid);
			addTextFlowToCell(1,i,element,TextAlignment.CENTER,currentGrid);
			
			// add tag numbers
			element = new TextFlow();
			element.getChildren().addAll(myConfig.format("[" + categoryNums.get(i) + "]"));
			createStyledCell(2,i,"",currentGrid);
			addTextFlowToCell(2,i,element,TextAlignment.CENTER,currentGrid);
		}
	}
	private Circle createBullet(int radius, Color theCenter) {
		Circle myCircle = new Circle(radius);
		Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(0.25f, theCenter),new Stop(0.75f, theCenter), new Stop(1, Color.BLACK)};
		LinearGradient myGradiant = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		myCircle.setFill(myGradiant);
		return myCircle;
	}
}
