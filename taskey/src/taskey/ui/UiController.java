package taskey.ui;

import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.content.UiContentManager;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiPopupFactory;

/**
 * This class is the main class that handles almost all of the Ui nodes
 * Called by UI_Manager to perform operations
 * @author JunWei
 *
 */
public class UiController {

    @FXML private TabPane myTabs;
    @FXML private TextField input;
    @FXML private Label textPrompt;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private ScrollPane weekPane;
    
    private Stage stage;
    private int currentTab;
    private UiClockService clockService;
    private UiContentManager myFormatter;
    
    public UiContentManager getFormatter() {
    	return myFormatter;
    }
	public void setUpNodes(Stage primaryStage, Parent root) {
		stage = primaryStage; // set up reference
		clockService = new UiClockService(timeLabel, dateLabel);
		clockService.start();
		setUpContentBoxes();
		setUpTabDisplay();
		registerEventHandlersToNodes(root);
	}

	public void setUpContentBoxes() {
		myFormatter = new UiContentManager(clockService);
		myFormatter.addScrollPane(weekPane,ContentBox.WEEKlY); // add weekly list as first
		for (int i = 0; i < myTabs.getTabs().size(); i++) {
			AnchorPane tabContent = (AnchorPane) myTabs.getTabs().get(i).getContent();
			ScrollPane content = (ScrollPane) tabContent.getChildren().get(0);
			myFormatter.addScrollPane(content,ContentBox.fromInteger(i+1));
		}
	}
	
	public void setUpTabDisplay() {
		currentTab = 0;
		input.requestFocus();
		displayTabContents(currentTab);
    }

	public void registerEventHandlersToNodes(Parent root) {
		registerInputEventHandler();
		registerRootEventHandler(root);
	}

	public void displayTabContents(int tabNo) {
		SingleSelectionModel<Tab> selectionModel = myTabs.getSelectionModel();
		selectionModel.select(tabNo);
	}

	public void process( ArrayList<Task> myTaskList, UiConstants.ContentBox contentID) {
		myFormatter.updateContentBox(myTaskList, contentID);
	}
	public void cleanUp() {
		clockService.restart();
		myFormatter.cleanUp();
	}
	
	/************************************ EVENT HANDLERS *******************************************/
	public void registerInputEventHandler() {
		input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					String line = input.getText();
					input.clear();
					
					// Logic.getInstance().getCommand(line);
					
					AnchorPane rootPane = (AnchorPane) input.getParent();
					Node newPopup = UiPopupFactory.getInstance().createPopup("Added " + line, rootPane, input.getLayoutX() , input.getLayoutY() + input.getPrefHeight() * 1.25f);
					UiPopupFactory.getInstance().startFadeTransition(newPopup, 2000, UiConstants.DEFAULT_FADE_TIME);
					
					event.consume();
				}
			}
		});
	}

	public void registerRootEventHandler(Parent root) {
		// for key inputs anywhere in main window
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.TAB) {
					currentTab = myTabs.getSelectionModel().getSelectedIndex();
					currentTab = (currentTab + 1) % myTabs.getTabs().size();
					displayTabContents(currentTab);
					event.consume();
				}
			}
		});
		
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					System.exit(0);
				}
			}
		});
		
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.Q && event.isControlDown()) {
						if ( stage.isIconified() == false ) {
							stage.setIconified(true);
						} 
					}
				}
		});
	}
}