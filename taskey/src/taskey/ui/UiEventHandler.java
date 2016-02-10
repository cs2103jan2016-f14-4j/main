package taskey.ui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import taskey.contants.Constants;
import taskey.parser.Parser;

public class UiEventHandler {
	
    @FXML private Tab pendingTab;
    @FXML private Tab expiredTab;
    @FXML private Tab completedTab;
    @FXML private Tab actionListTab;
    @FXML private TextField input;
    @FXML private Label textPrompt;
    private TextArea currentTextArea;
    
    @FXML protected void onKeyPressed(KeyEvent event) {
    	if ( event.getCode() == KeyCode.ENTER ) {
    		
    		String line = input.getText();
    		input.clear();
    		boolean isSuccessful = Parser.getInstance().parseInput(line);

    		if ( isSuccessful ) {
    			textPrompt.setText("Successful");
    			// only temporary here, should be getting a list from logic instead
    			AnchorPane content = (AnchorPane) pendingTab.getContent();
    	        currentTextArea = (TextArea)content.getChildren().get(0);
    	        currentTextArea.appendText(line + "\n");
    		}
    	}
    }
}