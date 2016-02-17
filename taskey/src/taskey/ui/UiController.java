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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class UiController {

    @FXML private TabPane myTabs;
    @FXML private TextField input;
    @FXML private Label textPrompt;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private TextFlow weekList;
    
    private int currentTab;
    private UiClockService clockService;
    private ArrayList<ObservableList<String>> tabLists;
    
    public void registerEventHandlersToNodes(Parent root) {
    	registerInputEventHandler();
    	registerRootEventHandler(root);	
    }
    public void setUpNodes() {
    	clockService = new UiClockService(timeLabel,dateLabel);
    	clockService.start();
    	setUpTabLists();
    	setUpTabDisplay();
    	weekList.getParent().getStyleClass().add("stackpane");
	 }
    
    public void setUpTabLists() {
    	tabLists = new ArrayList<ObservableList<String>>();
    	for ( int i = 0; i < 4; i ++ ) {
    		ListView<String> tabListView = getListView(i);
    		tabLists.add(FXCollections.observableArrayList());
    		tabListView.setItems(tabLists.get(i)); // link the list to the node
    	}
    }
    public void setUpTabDisplay() {
    	currentTab = 0;
    	input.requestFocus();
    	setWindowContentsToTab(currentTab);
    }
    public void setWindowContentsToTab(int tabNo) {
    	SingleSelectionModel<Tab> selectionModel = myTabs.getSelectionModel();
  		selectionModel.select(currentTab);
    }
    
    @SuppressWarnings("unchecked") // uses preplaced ui elements inside fxml
	public ListView<String> getListView(int tabNo) {
    	AnchorPane content = (AnchorPane) myTabs.getTabs().get(tabNo).getContent();
    	return (ListView<String>)content.getChildren().get(0);
    }
	
	public void updateNodesOnTab(ArrayList<String> myTaskList, ArrayList<String> myDeadlines, int tabNo) {
		ObservableList<String> tasks = tabLists.get(tabNo); 
		for ( int i = 0; i < myTaskList.size(); i++ ) {
			tasks.add((i+1) + ". " + myTaskList.get(i) + " on " + myDeadlines.get(i));
		}
		if (tabNo == 0) {
			updateWeeklyList(myTaskList,myDeadlines);
		}
	}
	
	public void updateWeeklyList (ArrayList<String> myTaskList, ArrayList<String> myDeadlines) {
		/*
		Text text1 = new Text("Big italic red text");
	     text1.setFill(Color.RED);
	     text1.setFont(Font.font("Helvetica", FontPosture.ITALIC, 10));
	     Text text2 = new Text(" little bold blue text");
	     text2.setFill(Color.BLUE);
	     text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));*/
	     
		for ( int i = 0; i < myTaskList.size(); i ++ ) {
			if ( Integer.parseInt(myDeadlines.get(i).split(" ")[0]) >= clockService.getDayOfMonth() ) {
			     Text newText = new Text("- " + myTaskList.get(i) + " (" + myDeadlines.get(i) + ") \n\n");
			     weekList.getChildren().add(newText);	  
			}
		}	 
	     
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
	public void cleanUp() {
	}
}