package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ActionMode;
import taskey.logic.Task;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;

/**
 * @@author A0125419H
 * This class is responsible to formatting the Action Tab
 * It provides additional functions like the help menu, but it also uses a pagination as its display
 * @author junwei
 */

public class UiActionFormatter extends UiFormatter {
	private UiPagination listView;
	private UiHelpMenu myHelpMenu;
	private UiPagination currentView;
	private int entriesPerPage = 6;
	private int stackPanePadding = 2;
	
	public UiActionFormatter(ScrollPane thePane) {
		super(thePane);	
		listView = new UiPagination(UiConstants.STYLE_HIGHLIGHT_BOX);
		myHelpMenu = new UiHelpMenu();
		mainPane.setFitToHeight(true);
	}

	@Override
	public void processArrowKey(KeyEvent event) {
		if ( currentView != null ) {
			currentView.processArrowKey(event);
		}
	}
	@Override
	public int processDeleteKey() {	
		if ( currentView == listView ) {
			return currentView.getSelection() + 1;
		}
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
		int totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double	
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i ++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_DEFAULT);
			//newGrid.setGridLinesVisible(true);
			ArrayList<StackPane> pageEntries = new ArrayList<StackPane>();
			for ( int j = 0; j < entriesPerPage; j ++ ) {
				if ( entryNo >= myTaskList.size() ) {
					break;
				}
				StackPane entryPane = gridHelper.createStyledCell(1, j, UiConstants.STYLE_DEFAULT_BOX, newGrid);
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
		UiTextBuilder myBuilder = new UiTextBuilder();
		myBuilder.addMarker(0, UiConstants.STYLE_TEXT_DEFAULT);
		String line = "" + (id + 1);
		Color theColor = Color.WHITE;
		System.out.println(theTask.getTaskType());
		for ( int i = 0; i < categoryList.size(); i ++ ) {
			String tag = theTask.getTaskType();
			if ( tag != null ) {
				if ( tag.equals("FLOATING")) { // testing since no tags yet
					tag = new String("general");
				} 
				tag = tag.toLowerCase();
			}
			String categoryTag = categoryList.get(i).getB().toLowerCase();
			if ( categoryTag.contains(tag)) {
				theColor = categoryList.get(i).getA();
				break;
			}
		}
		gridHelper.createStyledCell(0, row, "", theGrid);
		gridHelper.createScaledRectInCell(0, row, theColor, theGrid);
		gridHelper.addTextFlowToCell(0, row, myBuilder.build(line),TextAlignment.CENTER, theGrid);	
	}
	
	private void addTaskDescription(Task theTask, int row, GridPane newGrid) {
		assert(theTask != null);
		assert(theTask.getTaskType() != null);
		
		UiTextBuilder myBuilder = new UiTextBuilder();
		myBuilder.addMarkers(UiConstants.STYLE_TEXT_DEFAULT);
		String line = "";
		line += "Name: "; 
		line += theTask.getTaskName() + "\n";
		switch ( theTask.getTaskType() ) {
			case "EVENT": 
				String [] timings = theTask.getEventTime();
				line += "Event from: ";
				line += timings[0] + " to " + timings[1];
				break;
	 		case "DEADLINE":
				line += "Due by: ";	
				line += "" + theTask.getDeadline();
				break;
			default:
				break;
		}
		line += "\n";
		line += "Tags: ";
		if ( theTask.getTaskTags() != null ) {
			ArrayList<String> tags = theTask.getTaskTags();
			for ( String s : tags) {
				line += s + " ";
			}
		} else {
			line += "None";
		}
		StackPane pane = gridHelper.getWrapperAtCell(1, row, newGrid);
		pane.setPadding(new Insets(stackPanePadding));
		gridHelper.addTextFlowToCell(1, row, myBuilder.build(line),TextAlignment.LEFT, newGrid);
	}
}
