package taskey.ui;

import java.util.ArrayList;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import taskey.constants.UiConstants;
import taskey.ui.utility.UiAnimationManager;
import taskey.ui.utility.UiPopupManager;

/**
 * @@author A0125419H
 * This class implements the dropdown box used by AutoComplete
 * 
 * @author JunWei
 */

public class UiDropDown {
	private static final int MAX_ITEMS = 4;
	private TextField myInput;
	private Popup myMenu = null;
	private FadeTransition fade;
	private Window myWindow;
	private StackPane selectedPane;
	private int currentSelection;
	private int currentItemSize;
	
	public void createMenu(Stage primaryStage, TextField input) {
		assert(input != null);
		assert(primaryStage != null);
		myInput = input; // set up reference
		myMenu = UiPopupManager.getInstance().createPopupMenu(MAX_ITEMS);
		fade = UiAnimationManager.getInstance().createFadeTransition(myMenu.getContent().get(0), 5000, 1000, 1.0, 0.0);
		// create custom handler
		fade.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				closeMenu();
			}
		});
		myWindow = primaryStage.getScene().getWindow();
		currentItemSize = 0;
		currentSelection = -1;
		selectedPane = null;
	}

	public void updateMenuItems(ArrayList<String> items) {
		assert(items != null);
		VBox myContent = (VBox) myMenu.getContent().get(0);
		ObservableList<Node> menuItems = myContent.getChildren(); // list of stack panes
		menuItems.clear();
		for (int i = 0; i < items.size() && i < MAX_ITEMS; i++) {
			StackPane myPane = new StackPane();
			Label text = new Label(items.get(i));
			text.getStyleClass().add(UiConstants.STYLE_TEXT_ALL);
			text.getStyleClass().add(UiConstants.STYLE_PROMPT);
			myPane.setAlignment(Pos.CENTER_LEFT);
			myPane.getChildren().clear();
			myPane.getChildren().add(text);
			myContent.getChildren().add(myPane);
		}
		currentItemSize = items.size();
		deSelect();
	}
	
	/**
	 * Update menu, called to update position and correct the display issues.
	 */
	public void updateMenu() {
		if (myWindow == null || myMenu == null) { // if not initialized yet and received input
			return;
		}
		String line = myInput.getText();
		if (line.equals("")) {
			closeMenu();
		} else {
			ShiftMenu();
			refresh();  // fix display issues
		}
	}

	private void ShiftMenu() {
		assert(myInput != null);
		double width = getWidthOfTextFieldInput(myInput);
		Bounds screenBounds = UiPopupManager.getInstance().getScreenBoundsOfNode(myInput);
		myMenu.setAnchorX(Math.min(screenBounds.getMinX() + myInput.getWidth(), screenBounds.getMinX() + width));
		myMenu.setAnchorY(screenBounds.getMinY() + myInput.getHeight());
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
		text.setFont(field.getFont()); // Set the same font, so the size is the same
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
		myMenu.show(myWindow);
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
			toStyle.getStyleClass().add(UiConstants.STYLE_PROMPT);
			selectedPane = null;
			currentSelection = -1;
		}	
	}
	private void select(int selection) {
		VBox myContent = (VBox) myMenu.getContent().get(0);
		assert(selection >= 0 && selection < myContent.getChildren().size());

		deSelect(); 
		
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
