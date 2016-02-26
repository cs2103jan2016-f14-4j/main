package taskey.ui;

import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import taskey.constants.Constants;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;

/**
 *
 * This class is the main entry point for Taskey It performs the main setups for
 * the UI
 * 
 * @author JunWei
 *
 */

public class UiMain extends Application {

	private static UiMain instance = null;
	private UiController myController;
	private Parent root = null;

	public static UiMain getInstance() {
		if (instance == null) {
			instance = new UiMain();
		}
		return instance;
	}

	/**
	 * This method loads the .fxml file and set ups the scene
	 * 
	 * @param stage object which is called automatically by the javaFX plugin
	 *            
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
	}

	/**
	 * This method adds pre-defined style sheets to the scene, setups the nodes
	 * in the scene and registers event handlers to them.
	 * 
	 * @param primaryStage - The main stage from start
	 * @param root which is obtained after loading the .fxml file
	 *             
	 */
	public void setUpScene(Stage primaryStage, Parent root) {
		primaryStage.setTitle(Constants.PROGRAM_NAME);
		primaryStage.initStyle(StageStyle.DECORATED);
		Scene newScene = new Scene(root);

		primaryStage.setScene(newScene);
		primaryStage.setResizable(false);
		myController.setUpNodes(primaryStage, root); // must be done after loading .fxml file
		myController.setStyleSheets(UiConstants.UI_DEFAULT_STYLE);
		primaryStage.show();
		myController.setUpNodesWhichNeedBounds(); // layout bounds of nodes are only updated on show()
		
		testUI();
	}

	public UiController getController() {
		return myController;
	}

	@Override
	public void stop() {
		myController.cleanUp();
	}

	public static void main(String[] args) {
		launch(args); // calls the start() method
	}

	/************************************** MY TESTING ***********************************/
	public void testUI() {
		ArrayList<Task> myTaskList = new ArrayList<Task>();
		// Temporary;
		Task temp = new Task("General Task");
		myTaskList.add(temp);
		temp = new Task("Task A");
		temp.setDeadline("27 Feb 2016 01:00:00");
		myTaskList.add(temp);
		temp = new Task("Task C");
		temp.setDeadline("27 Feb 2016 04:00:00");
		myTaskList.add(temp);
		temp = new Task("Task B");
		temp.setDeadline("29 Feb 2016 09:00:00");
		myTaskList.add(temp);

		temp = new Task("Task C");
		temp.setDeadline("27 Feb 2016 04:00:00");
		myTaskList.add(temp);
		temp = new Task("Task B");
		temp.setDeadline("29 Feb 2016 09:00:00");
		myTaskList.add(temp);

		myController.updateDisplay(myTaskList, ContentBox.PENDING);

		myController.updateDisplay(myTaskList, ContentBox.ACTION);
	}

	public String doHash(String line, int offsest) {
		int encode = 7; // prime
		String temp = line.replace("[^A-Za-z0-9]", ""); // replace non-alphanumeric
		for (int i = 0; i < temp.length(); i++)
			encode = encode * offsest + temp.charAt(i);
		return String.valueOf(encode);
	}

	public ArrayList<String> randomInput(String line, int maxItems) {
		int test = (int) (Math.random() * maxItems) + 1;
		ArrayList<String> tempList = new ArrayList<String>();
		for (int i = 0; i < test; i++) {
			tempList.add(doHash(line, 5 * (i + 1)));
		}
		return tempList;
	}
}
