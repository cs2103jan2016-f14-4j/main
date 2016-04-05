package taskey.ui.content.views;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ImageID;
import taskey.ui.utility.UiGridHelper;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiTextBuilder;

/**
 * @@author A0125419H
 * This class is used mainly to separate help menu from UiActionFormatter,
 * in order to make UiActionFormatter cleaner
 * 
 * @author junwei
 */

public class UiHelpView {
	
	private UiGridHelper gridHelper;
	private UiPagination helpView;
	private ArrayList<UiPagination> commandViews;
	private UiPagination currentView;
	private int imageWidth = 333;
	
	public UiHelpView() {
		gridHelper = new UiGridHelper("");
		helpView = new UiPagination(UiConstants.STYLE_HELP_MENU_SELECTOR);
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
				+ "you would like to find out more about by arrow keys and pressing Enter",
				"This will guide you through how to add tasks",
				"This will guide you through how to delete tasks",
				"This will guide you through how to edit tasks",
				"This will guide you through how to complete tasks",
				"This will guide you through how to search for tasks",
				"This will guide you through how to undo tasks",
				"This will guide you through how to tag tasks"
				));
		int numCommmands = headers.size();
		int totalPages = (int) Math.ceil(numCommmands/1.0/UiConstants.ENTRIES_PER_PAGE_HELP_MENU); // convert to double	
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELP);
			ArrayList<StackPane> menuElements = new ArrayList<StackPane>();
			//newGrid.setGridLinesVisible(true);
			for ( int j = 0; j < UiConstants.ENTRIES_PER_PAGE_HELP_MENU; j ++ ) {
				if ( entryNo >= numCommmands ) {
					break;
				}
				Label current = gridHelper.createLabelInCell(0, j, headers.get(entryNo), 
															 UiConstants.STYLE_PROMPT_SELECTED, newGrid);
				GridPane.setFillWidth(current.getParent(), false);
				GridPane.setFillHeight(current.getParent(), false);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				if ( entryNo > 0 ) {
					menuElements.add(gridHelper.getWrapperAtCell(0,j,newGrid));
				}
				UiTextBuilder myBuilder = new UiTextBuilder();
				myBuilder.addMarker(0, UiConstants.STYLE_PROMPT_SELECTED);
				String line = info.get(entryNo);
				gridHelper.createStyledCell(1, j, "", newGrid);
				gridHelper.addTextFlowToCell(1, j, myBuilder.build(line),TextAlignment.LEFT, newGrid);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				entryNo++;
			}
			helpView.addGridToPagination(newGrid,menuElements);
		}	
		helpView.initializeDisplay(totalPages); // update UI and bind call back
	}
	
	private void addMenu(ArrayList<ImageID> images, ArrayList<String> info ) {
		UiPagination menu = new UiPagination("");
		commandViews.add(menu);
		int totalPages = images.size(); 
		for ( int i = 0 ; i < totalPages; i++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELP_MENU);
			gridHelper.createImageInCell(0,0,UiImageManager.getInstance().getImage(images.get(i)),imageWidth,0,newGrid);
			UiTextBuilder myBuilder = new UiTextBuilder();
			myBuilder.addMarker(0, UiConstants.STYLE_TEXT_DEFAULT);
			String line = info.get(i);
			gridHelper.createStyledCell(0, 1, UiConstants.STYLE_HIGHLIGHT_BOX, newGrid);
			gridHelper.addTextFlowToCell(0, 1, myBuilder.build(line),TextAlignment.CENTER, newGrid);
			menu.addGridToPagination(newGrid,new ArrayList<StackPane>()); // no interactions	
		}
		menu.initializeDisplay(totalPages); // update UI and bind call back
	}

	private void setUpHelpView() {
		addMainMenu();
		ArrayList<ImageID> images = new ArrayList<ImageID>(Arrays.asList(ImageID.ADD_FLOAT,ImageID.ADD_DEADLINE,
				ImageID.ADD_DEADLINE_DATE,ImageID.ADD_EVENT,ImageID.ADD_LAST));
		ArrayList<String> info = new ArrayList<String>(Arrays.asList(
				"type: add <task name> to add a general task",
				"type: add <task name> on/by <date> to add a deadline task.",
				"<date> can also be an actual date format",
				"type: add <task name> from <date> to <date> to add an event",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);
		images = new ArrayList<ImageID>(Arrays.asList(ImageID.DELETE_ID,ImageID.DELETE_NAME,
				ImageID.DELETE_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: del <ID> to delete a task, ID is shown on the left.",
				"type: del <task name>, to do the same operation.",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
		images = new ArrayList<ImageID>(Arrays.asList(ImageID.SET_ID_DATE,ImageID.SET_ID_EVENT,
				ImageID.SET_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: set <ID> [date] to change the deadline of a task",
				"use [date,date] to specify an event.",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
		images = new ArrayList<ImageID>(Arrays.asList(ImageID.DONE_ID,ImageID.DONE_NAME,
				ImageID.DONE_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: done <ID> to move a task to the archive",
				"<task name> can also be used",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
		images = new ArrayList<ImageID>(Arrays.asList(ImageID.SEARCH_NAME,ImageID.SEARCH_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: search <phrase> to search for a task",
				"A list of tasks will be shown in Action"
				));
		addMenu(images, info);	
		images = new ArrayList<ImageID>(Arrays.asList(ImageID.UNDO,ImageID.UNDO_LAST));
		info = new ArrayList<String>(Arrays.asList(
				"type: undo, to revert an action ",
				"That's it! Press Enter to return"
				));
		addMenu(images, info);	
		images = new ArrayList<ImageID>(Arrays.asList(ImageID.VIEW_GENERAL,ImageID.VIEW_DEADLINE,
				ImageID.VIEW_EVENT));
		info = new ArrayList<String>(Arrays.asList(
				"type: view general, to view all general tasks ",
				"type: view deadline, to view all tasks with deadlines ",
				"type: view events, to view all tasks with events"
				));
		addMenu(images, info);	
	}
	
	public void clear() {
		for ( int i = 0 ; i < commandViews.size(); i ++ ) {
			commandViews.get(i).clear();
		}
		helpView.clear();
	}
}
