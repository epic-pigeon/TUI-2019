package Problem_4;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class CanvasFX extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    private static final double R = 40_000_000;
    private List<TLogParser.TLogPoint> coordinates;
    private List<Pair<Image, Long>> photos = new ArrayList<>();
    private Button plus_Btn = new Button("+");
    private Slider slider = new Slider();
    private Rotate r;
    private double scale = 0.1;
    // private
    private double delta = 0;
    private Button okBtn = new Button("Start");
    private DoubleProperty
            canvasHeight = new SimpleDoubleProperty(4000),
            canvasWidth  = new SimpleDoubleProperty(4000);
    private double paneHeight = 800, paneWidth = 1000;
    private Canvas canvas = new Canvas(canvasWidth.get(), canvasHeight.get());
    {
        canvasWidth.bindBidirectional(canvas.widthProperty());
        canvasHeight.bindBidirectional(canvas.heightProperty());
    }
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private ScrollPane root;

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        //plus_Btn.setPadding(new Insets(30, 30 , 30, 30));
        plus_Btn.setTranslateX(100);
        initSlider();

        Pane rootMain = new Pane();

        root = new ScrollPane(canvas);
        root.setPrefWidth(paneWidth);
        root.setPrefHeight(paneHeight);
        rootMain.getChildren().addAll(root, okBtn, plus_Btn, slider);

        root.vvalueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);

            System.out.println(root.getHeight());
        });

        // Pane root = new Pane();
        /*root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");*/
        //  root.getChildren().add(canvas);
        Scene scene = new Scene(rootMain);
        stage.setScene(scene);
        stage.setTitle("Kar");
        stage.show();
        String imagePath = "/resources/birds.jpg";
        // Image image = new Image(imagePath);
        // Draw the Image
        coordinates = TLogParser.parseTLog(new File("./src/Problem_4/file.tlog"));

        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Files");
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Photo", "*.jpg", "*.png"));

        List<File> files = fileChooser.showOpenMultipleDialog(root.getScene().getWindow());
        for (File i : files) {
            String s = i.toURI().toString();
            photos.add(new Pair(new AsyncImage(s), Long.valueOf(s.substring(s.length() - 7, s.length() - 4))));
        }
        AtomicReference<Double> minX = new AtomicReference<>(metersFromDegrees(coordinates.get(0).getLongitude()));
        AtomicReference<Double> minY = new AtomicReference<>(metersFromDegrees(90 - coordinates.get(0).getLatitude()));
        AtomicLong c = new AtomicLong(0);
        okBtn.setOnAction(event -> {
            for (TLogParser.TLogPoint kar : coordinates
            ) {
                minX.set(Math.min(minX.get(), metersFromDegrees(kar.getLongitude())));
                minY.set(Math.min(minY.get(), metersFromDegrees(90 - kar.getLatitude())));
            }
            int w = (int) (300 * scale), h = (int) (160 * scale);
            for (TLogParser.TLogPoint kar : coordinates) {
                if (kar.getImgId() == 0) continue;
                Image image = findImage(kar.getImgId()).getKey();
                double x = kar.getLongitude(), y = 90 - kar.getLatitude();
                double imgX = delta + (metersFromDegrees(x) - minX.get()) * scale, imgY = delta + (metersFromDegrees(y) - minY.get()) * scale;
                Rotate r = new Rotate((180 * (kar.getYaw() - Math.PI)) / Math.PI, w / 2 + imgX, h / 2 + imgY);
                Rotate finalR = r;
                ((AsyncImage) image).onLoad(() -> {
                    gc.setTransform(finalR.getMxx(), finalR.getMyx(), finalR.getMxy(), finalR.getMyy(), finalR.getTx(), finalR.getTy());
                    gc.drawImage(image, imgX, imgY, w, h);
                }, true);
                r = new Rotate((180 * (kar.getYaw() - Math.PI)) / Math.PI, w / 2 + imgX, h / 2 + imgY);
                gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
                gc.drawImage(image, imgX, imgY, w, h);
                c.getAndIncrement();
            }
        });
        System.out.println(canvas.getScaleX());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() < -0.9) newValue = -0.9;
            System.out.println(newValue);
            //canvas.setTranslateX();
            //canvas.setTranslateY();
            double scaleX = newValue.doubleValue() + 1, scaleY = newValue.doubleValue() + 1;
            canvas.setScaleX(scaleX);
            canvas.setScaleY(scaleY);
            canvas.setTranslateX(100 + newValue.doubleValue()*canvas.getWidth()/2);
            canvas.setTranslateY(100 + newValue.doubleValue()*canvas.getHeight()/2);
        });
    }

    private Pair<Double, Double> getCanvasCenter() {
        return new Pair<>(
                root.getHvalue() * (canvasWidth.get() - paneWidth) + paneWidth / 2,
                root.getVvalue() * (canvasHeight.get() - paneHeight) + paneHeight / 2
        );
    }

    private Pair<Image, Long> findImage(long imgId) {
        for (Pair<Image, Long> i : photos) {
            if (i.getValue() == imgId) {
                return i;
            }
        }
        return new Pair<Image, Long>(new AsyncImage("/resources/birds.jpg"), (long) 0);
    }

    private static double metersFromDegrees(double deg) {
        return deg / 90 / 4 * R;
    }

    private void initSlider(){
        slider.setTranslateY(30);
        slider.setMin(-1);
        slider.setMax(1);
        slider.setValue(0);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(1);
    }
}