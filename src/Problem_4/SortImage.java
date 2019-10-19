package Problem_4;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    private int currentElem = 0;

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
            node = FXMLLoader.load(getClass().getResource("/Problem_4/filesUI.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        node.setPickOnBounds(true);
        anchor.getChildren().add(node);
       // node.prefHeight(400);
        backFrameButton.setDisable(true);
        nextFrameButton.setDisable(true);

        backFrameButton.setOnAction(event -> {
            if (currentElem > 0) {
                --currentElem;
                mainFrameImageView.setImage(new Image(inputFiles.get(currentElem).toURI().toString()));
            }
        });

        nextFrameButton.setOnAction(event -> {
            if (currentElem < inputFiles.size() - 1) {
                ++currentElem;
                mainFrameImageView.setImage(new Image(inputFiles.get(currentElem).toURI().toString()));
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

    //TODO : проверка , что все входящие файлы ТОЛЬКО фото
    private void startPlay(){
        if (!inputFiles.isEmpty()) {
            // inputFiles = new ArrayList<>(Arrays.asList(new File("src/resources.DronePhotos").listFiles()));
            currentElem = 0;

            Collections.sort(inputFiles, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return (int) (getImageDate(lhs).getTime() - getImageDate(rhs).getTime());
                }
            });
            //Инициализирую первым фрэймом image view
            mainFrameImageView.setImage(new Image(inputFiles.get(currentElem).toURI().toString()));
            //Делаю доступными кнопки переключения фрэймов (файлы уже загружены)
            backFrameButton.setDisable(false);
            nextFrameButton.setDisable(false);

            for (File file : inputFiles) {
                VBox vBox = new VBox();
                //нижняя линия
                System.out.println(frameBox.getHeight() + " " + frameBox.getPrefHeight() + " " + frameBox.getMaxHeight());
                Image image = new Image(file.toURI().toString(), (frameBox.getHeight() * 4032) / 3024, frameBox.getHeight(), true, true, true);
                ImageView iv = new ImageView();
                iv.setImage(image);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setCache(true);
                vBox.getChildren().addAll(iv);
                frameBox.getChildren().add(vBox);
            }
        }
    }

    private void startPlayWith(){
        if (!inputFiles.isEmpty()) {
            // inputFiles = new ArrayList<>(Arrays.asList(new File("src/resources.DronePhotos").listFiles()));
            //currentElem = 0;

            Collections.sort(inputFiles, new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    return (int) (getImageDate(lhs).getTime() - getImageDate(rhs).getTime());
                }
            });

            Timeline timeline = new Timeline();

            Duration totalDelay = Duration.ZERO;
            for (File file : inputFiles) {
                KeyFrame frame = new KeyFrame(totalDelay, e -> mainFrameImageView.setImage(new Image(file.toURI().toString(), (800 * 4032) / 3024, 800, true, true, true)));
                timeline.getKeyFrames().add(frame);
                totalDelay = totalDelay.add(new Duration(delay));
            }
            timeline.play();

            //Инициализирую первым фрэймом image view
           // mainFrameImageView.setImage(new Image(inputFiles.get(currentElem).toURI().toString()));
            //Делаю доступными кнопки переключения фрэймов (файлы уже загружены)
           // backFrameButton.setDisable(false);
          //  nextFrameButton.setDisable(false);

            for (File file : inputFiles) {
                VBox vBox = new VBox();
                Image image = new Image(file.toURI().toString(), (100 * 4032) / 3024, 100, true, true, true);
                ImageView iv = new ImageView();
                iv.setImage(image);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setCache(true);
                vBox.getChildren().addAll(iv);
                frameBox.getChildren().add(vBox);
            }
        }
    }

    private Date getImageDate(File file) {
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(file);
        } catch (ImageProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

        Date date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        return date;
    }
}
