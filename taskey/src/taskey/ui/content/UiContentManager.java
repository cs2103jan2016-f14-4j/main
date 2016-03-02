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
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.UiConstants.ActionContentMode;
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
		clockService = _clockService;
		contentBoxes = new ArrayList<ScrollPane>();
		myFormatters = new ArrayList<UiFormatter>();
	}

	public void setUpContentBox(ScrollPane pane, ContentBox contentID) {
		
		UiFormatter myFormatter;
		switch (contentID) {
		case WEEKLY:
			myFormatter = new UiWeeklyFormatter(setUpGrid(UiConstants.GRID_SETTINGS_WEEKLY),clockService);
			myFormatters.add(myFormatter);
			break;
		case ACTION:
			myFormatter = new UiActionFormatter(setUpGrid(UiConstants.GRID_SETTINGS_ACTION_LISTVIEW),clockService);
			myFormatter.addGrid(setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELPVIEW)); // additional grid
			myFormatters.add(myFormatter);
			break;
		default:
			myFormatter = new UiDefaultFormatter(setUpGrid(UiConstants.GRID_SETTINGS_PENDING), clockService);
			myFormatters.add(myFormatter);
			break;
		}
		
		pane.setContent(myFormatter.getGrid());
		pane.setFitToWidth(true);
		contentBoxes.add(pane);
	}

	public GridPane setUpGrid(UiGridSettings settings) {
		GridPane gridPane = new GridPane();
		//gridPane.setGridLinesVisible(true);
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
		UiFormatter myFormatter = myFormatters.get(contentID.getValue());
		myFormatter.clearGridContents();
		myFormatter.format(myTaskList);

		if (contentID == ContentBox.PENDING) { // update weekly list also when pending list is updated
			updateContentBox(myTaskList, ContentBox.WEEKLY);
		}
	}

	/**
	 * Update action list to have the correct grid, then display the grid accordingly
	 * @param myTaskList - which is the list of tasks
	 * @param mode - Content LIST, HELP
	 */
	public void updateActionContentBox(ArrayList<Task> myTaskList, ActionContentMode mode) {
		int arrayIndex = ContentBox.ACTION.getValue();
		UiActionFormatter myFormatter = (UiActionFormatter) myFormatters.get(arrayIndex);
		ScrollPane pane = contentBoxes.get(arrayIndex);
		myFormatter.clearGridContents();
		myFormatter.updateGrid(mode);
		pane.setContent(myFormatter.getGrid());
		myFormatter.updateContents(myTaskList); // update display
	}
	public void cleanUp() {
		for ( int i = 0; i < myFormatters.size(); i ++ ) {
			myFormatters.get(i).cleanUp();
		}
		myFormatters.clear();
		contentBoxes.clear();
	}
	
	
	public void processArrowKey(KeyEvent event) {
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
		TranslateTransition shiftGrid = new TranslateTransition();
		shiftGrid.setFromX(currentGrid.getLayoutX());
		shiftGrid.setDuration(Duration.millis(1000));
		shiftGrid.setToX(currentGrid.getLayoutX()+currentGrid.getWidth()*direction);
		shiftGrid.setNode(currentGrid);
		shiftGrid.play();
		shiftGrid.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				updateActionContentBox(null,ActionContentMode.HELP_MAIN);			
			}
		});
	}
}
