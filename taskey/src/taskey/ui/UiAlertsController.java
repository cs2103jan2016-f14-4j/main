package taskey.ui;

import java.util.ArrayList;
import java.util.logging.Level;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ImageID;
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;
import taskey.messenger.Task;
import taskey.ui.content.UiGridHelper;
import taskey.ui.content.UiTextBuilder;
import taskey.ui.utility.UiAnimationManager;
import taskey.ui.utility.UiImageManager;

/**
 * @@author A0125419H
 * 
 * This class implements another Stage / window for displaying tasks as alerts
 * @author Junwei
 *
 */
public class UiAlertsController {

	@FXML
	private ScrollPane scrollPane;
	private GridPane theGrid;
	private AnchorPane root;
	private Stage stage;
	private UiGridHelper gridHelper = new UiGridHelper("");
	private ArrayList<Task> currentAlerts = new ArrayList<Task>(); // used as array
	private ArrayList<Task> alertHistory = new ArrayList<Task>(); // dismissed tasks are not re-added

	public Stage getStage() {
		return stage;
	}
		
	public void setUpStage(Region contentRootRegion) {
		TaskeyLog.getInstance().log(LogSystems.UI, "Setting up Alert Window...", Level.ALL);
		
		stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(new Scene(contentRootRegion));
		stage.getScene().setFill(null);
		
        root = (AnchorPane) contentRootRegion;
        root.setOpacity(UiConstants.ALERTS_OPACITY);
        
        setUpScene();
        addRootEventHandlers();
        for ( int i = 0; i < UiConstants.MAX_ALERTS; i++ ) {
        	currentAlerts.add(null); // set as null array
        }
        
        TaskeyLog.getInstance().log(LogSystems.UI, "Alert Window has been set up...", Level.ALL);
	}
	
	private void setUpScene() {
		ObservableList<String> myStyleSheets = stage.getScene().getStylesheets();
        for ( int i = 0; i < UiConstants.STYLE_UI_ALERT_WINDOW.size(); i++ ) {
        	myStyleSheets.add(getClass().getResource(UiConstants.UI_CSS_PATH_OFFSET 
        										     + UiConstants.STYLE_UI_ALERT_WINDOW.get(i)).toExternalForm());
        }
        theGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ALERT);
        theGrid.setAlignment(Pos.BOTTOM_CENTER);
        scrollPane.setContent(theGrid);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
	}
	
	private void addRootEventHandlers() {
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.F5) { // for testing purposes
					Task temp = new Task();
					temp.setTaskName(String.valueOf(Math.random()).substring(0, 10));
					addEntry(temp);
				} else if (event.getCode() == KeyCode.W && event.isControlDown()) {
					hide();
				}
			}
		});
	}	
	
	public void hide() {
		stage.hide();
	}

	public void show() {
		stage.show();		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(primaryScreenBounds.getMaxX() - stage.getWidth());
        stage.setY(0);
        stage.setHeight(primaryScreenBounds.getHeight());      
        root.setMinHeight(primaryScreenBounds.getHeight()); // resize anchorPane, scrollpane will resize to fit    
        root.requestFocus();
	}
	
	private void addClickHandler(StackPane thePane) {
		thePane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				FadeTransition fade = UiAnimationManager.getInstance().createFadeTransition(thePane, 0,
						UiConstants.DEFAULT_FADE_TIME/2, 1.0, 0.0);
				fade.play();
				fade.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						StackPane wrapper = (StackPane) fade.getNode().getParent();
						int index = GridPane.getRowIndex(wrapper);
						removeEntry(index);
					}
				});
				thePane.setOnMouseReleased(null);
			}
		});	
	}
	
	private void addStartAnimation(StackPane thePane) {
		// animate within the grid, hence coordinates are 0
		TranslateTransition shift = UiAnimationManager.getInstance().createTranslateTransition(thePane, 
				new Pair<Double,Double>(stage.getWidth(), 0.0), 
				new Pair<Double,Double>(0.0, 0.0), 500);
		shift.play();
		shift.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ScaleTransition scale = UiAnimationManager.getInstance().
						createScaleTransition(thePane, 1.25f, 1.25f, 4, true, 200);
				scale.play();
			}
		});
	}
	
	public void addEntry(Task newEntry) {
		if ( checkTaskExisted(newEntry) == true ) {
			return;
		}
		int gridIndex = findFirstFreeSlot();
		if ( gridIndex == -1 ) {
			return;
		} else {
			UiTextBuilder myBuilder = new UiTextBuilder();
			myBuilder.addMarker(0, UiConstants.STYLE_TEXT_DEFAULT);
			String line = "" + newEntry.getTaskName();
			myBuilder.addMarker(line.length(), UiConstants.STYLE_TEXT_RED);
			line += "\n " + newEntry.getDeadline();
			Color theColor = null;
			switch ( newEntry.getPriority()) {
				case 2: theColor = Color.RED;
						break;
				case 1: theColor = Color.ORANGE;
						break;
				default:
						theColor = Color.GREEN;
			}
			StackPane thePane = gridHelper.createStackPaneInCell(0, gridIndex, UiConstants.STYLE_ALERT_BOX, theGrid);
			GridPane entryGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ALERT_ENTRY_PANE);
			gridHelper.createScaledRectInCell(0, 0, theColor, entryGrid);
			gridHelper.addTextFlowToCell(1, 0, myBuilder.build(line),TextAlignment.LEFT, entryGrid);	
			gridHelper.createImageInCell(2, 0, UiImageManager.getInstance().getImage(ImageID.URGENT_MARK), 
										 15, 15, entryGrid);
			thePane.getChildren().add(entryGrid);
			
			addStartAnimation(thePane);
			addClickHandler(thePane);					
			currentAlerts.set(gridIndex, newEntry);	
		}
	}
	
	private boolean checkTaskExisted(Task newEntry) {
		if ( currentAlerts.contains(newEntry)) {
			return true;
		} else if ( alertHistory.contains(newEntry)) {
			return true;
		}
		return false;
	}
	
	private int findFirstFreeSlot() {
		for ( int i = UiConstants.MAX_ALERTS-1; i >= 0; i -- ) {
			if ( currentAlerts.get(i) == null ) {
				return i;
			}
		}
		return -1;
	}
	
	private void removeEntry( int gridIndex ) {
		StackPane wrapper = gridHelper.getWrapperAtCell(0, gridIndex, theGrid);
		theGrid.getChildren().remove(wrapper); // free the slot	
		alertHistory.add(currentAlerts.get(gridIndex));
		currentAlerts.set(gridIndex, null);
		
		 // shift all entries down
		for ( int i = UiConstants.MAX_ALERTS-1; i >= 0; i -- ) {
			if ( currentAlerts.get(i) == null ) {
				int swapIndex = -1;
				Task swapTask = null;
				for ( int j = i-1; j >= 0; j -- ) {
					if ( currentAlerts.get(j) != null ) { // non empty
						StackPane temp = gridHelper.getWrapperAtCell(0, j, theGrid);
						theGrid.getChildren().remove(temp);
						theGrid.add(temp, 0, i); // move to empty slot
						swapIndex = j;
						swapTask = currentAlerts.get(j);
						break;
					}	
				}
				if ( swapIndex != -1 ) {
					currentAlerts.set(swapIndex, null); // free slot of task to be shifted down
					currentAlerts.set(i, swapTask);
				}
			}	
		}
	}
	
	/**
	 * Convenience method for setting all alerts
	 * @param taskList
	 */
	public void setAll(ArrayList<Task> taskList) { // set all task lists
		for ( int i = 0; i < UiConstants.MAX_ALERTS; i ++ ) {
			if ( taskList.contains(currentAlerts.get(i)) == false ) { // remove old alerts
				removeEntry(i);
			}
		}
		for ( int i = 0; i < taskList.size() ; i ++) {
			addEntry(taskList.get(i));
		}
	}
}
