package Problem_3;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

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
    private SplitPane splitPane;
    @FXML
    private AnchorPane anchor;
    @FXML
    private ImageView mainFrameImageView;
    @FXML
    private MenuItem uploadFiles;
    @FXML
    private MenuItem addFiles;
    @FXML
    private MenuItem run;

    public static ArrayList<File> inputFiles = new ArrayList<>();

    private final FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nu.pattern.OpenCV.loadShared();

        //Подготовил FileChooser для загрузки файлов, установив фильры
        fileChooser.setTitle("Select Files");
        fileChooser.setInitialDirectory(new File("C:/Users"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Photo", "*.jpg", "*.png"));

        Parent node = null;
        try {
            node = FXMLLoader.load(getClass().getResource("/Problem_3/filesUI.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        node.setPickOnBounds(true);
        anchor.getChildren().add(node);
       // node.prefHeight(400);

        //Menu analyzing
        uploadFiles.setOnAction(event -> FilesUIController.uploadFilesAndUpdate(false));
        addFiles.setOnAction(event -> FilesUIController.uploadFilesAndUpdate(true));
        run.setOnAction(event -> {
            try {
                startPlay();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    //TODO : проверка , что все входящие файлы ТОЛЬКО фото
    private void startPlay() throws IOException {
        if (!inputFiles.isEmpty()) {
            //BufferedImage bufferedImage = new Denoise().Denoise2(inputFiles, 100);
           // BufferedImage bufferedImage = new Denoise().Denoise(inputFiles);
                BufferedImage bufferedImage = new Denoise().DenoiseFromBits(inputFiles);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            mainFrameImageView.setImage(image);
        }
    }
}
