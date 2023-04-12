// Maliha Tarafdar and Pranav Tripuraneni

package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import view.MainController;

public class SongLib extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/view/main.fxml"));
			GridPane pane = (GridPane) loader.load();

			MainController controller = loader.getController();
			controller.shutdown(stage);

			Scene scene = new Scene(pane);
			stage.setScene(scene);
			stage.setTitle("Song Library");
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
