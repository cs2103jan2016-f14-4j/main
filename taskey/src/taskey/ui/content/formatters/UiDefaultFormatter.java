package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.IMAGE_ID;
import taskey.messenger.Task;
import taskey.ui.content.UiFormatter;
import taskey.ui.content.UiPagination;
import taskey.ui.content.UiTextBuilder;
import taskey.ui.utility.UiImageManager;

/**
 * @@author A0125419H
 * This class is responsible for formatting the Default box
 * The default box is used by this week, pending, expired
 * 
 * @author junwei
 */

public class UiDefaultFormatter extends UiFormatter {
	private int entriesPerPage = 6;
	private UiTaskView myTaskView;
	private int lastNum = -1;
	
	public UiDefaultFormatter(ScrollPane thePane) {
		super(thePane);
		myTaskView = new UiTaskView(entriesPerPage);
		
		mainPane.setContent(myTaskView.getView().getPagination());
		mainPane.setFitToHeight(true);
		
		createPromptNoTasks();
	}

	@Override
	public int processEnterKey() {
		return 0;
	}
	
	@Override
	public void processArrowKey(KeyEvent event) {
		myTaskView.getView().processKey(event);
	}

	@Override
	public int processDeleteKey() {
		return myTaskView.getView().getSelection() + 1;
	}
	
	@Override
	public void processPageUpAndDown(KeyEvent event) {
		myTaskView.getView().processKey(event);
	}
	
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);			
		if ( myTaskList.size() == 0 ) {	
			mainPane.setContent(currentGrid);	
		} else {
			mainPane.setContent(myTaskView.getView().getPagination());
			myTaskView.getView().clear();
			int totalPages = (int) Math.ceil(myTaskList.size()/1.0/entriesPerPage); // convert to double	
			myTaskView.createPaginationGrids(myTaskList,categoryList,totalPages);
			if ( lastNum != -1 && myTaskList.size() > lastNum ) { // addition of a task
				myTaskView.getView().selectInPage(totalPages-1, entriesPerPage); // select last
			} 
			lastNum = myTaskList.size();
		}
	}
	private void createPromptNoTasks() {
		// create prompt in absence of tasks
		setGrid(gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_SINGLE));
		RowConstraints row = new RowConstraints();
		row.setPercentHeight(100);
		currentGrid.getRowConstraints().add(row);
		gridHelper.createStyledCell(0, 0, "", currentGrid);
		gridHelper.createLabelInCell(0, 0, "No tasks in window, add some tasks to get started!", "", currentGrid);
	}

	@Override
	public void cleanUp() {
		myTaskView.clear();
		clearCurrentGridContents();
	}	
}
