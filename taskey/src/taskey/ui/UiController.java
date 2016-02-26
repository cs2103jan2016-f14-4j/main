package taskey.ui;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.stage.Popup;
import javafx.stage.Stage;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.content.UiContentManager;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiDropDown;
import taskey.ui.utility.UiPopupFactory;

/**
 * This class is the main class that handles all of the Ui nodes The
 * UiController interfaces with all the major components
 * 
 * @author JunWei
 *
 */
public class UiController {

	@FXML
	private TabPane myTabs;
	@FXML
	private TextField input;
	@FXML
	private Label textPrompt;
	@FXML
	private Label timeLabel;
	@FXML
	private Label dateLabel;
	@FXML
	private ScrollPane weekPane;

	private Stage stage;
	private int currentTab;
	private UiClockService clockService;
	private UiContentManager myManager;
	private UiDropDown myDropDown;

	public void setUpNodes(Stage primaryStage, Parent root) {
		stage = primaryStage; // set up reference
		clockService = new UiClockService(timeLabel, dateLabel);
		clockService.start();
		setUpContentBoxes();
		setUpTabDisplay();
		registerEventHandlersToNodes(root);
		myDropDown = new UiDropDown();
	}

	// nodes or classes that need layout bounds are initialized here
	public void setUpNodesWhichNeedBounds() {
		myDropDown.createMenu(stage, input);
	}

	public void setUpContentBoxes() {
		myManager = new UiContentManager(clockService);
		myManager.setUpContentBox(weekPane, ContentBox.WEEKLY); // add weekly list as first
		for (int i = 0; i < myTabs.getTabs().size(); i++) {
			AnchorPane tabContent = (AnchorPane) myTabs.getTabs().get(i).getContent();
			ScrollPane content = (ScrollPane) tabContent.getChildren().get(0);
			myManager.setUpContentBox(content, ContentBox.fromInteger(i + 1));
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

	public void updateDisplay(ArrayList<Task> myTaskList, UiConstants.ContentBox contentID) {
		myManager.updateContentBox(myTaskList, contentID);
	}

	public void cleanUp() {
		clockService.restart();
		myManager.cleanUp();
		UiPopupFactory.getInstance().cleanUp();
	}

	/**
	 * Sets scene style sheets, input is assumed to be checked before calling
	 * this method
	 * 
	 * @param styleSheets - style sheets to use for the display
	 *            : ArrayList<String>
	 */
	public void setStyleSheets(ArrayList<String> styleSheets) {
		ObservableList<String> myStyleSheets = stage.getScene().getStylesheets();
		myStyleSheets.clear();
		try {
			for (int i = 0; i < styleSheets.size(); i++) { // load all style sheets into list
				myStyleSheets.add(getClass().getResource(UiConstants.UI_CSS_PATH_OFFSET + styleSheets.get(i)).toExternalForm());
			}
		} catch (Exception excep) {
			System.out.println(excep + " loading style sheets");
		}
	}

	/************************************ EVENT HANDLERS  *******************************************/
	public void registerInputEventHandler() {

		input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				myDropDown.updateMenuItems(UiMain.getInstance().randomInput("TEST" + Math.random(), 5));
				myDropDown.updateMenu();
				if (event.getCode() == KeyCode.ENTER) {
					String line = input.getText();
					input.clear();

					// Logic.getInstance().getCommand(line);
					Popup newPopup = UiPopupFactory.getInstance().createPopupLabelAtNode("Added Successfully", input, 0,input.getHeight());
					UiPopupFactory.getInstance().createFadeTransition(newPopup, 2000, UiConstants.DEFAULT_FADE_TIME, 1.0, 0.0, true).play();

					event.consume();

					myDropDown.hideMenu();
				}
			}
		});
	}

	/**
	 * This method is for key inputs anywhere in main window, NOTE THIS HAS
	 * ISSUES, not sure if scene root gets different intervals for updates and
	 * displays Therefore anything that requires a small or instant display
	 * tweak don't put here
	 * 
	 * @param root - root object of scene
	 */
	public void registerRootEventHandler(Parent root) {
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.TAB) {
					currentTab = myTabs.getSelectionModel().getSelectedIndex();
					currentTab = (currentTab + 1) % myTabs.getTabs().size();
					displayTabContents(currentTab);
					event.consume();
				} else if (event.getCode() == KeyCode.ESCAPE) {
					System.exit(0);
				} else if (event.getCode() == KeyCode.Q && event.isControlDown()) {
					if (stage.isIconified() == false) {
						stage.setIconified(true);
					}
				} else if (event.getCode() == KeyCode.BACK_QUOTE) {
					setStyleSheets(UiConstants.UI_LIGHT_STYLE);
				}
			}
		});
	}
}