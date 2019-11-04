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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        nu.pattern.OpenCV.loadShared();

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

    private void startPlay() throws IOException {
        if (!inputFiles.isEmpty()) {
            BufferedImage result = new Denoise().Denoise(inputFiles);
            Image image = SwingFXUtils.toFXImage(result, null);
            mainFrameImageView.setImage(image);
        }
    }
}
