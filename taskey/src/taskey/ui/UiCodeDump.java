package taskey.ui;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import taskey.ui.utility.UiPopupFactory;

/**
 * For code that may be reused or provide ideas
 * @author junwei
 *
 */
public class UiCodeDump {

	/*
	// In UiContentFormatter
	public ArrayList<Text> convertStringToTextNodes(String line) {
		ArrayList<Text> myTexts = new ArrayList<Text>();
		String modifier = "#000000";
		for ( int i = 0; i < line.length(); i ++ ) {
			String myChar = String.valueOf(line.charAt(i));
			if ( myChar.equals("<")) {
				modifier = "";
				i++;
				myChar = String.valueOf(line.charAt(i));
				while ( myChar.equals(">") == false ) {
					modifier += myChar;	
					i++;
					myChar = String.valueOf(line.charAt(i));
				}
				i++;
				myChar = String.valueOf(line.charAt(i));
			}
			Text newText = new Text(myChar);
			newText.setFill(Color.web(modifier));
			myTexts.add(newText);
		}
		return myTexts;
	}
	
	public class UiAutoCompleter {

	private static final int MAX_ITEMS = 5;
	private TextField myInput;
	private ContextMenu myMenu;
	private AnchorPane myPane;
	public UiAutoCompleter(Parent root, TextField input) {
		myPane = (AnchorPane) root;
		myInput = input; // set up reference
		myMenu = UiPopupFactory.getInstance().createPopupMenu(myPane);
		//myInput.setContextMenu(myMenu);
		//myMenu.getStyleClass().add("whiteBox");
		
	//	FadeTransition ft = new FadeTransition(Duration.millis(0), myMenu)
	}

	public void updateMenu() {
		// TODO Auto-generated method stub
		String line = myInput.getText();
		ObservableList<MenuItem> myList = myMenu.getItems();
		if ( myList.size() > MAX_ITEMS) {
			return;
		}
		myList.clear();
		myList.add(new MenuItem(myInput.getText()));
		Bounds bounds = myInput.getBoundsInLocal();
        Bounds screenBounds = myInput.localToScreen(bounds);
		myMenu.show(myPane, 100, 100);
	}

}

public class UiAutoCompleter {

	private static final int MAX_ITEMS = 5;
	private TextField myInput;
	private AnchorPane myPane;
	private ArrayList<Label> myMenu;
	
	public UiAutoCompleter(Parent root, TextField input) {
		myPane = (AnchorPane) root;
		myInput = input; // set up reference
		
		myMenu = new ArrayList<Label>();
		VBox test = new VBox();
		myPane.getChildren().add(test);
		for ( int i = 0 ;i < MAX_ITEMS; i ++) {
			test.getChildren().add(UiPopupFactory.getInstance().createPopupLabel("sfsdfs",myPane,0,0));
		}
		
		
		test.setLayoutX(myInput.getLayoutX());
		test.setLayoutY(myInput.getLayoutY());
		
		//myInput.setContextMenu(myMenu);
		//myMenu.getStyleClass().add("whiteBox");
	//	FadeTransition ft = new FadeTransition(Duration.millis(0), myMenu)
	}

	public void updateMenu() {
		// TODO Auto-generated method stub
		for ( int i = 0 ; i < MAX_ITEMS; i++ ) {
			myMenu.get(i).setText(""+i);
		}
		
	}

}
	
	
	VBox myContent = (VBox)myMenu.getContent().get(0);
		ObservableList<Node> menuItems = myContent.getChildren(); // list of stack panes
		//menuItems.clear();
		for ( int i = 0; i < menuItems.size(); i ++ ) {
			StackPane myPane = (StackPane)menuItems.get(i);
			
			if ( i < items.size() ) {
				Label text = new Label(items.get(i));
				//row.getStyleClass().add("prompt");
				myPane.getChildren().clear();
				myPane.getChildren().add(text);
				myPane.setVisible(true);
			} else {
				myPane.setVisible(false);
			}
		}
	
	
	
	
	 */
}
