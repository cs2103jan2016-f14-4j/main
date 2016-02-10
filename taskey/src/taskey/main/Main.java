package taskey.main;
import javafx.application.Application;
import javafx.stage.Stage;
import taskey.ui.UiController;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		UiController.getInstance().initialize(primaryStage);
	}

	public static void main(String[] args) {
		launch(args); // calls the start() method
	}
}
