package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.IMAGE_ID;
import taskey.logic.Task;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;
import taskey.ui.utility.UiImageManager;

/**
 * This class is responsible for formatting the Default box
 * The default box is used by this week, pending, expired
 * 
 * @author junwei
 */
public class UiDefaultFormatter extends UiFormatter {
	private int entriesPerPage = 6;
	private UiPagination myPagination;
	
	public UiDefaultFormatter(ScrollPane thePane) {
		super(thePane);
		myPagination = new UiPagination(UiConstants.STYLE_HIGHLIGHT_BOX);
		mainPane.setContent(myPagination.getPagination());
		mainPane.setFitToHeight(true);
	}

	@Override
	public int processEnterKey() {
		return 0;
	}
	
	@Override
	public void processArrowKey(KeyEvent event) {
		myPagination.processArrowKey(event);
	}

	@Override
	public int processDeleteKey() {
		return myPagination.getSelection() + 1;
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		myPagination.clear();
		createPaginationGrids(myTaskList);
	}

	private void createPaginationGrids(ArrayList<Task> myTaskList) {
		int totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double	
		int entryNo = 0;
		for ( int i = 0; i < totalPages; i ++ ) {
			GridPane newGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_DEFAULT);
			newGrid.setGridLinesVisible(true);
			
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
				addImage(theTask, j,newGrid);
				entryNo++;
			}
			myPagination.addGridToPagination(newGrid,pageEntries);
		}
		myPagination.initialize(totalPages); // update UI and bind call back
	}

	private void addTaskID(Task theTask, int id, int row, GridPane theGrid) {
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
		pane.setPadding(new Insets(2));
		gridHelper.addTextFlowToCell(1, row, myBuilder.build(line),TextAlignment.LEFT, newGrid);
	}
	
	private void addImage(Task theTask, int row,  GridPane newGrid) { 
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
		ImageView img = gridHelper.createImageInCell(1,row,UiImageManager.getInstance().getImage(imgID),30,30,newGrid);
		img.setTranslateX(200);
	}
}
