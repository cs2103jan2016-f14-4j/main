package taskey.ui.content;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ContentBox;
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
	private ArrayList<ScrollPane> contentBoxes; // list of references to the ScrollPane objects, in case
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
			theGrid = setUpGrid(pane, UiConstants.GRID_SETTINGS_ACTION);
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
		// gridPane.setGridLinesVisible(true);
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
		UiFormatter myFormatter = myFormatters.get(contentID.getValue());
		myFormatter.clearGridContents();
		myFormatter.format(myTaskList);

		if (contentID == ContentBox.PENDING) { // update weekly list also when pending list is updated
			updateContentBox(myTaskList, ContentBox.WEEKLY);
		}
	}

	public void cleanUp() {
		myFormatters.clear();
		contentBoxes.clear();
	}

}
