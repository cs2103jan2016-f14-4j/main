package taskey.ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

public class UiController {
	
	private int currentTab;
    @FXML private TabPane myTabs;
    @FXML private TextField input;
    @FXML private Label textPrompt;
    private TextArea currentTextArea;
    
    public void registerHandlersToNodes(Parent root) {
    	input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
    						      public void handle(KeyEvent event) {
    						    	  if ( event.getCode() == KeyCode.ENTER ) {
    						      		String line = input.getText();
    						      		input.clear();
    						      		//Logic.getInstance().getCommand(line);

    						      		if ( true ) { // only temporary here, logic should update a method in UI instead
    						      			textPrompt.setText("Successful");
    						      	        currentTextArea.appendText(line + "\n");
    						      		}
    						      		event.consume();
    						    	  }
    						      }
    						});
    	
    	// for key inputs anywhere in main window
    	root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
		      public void handle(KeyEvent event) {
		    	  if ( event.getCode() == KeyCode.TAB) {
		      		currentTab = (currentTab + 1)% myTabs.getTabs().size();
		      		setWindowContentsToTab(currentTab);
		      		event.consume();
		    	  }
		      }
		});
    }
    public void setTabReferences() {
    	currentTab = 0;
    	setWindowContentsToTab(currentTab);
    }
    public void setWindowContentsToTab(int tabNo) {
    	AnchorPane content = (AnchorPane) myTabs.getTabs().get(tabNo).getContent();
    	currentTextArea = (TextArea)content.getChildren().get(0);
    	SingleSelectionModel<Tab> selectionModel = myTabs.getSelectionModel();
  		selectionModel.select(currentTab);
  		input.requestFocus();
    }
}