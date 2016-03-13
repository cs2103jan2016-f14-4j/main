package taskey.ui.content.formatters;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.content.UiGridHelper;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;
import taskey.ui.utility.UiImageManager;

public class UiHelpMenu {
	private UiGridHelper gridHelper;
	private UiPagination helpView;
	private ArrayList<UiPagination> commandViews;
	private UiPagination currentView;
	private int entriesPerPage = 10; // main menu
	private int imageWidth = 333;
	
	public UiHelpMenu() {
		gridHelper = new UiGridHelper("");
		helpView = new UiPagination(UiConstants.STYLE_RED_ELLIPSE);
		commandViews = new ArrayList<UiPagination>();
		setUpHelpView();
		currentView = helpView;
	}
	public void processEnterKey() {
		if ( currentView == helpView ) {
			currentView = commandViews.get(helpView.getSelection());
		} else {
			currentView = helpView;
		}
	}
	public void resetView() {
		currentView = helpView;
	}
	public UiPagination getView() {
		return currentView;
	}
	private void addMainMenu() {
		ArrayList<String> headers = new ArrayList<String>(Arrays.asList("Taskey Help","Adding Tasks",
				"Deleting Tasks","Set Tasks", "Done Tasks", "Search Tasks", "Undo Tasks", "Tagging Task"));
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
		int totalPages = (int) Math.ceil(numCommmands/1.0/entriesPerPage); // convert to double	
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELP);
			ArrayList<StackPane> menuElements = new ArrayList<StackPane>();
			
			for ( int j = 0; j < entriesPerPage; j ++ ) {
				if ( entryNo >= numCommmands ) {
					break;
				}
				Label current = gridHelper.createLabelInCell( 0, j, headers.get(entryNo), UiConstants.STYLE_NUMBER_ICON, newGrid);
				GridPane.setFillWidth(current.getParent(), false);
				GridPane.setFillHeight(current.getParent(), false);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				if ( entryNo > 0 ) {
					menuElements.add(gridHelper.getWrapperAtCell(0,j,newGrid));
				}
				UiTextBuilder myConfig = new UiTextBuilder();
				TextFlow element = new TextFlow();
				myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
				String line = info.get(entryNo);
				element.getChildren().addAll(myConfig.build(line));
				gridHelper.createStyledCell(1, j, UiConstants.STYLE_WHITE_BOX, newGrid);
				gridHelper.addTextFlowToCell(1, j, element,TextAlignment.CENTER, newGrid);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				entryNo++;
			}
			helpView.addGridToPagination(newGrid,menuElements);
		}	
		helpView.initialize(totalPages); // update UI and bind call back
	}
	private void addMenu(ArrayList<IMAGE_ID> images, ArrayList<String> info ) {
		UiPagination menu = new UiPagination("");
		commandViews.add(menu);
		int totalPages = images.size(); 
		for ( int i = 0 ; i < totalPages; i++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELP_MENU);
			gridHelper.createImageInCell(0,0,UiImageManager.getInstance().getImage(images.get(i)),imageWidth,0,newGrid);
			UiTextBuilder myConfig = new UiTextBuilder();
			TextFlow element = new TextFlow();
			myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
			String line = info.get(i);
			element.getChildren().addAll(myConfig.build(line));
			gridHelper.createStyledCell(0, 1, UiConstants.STYLE_NUMBER_ICON, newGrid);
			gridHelper.addTextFlowToCell(0, 1, element,TextAlignment.CENTER, newGrid);
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
		images = new ArrayList<IMAGE_ID>(Arrays.asList(IMAGE_ID.SEARCH_NAME,IMAGE_ID.SEARCH_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: search <phrase> to search for a task",
				"A list of tasks will be shown in Action"
				));
		addMenu(images, info);	
		images = new ArrayList<IMAGE_ID>(Arrays.asList(IMAGE_ID.UNDO,IMAGE_ID.UNDO_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: undo, to revert an action ",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
		images = new ArrayList<IMAGE_ID>(Arrays.asList(IMAGE_ID.VIEW_GENERAL,IMAGE_ID.VIEW_DEADLINE,
				IMAGE_ID.VIEW_EVENT));
		info = new ArrayList<String>(Arrays.asList(
				"type: view general, to view all general tasks ",
				"type: view deadline, to view all tasks with deadlines ",
				"type: view events, to view all tasks with events"
				));
		addMenu(images, info);	
	}
}
