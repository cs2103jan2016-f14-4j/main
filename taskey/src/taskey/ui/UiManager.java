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
		primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(false);
		primaryStage.show();
		myController.setUpNodes();  // must be done after loading .fxml file
		myController.registerEventHandlersToNodes(root);
    }
    
    public void updateDisplay() {
    	// call logic to get updated list of tasks
    	
    	ArrayList<String> myTaskList = new ArrayList<String>(
    			Arrays.asList("Meet Dave at Mcdonalds", 
    						  "Send Proposal"));
    	myController.updateNodes(myTaskList);
    }
    

    @Override
    public void stop() {
    	myController.cleanUp();
    }
	public static void main(String[] args) {
		launch(args); // calls the start() method
	}
}
