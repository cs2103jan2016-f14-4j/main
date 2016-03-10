package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.content.UiFormatter;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiTextBuilder;

public class UiDefaultFormatter extends UiFormatter {
	private Pagination myPages;
	private int currentPage;
	private int totalPages;
	private ArrayList<Task> theTasks;
	private int currentSelection;
	private int entriesPerPage = 5;
	private int marginSpacing = 3;
	private int constraintPadding = 1; // space for the pagination content bar
	private ArrayList<ArrayList<StackPane>> totalEntries; // track for arrow key events
	
	public UiDefaultFormatter(ScrollPane thePane) {
		super(thePane);
		totalEntries = new ArrayList<ArrayList<StackPane>>(); 
		mainPane.setFitToHeight(true);
		disableScrollBar();
		currentSelection = 0;
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
	public int processDeleteKey() {
		if ( totalEntries.size() == 0 ) {
			return -1;
		}
		return currentPage*entriesPerPage + currentSelection + 1;
	}
	@Override
	public void processArrowKey(KeyEvent event) {
		if ( totalEntries.size() == 0 ) {
			return;
		}
		if ( event.getCode() == KeyCode.DOWN) {
			select(currentSelection + 1);
		} else if ( event.getCode() == KeyCode.UP) {
			select(currentSelection - 1);
		} else if ( event.getCode() == KeyCode.RIGHT) {
			myPages.setCurrentPageIndex(currentPage+1); 
		} else {
			myPages.setCurrentPageIndex(currentPage-1); // note Pagination handles negative values
		}
	}	
	
	public ArrayList<Task> getTasksInPage() {
		if ( totalEntries.size() == 0 ) {
			return null;
		}
		int startIndex = currentPage * entriesPerPage;
		ArrayList<Task> subList = new ArrayList<Task>();
		for ( int i = startIndex; i < theTasks.size() && i < startIndex + entriesPerPage; i ++ ) {
			subList.add(theTasks.get(startIndex));
		}
		return subList;
	}
	private void select(int selection) {
		if ( totalEntries.size() == 0 ) {
			return;
		}
		ArrayList<StackPane> pageContent = totalEntries.get(currentPage);
		selection %= pageContent.size();
		if ( selection < 0 ) {
			selection = pageContent.size()-1;
		}
		// remove previous selection's style
		 StackPane myPane = pageContent.get(currentSelection);
		if ( myPane.getStyleClass().size() > 1) {
			myPane.getStyleClass().remove(1);
		}
		currentSelection = selection;
		myPane = (StackPane) pageContent.get(currentSelection);
		myPane.getStyleClass().add(UiConstants.STYLE_GRAY_BOX);
	}
	
	private void addGridToPagination() {
		addGrid(setUpGrid(UiConstants.GRID_SETTINGS_DEFAULT),true);	
		// Note row constraints can expand cells but not contract, tweak text sizes and spacings to achieve effect
		for (int i = 0; i < entriesPerPage; i++) {
			RowConstraints row = new RowConstraints();
			row.setPercentHeight((100.0-constraintPadding)/entriesPerPage);
			currentGrid.getRowConstraints().add(row);
		}
	}
	private void fillPageContent(int pageIndex) {
		ArrayList<StackPane> pageContent = new ArrayList<StackPane>();
		int entryNo = 0;
		int startIndex = pageIndex * entriesPerPage;
		for (int i = startIndex; i < theTasks.size() && i < startIndex + entriesPerPage; i++) {	
			Task theTask = theTasks.get(i);
			addTaskID(theTask, i, entryNo);	
			// Main content
			createStackForEntry(1,entryNo,pageContent);
			addTaskDescription(theTask, entryNo,pageContent);
			addImage(theTask, entryNo, pageContent);
			entryNo++;
		}
		totalEntries.add(pageContent); 
	}
	// Note this methods creates the whole page by default, therefore we modify it
	private GridPane createPage(int pageIndex) {
		if ( pageIndex >= totalPages ) {
			return null; // invalid pages
		} 
		if ( currentSelection != 0 ) {
			select(0); // de-select previous selection
		}
		if ( myGrids.size() < pageIndex+1  ) {
			// only create a new grid if not enough grids
			addGridToPagination();
			fillPageContent(pageIndex);
		} else {
			currentGrid = myGrids.get(pageIndex);
		}   
		currentSelection = 0;
		currentPage = pageIndex;
		select(0); // only after arraylist has been initialized
		return currentGrid;
    }
	
	// create a stack to place contents on
	private void createStackForEntry(int col, int row, ArrayList<StackPane> pageEntries) {
		StackPane stackOn = createStackPaneInCell(col, row, UiConstants.STYLE_WHITE_BOX, currentGrid);
		StackPane.setMargin(stackOn, new Insets(marginSpacing));
		pageEntries.add(stackOn);
	}

	private void createPagination() {
		myPages = new Pagination(); // because there's no method to clear pages
		if ( totalPages == 0 ) {// for formatting
			myPages.setPageCount(1);
			myPages.setMaxPageIndicatorCount(1);
		} else {
			myPages.setPageCount(totalPages);
			myPages.setMaxPageIndicatorCount(totalPages);
		}
		myPages.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
		myPages.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                return createPage(pageIndex);
            }
        });
	}
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		clearOtherVariables();
		theTasks = myTaskList;
		totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double
		createPagination();
		mainPane.setContent(myPages);
	}

	@Override
	public void clearOtherVariables() {
		totalEntries.clear();
		myGrids.clear(); // remove all grid references, even though they are kept by Pagination
	}
	
	private void addTaskID(Task theTask, int id, int row) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLUE);
		String line = "" + (id + 1);
		element.getChildren().addAll(myConfig.build(line));
		createStyledCell(0, row, UiConstants.STYLE_NUMBER_ICON, currentGrid);
		addTextFlowToCell(0, row, element,TextAlignment.CENTER, currentGrid);
	}
	
	private void addTaskDescription(Task theTask, int row, ArrayList<StackPane> pageEntries) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_BLACK_TO_PURPLE);
		String line = "";
		line += "NAME: "; 
		line += theTask.getTaskName() + "\n";
		line += "DUE: ";
		if (theTask.getDeadline().length() != 0 ) {
			line += " (" + theTask.getDeadline() + ")";
		} else {
			line += "---------";
		}
		line += "\n";
		line += "TAGS: ";
		element.getChildren().addAll(myConfig.build(line));
		addTextFlowToCell(1, row, element,TextAlignment.LEFT, currentGrid);
		pageEntries.get(row).getChildren().add(element); // switch to use this second level wrapper
		StackPane.setMargin(element, new Insets(marginSpacing));
	}
	
	private void addImage(Task theTask, int row,  ArrayList<StackPane> pageEntries) { 
		ImageView img = createImageInCell(1,row,UiImageManager.getInstance().getImage(IMAGE_ID.INBOX),
				30,30,currentGrid);
		pageEntries.get(row).getChildren().add(img); 
		StackPane.setMargin(img, new Insets(marginSpacing));
	}
}
