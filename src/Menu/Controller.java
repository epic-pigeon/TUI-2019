package Menu;

import Problem_1.Main;
import Problem_1.MapView;
import Problem_4.CanvasFX;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Button trajectoryBtn;
    @FXML
    private Button improvPhotoBtn;
    @FXML
    private Button improvPhotoBtn2;
    @FXML
    private Button chronologyBtn;
    @FXML
    private Button objectDetectionBtn;
    @FXML
    private Button wateringBtn;
    @FXML
    private Button helpBtn;
    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trajectoryBtn.setOnAction(event -> openWindow(1));
        improvPhotoBtn.setOnAction(event -> openWindow(2));
        improvPhotoBtn2.setOnAction(event -> openWindow(3));
        chronologyBtn.setOnAction(event -> openWindow(4));
        objectDetectionBtn.setOnAction(event -> openWindow(5));
        wateringBtn.setOnAction(event -> openWindow(6));
        helpBtn.setOnAction(event -> openWindow(7));

    }

    private void openWindow(int number){
        try {
            Parent root;
            Stage stage = new Stage();
            switch (number){
                case 1:
                    startFirst(stage);
                    break;
                case 2:
                    stage.setTitle("Построение изображения");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_2/sortImages.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1900, 960));
                    break;
                case 3:
                    stage.setTitle("Улучшение изображения по разным фото");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_3/sortImages.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1900, 960));
                    break;
                case 4:
                  /*  stage.setTitle("Хронолигечская последовательность");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_4/sortImages.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1900, 960));*/
                    CanvasFX canvasFX = new CanvasFX();
                    canvasFX.start(stage);
                    break;
                case 5:
                    stage.setTitle("Распознавание ситуации на фото");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_5/sortImages.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1450, 1000));
                    break;
                case 6:
                    stage.setTitle("Полив поля");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_6/ui.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1066, 850));
                    break;
                case 7:
                    stage.setTitle("Соси бибу");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_6/ui.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1066, 850));
                    break;
            }
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Pane pane;
    private static VBox vbox;
    private static ArrayList<Label> labels = new ArrayList<>();
    public static ArrayList<TextField> textFields = new ArrayList<>();
    private static final double vgap = 15.0;
    private static final double hgap = 10.0;
    public static Button okBtn;


    private void startFirst(Stage primaryStage) throws IOException {
        Platform.runLater(() -> {
            try {
                new Main().start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
