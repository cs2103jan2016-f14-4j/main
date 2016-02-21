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
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;

/**
 *
 * This class is the main entry point for Taskey
 * It also acts as the interface between the various components
 * It is the main UI component which calls other UI sub components
 * 
 * @author JunWei
 *
 */

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
    
    /**
     * This method loads the .fxml file and set ups the scene
     * @param stage object which is called automatically by the javaFX plugin
     */
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
		updateDisplay(new ArrayList<Task>(), UiConstants.ContentBox.PENDING);
    }
    
    /**
     * This method adds pre-defined style sheets to the scene,
     * setups the nodes in the scene and registers event handlers to them.
     * @param primaryStage : Stage
     * @param root : Parent, which is a subclass of Node
     */
    public void setUpScene(Stage primaryStage, Parent root) {
    	primaryStage.setTitle(Constants.PROGRAM_NAME);
    	Scene newScene = new Scene(root);
    	for ( int i = 0; i < UiConstants.UI_STYLE_SHEETS.size(); i ++ ) {
    		newScene.getStylesheets().add(getClass().getResource(UiConstants.UI_STYLE_SHEETS.get(i)).toExternalForm());
    	}
		primaryStage.setScene(newScene);
		primaryStage.setResizable(false);
		primaryStage.show();
		myController.setUpNodes(primaryStage, root);  // must be done after loading .fxml file
    }
    
    public void updateDisplay(ArrayList<Task> myTaskList, UiConstants.ContentBox contentID) {
    	// call logic to get updated list of tasks
    	
    	// Temporary;
    	Task temp = new Task("No deadline");
    	myTaskList.add(temp);
    	temp = new Task("Task A");
    	temp.setDeadline("23 Feb 2016 01:00:00");
    	myTaskList.add(temp);
    	temp = new Task("Task B");
    	temp.setDeadline("25 Feb 2016 09:00:00");
    	myTaskList.add(temp);
    	
    	myController.process(myTaskList,contentID);
    }
    

    @Override
    public void stop() {
    	myController.cleanUp();
    }
	public static void main(String[] args) {
		launch(args); // calls the start() method
	}
}
