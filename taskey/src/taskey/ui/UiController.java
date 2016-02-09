package taskey.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class UiController {
	
	@FXML private Text actiontarget;
    @FXML private TextField input;
    @FXML private TextArea display;
    
    @FXML protected void handleSubmitButtonAction(ActionEvent event) { 
        actiontarget.setText("Sent");
        String line = input.getText();
        input.clear();
        display.appendText(line + "\n");
    }
}
