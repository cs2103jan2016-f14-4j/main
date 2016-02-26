package taskey.ui.utility;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import taskey.ui.UiConstants;

public class UiDropDown {
	private static final int MAX_ITEMS = 5;
	private TextField myInput;
	private Popup myMenu = null;
	private FadeTransition fade;
	private Window myWindow;

	public void createMenu(Stage primaryStage, TextField input) {
		myInput = input; // set up reference
		myMenu =  UiPopupFactory.getInstance().createPopupMenu(MAX_ITEMS);
		fade = UiPopupFactory.getInstance().createFadeTransition(myMenu, 5000, 1000, 1.0, 0.0, false);
		// create custom handler
		fade.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
					hideMenu();
				}
		});
		myWindow = primaryStage.getScene().getWindow();
	}
	
	public void updateMenuItems(ArrayList<String> items) {
		VBox myContent = (VBox)myMenu.getContent().get(0);
		ObservableList<Node> menuItems = myContent.getChildren(); // list of stack panes
		for ( int i = 0; i < menuItems.size(); i ++ ) {
			StackPane myPane = (StackPane)menuItems.get(i);	
			if ( i < items.size() ) {
				Label text = new Label(items.get(i));
				myPane.getChildren().clear();
				myPane.getChildren().add(text);
				myPane.setVisible(true);
			} else {
				myPane.setVisible(false);
			}
		}
	}
	public void hideMenu() {
		myMenu.hide();
		fade.stop();
	}
	
	public void updateMenu() {
		if ( myWindow == null || myMenu == null ) { // if not initialized yet and received input
			return;
		}
		String line = myInput.getText();
		if ( line.equals("")) {
			hideMenu();
		} else {	
			displayMenu(line);
		}
	}
	public void displayMenu(String line) {
		double width = getWidthOfLineToTextField(line,myInput);
		Bounds screenBounds = UiPopupFactory.getInstance().getScreenBoundsOfNode(myInput);
		if ( myMenu.isShowing() == false ) {
			fade.getNode().setOpacity(1);
			fade.playFromStart();
			myMenu.show(myWindow,Math.min(screenBounds.getMinX()+myInput.getWidth(),screenBounds.getMinX() + width), 
					           screenBounds.getMinY() + myInput.getHeight() );
		} else {
			fade.getNode().setOpacity(1);
			fade.playFromStart();
			myMenu.setAnchorX(Math.min(screenBounds.getMinX()+myInput.getWidth(),screenBounds.getMinX() + width));
			myMenu.setAnchorY(screenBounds.getMinY() + myInput.getHeight());
		}
	}
	
	/**
	 * This method creates a Text object to approximate the bounds of a text field input
	 * @param line : String
	 * @param field : TextField
	 * @return
	 */
	public static double getWidthOfLineToTextField(String line, TextField field) {
		Text text = new Text(line);
		text.setFont(field.getFont()); // Set the same font, so the size is the same
		double width = text.getLayoutBounds().getWidth();
		return width;

	}
	
}
