package taskey.main;
	
import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {

		 Parent root = null;
		try {
			root = FXMLLoader.load(getClass().getResource("../ui/layout.fxml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 primaryStage.setTitle("Taskey");
		 primaryStage.setScene(new Scene(root));
		 primaryStage.setResizable(false);
		 primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
