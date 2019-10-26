package Menu;

import Problem_1.MapView;
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
   // @FXML
   // private Button problem3Btn;
    @FXML
    private Button chronologyBtn;
    @FXML
    private Button objectDetectionBtn;
    @FXML
    private Button wateringBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        trajectoryBtn.setOnAction(event -> openWindow(1));
        improvPhotoBtn.setOnAction(event -> openWindow(2));
       // problem3Btn.setOnAction(event -> openWindow(3));
        chronologyBtn.setOnAction(event -> openWindow(4));
        objectDetectionBtn.setOnAction(event -> openWindow(5));
        wateringBtn.setOnAction(event -> openWindow(6));
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
                    stage.setTitle("Задача 3");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_4/sortImages.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1900, 960));
                    break;
                case 4:
                    stage.setTitle("Хронолигечская последовательность");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_4/sortImages.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1900, 960));
                    break;
                case 5:
                    stage.setTitle("Распознавание ситуации на фото");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_5/sortImages.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1450, 1000));
                    break;
                case 6:
                    stage.setTitle("Полив полей");
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("Problem_6/ui.fxml"));
                    stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
                    stage.setScene(new Scene(root, 1066, 850));
                    break;
            }
            stage.show();
        } catch (IOException e) {
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
        Parent root = FXMLLoader.load(getClass().getResource("/Problem_1/sample.fxml"));
        primaryStage.setTitle("Enter the data");
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
        primaryStage.show();
        pane = new Pane();
        labels.add(new Label("Enter focus Distance"));
        labels.add(new Label("Enter photo censor height"));
        labels.add(new Label("Enter photo censor width"));
        labels.add(new Label("Enter height"));
        labels.add(new Label("Enter field height"));
        labels.add(new Label("Enter field width"));
        labels.add(new Label("Enter field latitude"));
        labels.add(new Label("Enter field longitude"));
        labels.add(new Label("Enter field diagonal"));
        labels.add(new Label("Charge per photo"));
        labels.add(new Label("Charge per meter"));
        labels.add(new Label("Max energy"));

        vbox = new VBox(vgap);
        vbox.setPadding(new Insets(10 ,10 ,10 ,10));

        for (int i = 0; i < 12; i++) {
            TextField temp = new TextField();
            if (i == 6) temp.setText("50.467977");
            if (i == 7) temp.setText("31.211438");
            if (i == 8) temp.setText("0.01");
            temp.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
                try {
                    Float.valueOf(temp.getText() + keyEvent.getCharacter());
                }catch (Exception e){
                    keyEvent.consume();
                }
            });
            textFields.add(temp);

            HBox hBox = new HBox(hgap, labels.get(i), textFields.get(i));
            vbox.getChildren().add(hBox);
        }
        okBtn = new Button("Enter");
        vbox.getChildren().add(okBtn);
        pane.getChildren().add(vbox);
        primaryStage.setScene(new Scene(pane));

        okBtn.setOnAction(event -> {
            Problem_1.Controller.createMapWindow(
                    Float.valueOf(textFields.get(0).getText()),
                    Float.valueOf(textFields.get(1).getText()),
                    Float.valueOf(textFields.get(2).getText()),
                    Float.valueOf(textFields.get(3).getText()),
                    Float.valueOf(textFields.get(4).getText()),
                    Float.valueOf(textFields.get(5).getText()),
                    new MapView.LatLng(
                            Float.valueOf(textFields.get(6).getText()),
                            Float.valueOf(textFields.get(7).getText())
                    ),
                    Float.valueOf(textFields.get(8).getText()),
                    Float.valueOf(textFields.get(9).getText()),
                    Float.valueOf(textFields.get(10).getText()),
                    Float.valueOf(textFields.get(11).getText())
            );
        });
    }
}
