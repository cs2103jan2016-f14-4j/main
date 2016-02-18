package taskey.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import taskey.constants.Constants;

public class UiManager extends Application {
	
	private static UiManager instance = null; 
	
	private static final ArrayList<String> myStyleSheets = new ArrayList<String>(
			Arrays.asList("style.css"));
	
	private UiController myController;
	private Parent root = null;
	
    public static UiManager getInstance () { 
    	if ( instance == null ) {
    		instance = new UiManager();
    	}
    	return instance;
    }
    
    @Override
    public void start(Stage primaryStage) {
    	myController = new UiController();
		FXMLLoader myloader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH));
		myloader.setController(myController);
		try {
			root = myloader.load();
		} catch (IOException e) {
			System.out.println(Constants.FXML_LOAD_FAIL);
		}
		setUpScene(primaryStage, root);
		updateDisplay();
    }
    
    public void setUpScene(Stage primaryStage, Parent root) {
    	primaryStage.setTitle(Constants.PROGRAM_NAME);
    	Scene newScene = new Scene(root);
    	for ( int i = 0; i < myStyleSheets.size(); i ++ ) {
    		newScene.getStylesheets().add(getClass().getResource(myStyleSheets.get(i)).toExternalForm());
    	}
		primaryStage.setScene(newScene);
		primaryStage.setResizable(false);
		primaryStage.show();
		myController.setUpNodes();  // must be done after loading .fxml file
		myController.registerEventHandlersToNodes(root);
    }
    
    public void updateDisplay() {
    	// call logic to get updated list of tasks
    	
    	// Temporary
    	ArrayList<String> myTaskList = new ArrayList<String>(
    			Arrays.asList("Meet a at Mcdonalds", 
    						  "Meet b at Mcdonalds", 
    						  "Meet c at Mcdonalds", 
    						  "Meet d at Mcdonalds", 
    						  "Meet e at Mcdonalds"));
    	
    	ArrayList<String> myDeadLines = new ArrayList<String>(
    			Arrays.asList("17 Feb 2016", 
    						  "18 Feb 2016",
    						  "19 Feb 2016", 
    						  "20 Feb 2016",
    						  "21 Feb 2016"));
    	
    	myController.updateNodesOnTab(myTaskList,myDeadLines,0);
    }
    

    @Override
    public void stop() {
    	myController.cleanUp();
    }
	public static void main(String[] args) {
		launch(args); // calls the start() method
	}
}
