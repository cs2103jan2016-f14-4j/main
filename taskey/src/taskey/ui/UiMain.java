package taskey.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ActionMode;
import taskey.constants.UiConstants.ContentBox;
import taskey.constants.UiConstants.IMAGE_ID;
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;
import taskey.logic.Task;
import taskey.ui.utility.UiImageManager;

/**
 * This class is the main entry point for Taskey. 
 * It performs the main setups for the UI.
 *
 * @author JunWei
 */

public class UiMain extends Application {

	private UiController myController;
	private Parent root = null;
	private UiTrayModule trayModule;
	
	/**
	 * This method loads the .fxml file and set ups the scene
	 *
	 * @param primaryStage the primary stage
	 */
	@Override
	public void start(Stage primaryStage) {
		myController = new UiController();
		FXMLLoader myloader = new FXMLLoader(getClass().getResource(UiConstants.FXML_PATH));
		myloader.setController(myController);
		try {
			root = myloader.load();
		} catch (IOException e) {
			System.out.println(UiConstants.FXML_LOAD_FAIL);
		}
		setUpScene(primaryStage, root);
	}

	/**
	 * This method setups the main scene window, calls UiController to
	 * set up nodes
	 * 
	 * @param primaryStage - The main stage from start()
	 * @param root which is obtained after loading the .fxml file
	 *             
	 */
	private void setUpScene(Stage primaryStage, Parent root) {
		UiImageManager.getInstance().loadImages();
		primaryStage.setTitle(UiConstants.PROGRAM_NAME);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		Scene newScene = new Scene(root);
		primaryStage.getIcons().add(UiImageManager.getInstance().getImage(IMAGE_ID.WINDOW_ICON));
		primaryStage.setScene(newScene);
		primaryStage.setResizable(false);
		trayModule = new UiTrayModule(primaryStage);
		
		myController.setUpNodes(primaryStage, root); // must be done after loading .fxml file
		myController.setStyleSheets(UiConstants.STYLE_UI_DEFAULT);
		primaryStage.show();
		myController.setUpNodesWhichNeedBounds(); // layout bounds of nodes are only updated on show()
		
		//testUI();
		
		TaskeyLog.getInstance().addHandler(LogSystems.UI, "UiLog.txt", 5);
		TaskeyLog.getInstance().log(LogSystems.UI, "Done setting up the Scene...", Level.ALL);
	}

	/**
	 * This method is overridden from Application to handle clean ups.
	 */
	@Override
	public void stop() {
		myController.cleanUp();
		UiImageManager.getInstance().cleanUp();
	}
	
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
}
