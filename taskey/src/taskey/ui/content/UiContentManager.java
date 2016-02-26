package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.UiConstants.ContentMode;
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
	private ArrayList<ScrollPane> contentBoxes; // list of references to the ScrollPane objects, since .getParent() of grid returns Skin
	private ArrayList<UiFormatter> myFormatters; // for the grid panes

	public UiContentManager(UiClockService _clockService) {
		clockService = _clockService;
		contentBoxes = new ArrayList<ScrollPane>();
		myFormatters = new ArrayList<UiFormatter>();
	}

	public void setUpContentBox(ScrollPane pane, ContentBox contentID) {
		contentBoxes.add(pane);
		pane.setFitToWidth(true);

		GridPane theGrid = null;
		switch (contentID) {
		case WEEKLY:
			theGrid = setUpGrid(pane, UiConstants.GRID_SETTINGS_WEEKLY);
			myFormatters.add(new UiWeeklyFormatter(theGrid, clockService));
			break;
		case ACTION:
			theGrid = setUpGrid(pane, UiConstants.GRID_SETTINGS_ACTION_LISTVIEW);
			myFormatters.add(new UiActionFormatter(theGrid, clockService));
			break;
		default:
			theGrid = setUpGrid(pane, UiConstants.GRID_SETTINGS_PENDING);
			myFormatters.add(new UiNormalFormatter(theGrid, clockService));
			break;
		}
	}

	public GridPane setUpGrid(ScrollPane pane, UiGridSettings settings) {
		GridPane gridPane = new GridPane();
		pane.setContent(gridPane);
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

	public void updateContentBox(ArrayList<Task> myTaskList, ContentBox contentID) {
		if ( contentID == ContentBox.ACTION ) {
			updateActionContentBox(myTaskList, ContentMode.LIST); // redirect to appropriate method
			return;
		}
		UiFormatter myFormatter = myFormatters.get(contentID.getValue());
		myFormatter.clearGridContents();
		myFormatter.format(myTaskList);

		if (contentID == ContentBox.PENDING) { // update weekly list also when pending list is updated
			updateContentBox(myTaskList, ContentBox.WEEKLY);
		}
	}

	/**
	 * Update action list to have the correct grid, then update the grid accordingly
	 * @param myTaskList - which is the list of tasks
	 * @param mode - Content LIST, HELP
	 */
	public void updateActionContentBox(ArrayList<Task> myTaskList, ContentMode mode) {
		int arrayIndex = ContentBox.ACTION.getValue();
		UiActionFormatter myFormatter = (UiActionFormatter) myFormatters.get(arrayIndex);
		ScrollPane pane = contentBoxes.get(arrayIndex);
		myFormatter.clearGridContents();
		switch ( mode ) {
			case LIST:
				if ( myFormatter.getCurrentMode() != ContentMode.LIST ) {
					myFormatter.setGrid(setUpGrid(pane, UiConstants.GRID_SETTINGS_ACTION_LISTVIEW));
					myFormatter.setMode(ContentMode.LIST);
				}
				myFormatter.format(myTaskList);
				break;
			case HELP:
				if ( myFormatter.getCurrentMode() != ContentMode.HELP ) {
					myFormatter.setGrid(setUpGrid(pane, UiConstants.GRID_SETTINGS_ACTION_HELPVIEW));
					myFormatter.setMode(ContentMode.HELP);
				}
				myFormatter.showHelp();
				break;
			default:
				System.out.println("MODE NOT SPECIFIED FOR ACTION LIST");
				break;
		}
	}
	public void cleanUp() {
		myFormatters.clear();
		contentBoxes.clear();
	}

}
