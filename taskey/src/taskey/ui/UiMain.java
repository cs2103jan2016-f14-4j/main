package taskey.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import taskey.constants.UiConstants;
import taskey.constants.UiConstants.ActionMode;
import taskey.constants.UiConstants.ContentBox;
import taskey.constants.UiConstants.IMAGE_ID;
import taskey.logger.TaskeyLog;
import taskey.logger.TaskeyLog.LogSystems;
import taskey.logic.Task;
import taskey.parser.AutoComplete;
import taskey.ui.utility.UiImageManager;

/**
 * @@author A0125419H
 * This class is the main entry point for Taskey. 
 * It performs the main setups for the UI.
 *
 * @author JunWei
 */

public class UiMain extends Application {

	private UiController myController;
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

		Region contentRootRegion = null;
		try {
			contentRootRegion = (Region) myloader.load();
		} catch (IOException e) {
			System.out.println("Unable to load fxml file");
		}

		Parent root = setUpResize(primaryStage, contentRootRegion);
		setUpScene(primaryStage, root);
	}
	
	/**
	 * This method setups the scene for resizing, by placing all the nodes inside a group object centered in a StackPane
	 * and setting its scale according to the size of the scene
	 * 
	 * Credits: http://gillius.org/blog/2013/02/javafx-window-scaling-on-resize.html
	 */
	private StackPane setUpResize(Stage primaryStage, Region contentRootRegion) {
		 //Set a default "standard" or "100%" resolution
	    double origW = 960;
	    double origH = 540;
		// If the Region containing the GUI does not already have a preferred
		// width and height, set it.
		// But, if it does, we can use that setting as the "standard"
		// resolution.
		if (contentRootRegion.getPrefWidth() == Region.USE_COMPUTED_SIZE)
			contentRootRegion.setPrefWidth(origW);
		else
			origW = contentRootRegion.getPrefWidth();

		if (contentRootRegion.getPrefHeight() == Region.USE_COMPUTED_SIZE)
			contentRootRegion.setPrefHeight(origH);
		else
			origH = contentRootRegion.getPrefHeight();

		// Wrap the resizable content in a non-resizable container (Group)
		Group group = new Group(contentRootRegion);
		// Place the Group in a StackPane, which will keep it centered
		StackPane rootPane = new StackPane();
		rootPane.getChildren().add(group);

		Scene newScene = new Scene(rootPane, origW, origH);
		primaryStage.setScene(newScene);
		// Bind the scene's width and height to the scaling parameters on the group
		group.scaleXProperty().bind(newScene.widthProperty().divide(origW));
		group.scaleYProperty().bind(newScene.heightProperty().divide(origH));

		// set event handlers / listener
		UiResizeListener listener = new UiResizeListener(newScene, primaryStage);
		newScene.setOnMouseMoved(listener);
		newScene.setOnMousePressed(listener);
		newScene.setOnMouseDragged(listener);
		return rootPane;
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
		
		primaryStage.getIcons().add(UiImageManager.getInstance().getImage(IMAGE_ID.WINDOW_ICON));
		
		primaryStage.setResizable(true);
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
