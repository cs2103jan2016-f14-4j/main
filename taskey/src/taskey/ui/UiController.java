package taskey.ui;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import javafx.util.Duration;

public class UiController {
	
	private int currentTab;
    @FXML private TabPane myTabs;
    @FXML private TextField input;
    @FXML private Label textPrompt;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    private TextArea currentTextArea;
    private UiClockService clockService;
    
    public void registerEventHandlersToNodes(Parent root) {
    	registerInputEventHandler();
    	registerRootEventHandler(root);	
    }
    public void setUpNodes() {
    	clockService = new UiClockService(timeLabel,dateLabel);
    	clockService.start();
    	currentTab = 0;
    	input.requestFocus();
    	setWindowContentsToTab(currentTab);
	 }
    public void setWindowContentsToTab(int tabNo) {
    	AnchorPane content = (AnchorPane) myTabs.getTabs().get(tabNo).getContent();
    	currentTextArea = (TextArea)content.getChildren().get(0);
    	SingleSelectionModel<Tab> selectionModel = myTabs.getSelectionModel();
  		selectionModel.select(currentTab);
    }
    public void registerInputEventHandler() {
    	input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
		      public void handle(KeyEvent event) {
		    	  if ( event.getCode() == KeyCode.ENTER ) {
		      		String line = input.getText();
		      		input.clear();
		      		//Logic.getInstance().getCommand(line);
		      		event.consume();
		    	  }
		      }
		});
    }
    public void registerRootEventHandler(Parent root) {
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
	
	public void updateNodes(ArrayList<String> myTaskList) {
		// TODO Auto-generated method stub
		currentTextArea.clear();
		for ( int i = 0; i < myTaskList.size(); i++ ) {
			currentTextArea.appendText(myTaskList.get(i)+ "\n");
		}
	}
	
	public void cleanUp() {
	}
}