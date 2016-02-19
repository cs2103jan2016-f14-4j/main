package taskey.ui;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UiController {

	private static final int WORD_LIMIT_WEEKLIST = 10;
    @FXML private TabPane myTabs;
    @FXML private TextField input;
    @FXML private Label textPrompt;
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    @FXML private TextFlow weekList;
    
    private int currentTab;
    private UiClockService clockService;
    private ArrayList<TextFlow> tabTexts; // list of references to the TextFlow objects
    
	public void registerEventHandlersToNodes(Parent root) {
		registerInputEventHandler();
		registerRootEventHandler(root);
	}

	public void setUpNodes() {
		clockService = new UiClockService(timeLabel, dateLabel);
		clockService.start();
		setUpTabLists();
		setUpStyles();
		setUpTabDisplay();
	}

	public void setUpStyles() {
		weekList.getParent().getStyleClass().add("stackpane");
		for (int i = 0; i < myTabs.getTabs().size(); i++) {
			tabTexts.get(i).getStyleClass().add("stackpane");
		}
	}

	public void setUpTabLists() {
		tabTexts = new ArrayList<TextFlow>();
		for (int i = 0; i < myTabs.getTabs().size(); i++) {
			AnchorPane content = (AnchorPane) myTabs.getTabs().get(i).getContent();
			tabTexts.add((TextFlow) content.getChildren().get(0));
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

	public void updateNodesOnTab(ArrayList<String> myTaskList, ArrayList<String> myDeadlines, int tabNo) {
		TextFlow myText = tabTexts.get(tabNo);
		myText.getChildren().removeAll(myText.getChildren());
		for (int i = 0; i < myTaskList.size(); i++) {
			Text newText = new Text((i + 1) + ". " + myTaskList.get(i) + " on " );
			Text deadLine = new Text(myDeadlines.get(i) + "\n\n");
			deadLine.setFill(Color.RED);
			
			myText.getChildren().addAll(newText,deadLine);
			ObservableList<Node> text = myText.getChildren();
			//System.out.println(Font.getFontNames());
			for ( int j = 0; j < text.size(); j++ ) {
				((Text)text.get(j)).setFont(Font.font("Comic Sans MS", FontWeight.SEMI_BOLD, 13));
			}
		}
		if (tabNo == 0) {
			updateWeeklyList(myTaskList, myDeadlines);
		}
	}

	public void updateWeeklyList(ArrayList<String> myTaskList, ArrayList<String> myDeadlines) {
		/*
		 * Text text1 = new Text("Big italic red text");
		 * text1.setFill(Color.RED); text1.setFont(Font.font("Helvetica",
		 * FontPosture.ITALIC, 10)); Text text2 = new Text(
		 * " little bold blue text"); text2.setFill(Color.BLUE);
		 * text2.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
		 */

		for (int i = 0; i < myTaskList.size(); i++) {
			if (Integer.parseInt(myDeadlines.get(i).split(" ")[0]) >= clockService.getDayOfMonth()) {
				String taskText = myTaskList.get(i);
				if (taskText.length() > WORD_LIMIT_WEEKLIST) {
					taskText = taskText.substring(0, WORD_LIMIT_WEEKLIST) + "...*";
				}
				Text newText = new Text("- " + taskText + " (" + myDeadlines.get(i) + ") \n\n");
				newText.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
				weekList.getChildren().add(newText);
			}
		}

	}

	public void registerInputEventHandler() {
		input.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					String line = input.getText();
					input.clear();
					// Logic.getInstance().getCommand(line);
					event.consume();
				}
			}
		});
	}

	public void registerRootEventHandler(Parent root) {
		// for key inputs anywhere in main window
		root.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.TAB) {
					currentTab = myTabs.getSelectionModel().getSelectedIndex();
					currentTab = (currentTab + 1) % myTabs.getTabs().size();
					setWindowContentsToTab(currentTab);
					event.consume();
				}
			}
		});
	}

	public void cleanUp() {
	}
}