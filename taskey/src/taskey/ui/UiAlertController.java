package taskey.ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Pair;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ActionMode;
import taskey.constants.UiConstants.ContentBox;
import taskey.constants.UiConstants.IMAGE_ID;
import taskey.logic.Task;
import taskey.ui.content.UiGridHelper;
import taskey.ui.content.UiTextBuilder;
import taskey.ui.utility.UiAnimationManager;
import taskey.ui.utility.UiImageManager;

/**
 * @@author A0125419H
 * 
 * This class implements another Stage / window for displaying alerts
 * @author Junwei
 *
 */
public class UiAlertController {

	@FXML
	private ScrollPane scrollPane;
	private PieChart chart;
	private GridPane theGrid;
	private AnchorPane root;
	private Stage stage;
	private UiGridHelper gridHelper = new UiGridHelper("");
	private ArrayList<Boolean> isSlotFree = new ArrayList<Boolean>();
	private int MAX_SLOTS = 10;
	
	private static UiAlertController instance = null;
	private UiAlertController(){
	}
	public static UiAlertController getInstance() {
		if ( instance == null ) {
			instance = new UiAlertController();
		}
		return instance;
	}
		
	public void setUpStage(Region contentRootRegion) {
		stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
		stage.setScene(new Scene(contentRootRegion));
		stage.getScene().setFill(null);
		ObservableList<String> myStyleSheets = stage.getScene().getStylesheets();
		myStyleSheets.add(getClass().getResource(UiConstants.UI_CSS_PATH_OFFSET + "sharedStyles.css").toExternalForm());
        myStyleSheets.add(getClass().getResource(UiConstants.UI_CSS_PATH_OFFSET + "alertStyles.css").toExternalForm());

        theGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ALERT);
        theGrid.setAlignment(Pos.BOTTOM_CENTER);
        scrollPane.setContent(theGrid);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
       
        root = (AnchorPane) contentRootRegion;
        root.setOpacity(0.8f);
        
        for ( int i = 0; i < MAX_SLOTS; i++ ) {
        	isSlotFree.add(new Boolean(true));
        }
       // setUpChart();
        // show();
        root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if ( event.getCode() == KeyCode.F5) {
					Task temp = new Task();
					temp.setPriority(1);
					temp.setTaskName(String.valueOf(Math.random()));
					addEntry(temp);
				} else if ( event.getCode() == KeyCode.W && event.isControlDown() ) {
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
	private void setUpChart() {
		ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data("Grapefruit", 13),
                new PieChart.Data("Oranges", 25),
                new PieChart.Data("Plums", 10),
                new PieChart.Data("Pears", 22),
                new PieChart.Data("Apples", 30));
        chart = new PieChart(pieChartData);
        chart.getStyleClass().add("numberIcon");

        StackPane wrapper = gridHelper.getWrapperAtCell(0, 0, theGrid);
        wrapper.getChildren().add(chart);
        isSlotFree.set(0, false);		
	}
	public void updateChart( ArrayList<ArrayList<Task>> theLists) {
		
	}
	
	private void addClickHandler(StackPane theEntry) {
		theEntry.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				FadeTransition fade = UiAnimationManager.getInstance().createFadeTransition(theEntry, 0,
						UiConstants.DEFAULT_FADE_TIME, 1.0, 0.0);
				fade.play();
				fade.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						StackPane wrapper = (StackPane) fade.getNode().getParent();
						int index = GridPane.getRowIndex(wrapper);
						theGrid.getChildren().remove(wrapper); // free the slot

						removeEntry(index);
					}
				});
				theEntry.setOnMouseReleased(null);

			}
		});	
	}
	public void addEntry(Task newEntry) {
		int gridIndex = findFirstFreeSlot();
		if ( gridIndex == -1 ) {
			return;
		} else {
			UiTextBuilder myBuilder = new UiTextBuilder();
			myBuilder.addMarker(0, UiConstants.STYLE_TEXT_DEFAULT);
			String line = "" + newEntry;
			Color theColor = null;
			switch ( newEntry.getPriority()) {
				case 2: theColor = Color.RED;
						break;
				case 1: theColor = Color.ORANGE;
						break;
				default:
						theColor = Color.GREEN;
			}
			StackPane thePane = gridHelper.createStackPaneInCell(0, gridIndex, "numberIcon", theGrid);
			GridPane entryGrid = gridHelper.setUpGrid(UiConstants.GRID_SETTINGS_ALERT_ENTRY_PANE);
			gridHelper.createScaledRectInCell(0, 0, theColor, entryGrid);
			gridHelper.addTextFlowToCell(1, 0, myBuilder.build(line),TextAlignment.LEFT, entryGrid);	
			thePane.getChildren().add(entryGrid);
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
			addClickHandler(thePane);					
			isSlotFree.set(gridIndex, false);			
		}
	}
	
	private int findFirstFreeSlot() {
		for ( int i = MAX_SLOTS-1; i >= 0; i -- ) {
			if ( isSlotFree.get(i) == true ) {
				return i;
			}
		}
		return -1;
	}
	
	private void removeEntry( int gridIndex ) {
		isSlotFree.set(gridIndex, true);
		 // shift all entries down
		for ( int i = MAX_SLOTS-1; i >= 0; i -- ) {
			if ( isSlotFree.get(i) == true ) {
				int swapIndex = -1;
				for ( int j = i-1; j >= 0; j -- ) {
					if ( isSlotFree.get(j) == false ) { // non empty
						StackPane temp = gridHelper.getWrapperAtCell(0, j, theGrid);
						theGrid.getChildren().remove(temp);
						theGrid.add(temp, 0, i); // move to empty slot
						swapIndex = j;
						break;
					}	
				}
				if ( swapIndex != -1 ) {
					isSlotFree.set(swapIndex, true);
					isSlotFree.set(i, false);
				}
			}	
		}
	}
	public Stage getStage() {
		return stage;
	}
}
