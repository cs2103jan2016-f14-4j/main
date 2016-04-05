package taskey.ui;

import java.util.ArrayList;
import java.util.logging.Level;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import taskey.constants.UiConstants;
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;
import taskey.ui.utility.UiAnimationManager;
import taskey.ui.utility.UiPopupManager;

/**
 * @@author A0125419H
 * This class implements the dropdown box used by AutoComplete
 * Note that the menu created is not within the main window,
 * but as a dropDown box implemented in a pop up
 * 
 * @author JunWei
 */

public class UiDropDown {
	private static final int TEXT_SIZE_FROM_CSS = 15; // these have to be referenced from sharedStyles.css
	private static final String FONT_NAME_FROM_CSS = "Montserrat";
	private static final int DROPDOWN_OFFSET = 10; // so dropdown doesn't cover text if window is too big
	
	private TextField inputBox;
	private Window mainWindow;
	private Popup myMenu = null;
	private FadeTransition fade;
	private StackPane selectedPane; // selected stack pane for styling
	private int currentSelection;
	private int currentItemSize;
	
	public void createMenu(Stage primaryStage, TextField input) {
		assert(input != null);
		assert(primaryStage != null);
		
		TaskeyLog.getInstance().log(LogSystems.UI, "Setting up Drop down Menu...", Level.ALL);
		
		// set up references
		inputBox = input; 
		mainWindow = primaryStage.getScene().getWindow();
		myMenu = UiPopupManager.getInstance().createPopupMenu();
		createFadeAnimation();
		currentItemSize = 0;
		currentSelection = -1;
		selectedPane = null;
		
		TaskeyLog.getInstance().log(LogSystems.UI, "Drop down menu has been set up...", Level.ALL);
	}

	private void createFadeAnimation() {
		fade = UiAnimationManager.getInstance().createFadeTransition(myMenu.getContent().get(0), 
				 UiConstants.DEFAULT_FADE_START_DELAY*5, 
				 UiConstants.DEFAULT_ANIM_DURATION, 1.0, 0.0);
		// create custom handler
		fade.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				closeMenu();
			}
		});
	}
	
	public void updateMenuItems(ArrayList<String> items) {
		assert(items != null);
		currentItemSize = items.size(); 
		deSelect();
		if ( items.size() == 0 ) {		
			return;
		}
		int longestIndex = findIndexOfLongestItem(items);
		createStackPanes(items,longestIndex);
	}
	
	private int findIndexOfLongestItem(ArrayList<String> items) {
		int longestIndex = 0;
		// find longest text bounds
		Text longest = new Text(items.get(0));
		// setting font for Text recalculates layout bounds
		longest.setFont(Font.font(FONT_NAME_FROM_CSS, TEXT_SIZE_FROM_CSS));
		for ( int i = 1; i < items.size(); i++) {
			Text itemText = new Text(items.get(i));
			itemText.setFont(Font.font(FONT_NAME_FROM_CSS, TEXT_SIZE_FROM_CSS)); 
			if ( itemText.getLayoutBounds().getWidth() > longest.getLayoutBounds().getWidth() ) {
				longest = itemText;
				longestIndex = i;
			}
		}
		return longestIndex;
	}
	
	private void createStackPanes(ArrayList<String> items, int longestIndex) {
		VBox myContent = (VBox) myMenu.getContent().get(0);
		ObservableList<Node> menuItems = myContent.getChildren(); // list of stack panes
		menuItems.clear();
		
		StackPane longestPane = new StackPane();
		Label longestLabel = new Label(items.get(longestIndex)); 
		longestPane.getChildren().add(longestLabel);
		
		for (int i = 0; i < items.size(); i++) {
			if ( i == longestIndex ) {
				myContent.getChildren().add(longestPane);
			} else {
				StackPane myPane = new StackPane();
				Label myLabel = new Label(items.get(i));
				myPane.getChildren().add(myLabel);
				
				 // bind width (calculations delay till stage is shown)
				myLabel.prefWidthProperty().bind(longestPane.widthProperty());				
				myContent.getChildren().add(myPane);
			}
		}	
		// add styles
		for ( int i = 0; i < myContent.getChildren().size(); i ++ ) {
			StackPane currentPane = (StackPane) myContent.getChildren().get(i);
			Label currentLabel = (Label) currentPane.getChildren().get(0);
			currentLabel.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
			currentLabel.getStyleClass().add(UiConstants.STYLE_PROMPT_DEFAULT);
		}
	}
	
	/**
	 * Update menu, called to update position and correct the display issues.
	 */
	public void updateMenu() {
		if (mainWindow == null || myMenu == null) { // if not initialized yet and received input
			return;
		}
		String line = inputBox.getText();
		if (line.equals("")) {
			closeMenu();
		} else {
			refresh();  // fix display issues
			ShiftMenu();
		}
	}

	private void ShiftMenu() {
		assert(inputBox != null);
		double width = getWidthOfTextFieldInput(inputBox);
		Bounds bounds = inputBox.getBoundsInLocal();
		Bounds screenBounds = inputBox.localToScreen(bounds);
		UiPopupManager.getInstance().resize(myMenu);

		// restrict menu from stretching beyond the TextField
		myMenu.setAnchorX(Math.min(screenBounds.getMinX() + inputBox.getWidth() * UiPopupManager.getInstance().getXRatio(), 
								   screenBounds.getMinX() + width * UiPopupManager.getInstance().getXRatio()));
		myMenu.setAnchorY(screenBounds.getMinY() + inputBox.getHeight() * UiPopupManager.getInstance().getYRatio());
		myMenu.setAnchorX(myMenu.getAnchorX() + DROPDOWN_OFFSET * UiPopupManager.getInstance().getXRatio());
	}

	/**
	 * This method creates a Text object to approximate the bounds of a text
	 * field input.
	 *
	 * @param field - TextField to approximate from
	 * @return width
	 */
	private double getWidthOfTextFieldInput(TextField field) {
		assert(field != null);
		Text text = new Text(field.getText());
		text.setFont(Font.font(FONT_NAME_FROM_CSS, TEXT_SIZE_FROM_CSS)); 
		double width = text.getLayoutBounds().getWidth();
		return width;
	}
	
	public String getSelectedItem() {
		assert(myMenu.getContent().get(0) != null);
		if ( currentSelection == -1 ) {
			return "";
		}
		ObservableList<Node> menuItems = ((VBox)myMenu.getContent().get(0)).getChildren(); // list of stack panes
		StackPane myPane = (StackPane) menuItems.get(currentSelection);
		Label content = (Label)myPane.getChildren().get(0);
		return content.getText();
	}

	public void closeMenu() {
		assert(myMenu != null);
		myMenu.hide();
		currentItemSize = 0;
		deSelect();
	}
	
	public boolean isMenuShowing() {
		assert(myMenu != null);
		return myMenu.isShowing();
	}
	
	/**
	 * Most of the time after switching styles or setting visibility
	 * The popup window has some tears, probably because it renders separately from the main program
	 * This fixes that issue
	 */
	private void refresh() {
		assert(myMenu != null);
		assert(fade != null);
		myMenu.hide();
		restartFade();
		myMenu.show(mainWindow);
	}
	
	/**
	 * This method sets opacity full for the menu
	 */
	private void restartFade() {
		fade.stop();
		fade.getNode().setOpacity(1);
		fade.playFromStart();
	}
	
	private void deSelect() {
		if ( selectedPane != null ) {
			Label toStyle = (Label) selectedPane.getChildren().get(0);
			toStyle.getStyleClass().remove(UiConstants.STYLE_PROMPT_SELECTED);
			toStyle.getStyleClass().add(UiConstants.STYLE_PROMPT_DEFAULT);
			selectedPane = null;
			currentSelection = -1;
		}	
	}
	
	private void select(int selection) {
		VBox myContent = (VBox) myMenu.getContent().get(0);
		assert(selection >= 0 && selection < myContent.getChildren().size());

		deSelect(); // remove styles first if any
		
		// do selection
		currentSelection = selection;
		ObservableList<Node> menuItems = ((VBox)myMenu.getContent().get(0)).getChildren(); // list of stack panes
		selectedPane = (StackPane) menuItems.get(currentSelection);
		Label toStyle = (Label) selectedPane.getChildren().get(0);
		toStyle.getStyleClass().remove(UiConstants.STYLE_PROMPT_SELECTED);
		toStyle.getStyleClass().add(UiConstants.STYLE_PROMPT_SELECTED);
		restartFade();
	}
	
	public void processArrowKey(KeyEvent event) {
		assert(event != null);
		if ( currentItemSize == 0 ) {
			return;
		}
		if ( event.getCode() == KeyCode.DOWN) {
			select((currentSelection + 1) % currentItemSize);
		} else if ( event.getCode() == KeyCode.UP) {
			select((currentSelection - 1) < 0 ? currentItemSize - 1 : currentSelection - 1);
		}
	}
}