package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import taskey.constants.Triplet;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.IMAGE_ID;
import taskey.logic.Task;
import taskey.ui.content.UiGridHelper;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;
import taskey.ui.utility.UiImageManager;

public class UiListView {

	private int stackPanePadding = 2;
	private UiPagination myPagination;
	private int entriesPerPage;
	private UiGridHelper gridHelper;
	
	public UiListView(int _entriesPerPage) {
		gridHelper = new UiGridHelper(UiConstants.STYLE_DEFAULT_BOX);
		entriesPerPage = _entriesPerPage;
		myPagination = new UiPagination(UiConstants.STYLE_HIGHLIGHT_BOX);
	}
	public UiPagination getView() {
		return myPagination;
	}
	public void createPaginationGrids(ArrayList<Task> myTaskList, ArrayList<Triplet<Color,String,Integer>> categoryList, int totalPages) {
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i ++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_DEFAULT);
			ArrayList<StackPane> pageEntries = new ArrayList<StackPane>();
			for ( int j = 0; j < entriesPerPage; j ++ ) {
				if ( entryNo >= myTaskList.size() ) {
					break;
				}
				StackPane entryPane = gridHelper.createStyledCell(1, j, UiConstants.STYLE_DEFAULT_BOX, newGrid);
				// another grid within the pane for formatting while keeping a common styling pane
				GridPane paneGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ENTRY_PANE); 
				entryPane.getChildren().add(paneGrid);
				
				Task theTask = myTaskList.get(entryNo);
				addTaskID(theTask, entryNo, j, categoryList, newGrid); // add to main grid
				addTaskDescription(theTask,paneGrid);
				addImage(theTask,paneGrid);
				entryNo++;
				
				pageEntries.add(entryPane);
			}
			myPagination.addGridToPagination(newGrid,pageEntries);
		}
		myPagination.initialize(totalPages); // update UI and bind call back
	}

	private void addTaskID(Task theTask, int id, int row, ArrayList<Triplet<Color,String,Integer>> categoryList, GridPane theGrid) {
		assert(theTask != null);
		UiTextBuilder myBuilder = new UiTextBuilder();
		myBuilder.addMarker(0, UiConstants.STYLE_TEXT_DEFAULT);
		String line = "" + (id + 1);
		Color theColor = Color.WHITE;
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
		
	private void addTaskDescription(Task theTask, GridPane newGrid) {
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
		StackPane pane = gridHelper.createStyledCell(0, 0, "", newGrid);
		pane.setPadding(new Insets(stackPanePadding));
		gridHelper.addTextFlowToCell(0, 0, myBuilder.build(line),TextAlignment.LEFT, newGrid);
	}
	
	private void addImage(Task theTask, GridPane newGrid) { 
		assert(theTask.getTaskType() != null);
		IMAGE_ID imgID;
		switch ( theTask.getTaskType() ) {
			case "EVENT":
				imgID = IMAGE_ID.EVENT;
				break;
			case "DEADLINE":
				imgID = IMAGE_ID.DEADLINE;
				break;
			default:
				imgID = IMAGE_ID.FLOATING;
				break;
		}
		gridHelper.createStyledCell(1, 0, "", newGrid);
		gridHelper.createImageInCell(1,0,UiImageManager.getInstance().getImage(imgID),30,30,newGrid);
	}
}
