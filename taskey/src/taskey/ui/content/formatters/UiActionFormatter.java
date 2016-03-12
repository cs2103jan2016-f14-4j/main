package taskey.ui.content.formatters;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.ActionMode;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiTextBuilder;

public class UiActionFormatter extends UiFormatter {
	private UiPagination listView;
	private UiPagination helpView;
	private ArrayList<UiPagination> commandViews;
	private UiPagination currentView;
	public UiActionFormatter(ScrollPane thePane) {
		super(thePane);	
		listView = new UiPagination();
		helpView = new UiPagination();
		commandViews = new ArrayList<UiPagination>();
		setUpHelpView(); // static
		mainPane.setFitToHeight(true);
	}

	@Override
	public void processArrowKey(KeyEvent event) {
		// TODO Auto-generated method stub
		currentView.processArrowKey(event);
	}

	@Override
	public int processDeleteKey() {
		// TODO Auto-generated method stub
		//currentView.processDeleteKey()
		return -1;
	}
	

	@Override
	public int processEnterKey() {
		if ( currentView == helpView) {
			currentView = commandViews.get(helpView.getSelection());
			mainPane.setContent(currentView.getPagination());
		} else if ( currentView != listView && commandViews.contains(currentView)) {
			currentView = helpView;
			mainPane.setContent(currentView.getPagination());
		}
		return 0;
	}

	private void addMainMenu() {
		ArrayList<String> headers = new ArrayList<String>(Arrays.asList("Taskey Help Menu","Add Command",
				"Delete Command","Edit Command", "Done Command", "Search Command", "Undo Command", "Tagging"));
		ArrayList<String> info = new ArrayList<String>(Arrays.asList(
				"Welcome to Taskey's help menu, select the command "
				+ "you would like to find out more about by use arrow keys and pressing Enter",
				"This will guide you through how to add tasks",
				"This will guide you through how to delete tasks",
				"This will guide you through how to edit tasks",
				"This will guide you through how to complete tasks",
				"This will guide you through how to search for tasks",
				"This will guide you through how to undo tasks",
				"This will guide you through how to tag tasks"
				));
		int numCommmands = headers.size();
		int entriesPerPage = 6;
		int totalPages = (int) Math.ceil(numCommmands/1.0/entriesPerPage); // convert to double	
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i++ ) {
			GridPane newGrid = setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELP);
			ArrayList<StackPane> menuElements = new ArrayList<StackPane>();
			
			for ( int j = 0; j < entriesPerPage; j ++ ) {
				if ( entryNo >= numCommmands ) {
					break;
				}
				Label current = createLabelInCell( 0, j, headers.get(entryNo), UiConstants.STYLE_NUMBER_ICON, newGrid);
				GridPane.setFillWidth(current.getParent(), false);
				GridPane.setFillHeight(current.getParent(), false);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				if ( entryNo > 0 ) {
					menuElements.add(getWrapperAtCell(0,j,newGrid));
				}
				UiTextBuilder myConfig = new UiTextBuilder();
				TextFlow element = new TextFlow();
				myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
				String line = info.get(entryNo);
				element.getChildren().addAll(myConfig.build(line));
				StackPane pane = createStyledCell(1, j, UiConstants.STYLE_WHITE_BOX, newGrid);
				addTextFlowToCell(1, j, element,TextAlignment.CENTER, newGrid);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				entryNo++;
			}
			helpView.addGridToPagination(newGrid,menuElements);
		}	
		helpView.initialize(totalPages); // update UI and bind call back
	}
	private void addMenu(ArrayList<IMAGE_ID> images, ArrayList<String> info ) {
		UiPagination menu = new UiPagination();
		commandViews.add(menu);
		int widthOfImage = 333;
		int totalPages = images.size(); 
		for ( int i = 0 ; i < totalPages; i++ ) {
			GridPane newGrid = setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELP_ADD);
			createImageInCell(0,0,UiImageManager.getInstance().getImage(images.get(i)),widthOfImage,0,newGrid);
			UiTextBuilder myConfig = new UiTextBuilder();
			TextFlow element = new TextFlow();
			myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
			String line = info.get(i);
			element.getChildren().addAll(myConfig.build(line));
			createStyledCell(0, 1, UiConstants.STYLE_NUMBER_ICON, newGrid);
			addTextFlowToCell(0, 1, element,TextAlignment.CENTER, newGrid);
			menu.addGridToPagination(newGrid,new ArrayList<StackPane>()); // no interactions	
		}
		menu.initialize(totalPages); // update UI and bind call back
	}
	
	private void setUpHelpView() {
		addMainMenu();
		ArrayList<IMAGE_ID> images = new ArrayList<IMAGE_ID>(Arrays.asList(IMAGE_ID.ADD_FLOAT,IMAGE_ID.ADD_DEADLINE,
				IMAGE_ID.ADD_DEADLINE_DATE,IMAGE_ID.ADD_EVENT,IMAGE_ID.ADD_LAST));
		ArrayList<String> info = new ArrayList<String>(Arrays.asList(
				"type: add <task name> to add a general task",
				"type: add <task name> on/by <date> to add a deadline task.",
				"<date> can also be an actual date format",
				"type: add <task name> from <date> to <date> to add an event",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);
		images = new ArrayList<IMAGE_ID>(Arrays.asList(IMAGE_ID.DELETE_ID,IMAGE_ID.DELETE_NAME,
				IMAGE_ID.DELETE_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: del <ID> to delete a task, ID is shown on the left.",
				"type: del <task name>, to do the same operation.",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
		images = new ArrayList<IMAGE_ID>(Arrays.asList(IMAGE_ID.SET_ID_DATE,IMAGE_ID.SET_ID_EVENT,
				IMAGE_ID.SET_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: set <ID> [date] to change the deadline of a task",
				"use [date,date] to specify an event.",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
		images = new ArrayList<IMAGE_ID>(Arrays.asList(IMAGE_ID.DONE_ID,IMAGE_ID.DONE_NAME,
				IMAGE_ID.DONE_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: done <ID> to move a task to the archive",
				"<task name> can also be used",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
	}


	public void updateContents(ArrayList<Task> myList, ActionMode mode) {
		switch ( mode ) {
			case HELP: 
				mainPane.setContent(helpView.getPagination());
				currentView = helpView;
				break;
			default:
				mainPane.setContent(listView.getPagination());
				currentView = listView;
				format(myList);
		}
	}
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		listView.clear();
		createPaginationGrids(myTaskList);
	}

	// This function creates the grids used by pagination
	private void createPaginationGrids(ArrayList<Task> myTaskList) {
		int entriesPerPage = 5;
		int totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double	
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i ++ ) {
			GridPane newGrid = setUpGrid(UiConstants.GRID_SETTINGS_DEFAULT);
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
				StackPane entryPane = createStyledCell(1, j, UiConstants.STYLE_WHITE_BOX, newGrid);
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
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
		String line = "" + (id + 1);
		element.getChildren().addAll(myConfig.build(line));
		createStyledCell(0, row, UiConstants.STYLE_NUMBER_ICON, theGrid);
		addTextFlowToCell(0, row, element,TextAlignment.CENTER, theGrid);
	}
	
	private void addTaskDescription(Task theTask, int row, GridPane newGrid) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
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
		element.getChildren().addAll(myConfig.buildBySymbol(line));
		addTextFlowToCell(1, row, element,TextAlignment.LEFT, newGrid);
	}
}
