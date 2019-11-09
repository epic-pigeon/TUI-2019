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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class CanvasFX extends Application implements Initializable {

    private static final double R = 40_000_000;
    private static final boolean ASYNC_MODE = true;
    private static final double EXPECTED_IMAGE_WIDTH = 300*2.2;
    private static final double EXPECTED_IMAGE_HEIGHT = 160*2.2;
    private static final int SCROLL_PANE_PADDING = 10;

    private static File tlogFile;

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
        run.setOnAction(event -> drawPictures());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> scaleCanvas(newValue));
    }

    private void triggerUploadFiles() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Photo and TLOG", "*.jpg", "*.png","*.tlog"));

        List<File> files = fileChooser.showOpenMultipleDialog(canvas.getScene().getWindow());
        photos = new ArrayList<>();
        for (File i : files) {
            String s = i.toURI().toString();
            if (s.endsWith(".tlog")){
                tlogFile = i;
                continue;
            }
            photos.add(new Pair<>(new AsyncImage(s, 400, 300, true, false, true),
                    Long.valueOf(s.substring(s.length() - 7, s.length() - 4))));
        }
    }

    private void loadCoordinates() {
       // coordinates = TLogParser.parseTextFile(new File("./src/Problem_4/resources/tlog_valid_parsed.txt"));
        try {
            coordinates = TLogParser.parseTLog(tlogFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        minX = new AtomicReference<>(metersFromCords(coordinates.get(0).getLongitude(), coordinates.get(0).getLatitude()));
        minY = new AtomicReference<>(metersFromLatitude(90 - coordinates.get(0).getLatitude()));
        maxX = new AtomicReference<>(0D);
        maxY = new AtomicReference<>(0D);

        for (TLogPoint kar : coordinates) {
            minX.set(Math.min(minX.get(), metersFromCords(kar.getLongitude(), kar.getLatitude())));
            minY.set(Math.min(minY.get(), metersFromLatitude(90 - kar.getLatitude())));
            maxX.set(Math.max(maxX.get(), metersFromCords(kar.getLongitude(), kar.getLatitude())));
            maxY.set(Math.max(maxY.get(), metersFromLatitude(90 - kar.getLatitude())));
        }
    }

    private void drawPictures() {
        loadCoordinates();
        clearCanvas();
        calculateDrawingParams();

        for (TLogPoint tLogPoint : coordinates) {
           // System.out.println(tLogPoint.getAltitude());
            Image image = findImage(tLogPoint.getImgId()).getKey();
            double altitude = tLogPoint.getAltitude();
            double x = tLogPoint.getLongitude(),
                    y = 90 - tLogPoint.getLatitude();
            double imgX = delta + (metersFromCords(x, 90-y) - minX.get()) * scale,
                    imgY = delta + (metersFromLatitude(y) - minY.get()) * scale;
            double imgWidth = imgW * altitude / 325, imgHeight = imgH * altitude / 325;
            //System.out.println(tLogPoint.getYaw());
            Rotate r = new Rotate(tLogPoint.getYaw(), imgWidth / 2 + imgX, imgHeight / 2 + imgY);
            if (ASYNC_MODE) {
                ((AsyncImage) image).onLoad(() -> {
                    gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                    gc.drawImage(image, imgX, imgY, imgWidth, imgHeight);
                }, true);
            } else {
                gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                gc.drawImage(image, imgX, imgY, imgWidth, imgHeight);
            }
        }

        // Set optimal slider value so that all photos fit to screen.
        double optimalSliderValue = Math.min((canvasScrollPane.getWidth() - SCROLL_PANE_PADDING) / canvas.getWidth(),
                ((canvasScrollPane.getHeight() - SCROLL_PANE_PADDING) / canvas.getHeight()));
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

    private static double metersFromLatitude(double deg) {
        return deg / 90 / 4 * R;
    }

    private static double metersFromCords(double lon, double lat) {
        return (lon / 90.0 / 4 * R) * Math.sin((90.0 - lat) / 90.0);
    }


    private static final double Rz = 6371000;

   /* private static double metersFromLatitude(double deg) {
        return Rz*Math.log(Math.tan(Math.PI*(90 + deg)/360));
    }

    private static double metersFromCoords(double lon, double lat) {
        return Rz*lon*Math.PI/180;
    }*/
}