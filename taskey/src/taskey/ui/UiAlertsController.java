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
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ImageID;
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;
import taskey.ui.utility.UiAnimationManager;
import taskey.ui.utility.UiGridHelper;
import taskey.ui.utility.UiImageManager;

/**
 * @@author A0125419H
 * 
 * This class implements another Stage / window for displaying alerts
 * Note that this is separate from the main window, and hence
 * we are reusing some classes for formatting data
 * @author Junwei
 *
 */
public class UiAlertsController {

	@FXML
	private ScrollPane scrollPane;
	
	private GridPane displayGrid;
	private AnchorPane rootNode;
	private Stage stage;
	private UiGridHelper gridHelper = new UiGridHelper("");
	private ArrayList<UiAlert> currentAlerts = new ArrayList<UiAlert>();
	private ArrayList<UiAlert> alertHistory = new ArrayList<UiAlert>(); // dismissed alerts are not re-added
	
	//----- Used by UiTrayModule ------
	public Stage getStage() {
		return stage;
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
        rootNode.setMinHeight(primaryScreenBounds.getHeight()); // resize anchorPane, scrollpane will resize to fit    
        rootNode.requestFocus();
	}
	//-------------------------------
	
	public void setUpStage(Region contentRootRegion) {
		TaskeyLog.getInstance().log(LogSystems.UI, "Setting up Alert Window...", Level.ALL);
		
		stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT); 
		stage.setScene(new Scene(contentRootRegion));
		stage.getScene().setFill(null);
		
        rootNode = (AnchorPane) contentRootRegion;
        rootNode.setOpacity(UiConstants.ALERTS_OPACITY);
        
        setUpScene();
        addRootEventHandlers();

        TaskeyLog.getInstance().log(LogSystems.UI, "Alert Window has been set up...", Level.ALL);
	}
	
	private void setUpScene() {
		ObservableList<String> myStyleSheets = stage.getScene().getStylesheets();
        for ( int i = 0; i < UiConstants.STYLE_UI_ALERT_WINDOW.size(); i++ ) {
        	myStyleSheets.add(getClass().getResource(UiConstants.UI_CSS_PATH_OFFSET 
        										     + UiConstants.STYLE_UI_ALERT_WINDOW.get(i)).toExternalForm());
        }
        
        displayGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ALERT);
        displayGrid.setAlignment(Pos.BOTTOM_CENTER); // such that alerts appear from the bottom
        scrollPane.setContent(displayGrid);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        
        for ( int i = 0; i < UiConstants.MAX_ALERTS; i++ ) {  // used as an array
        	currentAlerts.add(null); 
        }
	}
	
	private void addRootEventHandlers() {
		rootNode.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.F5) { // for testing purposes
					addEntry(new UiAlert());
				} else if (event.getCode() == KeyCode.W && event.isControlDown()) {
					hide();
				}
			}
		});
	}	
	
	private void addClickHandler(StackPane thePane) {
		thePane.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				FadeTransition fade = UiAnimationManager.getInstance().createFadeTransition(thePane, 0,
						UiConstants.DEFAULT_ANIM_DURATION/2, 1.0, 0.0);
				fade.play();
				fade.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						StackPane wrapper = (StackPane) fade.getNode().getParent();
						int index = GridPane.getRowIndex(wrapper);
						removeEntry(index);
					}
				});
				thePane.setOnMouseReleased(null); // remove handler
			}
		});	
	}
	
	private void addStartAnimation(StackPane thePane) {
		// animate within the grid, hence translation coordinates are 0.0
		TranslateTransition shift = UiAnimationManager.getInstance().createTranslateTransition(thePane, 
				new Pair<Double,Double>(stage.getWidth(), 0.0), 
				new Pair<Double,Double>(0.0, 0.0), UiConstants.DEFAULT_ANIM_DURATION/2);
		shift.play();
		shift.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ScaleTransition scale = UiAnimationManager.getInstance().
						createScaleTransition(thePane, 1.25f, 1.25f, 4, true, 
											  UiConstants.DEFAULT_ANIM_DURATION/5);
				scale.play();
			}
		});
	}
	
	private void addEntry(UiAlert newAlert) {
		if ( checkAlertExisted(newAlert) == true ) {
			return;
		}
		int gridIndex = findFirstFreeSlot();
		if ( gridIndex == -1 ) {
			return;
		} else {
			StackPane thePane = gridHelper.createStackPaneInCell(0, gridIndex, UiConstants.STYLE_ALERT_BOX, displayGrid);
			GridPane entryGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ALERT_ENTRY_PANE);
			gridHelper.createScaledRectInCell(0, 0, newAlert.getColor(), entryGrid);
			gridHelper.addTextFlowToCell(1, 0, newAlert.getTextFlow(),TextAlignment.LEFT, entryGrid);	
			gridHelper.createImageInCell(2, 0, UiImageManager.getInstance().getImage(ImageID.URGENT_MARK), 
										 15, 15, entryGrid);
			thePane.getChildren().add(entryGrid);
			
			addStartAnimation(thePane);
			addClickHandler(thePane);					
			currentAlerts.set(gridIndex, newAlert);	// set to array
		}
	}
	
	private boolean checkAlertExisted(UiAlert newAlert) {
		if ( currentAlerts.contains(newAlert)) {
			return true;
		} else if ( alertHistory.contains(newAlert)) {
			return true;
		}
		return false;
	}
	
	private int findFirstFreeSlot() {
		for ( int i = UiConstants.MAX_ALERTS-1; i >= 0; i -- ) { // search from the bottom of screen
			if ( currentAlerts.get(i) == null ) {
				return i;
			}
		}
		return -1;
	}
	
	private void removeEntry( int gridIndex ) {
		StackPane wrapper = gridHelper.getWrapperAtCell(0, gridIndex, displayGrid);
		displayGrid.getChildren().remove(wrapper); // free the slot	in the grid display
		alertHistory.add(currentAlerts.get(gridIndex)); 
		currentAlerts.set(gridIndex, null); 
		
		 // shift all entries down
		for ( int i = UiConstants.MAX_ALERTS-1; i >= 0; i -- ) {
			if ( currentAlerts.get(i) == null ) {
				int swapIndex = -1;
				UiAlert swapAlert = null;
				for ( int j = i-1; j >= 0; j -- ) {
					if ( currentAlerts.get(j) != null ) { // has an alert
						StackPane temp = gridHelper.getWrapperAtCell(0, j, displayGrid);
						displayGrid.getChildren().remove(temp);
						displayGrid.add(temp, 0, i); // move to empty slot
						swapIndex = j;
						swapAlert = currentAlerts.get(j);
						break;
					}	
				}
				if ( swapIndex != -1 ) {
					currentAlerts.set(swapIndex, null); // free slot of alert to be shifted down
					currentAlerts.set(i, swapAlert);
				}
			}	
		}
	}
	
	/**
	 * Convenience method for setting all alerts
	 * @param alertList
	 */
	public void setAllAlerts(ArrayList<UiAlert> alertList) { 
		for ( int i = 0; i < UiConstants.MAX_ALERTS; i ++ ) {
			if ( alertList.contains(currentAlerts.get(i)) == false ) { // remove old / invalid alerts
				removeEntry(i);
			}
		}
		for ( int i = 0; i < alertList.size() ; i ++) {
			addEntry(alertList.get(i));
		}
	}
}
