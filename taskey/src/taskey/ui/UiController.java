package taskey.ui;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
    @FXML private ListView<String> weekList;
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
    	getListView(tabNo);
    	SingleSelectionModel<Tab> selectionModel = myTabs.getSelectionModel();
  		selectionModel.select(currentTab);
    }
    
    public ListView<String> getListView(int tabNo) {
    	AnchorPane content = (AnchorPane) myTabs.getTabs().get(tabNo).getContent();
    	return (ListView<String>)content.getChildren().get(0);
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
	
	public void updateNodesOnTab(ArrayList<String> myTaskList, ArrayList<String> myDeadlines, int tabNo) {
		ListView<String> targetList = getListView(tabNo);
		ObservableList<String> tasks = FXCollections.observableArrayList(); // can be placed as a class variable for efficiency
		for ( int i = 0; i < myTaskList.size(); i++ ) {
			tasks.add((i+1) + ". " + myTaskList.get(i) + " at " + myDeadlines.get(i));
		}
		targetList.setItems(tasks);
		if (tabNo == 0) {
			updateWeeklyList(targetList);
		}
	}
	
	public void updateWeeklyList (ListView<String> targetList) {
	
		ObservableList<String> names = FXCollections.observableArrayList(
		          "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
		weekList.setItems(names);
	}
	public void cleanUp() {
	}
}