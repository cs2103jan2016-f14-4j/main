package taskey.ui.utility;

import java.util.ArrayList;

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
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import taskey.ui.UiConstants;

public class UiDropDown {
	private static final int MAX_ITEMS = 4;
	
	private TextField myInput;
	private Popup myMenu = null;
	private FadeTransition fade;
	private Window myWindow;
	private int currentSelection;
	private int currentItemSize;
	
	public void createMenu(Stage primaryStage, TextField input) {
		myInput = input; // set up reference
		myMenu = UiPopupFactory.getInstance().createPopupMenu(MAX_ITEMS);
		fade = UiPopupFactory.getInstance().createFadeTransition(myMenu, 5000, 1000, 1.0, 0.0, false);
		// create custom handler
		fade.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				closeMenu();
			}
		});
		myWindow = primaryStage.getScene().getWindow();
		currentItemSize = 0;
	}

	public void updateMenuItems(ArrayList<String> items) {
		VBox myContent = (VBox) myMenu.getContent().get(0);
		ObservableList<Node> menuItems = myContent.getChildren(); // list of stack panes
		for (int i = 0; i < menuItems.size(); i++) {
			StackPane myPane = (StackPane) menuItems.get(i);
			if (i < items.size()) {
				Label text = new Label(items.get(i));
				myPane.getChildren().clear();
				myPane.getChildren().add(text);
				myPane.setVisible(true);
			} else {
				myPane.setVisible(false);
			}
		}
		currentItemSize = items.size();
		select(0);
	}
	
	public void updateMenu() {
		if (myWindow == null || myMenu == null) { // if not initialized yet and received input
			return;
		}
		String line = myInput.getText();
		if (line.equals("")) {
			closeMenu();
		} else {
			ShiftMenu(line);
			refresh();  // fix display issues
		}
	}

	private void ShiftMenu(String line) {
		double width = getWidthOfTextFieldInput(myInput);
		Bounds screenBounds = UiPopupFactory.getInstance().getScreenBoundsOfNode(myInput);
		myMenu.setAnchorX(Math.min(screenBounds.getMinX() + myInput.getWidth(), screenBounds.getMinX() + width));
		myMenu.setAnchorY(screenBounds.getMinY() + myInput.getHeight());
	}

	/**
	 * This method creates a Text object to approximate the bounds of a text
	 * field input
	 * @param field - TextField to approximate from
	 * @return width
	 */
	private double getWidthOfTextFieldInput(TextField field) {
		Text text = new Text(field.getText());
		text.setFont(field.getFont()); // Set the same font, so the size is the same
		double width = text.getLayoutBounds().getWidth();
		return width;

	}
	public String getSelectedItem() {
		ObservableList<Node> menuItems = ((VBox)myMenu.getContent().get(0)).getChildren(); // list of stack panes
		StackPane myPane = (StackPane) menuItems.get(currentSelection);
		Label content = (Label)myPane.getChildren().get(0);
		return content.getText();
	}

	public void closeMenu() {
		myMenu.hide();
		currentItemSize = 0;
	}
	
	/**
	 * Most of the time after switching styles or setting visibility
	 * The popup window has some tears, probably because it renders separately from the main program
	 * This fixes that issue, and also sets opacity full for display
	 */
	private void refresh() {
		myMenu.hide();
		fade.stop();
		fade.getNode().setOpacity(1);
		fade.playFromStart();
		myMenu.show(myWindow);
	}
	
	private void select(int selection) {
		ObservableList<Node> menuItems = ((VBox)myMenu.getContent().get(0)).getChildren(); // list of stack panes
		StackPane myPane;
	
		// remove previous
		myPane = (StackPane) menuItems.get(currentSelection);
		if ( myPane.getStyleClass().size() > 1) {
			myPane.getStyleClass().remove(1);
		}
		
		currentSelection = selection;
		myPane = (StackPane) menuItems.get(currentSelection);
		myPane.getStyleClass().add(UiConstants.STYLE_PROMPT_SELECTED);
		
		refresh();	
	}
	public void processArrowKey(KeyEvent event) {
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
