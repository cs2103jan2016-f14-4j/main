package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.content.UiFormatter;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiTextBuilder;

public class UiWeekFormatter extends UiFormatter {
	
	public UiWeekFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
	}

	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);
		for (int i = 0; i < myTaskList.size(); i++) {
			Task theTask = myTaskList.get(i);
			addTaskEntry(theTask, i);
		}
	}

	private void addTaskEntry(Task theTask, int row) {
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
		line += "\n\n";
		line += "TAGS: ";
		element.getChildren().addAll(myConfig.build(line));
		addTextFlowToCell(0, row, element,TextAlignment.LEFT, currentGrid);
		
		element = new TextFlow();
		element.getChildren().addAll(myConfig.build("ID: -1"));
		
		StackPane ID_Wrapper = createStackPaneInCell(0, row, UiConstants.STYLE_NUMBER_ICON, currentGrid);
		ID_Wrapper.getChildren().add(element);
		GridPane.setValignment(ID_Wrapper, VPos.TOP);
		GridPane.setHalignment(ID_Wrapper, HPos.RIGHT);
		
		StackPane ImageWrapper = addImageToCell(0,row,UiImageManager.getInstance().getImage(IMAGE_ID.INBOX),
												30,30,currentGrid);
		GridPane.setValignment(ImageWrapper, VPos.CENTER);
		GridPane.setHalignment(ImageWrapper, HPos.CENTER);
		ImageWrapper.setTranslateX(50);
	}
}