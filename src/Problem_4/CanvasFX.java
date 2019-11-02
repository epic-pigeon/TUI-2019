package Problem_4;

import Problem_4.TLogParser.TLogPoint;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class CanvasFX extends Application implements Initializable {

    private static final double R = 40_000_000;
    private static final boolean ASYNC_MODE = true;
    private static final int EXPECTED_IMAGE_WIDTH = 300;
    private static final int EXPECTED_IMAGE_HEIGHT = 160;
    private static final int SCROLL_PANE_PADDING = 10;

    @FXML
    private MenuItem uploadFiles;
    @FXML
    private MenuItem run;
    @FXML
    private Slider slider;
    @FXML
    private Canvas canvas;
    @FXML
    private ScrollPane canvasScrollPane;

    private DoubleProperty
            canvasHeight = new SimpleDoubleProperty(4000),
            canvasWidth = new SimpleDoubleProperty(4000);
    private GraphicsContext gc;

    private List<TLogPoint> coordinates;
    private List<Pair<Image, Long>> photos = new ArrayList<>();
    private double scale = 0.2;
    private double delta = 0;
    private int imgW, imgH;
    AtomicReference<Double> minX, minY, maxX, maxY;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Problem_4/canvas.fxml"));
        stage.setTitle("Последовательность фото");
        stage.getIcons().add(new Image("/resources/cloud-storage-uploading-option.png"));
        stage.setScene(new Scene(root, 1000, 600));
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        canvas.widthProperty().bindBidirectional(canvasWidth);
        canvas.heightProperty().bindBidirectional(canvasHeight);
        gc = canvas.getGraphicsContext2D();

        uploadFiles.setOnAction(event -> triggerUploadFiles());
        loadCoordinates();
        run.setOnAction(event -> drawPictures());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> scaleCanvas(newValue));
    }

    private void triggerUploadFiles() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Photo", "*.jpg", "*.png"));

        List<File> files = fileChooser.showOpenMultipleDialog(canvas.getScene().getWindow());
        photos = new ArrayList<>();
        for (File i : files) {
            String s = i.toURI().toString();
            photos.add(new Pair<>(new AsyncImage(s, 400, 300, true, false, true),
                    Long.valueOf(s.substring(s.length() - 7, s.length() - 4))));
        }
    }

    private void loadCoordinates() {
        coordinates = TLogParser.parseTextFile(new File("./src/Problem_4/resources/tlog_valid_parsed.txt"));
//        coordinates = TLogParser.parseTLog(new File("./src/Problem_4/resources/file.tlog"));

        minX = new AtomicReference<>(metersFromDegrees(coordinates.get(0).getLongitude()));
        minY = new AtomicReference<>(metersFromDegrees(90 - coordinates.get(0).getLatitude()));
        maxX = new AtomicReference<>(0D);
        maxY = new AtomicReference<>(0D);

        for (TLogPoint kar : coordinates) {
            minX.set(Math.min(minX.get(), metersFromDegrees(kar.getLongitude())));
            minY.set(Math.min(minY.get(), metersFromDegrees(90 - kar.getLatitude())));
            maxX.set(Math.max(maxX.get(), metersFromDegrees(kar.getLongitude())));
            maxY.set(Math.max(maxY.get(), metersFromDegrees(90 - kar.getLatitude())));
        }
    }

    private void drawPictures() {
        clearCanvas();
        calculateDrawingParams();

        for (TLogPoint tLogPoint : coordinates) {
            Image image = findImage(tLogPoint.getImgId()).getKey();
            double x = tLogPoint.getLongitude(),
                    y = 90 - tLogPoint.getLatitude();
            double imgX = delta + (metersFromDegrees(x) - minX.get()) * scale,
                    imgY = delta + (metersFromDegrees(y) - minY.get()) * scale;
            Rotate r = new Rotate((180 * (tLogPoint.getYaw() - Math.PI)) / Math.PI, imgW / 2 + imgX, imgH / 2 + imgY);
            if (ASYNC_MODE) {
                ((AsyncImage) image).onLoad(() -> {
                    gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                    gc.drawImage(image, imgX, imgY, imgW, imgH);
                }, true);
            } else {
                gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                gc.drawImage(image, imgX, imgY, imgW, imgH);
            }
        }

        // Set optimal slider value so that all photos fit to screen.
        double optimalSliderValue = Math.min((canvasScrollPane.getWidth() - SCROLL_PANE_PADDING) / canvas.getWidth(),
                Math.min((canvasScrollPane.getHeight() - SCROLL_PANE_PADDING), canvas.getHeight()));
        slider.valueProperty().setValue(optimalSliderValue);
    }

    private void calculateDrawingParams() {
        Image sampleImage = findImage(coordinates.get(0).getImgId()).getKey();
        double originalToExpectedRatio = Math.min(sampleImage.getWidth() / EXPECTED_IMAGE_WIDTH,
                sampleImage.getHeight() / EXPECTED_IMAGE_HEIGHT);
        int adaptedImgWidth = (int) (originalToExpectedRatio * sampleImage.getWidth());
        int adaptedImgHeight = (int) (originalToExpectedRatio * sampleImage.getHeight());
        int maxRotationShift = (int) Math.sqrt(Math.pow(adaptedImgWidth, 2) + Math.pow(adaptedImgHeight, 2));

        scale = Math.min(canvasWidth.doubleValue() / (maxX.get() - minX.get() + 1.5 * maxRotationShift),
                canvasHeight.doubleValue() / (maxY.get() - minY.get() + 1.5 * maxRotationShift));
        imgW = (int) (adaptedImgWidth * scale);
        imgH = (int) (adaptedImgHeight * scale);
        delta = maxRotationShift * scale / 2;
    }

    private void clearCanvas() {
        Rotate zeroAngle = new Rotate(0);
        gc.setTransform(zeroAngle.getMxx(), zeroAngle.getMyx(), zeroAngle.getMxy(), zeroAngle.getMyy(),
                zeroAngle.getTx(), zeroAngle.getTy());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void scaleCanvas(Number newValue) {
        if (newValue.doubleValue() < 0.1) newValue = 0.1;
        double scaleX = newValue.doubleValue(), scaleY = newValue.doubleValue();
        canvas.setScaleX(scaleX);
        canvas.setScaleY(scaleY);
        canvas.setTranslateX((newValue.doubleValue() - 1) * canvas.getWidth() / 2);
        canvas.setTranslateY((newValue.doubleValue() - 1) * canvas.getHeight() / 2);
    }

    private Pair<Image, Long> findImage(long imgId) {
        for (Pair<Image, Long> i : photos) {
            if (i.getValue() == imgId) {
                return i;
            }
        }
        return new Pair<>(new AsyncImage("/resources/birds.jpg", 400, 300, true, false),
                (long) 0);
    }

    private static double metersFromDegrees(double deg) {
        return deg / 90 / 4 * R;
    }
}