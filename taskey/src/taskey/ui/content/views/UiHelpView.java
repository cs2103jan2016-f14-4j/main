package taskey.ui.content.views;

import java.util.ArrayList;
import java.util.EnumSet;

import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;
import taskey.constants.UiConstants;
import taskey.ui.utility.UiGridHelper;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiImageManager.ImageID;
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
	
	/**
	 * @@author A0121618M
	 * This method sets up the the main help menu and then the pages within each sub-menu.
	 */
	private void setUpHelpView() {
		ArrayList<Pair<String, String>> mainMenuOptions = new ArrayList<Pair<String, String>>();
		// In the following key-value pairs, key is the option in the main menu and value is the description
		mainMenuOptions.add(new Pair<String, String>("Help main menu", "Use Page Up/Down and Enter to select.\n"
				+ "Press Enter in any submenu to return to the main menu"));
		mainMenuOptions.add(new Pair<String, String>("Add", "How to add tasks"));
		mainMenuOptions.add(new Pair<String, String>("Delete", "How to delete tasks"));
		mainMenuOptions.add(new Pair<String, String>("Set", "How to edit tasks"));
		mainMenuOptions.add(new Pair<String, String>("Done", "How to complete tasks"));
		mainMenuOptions.add(new Pair<String, String>("Search", "How to search for tasks"));
		mainMenuOptions.add(new Pair<String, String>("Undo", "How to undo your last action"));
		mainMenuOptions.add(new Pair<String, String>("Tags", "How to add tasks with tags"));
		mainMenuOptions.add(new Pair<String, String>("View", "How to filter pending tasks by type"));
		addMainMenu(mainMenuOptions);
		
		ArrayList<EnumSet<ImageID>> helpImageSets_byCategory = new ArrayList<EnumSet<ImageID>>();
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_Add());
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_Del());
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_Edit());
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_Done());
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_Search());
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_Undo());
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_Tag());
		helpImageSets_byCategory.add(ImageID.getHelpImageSet_View());
		
		for (EnumSet<ImageID> helpImageSet : helpImageSets_byCategory) {
			ArrayList<Pair<ImageID, String>> subMenuPages = new ArrayList<Pair<ImageID, String>>();
			for (ImageID imageID : helpImageSet) {
				//Each page in a submenu consists of an image and its caption
				subMenuPages.add(new Pair<ImageID, String>(imageID, imageID.getCaption()));
			}
			addMenu(subMenuPages);
		}
	}

	/**
	 * @@author A0125419H
	 */
	private void addMainMenu(ArrayList<Pair<String, String>> mainMenuOptions) {
		int numCommmands = mainMenuOptions.size();
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
				String optionText = mainMenuOptions.get(entryNo).getKey();
				Label current = gridHelper.createLabelInCell(0, j, optionText, UiConstants.STYLE_PROMPT_SELECTED, newGrid);
				GridPane.setFillWidth(current.getParent(), false);
				GridPane.setFillHeight(current.getParent(), false);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				if ( entryNo > 0 ) {
					menuElements.add(gridHelper.getWrapperAtCell(0,j,newGrid));
				}
				UiTextBuilder myBuilder = new UiTextBuilder();
				myBuilder.addMarker(0, UiConstants.STYLE_PROMPT_SELECTED);
				
				String descriptionText = mainMenuOptions.get(entryNo).getValue();
				gridHelper.createStyledCell(1, j, "", newGrid);
				gridHelper.addTextFlowToCell(1, j, myBuilder.build(descriptionText),TextAlignment.LEFT, newGrid);
				GridPane.setHalignment(current.getParent(), HPos.CENTER);
				entryNo++;
			}
			helpView.addGridToPagination(newGrid,menuElements);
		}	
		helpView.initializeDisplay(totalPages); // update UI and bind call back
	}
	
	private void addMenu(ArrayList<Pair<ImageID, String>> helpPages) {
		UiPagination menu = new UiPagination("");
		commandViews.add(menu);
		int totalPages = helpPages.size(); 
		for ( int i = 0 ; i < totalPages; i++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ACTION_HELP_MENU);
			
			ImageID imageID = helpPages.get(i).getKey();
			gridHelper.createImageInCell(0, 0, UiImageManager.getInstance().getImage(imageID), imageWidth, 0, newGrid);
			UiTextBuilder myBuilder = new UiTextBuilder();
			myBuilder.addMarker(0, UiConstants.STYLE_TEXT_DEFAULT);
			
			String helpText = helpPages.get(i).getValue();
			gridHelper.createStyledCell(0, 1, UiConstants.STYLE_HIGHLIGHT_BOX, newGrid);
			gridHelper.addTextFlowToCell(0, 1, myBuilder.build(helpText),TextAlignment.CENTER, newGrid);
			menu.addGridToPagination(newGrid,new ArrayList<StackPane>()); // no interactions	
		}
		menu.initializeDisplay(totalPages); // update UI and bind call back
	}
	
	public void clear() {
		for ( int i = 0 ; i < commandViews.size(); i ++ ) {
			commandViews.get(i).clear();
		}
		helpView.clear();
	}
}
