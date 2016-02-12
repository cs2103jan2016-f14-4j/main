package taskey.ui;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

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
import taskey.constants.Constants;
import taskey.logic.Logic;
import taskey.parser.Parser;

public class UiEventHandler {
	
	private final Set<KeyCode> isPressed = (Set<KeyCode>) EnumSet.noneOf(KeyCode.class);
	private int currentTab;
	private Tab myTabs[];
    @FXML private Tab pendingTab;
    @FXML private Tab expiredTab;
    @FXML private Tab completedTab;
    @FXML private Tab actionListTab;
    @FXML private TextField input;
    @FXML private Label textPrompt;
    private TextArea currentTextArea;
    
    void setTabReferences() {
    	myTabs = new Tab[4];
    	myTabs[0] = pendingTab;
    	myTabs[1] = expiredTab;
    	myTabs[2] = completedTab;
    	myTabs[3] = actionListTab;
    	currentTab = 0;
    	setWindowContentsToTab(currentTab);
    }
    public void setWindowContentsToTab(int tabNo) {
    	AnchorPane content = (AnchorPane) myTabs[tabNo].getContent();
    	currentTextArea = (TextArea)content.getChildren().get(0);
    }
    @FXML protected void onKeyPressed(KeyEvent event) {
    	if ( event.getCode() == KeyCode.ENTER ) {
    		
    		String line = input.getText();
    		input.clear();
    		Logic.getInstance().getCommand(line);

    		if ( true ) { // only temporary here, logic should update a method in UI instead
    			textPrompt.setText("Successful");
    	        currentTextArea.appendText(line + "\n");
    		}
    	} else if ( event.getCode() == KeyCode.TAB ) {
    		//temporary
    		myTabs[currentTab].setDisable(true);
    		currentTab = (currentTab + 1)%4;
    		//myTabs[currentTab].
    		setWindowContentsToTab(currentTab);
    	}
    }
}