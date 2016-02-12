package taskey.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import taskey.constants.Constants;

public class UiController extends Application {
	
	private static UiController instance = null; 
	
	private UiEventHandler myEventHandler;
	
    public static UiController getInstance () { 
    	if ( instance == null ) {
    		instance = new UiController();
    	}
    	return instance;
    }
    
    @Override
    public void start(Stage primaryStage) {
		myEventHandler = new UiEventHandler();
		FXMLLoader myloader = new FXMLLoader(getClass().getResource(Constants.FXML_PATH));
		myloader.setController(myEventHandler);
		Parent root = null;
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
		myEventHandler.setTabReferences(); // must be done after loading .fxml file
    }
    
    public void updateDisplay() {
    	// call logic to get updated list of tasks
    }
    

	public static void main(String[] args) {
		launch(args); // calls the start() method
	}
}
