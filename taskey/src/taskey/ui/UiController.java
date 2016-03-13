package taskey.ui;

import java.util.ArrayList;
import java.util.Arrays;

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
import taskey.logic.Logic;
import taskey.logic.LogicConstants.ListID;
import taskey.logic.LogicFeedback;
import taskey.logic.ProcessedObject;
import taskey.logic.Task;
import taskey.ui.UiConstants.ActionMode;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.content.UiContentManager;
import taskey.ui.utility.UiImageManager;
import taskey.ui.utility.UiPopupManager;

/**
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
	
	private int mouseX, mouseY;
	private Stage stage;
	private Logic logic;
	private UiClockService clockService;
	private UiDropDown myDropDown;
	private UiContentManager myContentManager;
	private int currentTab;
	private ContentBox currentContent;
	
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
	public void updateDisplay(ArrayList<Task> myTaskList, UiConstants.ContentBox contentID) {
		assert(myTaskList != null);
		myContentManager.updateContentBox(myTaskList, contentID);
	}
	
	public void updateActionDisplay(ArrayList<Task> myTaskList, ActionMode mode) {
		assert(myTaskList != null);
		myContentManager.updateActionContentBox(myTaskList,mode);
	}

	public void updateCategoryDisplay(ArrayList<String> categoryNames, ArrayList<Integer> categoryNums, ArrayList<Color> categoryColors) {
		assert(categoryNames != null);
		assert(categoryNums != null);
		assert(categoryColors != null);
		myContentManager.updateCategoryContentBox(categoryNames,categoryNums,categoryColors);
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

	private ContentBox getCurrentContent() {
		currentContent = ContentBox.fromInteger(myTabs.getSelectionModel().getSelectedIndex());
		return currentContent;
	}
	
	private void handleFeedback( LogicFeedback feedback ) {
		assert(feedback != null);
		Exception statusCode = feedback.getException();
		if ( statusCode != null ) {
			UiPopupManager.getInstance().createPopupLabelAtNode(statusCode.getMessage(), input, 0,input.getHeight(),true); // just set pop up to below input
		}
		
		ArrayList<ArrayList<Task>> allLists = feedback.getTaskLists();
		updateAll(allLists);
		ProcessedObject processed = feedback.getPo();
		String command = processed.getCommand();
		switch (command) {		// change display based on which command was inputted
			case "ADD_DEADLINE": // update 2 lists
			case "ADD_EVENT":
			case "ADD_FLOATING":
				displayTabContents(ContentBox.PENDING);
				break;
			case "DELETE_BY_INDEX":
			case "DELETE_BY_NAME":
				break;	
			case "VIEW":
				String viewType = processed.getViewType();
				if (viewType.equals("GENERAL")) {
					updateActionDisplay(allLists.get(ListID.GENERAL.getIndex()), ActionMode.LIST);
				} else if (viewType.equals("DEADLINES")) {
					updateActionDisplay(allLists.get(ListID.DEADLINE.getIndex()), ActionMode.LIST);
				} else if (viewType.equals("EVENTS")) {
					updateActionDisplay(allLists.get(ListID.EVENT.getIndex()), ActionMode.LIST);
				} else if (viewType.equals("ARCHIVE")) {
					updateActionDisplay(allLists.get(ListID.COMPLETED.getIndex()), ActionMode.LIST);
				}
				displayTabContents(ContentBox.ACTION);
				break;
			case "SEARCH":
				//displayTabContents(ContentBox.ACTION);
				break;	
			case "DONE_BY_INDEX":
			case "DONE_BY_NAME":
				break;	
			case "UPDATE_BY_INDEX_CHANGE_NAME":
			case "UPDATE_BY_INDEX_CHANGE_DATE":
				break;
			default:
				System.out.println("Command not defined");
				break;
		}
	}
	
	public void updateAll(ArrayList<ArrayList<Task>> allLists) {
		updateDisplay(allLists.get(ListID.THIS_WEEK.getIndex()), UiConstants.ContentBox.THIS_WEEK);
		updateDisplay(allLists.get(ListID.PENDING.getIndex()), UiConstants.ContentBox.PENDING);
		updateDisplay(allLists.get(ListID.EXPIRED.getIndex()), UiConstants.ContentBox.EXPIRED);
		ArrayList<String> categoryList = new ArrayList<String>(
				Arrays.asList("General","Deadlines","Events","Archive"));
		ArrayList<Integer> categoryNums = new ArrayList<Integer>(
				Arrays.asList(allLists.get(ListID.GENERAL.getIndex()).size(),allLists.get(ListID.DEADLINE.getIndex()).size(),
						allLists.get(ListID.EVENT.getIndex()).size(),allLists.get(ListID.COMPLETED.getIndex()).size()));
		ArrayList<Color> categoryColors = new ArrayList<Color>(
				Arrays.asList(Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW));
		updateCategoryDisplay(categoryList, categoryNums, categoryColors);
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
				if ( event.getCode().isLetterKey() || event.getCode() == KeyCode.BACK_SPACE) {
					myDropDown.updateMenuItems(new ArrayList<String>(Arrays.asList("PLACEHOLDER" + Math.random(),"PLACEHOLDER" + Math.random())));
					myDropDown.updateMenu();
				}
				if (event.getCode() == KeyCode.ENTER) {
					String line = input.getText();
					if ( line.isEmpty() == false ) {
						input.clear();	
						handleFeedback(logic.executeCommand(getCurrentContent(),line));
						event.consume();
						myDropDown.closeMenu();
					} else {
						myContentManager.processEnter(getCurrentContent());
					}
				}
			}
		});
	
		// to override the default events which shift the caret / cursor position to the start and end
		input.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
	          public void handle(KeyEvent event) {
	        	  if ( event.getCode().isArrowKey()) {
	        		  if ( myDropDown.isMenuOpen()) {
	        			  myDropDown.processArrowKey(event);
	        		  } else {
	        			  myContentManager.processArrowKey(event, getCurrentContent());
	        		  }	  
	        		  event.consume();
	        	  }
	        	  if (event.getCode() == KeyCode.TAB) {
	        		  event.consume();
	        	  }
	        	  if ( event.getCode() == KeyCode.DELETE ) {
	        		  if ( myDropDown.isMenuOpen() == false ) {
	        			  int id = myContentManager.processDelete(getCurrentContent()); 
	        			  if ( id != -1 ) {
	        				  handleFeedback(logic.executeCommand(getCurrentContent(),"del " + id));
	        			  }
	        		  }
	        	  }
	          };
	        });	
	}

	/**
	 * This method is for key inputs anywhere in main window, NOTE THIS HAS
	 * ISSUES, not sure if scene root gets different intervals for updates and
	 * displays Therefore anything that requires a small or instant display
	 * tweak don't put here.
	 *
	 * @param root - root object of scene
	 */
	private void registerRootEventHandler(Parent root) {
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
				} else if (event.getCode() == KeyCode.BACK_QUOTE) {
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