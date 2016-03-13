package taskey.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import taskey.constants.Constants;
import taskey.logic.Logic;
import taskey.logic.Task;
import taskey.ui.UiConstants.ContentBox;
import taskey.ui.UiConstants.IMAGE_ID;
import taskey.ui.utility.UiImageManager;
import taskey.ui.UiConstants.ActionMode;

// TODO: Auto-generated Javadoc
/**
 * This class is the main entry point for Taskey. 
 * It performs the main setups for the UI.
 *
 * @author JunWei
 */

public class UiMain extends Application {

	/** The my controller. */
	private UiController myController;
	
	/** The root. */
	private Parent root = null;

	/**
	 * This method loads the .fxml file and set ups the scene
	 *
	 * @param primaryStage the primary stage
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
	private void setUpScene(Stage primaryStage, Parent root) {
		UiImageManager.getInstance().loadImages();
		primaryStage.setTitle(Constants.PROGRAM_NAME);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		Scene newScene = new Scene(root);
		primaryStage.getIcons().add(UiImageManager.getInstance().getImage(IMAGE_ID.WINDOW_ICON));
		primaryStage.setScene(newScene);
		primaryStage.setResizable(false);
		myController.setUpNodes(primaryStage, root); // must be done after loading .fxml file
		myController.setStyleSheets(UiConstants.STYLE_UI_DEFAULT);
		primaryStage.show();
		myController.setUpNodesWhichNeedBounds(); // layout bounds of nodes are only updated on show()
		testUI();
	}

	/**
	 * This method is overridden from Application to handle clean ups.
	 */
	@Override
	public void stop() {
		myController.cleanUp();
		UiImageManager.getInstance().cleanUp();
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args); // calls the start() method
	}

	/**
	 * ************************************ MY TESTING **********************************.
	 */
	private void testUI() {
		ArrayList<Task> myTaskList = new ArrayList<Task>();
		// Temporary;
		for ( int i = 0; i < 8; i ++ ) {
			Task temp = new Task("General Task " + i);
			temp.setTaskType("FLOATING");
			myTaskList.add(temp);
		}
		
		myController.updateDisplay(myTaskList, ContentBox.PENDING);	
		myController.updateDisplay(myTaskList, ContentBox.THIS_WEEK);
		myController.updateActionDisplay(myTaskList, ActionMode.HELP);
		myController.displayTabContents(ContentBox.ACTION);
	}

	/**
	 * Do hash.
	 *
	 * @param line the line
	 * @param offsest the offsest
	 * @return the string
	 */
	public static String doHash(String line, int offsest) {
		int encode = 7; // prime
		String temp = line.replace("[^A-Za-z0-9]", ""); // replace non-alphanumeric
		for (int i = 0; i < temp.length(); i++)
			encode = encode * offsest + temp.charAt(i);
		return String.valueOf(encode);
	}

	/**
	 * Random input.
	 *
	 * @param line the line
	 * @param maxItems the max items
	 * @return the array list
	 */
	public static ArrayList<String> randomInput(String line, int maxItems) {
		int test = (int) (Math.random() * maxItems) + 1;
		ArrayList<String> tempList = new ArrayList<String>();
		for (int i = 0; i < test; i++) {
			tempList.add(doHash(line, 5 * (i + 1)));
		}
		return tempList;
	}
}
