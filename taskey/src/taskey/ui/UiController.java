package taskey.ui;

import java.io.IOException;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.UiConstants.ActionListMode;
import taskey.ui.content.UiContentManager;
import taskey.ui.utility.UiClockService;
import taskey.ui.utility.UiDropDown;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiPopupFactory;
import taskey.logic.Logic;

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
	private Label dateLabel;
	@FXML
	private StackPane dragBar;
	@FXML
	private ScrollPane categoryPane;
	@FXML
	private ImageView crossButton;
	
	private int mouseX, mouseY;
	private Stage stage;
	
	private UiClockService clockService;
	private UiDropDown myDropDown;
	private UiContentManager myContentManager;
	private int currentTab;
	private ContentBox currentContent;
	
	public void setUpNodes(Stage primaryStage, Parent root) {
		stage = primaryStage; // set up reference
		clockService = new UiClockService(null, dateLabel);
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

	private void setUpContentBoxes() {
		myContentManager = new UiContentManager(clockService);
		for (int i = 0; i < myTabs.getTabs().size(); i++) {
			AnchorPane tabContent = (AnchorPane) myTabs.getTabs().get(i).getContent();
			ScrollPane content = (ScrollPane) tabContent.getChildren().get(0);
			myContentManager.setUpContentBox(content, ContentBox.fromInteger(i));
		}
		myContentManager.setUpContentBox(categoryPane,ContentBox.CATEGORY);
	}

	private void setUpTabDisplay() {
		currentTab = 0;
	//	input.requestFocus();
		displayTabContents(currentTab);
	}

	private void registerEventHandlersToNodes(Parent root) {
		registerInputEventHandler();
		registerRootEventHandler(root);
		registerDragHandler();
		registerButtonHandlers();
	}

	public void displayTabContents(int tabNo) {
		SingleSelectionModel<Tab> selectionModel = myTabs.getSelectionModel();
		selectionModel.select(tabNo);
		currentContent = ContentBox.fromInteger(tabNo);
	}

	public void updateDisplay(ArrayList<Task> myTaskList, UiConstants.ContentBox contentID) {
		myContentManager.updateContentBox(myTaskList, contentID);
	}
	
	public void updateActionDisplay(ArrayList<Task> myTaskList, ActionListMode mode) {
		myContentManager.updateActionContentBox(myTaskList,mode);
		currentTab = myTabs.getTabs().size()-1; // focus the tab
		//displayTabContents(currentTab);
	}
	public void updateCategoryDisplay(ArrayList<String> myCategoryList, ArrayList<Integer> categoryNums) {
		// TODO Auto-generated method stub
		myContentManager.updateCategoryContentBox(myCategoryList,categoryNums);
	}

	public void cleanUp() {
		clockService.restart();
		myContentManager.cleanUp();
		UiPopupFactory.getInstance().cleanUp();
	}

	/**
	 * Sets scene style sheets, input is assumed to be checked before calling
	 * this method
	 * 
	 * @param styleSheets - style sheets to use for the display as an Array List
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
	private void registerInputEventHandler() {

		input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {	
				if ( event.getCode().isLetterKey() || event.getCode() == KeyCode.BACK_SPACE) {
					myDropDown.updateMenuItems(UiMain.getInstance().randomInput("TEST" + Math.random(), 4));
					myDropDown.updateMenu();
				}
				if ( event.getCode().isArrowKey()) {
	        		  myDropDown.processArrowKey(event);
	        		  myContentManager.processArrowKey(event);
				}
				if (event.getCode() == KeyCode.ENTER) {
					String line = input.getText();
					input.clear();

					int statusCode = 0;
					statusCode = Logic.getInstance().executeCommand(currentContent,line);
					
					Popup newPopup = UiPopupFactory.getInstance().createPopupLabelAtNode("Status code: " + statusCode, input, 0,input.getHeight());
					UiPopupFactory.getInstance().createFadeTransition(newPopup, 2000, UiConstants.DEFAULT_FADE_TIME, 1.0, 0.0, true).play();

					event.consume();

					myDropDown.closeMenu();
				}
			}
		});
	
		// to override the default events which shift the caret / cursor position to the start and end
		input.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
	          public void handle(KeyEvent event) {
	        	  if ( event.getCode().isArrowKey()) {
	        		  if  (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) {
	        			  event.consume();
	        		  }
	        	  }
	          };
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
	private void registerRootEventHandler(Parent root) {
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
					setStyleSheets(UiConstants.STYLE_UI_LIGHT);
				}
			}
		});

	}
	
	private void registerDragHandler() {
		dragBar.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
			    // record X,Y differences
				mouseX = (int) (stage.getX() - mouseEvent.getScreenX());
				mouseY = (int) (stage.getY() - mouseEvent.getScreenY());
		  }
		});
		dragBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
			  @Override public void handle(MouseEvent mouseEvent) {
			  	stage.setX(mouseEvent.getScreenX() + mouseX);
			    stage.setY(mouseEvent.getScreenY() + mouseY);
			  }
		});
	}
	
	private void registerButtonHandlers() {
		crossButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				crossButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.CROSS_SELECT));
		  }
		});
		
		crossButton.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				// 1st level intersect
				if ( mouseEvent.getPickResult().getIntersectedNode() == crossButton) {
					System.exit(0);
				} else {
					crossButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.CROSS_DEFAULT));  
				}
			}
		});
	}
}