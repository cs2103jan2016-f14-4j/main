package taskey.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

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
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;
import taskey.logic.Logic;
import taskey.logic.LogicFeedback;
import taskey.logic.LogicMemory;
import taskey.logic.ProcessedObject;
import taskey.logic.TagCategory;
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
	private UiUpdateService updateService;
	private UiDropDown myDropDown;
	private UiContentManager myContentManager;
	private int currentTab;
	private ContentBox currentContent;
	private ArrayList<String> inputHistory;
	private int historyIterator;
	
	/**
	 * Sets up the nodes.
	 *
	 * @param primaryStage the primary stage
	 * @param root the root
	 */
	public void setUpNodes(Stage primaryStage, Parent root) {
		assert(primaryStage != null);
		assert(root != null);
		TaskeyLog.getInstance().log(LogSystems.UI, "Setting up Main Controller...", Level.ALL);
		stage = primaryStage; // set up reference
		myDropDown = new UiDropDown();
		setUpContentBoxes();
		setUpTabDisplay();		
		setUpButtonStyles();
		setUpInput();
		registerEventHandlersToNodes(root);	
		setUpLogic();	
		setUpUpdateService();
		
		myTabs.requestFocus(); // to display prompt at the start
		TaskeyLog.getInstance().log(LogSystems.UI, "Main Controller has been set up...", Level.ALL);
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
	
	private void setUpButtonStyles() {
		crossButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.CROSS_DEFAULT)); 
		minusButton.setImage(UiImageManager.getInstance().getImage(IMAGE_ID.MINUS_DEFAULT)); 
	}
	
	/**
	 * Sets up variables related to input
	 */
	private void setUpInput() {
		input.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
		input.getStyleClass().add(UiConstants.STYLE_INPUT_NORMAL);		
		inputHistory = new ArrayList<String>();
		historyIterator = 0;
	}
	
	private void registerEventHandlersToNodes(Parent root) {
		registerInputEventHandler();
		registerRootEventHandler(root);
		registerDragHandler();
		registerButtonHandlers();
	}
	
	private void setUpLogic() {
		logic = new Logic();
		updateAllContents(logic.getTagCategoryList(),logic.getAllTaskLists());
	}

	private void setUpUpdateService() {
		updateService = new UiUpdateService(dateLabel);
		updateService.start();
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
				myStyleSheets.add(getClass().getResource(UiConstants.UI_CSS_PATH_OFFSET 
														 + styleSheets.get(i)).toExternalForm());
			}
		} catch (Exception excep) {
			System.out.println(excep + UiConstants.STYLE_SHEETS_LOAD_FAIL);
		}
	}
	
	private void handleFeedback( LogicFeedback feedback ) {
		assert(feedback != null);
		Exception statusCode = feedback.getException();
		if ( statusCode != null ) {
			 // just set pop up to appear below input
			UiPopupManager.getInstance().updatePromptMessage(statusCode.getMessage(), input, 
																0,input.getHeight()*1.25F);
		}
		
		ArrayList<ArrayList<Task>> allLists = feedback.getTaskLists();	
		ProcessedObject processed = feedback.getPo();
		String command = processed.getCommand();
		switch (command) {		 // change display based on which command was input
			case "ADD_DEADLINE": 
			case "ADD_EVENT":
			case "ADD_FLOATING":
				displayTabContents(ContentBox.PENDING);
				break;
			case "VIEW_BASIC":
				if ( processed.getViewType().get(0).equals("help")) {
					displayTabContents(ContentBox.ACTION);
					myContentManager.setActionMode(UiConstants.ActionMode.HELP);
					return; // don't need to update all
				}
			case "VIEW_TAGS":
			case "SEARCH":
				displayTabContents(ContentBox.ACTION);
				myContentManager.setActionMode(UiConstants.ActionMode.LIST);
				break;	
			default:
				break;
		}
		// just update all displays, rather than splitting it into each switch case
		updateAllContents(logic.getTagCategoryList(),allLists); 
	}
	
	/**
	 * Create a header of fixed categories for the category list
	 * @param allLists - all the task lists
	 * @return categoryListHeader
	 */
	private ArrayList<Triplet<Color, String, Integer>> createCategoriesHeader(ArrayList<ArrayList<Task>> allLists) {
		ArrayList<Triplet<Color,String,Integer>> categoryListHeader = new ArrayList<Triplet<Color,String,Integer>>();
		ArrayList<Task> pendingList = allLists.get(LogicMemory.INDEX_PENDING);
		int priorityNums[] = new int[3];
		for ( int i = 0; i < pendingList.size(); i++ ) {
			priorityNums[pendingList.get(i).getPriority()-1]++; // increase numbers for each priority
		}
		categoryListHeader.add(new Triplet<Color,String,Integer>(Color.RED,"HIGH", priorityNums[2]));
		categoryListHeader.add(new Triplet<Color,String,Integer>(Color.ORANGE,"MED", priorityNums[1]));
		categoryListHeader.add(new Triplet<Color,String,Integer>(Color.GREEN,"LOW", priorityNums[0]));
		
		categoryListHeader.add(new Triplet<Color,String,Integer>(Color.CADETBLUE,"General",
																 allLists.get(LogicMemory.INDEX_FLOATING).size()));
		categoryListHeader.add(new Triplet<Color,String,Integer>(Color.CADETBLUE,"Deadlines",
																 allLists.get(LogicMemory.INDEX_DEADLINE).size()));
		categoryListHeader.add(new Triplet<Color,String,Integer>(Color.CADETBLUE,"Events",
																 allLists.get(LogicMemory.INDEX_EVENT).size()));
		categoryListHeader.add(new Triplet<Color,String,Integer>(Color.CADETBLUE,"Archive",
																 allLists.get(LogicMemory.INDEX_COMPLETED).size()));
		
		return categoryListHeader;
	}
	
	private void updateAllContents(ArrayList<TagCategory> tagList, ArrayList<ArrayList<Task>> allLists) {
		ArrayList<Triplet<Color,String,Integer>> categoryList = createCategoriesHeader(allLists);
		// Add tags in addition to the default categories
		for ( int i = 0 ; i < tagList.size(); i++ ) {
			categoryList.add(new Triplet<Color,String,Integer>(Color.DIMGRAY,tagList.get(i).getTagName(), 
															   tagList.get(i).getNumTags()));
		}
		myContentManager.updateCategoryContentBox(categoryList);
		
		myContentManager.updateContentBox(allLists.get(LogicMemory.INDEX_THIS_WEEK), UiConstants.ContentBox.THIS_WEEK);
		myContentManager.updateContentBox(allLists.get(LogicMemory.INDEX_PENDING), UiConstants.ContentBox.PENDING);
		myContentManager.updateContentBox(allLists.get(LogicMemory.INDEX_EXPIRED), UiConstants.ContentBox.EXPIRED);	
		myContentManager.updateContentBox(allLists.get(LogicMemory.INDEX_ACTION), UiConstants.ContentBox.ACTION);	
		expiredIcon.setText(String.valueOf(allLists.get(LogicMemory.INDEX_EXPIRED).size()));
	}
	
	public void cleanUp() {
		updateService.restart();
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
				if ( event.getCode().isDigitKey() || event.getCode().isLetterKey() 
					 || event.getCode() == KeyCode.BACK_SPACE) {
					processAutoComplete();
				}
				if (event.getCode() == KeyCode.ENTER) {	
					processEnter();
				}
			}
		});
	
		// to override default events such as shifting the caret / cursor position to the start and end
		input.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode().isArrowKey()) {
					if (event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN) { 
						// get previous / next input history
						setInputFromHistory(event.getCode());
						event.consume();
					}
				} else if (event.getCode() == KeyCode.TAB) {
					event.consume();
				}
			};
		});
	}

	private void processAutoComplete() {
		input.getStyleClass().remove(UiConstants.STYLE_INPUT_ERROR);	
		ArrayList<String> suggestions = logic.autoCompleteLine(input.getText().trim(), getCurrentContent());		
		if ( suggestions == null ) {
			input.getStyleClass().add(UiConstants.STYLE_INPUT_ERROR); // suggestion not found, invalid input
			myDropDown.closeMenu();
		} else {
			myDropDown.updateMenuItems(suggestions);
			myDropDown.updateMenu();
		}
	}
	
	private void processEnter() {
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
	
	/**
	 * This method sets the textfield input depending on up and down arrows,
	 * which mean previous and next respectively.
	 * 
	 * @param code - KeyCode up or down
	 */
	private void setInputFromHistory( KeyCode code ) {
		if (inputHistory.size() != 0) {
			String line;
			if (code == KeyCode.UP) {
				historyIterator = Math.max(historyIterator - 1, 0);
			} else if (code == KeyCode.DOWN) {
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

	/**
	 * This method is for key inputs anywhere in main window
	 *
	 * @param root - root object of scene
	 */
	private void registerRootEventHandler(Parent root) {
		root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				handleKeyPressInRoot(event);
			}
		});
		
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				handleKeyReleaseInRoot(event);
			}
		});
	}
	
	private void handleKeyPressInRoot(KeyEvent event) {
		input.requestFocus(); // give focus to textfield
		if (myDropDown.isMenuShowing()) {
			if (event.getCode().isArrowKey()) {
				myDropDown.processArrowKey(event);
				event.consume();
			}
		} else if (input.getText().isEmpty()) {
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
	
	private void handleKeyReleaseInRoot(KeyEvent event) {
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
			myContentManager.setActionMode(ActionMode.HELP);
			displayTabContents(ContentBox.ACTION);
		} else if (event.getCode() == KeyCode.F2) {
			setStyleSheets(UiConstants.STYLE_UI_DEFAULT);
		} else if (event.getCode() == KeyCode.F3) {
			setStyleSheets(UiConstants.STYLE_UI_LIGHT);
		} 
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
			    myDropDown.closeMenu();
			  }
		});
	}
	
	private void registerButtonHandlers() {
		assert(crossButton != null);
		assert(minusButton != null);	
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