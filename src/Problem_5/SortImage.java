package Problem_5;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SortImage implements Initializable {

    @FXML
    private Button kar;
    @FXML
    private AnchorPane anchor;
    @FXML
    private HBox frameBox;
    @FXML
    private ImageView mainFrameImageView;
    @FXML
    private Button backFrameButton;
    @FXML
    private Button nextFrameButton;
    @FXML
    private MenuItem uploadFiles;
    @FXML
    private MenuItem addFiles;
    @FXML
    private MenuItem run;
    @FXML
    private MenuItem runAuto;
    @FXML
    private MenuItem param;

    public static ArrayList<File> inputFiles = new ArrayList<>();

    private ArrayList<Image> images = new ArrayList<>();

    private int currentElem = 0;

    private boolean fl = true;

    private static double delay = 1500;

    private final FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Подготовил FileChooser для загрузки файлов, установив фильры
        fileChooser.setTitle("Select Files");
        fileChooser.setInitialDirectory(new File("C:/Users"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Photo", "*.jpg", "*.png"));

        Parent node = null;
        try {
            node = FXMLLoader.load(getClass().getResource("/Problem_5/filesUI.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        node.setPickOnBounds(true);
        anchor.getChildren().add(node);
        backFrameButton.setDisable(true);
        nextFrameButton.setDisable(true);

        backFrameButton.setOnAction(event -> {
            if (currentElem > 0) {
                --currentElem;
                mainFrameImageView.setImage(images.get(currentElem));
            }
        });

        nextFrameButton.setOnAction(event -> {
            if (currentElem < inputFiles.size() - 1) {
                ++currentElem;
                mainFrameImageView.setImage(images.get(currentElem));
            }
        });

        //Menu analyzing
        uploadFiles.setOnAction(event -> FilesUIController.uploadFilesAndUpdate(false));
        addFiles.setOnAction(event -> FilesUIController.uploadFilesAndUpdate(true));
        run.setOnAction(event -> startPlay());
        runAuto.setOnAction(event -> startPlayWith());
        param.setOnAction(event -> setParam());
    }

    private void setParam(){
        Stage dialog = new Stage();
        VBox root = new VBox(15);
        root.setPadding(new Insets(10 ,10 ,10 ,10));
            TextField temp = new TextField();
            temp.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
                try {
                    Integer.valueOf(temp.getText() + keyEvent.getCharacter());
                }catch (Exception e){
                    keyEvent.consume();
                }
            });
            Button okBtn = new Button("OK");
            okBtn.setOnAction(event -> {
                try {
                    if (Integer.valueOf(temp.getText()) > 0){
                        delay = Integer.valueOf(temp.getText());
                        dialog.close();
                    }
                }catch (Exception e){
                }
            });

            okBtn.setPrefWidth(200);
            root.getChildren().addAll(temp, okBtn);
        dialog.initOwner(anchor.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(new Scene(root , 200 , 100));
        dialog.setTitle("Введите задержку!");
        dialog.showAndWait();
    }

    private void startPlay(){
        if (!inputFiles.isEmpty()) {
           /// for (int i = 0; i < objects.size(); i++) {
           //     System.out.println(objects.get(i) + " " + status.get(i));
         //   }
            currentElem = 0;
            for (File file : inputFiles) {
                BufferedImage bufferedImage = (new Detection()).Detection(file);
                VBox vBox = new VBox();
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                images.add(image);

                ImageView iv = new ImageView();
                iv.setImage(image);
                iv.setFitWidth((200 * 4032) / 3024);
                iv.setFitHeight(200);
                iv.setPreserveRatio(true);
                iv.setSmooth(false);
                iv.setCache(true);
                vBox.getChildren().addAll(iv);
                frameBox.getChildren().add(vBox);
            }
            mainFrameImageView.setImage(images.get(currentElem));
            mainFrameImageView.setFitHeight((800 * 4032) / 3024);
            mainFrameImageView.setFitHeight(800);
            mainFrameImageView.setSmooth(false);
            mainFrameImageView.setPreserveRatio(true);

            backFrameButton.setDisable(false);
            nextFrameButton.setDisable(false);
        }
    }

    private void startPlayWith(){
        if (!inputFiles.isEmpty()) {
            for (File file : inputFiles) {
                BufferedImage bufferedImage = (new Detection()).Detection(file);
                VBox vBox = new VBox();
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                images.add(image);

                ImageView iv = new ImageView();
                iv.setImage(image);
                iv.setFitWidth((200 * 4032) / 3024);
                iv.setFitHeight(200);
                iv.setPreserveRatio(true);
                iv.setSmooth(false);
                iv.setCache(true);
                vBox.getChildren().addAll(iv);
                frameBox.getChildren().add(vBox);
            }

            //Collections.sort(inputFiles, (lhs, rhs) -> (int) (getImageDate(lhs).getTime() - getImageDate(rhs).getTime()));
            backFrameButton.setDisable(false);
            nextFrameButton.setDisable(false);
            Timeline timeline = new Timeline();
            Duration totalDelay = Duration.ZERO;
            for (Image image : images) {
                KeyFrame frame = new KeyFrame(totalDelay, e -> {
                    mainFrameImageView.setImage(image);
                    mainFrameImageView.setFitHeight((800 * 4032) / 3024);
                    mainFrameImageView.setFitHeight(800);
                    mainFrameImageView.setSmooth(false);
                    mainFrameImageView.setPreserveRatio(true);
                });
                timeline.getKeyFrames().add(frame);
                totalDelay = totalDelay.add(new Duration(delay));
            }
            timeline.play();
        }
    }
}
