package taskey.ui.content.formatters;

import java.util.ArrayList;

import javafx.animation.TranslateTransition;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Pair;
import taskey.logic.Task;
import taskey.ui.UiConstants;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.content.UiFormatter;
import taskey.ui.utility.UiAnimationManager;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiTextBuilder;

public class UiDefaultFormatter extends UiFormatter {
	
	//private ArrayList<Task>
	private ArrayList<StackPane> myEntries;
	private int spacing = 5;
	
	public UiDefaultFormatter(GridPane _gridPane, UiClockService _clockService) {
		super(_gridPane, _clockService);
		myEntries = new ArrayList<StackPane>(); 
	}

	public void createEntry(int col, int row) {
		StackPane stackOn = createStackPaneInCell(0, row, UiConstants.STYLE_RED_BOX, currentGrid);
		StackPane.setMargin(stackOn, new Insets(5));
		myEntries.add(stackOn);
	}
	@Override
	public void format(ArrayList<Task> myTaskList) {
		assert(myTaskList != null);	
		for (int i = 0; i < myTaskList.size(); i++) {	
			createEntry(0,i);
			Task theTask = myTaskList.get(i);
			addTaskDescription(theTask,  i);
			addTaskID(theTask, i);	
			addImage(theTask,  i);
		}
	}

	@Override
	public void clearOtherVariables() {
		myEntries.clear();
	}
	
	private void addTaskDescription(Task theTask, int row) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		myConfig.addMarker(0, UiConstants.STYLE_TEXT_GREEN);
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
		myEntries.get(row).getChildren().add(element); // switch to use this second level wrapper
	}
	
	private void addTaskID(Task theTask, int row) {
		assert(theTask != null);
		UiTextBuilder myConfig = new UiTextBuilder();
		TextFlow element = new TextFlow();
		element = new TextFlow();
		element.getChildren().addAll(myConfig.build("ID: -1"));
		
		Label temp = createLabelInCell(0, row, "ID: #@!#", UiConstants.STYLE_NUMBER_ICON, currentGrid);
		myEntries.get(row).getChildren().add(temp); 
		StackPane.setAlignment(temp, Pos.TOP_RIGHT);
	}
	
	private void addImage(Task theTask, int row) { 
		ImageView img = createImageInCell(0,row,UiImageManager.getInstance().getImage(IMAGE_ID.INBOX),
				30,30,currentGrid);
		myEntries.get(row).getChildren().add(img); 
		StackPane thePane = myEntries.get(row);
		TranslateTransition shift = UiAnimationManager.getInstance().createTranslateTransition(
				thePane, 
				new Pair<Double,Double>(thePane.getLayoutX(),thePane.getLayoutY()),
				new Pair<Double,Double>(thePane.getLayoutX()-currentGrid.getWidth(), thePane.getLayoutY()),
				1000);
		shift.play();
	}

	
}
