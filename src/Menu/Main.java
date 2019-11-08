package Menu;

import Problem_1.Controller;
import com.drew.imaging.ImageProcessingException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Menu/ui.fxml"));
        primaryStage.setTitle("Menu");
        primaryStage.getIcons().add(new Image("/Menu/drone.png"));
        //  root.setStyle("-fx-background-color:rgba(0, 255, 0 , 0.3)");
        primaryStage.setScene(new Scene(root, 930, 500));
        primaryStage.show();
    }


    public static void main(String[] args) throws ImageProcessingException, IOException {
        if (args.length == 0) {
            launch(args);
        } else {
            File file = new File(args[0]);
            assert file.exists();
            Problem_1.Controller.Data data = Problem_1.Controller.read(file);
            Platform.runLater(() -> Controller.invokeMapWindow(data));
        }
    }
}
