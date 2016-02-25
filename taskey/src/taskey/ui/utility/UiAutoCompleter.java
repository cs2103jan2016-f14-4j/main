package taskey.ui.utility;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class UiAutoCompleter {
	private static final int MAX_ITEMS = 5;
	private TextField myInput;
	private ContextMenu myMenu;
	
	public UiAutoCompleter(Parent root, TextField input) {
		myInput = input; // set up reference
		myMenu = UiPopupFactory.getInstance().createPopupMenu();
		myInput.setContextMenu(myMenu);
		//myMenu.setAutoFix(false);
		myMenu.getStyleClass().add("whiteBox");
	}
	
	public void updateMenu() {
		String line = myInput.getText();
		if ( line.isEmpty() ) {
			hideMenu();
		} else {
			ObservableList<MenuItem> myList = myMenu.getItems();
			if ( myList.size() > MAX_ITEMS) {
				return;
			}
			myList.clear();
			myList.add(createMenuItem(line));
			if (!myMenu.isShowing()) {
				myMenu.show(myInput, Side.BOTTOM, 0, 0);
			}
		}
	}

	public MenuItem createMenuItem(String name) {
		MenuItem newItem = new MenuItem(name);
		newItem.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		        System.out.println(newItem.getText());
		    }
		});
		return newItem;
	}
	public void hideMenu() {
		myMenu.hide();
	}

}
