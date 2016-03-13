package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;
import taskey.ui.utility.UiImageManager;

public class UiDefaultFormatter extends UiFormatter {
	private UiPagination myPagination;
	public UiDefaultFormatter(ScrollPane thePane) {
		super(thePane);
		disableScrollBar();
		myPagination = new UiPagination(UiConstants.STYLE_GRAY_BOX);
		mainPane.setContent(myPagination.getPagination());
		mainPane.setFitToHeight(true);
	}

	// disable all default inputs, inputs are passed through UiContentManager
	private void disableScrollBar() {
		mainPane.setVbarPolicy(ScrollBarPolicy.NEVER);
		mainPane.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() { 
          public void handle(KeyEvent event) {
        	  if ( event.getCode().isArrowKey()) {
        			  event.consume();
        	  }
          }
        });	
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
		return myPagination.getSelection();
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		myPagination.clear();
		createPaginationGrids(myTaskList);
	}

	// This function creates the grids used by pagination
	private void createPaginationGrids(ArrayList<Task> myTaskList) {
		int entriesPerPage = 5;
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
				StackPane entryPane = gridHelper.createStyledCell(1, j, UiConstants.STYLE_WHITE_BOX, newGrid);
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
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
		String line = "" + (id + 1);
		element.getChildren().addAll(myConfig.build(line));
		gridHelper.createStyledCell(0, row, UiConstants.STYLE_NUMBER_ICON, theGrid);
		gridHelper.addTextFlowToCell(0, row, element,TextAlignment.CENTER, theGrid);
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
		gridHelper.addTextFlowToCell(1, row, element,TextAlignment.LEFT, newGrid);
	}
	
	private void addImage(Task theTask, int row,  GridPane newGrid) { 
		ImageView img = gridHelper.createImageInCell(1,row,UiImageManager.getInstance().getImage(IMAGE_ID.INBOX),
				30,30,newGrid);
		img.setTranslateX(150);
	}
}
