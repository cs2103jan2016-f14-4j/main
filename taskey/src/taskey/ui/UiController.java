package taskey.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import taskey.constants.Triplet;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ActionMode;
import taskey.constants.UiConstants.ContentBox;
import taskey.constants.UiConstants.IMAGE_ID;
import taskey.logic.Logic;
import taskey.logic.LogicConstants.ListID;
import taskey.parser.AutoComplete;
import taskey.logic.LogicFeedback;
import taskey.logic.ProcessedObject;
import taskey.logic.Task;
import taskey.ui.content.UiContentManager;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiPopupManager;

/**
 * @@author A0125419H
 * This class is the main class that handles all of the Ui nodes. 
 * UiController is the only interface between Ui and Logic
 *
 * @author JunWei
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
	@FXML
	private ImageView minusButton;
	@FXML
	private Label expiredIcon;
	
	private int mouseX, mouseY;
	private Stage stage;
	private Logic logic;
	private UiClockService clockService;
	private UiDropDown myDropDown;
	private UiContentManager myContentManager;
	private int currentTab;
	private ContentBox currentContent;
	private ArrayList<String> inputHistory;
	private int historyIterator;
	/**
	 * Sets the up nodes.
	 *
	 * @param primaryStage the primary stage
	 * @param root the root
	 */
	public void setUpNodes(Stage primaryStage, Parent root) {
		assert(primaryStage != null);
		assert(root != null);
		stage = primaryStage; // set up reference
		clockService = new UiClockService(null, dateLabel);
		clockService.start();
		setUpContentBoxes();
		setUpTabDisplay();
		registerEventHandlersToNodes(root);
		myDropDown = new UiDropDown();
		input.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
		input.getStyleClass().add(UiConstants.STYLE_INPUT_NORMAL);		
		inputHistory = new ArrayList<String>();
		historyIterator = 0;
		myTabs.requestFocus(); // to display prompt at the start
		
		logic = new Logic();
		updateAll(logic.getAllTaskLists());
	}

	/**
	 * Sets up nodes which need bounds.
	 * nodes or classes that need layout bounds are initialized here
	 */
	public void setUpNodesWhichNeedBounds() {
		assert(myDropDown != null);
		myDropDown.createMenu(stage, input);
	}

	private void setUpContentBoxes() {
		assert(myTabs != null);
		myContentManager = new UiContentManager();
		for (int i = 0; i < myTabs.getTabs().size(); i++) {
			AnchorPane tabContent = (AnchorPane) myTabs.getTabs().get(i).getContent();
			ScrollPane content = (ScrollPane) tabContent.getChildren().get(0);
			myContentManager.setUpContentBox(content, ContentBox.fromInteger(i));
		}
		myContentManager.setUpContentBox(categoryPane,ContentBox.CATEGORY);
	}

	private void setUpTabDisplay() {
		currentTab = 0;
		input.requestFocus();
		displayTabContents(ContentBox.THIS_WEEK);
	}
	
	private void registerEventHandlersToNodes(Parent root) {
		registerInputEventHandler();
		registerRootEventHandler(root);
		registerDragHandler();
		registerButtonHandlers();
	}

	public void displayTabContents(ContentBox toContent) {
		SingleSelectionModel<Tab> selectionModel = myTabs.getSelectionModel();
		selectionModel.select(toContent.getValue());
		currentContent = toContent;
	}
	
	private ContentBox getCurrentContent() {
		currentContent = ContentBox.fromInteger(myTabs.getSelectionModel().getSelectedIndex());
		return currentContent;
	}
	
	public void updateDisplay(ArrayList<Task> myTaskList, UiConstants.ContentBox contentID) {
		assert(myTaskList != null);
		myContentManager.updateContentBox(myTaskList, contentID);
	}
	
	public void updateActionDisplay(ArrayList<Task> myTaskList, ActionMode mode) {
		myContentManager.updateActionContentBox(myTaskList,mode);
	}

	public void updateCategoryDisplay(ArrayList<Triplet<Color,String,Integer>> categoryList) {
		assert(categoryList != null);
		myContentManager.updateCategoryContentBox(categoryList);
	}

	/**
	 * Sets scene style sheets, input is assumed to be valid before calling
	 * this method, if input is invalid, throws an exception
	 *
	 * @param styleSheets - style sheets to use for the display as an Array List
	 */
	public void setStyleSheets(ArrayList<String> styleSheets) {
		assert(styleSheets != null);
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
	
	private void handleFeedback( LogicFeedback feedback ) {
		assert(feedback != null);
		Exception statusCode = feedback.getException();
		if ( statusCode != null ) {
			UiPopupManager.getInstance().createPopupLabelAtNode(statusCode.getMessage(), input, 0,input.getHeight()*1.25F,true); // just set pop up to below input
		}
		
		ArrayList<ArrayList<Task>> allLists = feedback.getTaskLists();	
		ProcessedObject processed = feedback.getPo();
		String command = processed.getCommand();
		switch (command) {		// change display based on which command was inputted
			case "ADD_DEADLINE": // update 2 lists
			case "ADD_EVENT":
			case "ADD_FLOATING":
				displayTabContents(ContentBox.PENDING);
				updateAll(allLists);
				break;
			case "VIEW":
				/*
				String viewType = processed.getViewType();
				if (viewType.equals("GENERAL")) {
					updateActionDisplay(allLists.get(ListID.GENERAL.getIndex()), ActionMode.LIST);
				} else if (viewType.equals("DEADLINES")) {
					updateActionDisplay(allLists.get(ListID.DEADLINE.getIndex()), ActionMode.LIST);
				} else if (viewType.equals("EVENTS")) {
					updateActionDisplay(allLists.get(ListID.EVENT.getIndex()), ActionMode.LIST);
				} else if (viewType.equals("ARCHIVE")) {
					updateActionDisplay(allLists.get(ListID.COMPLETED.getIndex()), ActionMode.LIST);
				} else if (viewType.equals("HELP")) {
					updateActionDisplay(null, ActionMode.HELP);
				}
				displayTabContents(ContentBox.ACTION); */ 
				break;
			case "SEARCH":
				updateActionDisplay(allLists.get(ListID.SEARCH.getIndex()), ActionMode.LIST);
				displayTabContents(ContentBox.ACTION);
				break;	
			default:
				updateAll(allLists);
				break;
		}
	}
	
	public void updateAll(ArrayList<ArrayList<Task>> allLists) {
		ArrayList<Triplet<Color,String,Integer>> categoryList;
		categoryList = new ArrayList<Triplet<Color,String,Integer>>(Arrays.asList(
				new Triplet<Color,String,Integer>(Color.BLUE,"General",allLists.get(ListID.GENERAL.getIndex()).size()),
				new Triplet<Color,String,Integer>(Color.RED,"Deadlines",allLists.get(ListID.DEADLINE.getIndex()).size()),
				new Triplet<Color,String,Integer>(Color.GREEN,"Events",allLists.get(ListID.EVENT.getIndex()).size()),
				new Triplet<Color,String,Integer>(Color.YELLOW,"Archive",allLists.get(ListID.COMPLETED.getIndex()).size())
				));
		updateCategoryDisplay(categoryList);
		
		updateDisplay(allLists.get(ListID.THIS_WEEK.getIndex()), UiConstants.ContentBox.THIS_WEEK);
		updateDisplay(allLists.get(ListID.PENDING.getIndex()), UiConstants.ContentBox.PENDING);
		updateDisplay(allLists.get(ListID.EXPIRED.getIndex()), UiConstants.ContentBox.EXPIRED);	
		expiredIcon.setText(String.valueOf(allLists.get(ListID.EXPIRED.getIndex()).size()));
	}
	
	public void cleanUp() {
		clockService.restart();
		myContentManager.cleanUp();
		UiPopupManager.getInstance().cleanUp();
	}
	
	/**
	 * ********************************** EVENT HANDLERS  ******************************************.
	 */
	private void registerInputEventHandler() {
		assert(input != null);
		input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {	
				input.getStyleClass().remove(UiConstants.STYLE_INPUT_ERROR);			
				if ( event.getCode().isDigitKey() || event.getCode().isLetterKey() || event.getCode() == KeyCode.BACK_SPACE) {
					ArrayList<String> suggestions = logic.autoCompleteLine(input.getText().trim(), getCurrentContent());		
					if ( suggestions == null ) {
						input.getStyleClass().add(UiConstants.STYLE_INPUT_ERROR); // suggestion not found, invalid input
						myDropDown.closeMenu();
					} else {
						myDropDown.updateMenuItems(suggestions);
						myDropDown.updateMenu();
					}
				}
				if (event.getCode() == KeyCode.ENTER) {	
					String selection = myDropDown.getSelectedItem();
					if ( selection.isEmpty() == false ) { // make selected item as input text
						input.setText(selection + " ");
						input.selectEnd();
						input.deselect();
						myDropDown.closeMenu();
					} else  {
						String line = input.getText();
						if ( line.isEmpty() == false ) {						
							input.clear();	
							handleFeedback(logic.executeCommand(getCurrentContent(),line));
							event.consume();
							myDropDown.closeMenu();
							inputHistory.add(line);
							if ( inputHistory.size() > UiConstants.MAX_INPUT_HISTORY ) {
								inputHistory.remove(0);
								inputHistory.trimToSize();
							}
							historyIterator = inputHistory.size(); // set to size instead of size()-1, for up key to work properly
						} else {
							myContentManager.processEnter(getCurrentContent());
						}
					}
				}
			}
		});
	
		// to override default events such as shifting the caret / cursor position to the start and end
		input.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode().isArrowKey()) {
					if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) { // get previous / next input history
						event.consume();
						if (inputHistory.size() != 0) {
							String line;
							if (event.getCode() == KeyCode.UP) {
								historyIterator = Math.max(historyIterator - 1, 0);
							} else if (event.getCode() == KeyCode.DOWN) {
								historyIterator++; 
							}
							if ( historyIterator > inputHistory.size()-1) {
								historyIterator = inputHistory.size(); // out of bounds
								line = "";
							} else {
								line = inputHistory.get(historyIterator);
							}
							input.setText(line);
						} 
						input.selectEnd();
						input.deselect();
					}
				} else if (event.getCode() == KeyCode.TAB) {
					event.consume();
				}
			};
		});
	}

	/**
	 * This method is for key inputs anywhere in main window
	 *
	 * @param root - root object of scene
	 */
	private void registerRootEventHandler(Parent root) {
		root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				input.requestFocus(); // give focus to textfield
				if (myDropDown.isMenuShowing()) {
					if (event.getCode().isArrowKey()) {
						myDropDown.processArrowKey(event);
						event.consume();
					}
				} else {
					if (event.getCode() == KeyCode.DELETE) {
						int id = myContentManager.processDelete(getCurrentContent());
						if (id != 0) {
							handleFeedback(logic.executeCommand(getCurrentContent(), "del " + id));
						}
					} else if ( event.getCode().isArrowKey()) {
						if  ( event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT) {
							myContentManager.processArrowKey(event, getCurrentContent());
						}
					}
				}
				if ( event.getCode() == KeyCode.PAGE_UP || event.getCode() == KeyCode.PAGE_DOWN) {
					myContentManager.processPageUpAndDown(event, getCurrentContent());
				}
			}
		});
		
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.TAB) {
					currentTab = myTabs.getSelectionModel().getSelectedIndex();
					currentTab = (currentTab + 1) % myTabs.getTabs().size();
					displayTabContents(ContentBox.fromInteger(currentTab));
					event.consume();
				} else if (event.getCode() == KeyCode.ESCAPE) {
					System.exit(0);
				} else if (event.isControlDown() && event.getCode() == KeyCode.W){
					crossButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.CROSS_DEFAULT));  
					stage.close();
				} else if (event.getCode() == KeyCode.F1) {
					updateActionDisplay(null, ActionMode.HELP);
					displayTabContents(ContentBox.ACTION);
				} else if (event.getCode() == KeyCode.F2) {
					setStyleSheets(UiConstants.STYLE_UI_DEFAULT);
				} else if (event.getCode() == KeyCode.F3) {
					setStyleSheets(UiConstants.STYLE_UI_LIGHT);
				}
			}
		});

	}
	
	private void registerDragHandler() {
		assert(dragBar != null);
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
		assert(crossButton != null);
		assert(minusButton != null);
		crossButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.CROSS_DEFAULT)); 
		minusButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.MINUS_DEFAULT)); 
		
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
		minusButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				minusButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.MINUS_SELECT));
		  }
		});
		minusButton.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent mouseEvent) {
				if ( mouseEvent.getPickResult().getIntersectedNode() == minusButton) {
					minusButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.MINUS_DEFAULT)); 
					stage.close();
				} else {
					minusButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.MINUS_DEFAULT)); 
				}
			}
		});
	}
}