package taskey.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import taskey.contants.Constants;

public class UiController {
	
	private static UiController instance = null; 
	
	private UiEventHandler myEventHandler;
	private UiTimeDisplay myTimer;
	
    public static UiController getInstance () { 
    	if ( instance == null ) {
    		instance = new UiController();
    	}
    	return instance;
    }
    
    public void initialize(Stage primaryStage) {
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
    }
    
    public void setUpScene(Stage primaryStage, Parent root) {
    	primaryStage.setTitle(Constants.PROGRAM_NAME);
		primaryStage.setScene(new Scene(root));
		primaryStage.setResizable(false);
		primaryStage.show();
		myTimer = new UiTimeDisplay();
		myTimer.run();
    }
    
    public void update() {
    	// get list from logic and update ui
    }
}
