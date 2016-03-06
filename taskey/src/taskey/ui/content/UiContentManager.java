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
 * It does the main job of setting up the grids and attaching UiFormatters to
 * them
 * 
 * @author JunWei
 *
 */
public class UiContentManager {
	private UiClockService clockService; // reference to ui clock
	private ArrayList<ScrollPane> contentBoxes; // list of references to the ScrollPane objects, since .getParent() of GridPane returns Skin
	private ArrayList<UiFormatter> myFormatters; // for the grid panes

	public UiContentManager(UiClockService _clockService) {
		assert(_clockService != null);
		clockService = _clockService;
		contentBoxes = new ArrayList<ScrollPane>();
		myFormatters = new ArrayList<UiFormatter>();
	}

	public void setUpContentBox(ScrollPane pane, ContentBox contentID) {
		assert(pane != null);
		UiFormatter myFormatter;
		switch (contentID) {
		case ACTION:
			myFormatter = new UiActionFormatter(setUpGrid(UiConstants.GRID_SETTINGS_ACTION_LISTVIEW),clockService);
			myFormatter.addGrid(setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELPVIEW)); // additional grid
			break;
		case CATEGORY:
			myFormatter = new UiCategoryFormatter(setUpGrid(UiConstants.GRID_SETTINGS_CATEGORY),clockService);
			break;
		default:
			myFormatter = new UiDefaultFormatter(setUpGrid(UiConstants.GRID_SETTINGS_DEFAULT), clockService);
			break;
		}
		myFormatters.add(myFormatter);
		pane.setContent(myFormatter.getGrid());
		pane.setFitToWidth(true);
		contentBoxes.add(pane);
	}

	private GridPane setUpGrid(UiGridSettings settings) {
		assert(settings != null);
		GridPane gridPane = new GridPane();
		gridPane.setGridLinesVisible(true);
		gridPane.setPadding(settings.getPaddings());
		gridPane.setHgap(settings.getHGap());
		gridPane.setVgap(settings.getVGap());
		ArrayList<Integer> colPercents = settings.getColPercents();
		for (int i = 0; i < colPercents.size(); i++) {
			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(colPercents.get(i));
			gridPane.getColumnConstraints().add(column);
		}
		return gridPane;
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
		myFormatter.clearGridContents();
		myFormatter.format(myTaskList);
	}

	/**
	 * Update action list to have the correct grid, then display the grid accordingly
	 * @param myTaskList - which is the list of tasks
	 * @param mode - Content LIST, HELP
	 */
	public void updateActionContentBox(ArrayList<Task> myTaskList, ActionListMode mode) {
		//assert(myTaskList != null);
		int arrayIndex = ContentBox.ACTION.getValue();
		UiActionFormatter myFormatter = (UiActionFormatter) myFormatters.get(arrayIndex);
		ScrollPane pane = contentBoxes.get(arrayIndex);
		myFormatter.clearGridContents();
		myFormatter.updateGrid(mode);
		pane.setContent(myFormatter.getGrid());
		myFormatter.updateContents(myTaskList); // update display
	}
	public void updateCategoryContentBox(ArrayList<String> myCategoryList, ArrayList<Integer> categoryNums, ArrayList<Color> categoryColors) {
		assert(myCategoryList != null);
		assert(categoryNums != null);
		int arrayIndex = ContentBox.CATEGORY.getValue();
		UiCategoryFormatter myFormatter = (UiCategoryFormatter) myFormatters.get(arrayIndex);
		myFormatter.clearGridContents();
		myFormatter.updateCategories(myCategoryList,categoryNums,categoryColors);
	}

	public void cleanUp() 		{
		for ( int i = 0; i < myFormatters.size(); i ++ ) {
			myFormatters.get(i).cleanUp();
		}
		myFormatters.clear();
		contentBoxes.clear();
	}
	
	
	public void processArrowKey(KeyEvent event) {
		assert(event != null);
		// temporary, need to get current tab
		int arrayIndex = ContentBox.ACTION.getValue();
		UiActionFormatter myFormatter = (UiActionFormatter) myFormatters.get(arrayIndex);
		GridPane currentGrid = myFormatter.getGrid();
		int direction = 1;
		if ( event.getCode() == KeyCode.RIGHT) {
			direction = 1;		
		} else if ( event.getCode() == KeyCode.LEFT) {
			direction = -1;
		} else {
			return;
		}
		TranslateTransition shiftGrid = UiAnimationManager.getInstance().createTranslateTransition(
				currentGrid, 
				new Pair<Double,Double>(currentGrid.getLayoutX(),currentGrid.getLayoutY()),
				new Pair<Double,Double>(currentGrid.getLayoutX()+currentGrid.getWidth()*direction, currentGrid.getLayoutY()+100),
				1000);
		shiftGrid.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				updateActionContentBox(null,ActionListMode.HELP_MAIN);			
			}
		});
		shiftGrid.play();
	}
}
