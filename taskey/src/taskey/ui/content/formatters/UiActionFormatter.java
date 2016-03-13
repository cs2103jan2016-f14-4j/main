package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ActionMode;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;

/**
 * This class is responsible to formatting the Action Tab
 * It provides additional functions like the help menu, but it also uses a pagination as its display
 * @author junwei
 */
public class UiActionFormatter extends UiFormatter {
	private UiPagination listView;
	private UiHelpMenu myHelpMenu;
	private UiPagination currentView;
	
	public UiActionFormatter(ScrollPane thePane) {
		super(thePane);	
		listView = new UiPagination(UiConstants.STYLE_GRAY_BOX);
		myHelpMenu = new UiHelpMenu();
		mainPane.setFitToHeight(true);
	}

	@Override
	public void processArrowKey(KeyEvent event) {
		currentView.processArrowKey(event);
	}
	@Override
	public int processDeleteKey() {
		//currentView.processDeleteKey()
		return -1;
	}
	@Override
	public int processEnterKey() {
		if ( currentView != listView ) {
			myHelpMenu.processEnterKey();
			currentView = myHelpMenu.getView();
			mainPane.setContent(currentView.getPagination());
		} 
		return 0;
	}

	/**
	 * Update content based on the mode provided
	 *
	 * @param myList - the task list
	 * @param mode - the mode of display
	 */
	public void updateContents(ArrayList<Task> myList, ActionMode mode) {
		switch ( mode ) {
			case HELP: 
				myHelpMenu.resetView();
				currentView = myHelpMenu.getView();
				mainPane.setContent(currentView.getPagination());				
				break;
			default:
				currentView = listView;
				mainPane.setContent(currentView.getPagination());
				format(myList);
		}
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		listView.clear();
		createPaginationGrids(myTaskList);
	}

	private void createPaginationGrids(ArrayList<Task> myTaskList) {
		int entriesPerPage = 5;
		int totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double	
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i ++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_DEFAULT);
			//newGrid.setGridLinesVisible(true);
			for (int k = 0; k < entriesPerPage; k++) {
				RowConstraints row = new RowConstraints();
				row.setPercentHeight((100.0-1.0)/entriesPerPage); // 1.0 to prevent cut off due to the pagination bar
				newGrid.getRowConstraints().add(row);
			}
			
			ArrayList<StackPane> pageEntries = new ArrayList<StackPane>();
			for ( int j = 0; j < entriesPerPage; j ++ ) {
				if ( entryNo >= myTaskList.size() ) {
					break;
				}
				StackPane entryPane = gridHelper.createStyledCell(1, j, UiConstants.STYLE_WHITE_BOX, newGrid);
				pageEntries.add(entryPane);
				Task theTask = myTaskList.get(entryNo);
				addTaskID(theTask, entryNo, j, newGrid);	
				addTaskDescription(theTask, j,newGrid);
				entryNo++;
			}
			listView.addGridToPagination(newGrid,pageEntries);
		}
		listView.initialize(totalPages); // update UI and bind call back
	}

	private void addTaskID(Task theTask, int id, int row, GridPane theGrid) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
		String line = "" + (id + 1);
		gridHelper.createStyledCell(0, row, UiConstants.STYLE_NUMBER_ICON, theGrid);
		gridHelper.addTextFlowToCell(0, row, myConfig.build(line),TextAlignment.CENTER, theGrid);
	}
	
	private void addTaskDescription(Task theTask, int row, GridPane newGrid) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		myConfig.addMarkers(UiConstants.STYLE_TEXT_BLACK_TO_PURPLE);
		String line = "";
		line += "$Name: "; 
		line += theTask.getTaskName() + "\n";
		switch ( theTask.getTaskType() ) {
			case "EVENT": 
				String [] timings = theTask.getEventTime();
				myConfig.addMarkers(UiConstants.STYLE_TEXT_BLUE,
						UiConstants.STYLE_TEXT_BLACK_TO_PURPLE, 
						UiConstants.STYLE_TEXT_BLUE);
				line += "Event from: $";
				line += timings[0] + "$ to $" + timings[1];
				break;
	 		case "DEADLINE":
				line += "Due by: ";
				myConfig.addMarkers(UiConstants.STYLE_TEXT_BLUE);			
				line += "$" + theTask.getDeadline();
				break;
			default:
				break;
		}
		line += "\n";
		myConfig.addMarkers(UiConstants.STYLE_TEXT_BLACK_TO_PURPLE);
		line += "$Tags: ";
		if ( theTask.getTaskTags() != null ) {
			ArrayList<String> tags = theTask.getTaskTags();
			for ( String s : tags) {
				line += s + " ";
			}
		} else {
			line += "None";
		}
		gridHelper.addTextFlowToCell(1, row, myConfig.buildBySymbol(line),TextAlignment.LEFT, newGrid);
	}
}
