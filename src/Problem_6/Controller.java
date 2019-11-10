/*package Problem_6;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane originalAnchorPane;
    @FXML
    private AnchorPane processedAnchorPane;

    @FXML
    private MenuItem uploadImageBtn;

    private final FileChooser fileChooser = new FileChooser();

    private File fileImg;

    private ColorApproximator approximator;

    public static Color ROAD_COLOR = Color.GRAY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser.setTitle("Select Image");
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Photo", "*.jpg", "*.png"));

        uploadImageBtn.setOnAction(event -> {
            try {
                fileImg = fileChooser.showOpenDialog(originalAnchorPane.getScene().getWindow());
                VBox vBox = new VBox();
                String filePath = fileImg.toURI().toString();

                try (InputStream input = new FileInputStream(fileImg)) {
                    try {
                        ImageIO.read(input).toString();
                    } catch (Exception e) {
                        filePath = "/resources/unknown.png";
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Image image = new Image(filePath, (800 * 4032) / 3024, 800, true, true, false);
                ImageView iv = new ImageView();
                iv.setImage(image);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setCache(true);
                vBox.getChildren().addAll(iv, new Label(fileImg.getName()));
                vBox.setPadding(new Insets(1, 1, 1, 1));

                Image processedImg = processingPhoto(image);
                VBox vBox2 = new VBox();
                ImageView iv2 = new ImageView();
                iv2.setImage(processedImg);
                iv2.setPreserveRatio(true);
                iv2.setSmooth(true);
                iv2.setCache(true);
                vBox2.getChildren().addAll(iv2, new Label(fileImg.getName()));
                vBox2.setPadding(new Insets(1, 1, 1, 1));

                originalAnchorPane.getChildren().add(vBox);
                processedAnchorPane.getChildren().add(vBox2);
            } catch (NullPointerException e) {
            }
        });

        approximator = new ColorApproximator(new ArrayList<Color>() {{
            //add(new Color(77, 120, 49));
            //add(new Color(77, 100, 49));
            //add(new Color(111, 125, 64));
            //add(new Color(122, 136, 70));
            //add(new Color(118, 128, 65));
            // add(new Color(136, 123, 79));
            //add(new Color(139, 173, 141));
            //add(new Color(38, 111, 16));
            //add(new Color(51, 152, 22));
            //add(new Color(120, 120, 40));
            add(ROAD_COLOR);
            add(new Color(77, 145, 57));
            //add(new Color(136, 145, 80));
        }}, 7000);
    }

    //TODO: Отрегулировать фильтр (менять только числа, а не алгоритм)
    private Image processingPhoto(Image image) {
        //BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        //Это более точная
        BufferedImage bufferedImage2 = SwingFXUtils.fromFXImage(image, null);
        for (int i = 0; i < image.getHeight(); ++i) {
            for (int j = 0; j < image.getWidth(); ++j) {
                javafx.scene.paint.Color _color = image.getPixelReader().getColor(j, i);
                java.awt.Color color = new Color(
                        (int) (_color.getRed() * 255),
                        (int) (_color.getGreen() * 255),
                        (int) (_color.getBlue() * 255)
                );
                //  java.awt.Color approximated = approximator.approximate(_color);
                //bufferedImage2.setRGB(j, i, approximated == null || approximated.getRGB() == ROAD_COLOR.getRGB() ? 0 : approximated.getRGB());
                // if (_color.getGreen() > 0.4 && _color.getRed() < _color.getGreen() && _color.getBlue() < _color.getRed()) {
                //   bufferedImage2.setRGB(j, i, new Color(20 , 20 , (int)(_color.getGreen() * 255)).getRGB());
                // }

                Color greenGradientStart = new Color(0, 80, 0);
                Color greenGradientEnd = new Color(200, 200, 100);
                Color blueGradientStart = new Color(0, 0, 255);
                Color blueGradientEnd = new Color(255, 0, 0);

                if (checkInGradient(greenGradientStart, greenGradientEnd, color)) {
                    bufferedImage2.setRGB(j, i,
                            mapColor(
                                    greenGradientStart, greenGradientEnd,
                                    blueGradientStart, blueGradientEnd,
                                    color
                            ).getRGB()
                    );
                } else {
                    bufferedImage2.setRGB(j, i, 0);
                }
            }
        }
        return SwingFXUtils.toFXImage(bufferedImage2, null);
    }

    private static Color mapColor(
            Color gradientFromStart, Color gradientFromEnd,
            Color gradientToStart, Color gradientToEnd,
            Color toMap
    ) {
        double pR = (double) (toMap.getRed() - gradientFromStart.getRed()) / (gradientFromEnd.getRed() - gradientFromStart.getRed());
        double pG = (double) (toMap.getGreen() - gradientFromStart.getGreen()) / (gradientFromEnd.getGreen() - gradientFromStart.getGreen());
        double pB = (double) (toMap.getBlue() - gradientFromStart.getBlue()) / (gradientFromEnd.getBlue() - gradientFromStart.getBlue());
        return new Color(
                (int) (gradientToStart.getRed() + pR * (gradientToEnd.getRed() - gradientToStart.getRed())),
                (int) (gradientToStart.getGreen() + pG * (gradientToEnd.getGreen() - gradientToStart.getGreen())),
                (int) (gradientToStart.getBlue() + pB * (gradientToEnd.getBlue() - gradientToStart.getBlue()))
        );
    }

    private static boolean checkInGradient(
            Color gradientFromStart, Color gradientFromEnd,
            Color toCheck
    ) {
        return
                ((toCheck.getRed() >= gradientFromStart.getRed()) && (toCheck.getRed() <= gradientFromEnd.getRed())) &&
                        ((toCheck.getGreen() >= gradientFromStart.getGreen()) && (toCheck.getGreen() <= gradientFromEnd.getGreen())) &&
                        ((toCheck.getBlue() >= gradientFromStart.getBlue()) && (toCheck.getBlue() <= gradientFromEnd.getBlue()));
    }
}


*/

package Problem_6;

import Problem_2.BlurService;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private Pane originalPane;
    @FXML
    private Pane processedPane;
    @FXML
    private Pane processedProPane;
    @FXML
    private MenuItem uploadImageBtn;

    private final FileChooser fileChooser = new FileChooser();

    private File fileImg;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser.setTitle("Select Image");
        fileChooser.setInitialDirectory(new File("C:/Users"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Photo", "*.jpg", "*.png"));

        uploadImageBtn.setOnAction(event -> {
            try {
                fileImg = fileChooser.showOpenDialog(originalPane.getScene().getWindow());
                VBox vBox = new VBox();
                String filePath = fileImg.toURI().toString();

                try (InputStream input = new FileInputStream(fileImg)) {
                    try {
                        ImageIO.read(input).toString();
                    } catch (Exception e) {
                        filePath = "/resources/unknown.png";
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Image image = new Image(filePath, (800 * 4032) / 3024, 800, true, true, false);
                ImageView iv = new ImageView();
                iv.setImage(image);
                iv.setPreserveRatio(true);
                iv.setSmooth(true);
                iv.setCache(true);
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(iv, new Label(fileImg.getName()));
                vBox.setPadding(new Insets(1, 1, 1, 1));

                Image processedImgPro = processingPhotoPro(image);
                Image processedImgContours = processingPhoto(processedImgPro, image);

                VBox vBox2 = new VBox();
                ImageView iv2 = new ImageView();
                iv2.setImage(processedImgContours);
                iv2.setPreserveRatio(true);
                iv2.setSmooth(true);
                iv2.setCache(true);
                vBox2.setAlignment(Pos.CENTER);
                vBox2.getChildren().addAll(iv2, new Label(fileImg.getName()));
                vBox2.setPadding(new Insets(1, 1, 1, 1));

                VBox vBox3 = new VBox();
                ImageView iv3 = new ImageView();
                iv3.setImage(processedImgPro);
                iv3.setPreserveRatio(true);
                iv3.setSmooth(true);
                iv3.setCache(true);
                vBox3.setAlignment(Pos.CENTER);
                vBox3.getChildren().addAll(iv3, new Label(fileImg.getName()));
                vBox3.setPadding(new Insets(1, 1, 1, 1));

                originalPane.getChildren().add(vBox);
                processedPane.getChildren().add(vBox2);
                processedProPane.getChildren().add(vBox3);
            } catch (NullPointerException e) {
            }
        });
    }

    private Image processingPhoto(Image processedImage, Image originalImage) {
        nu.pattern.OpenCV.loadShared();
        BufferedImage bufferedImage = getRgbBufferedImageFromFx(processedImage);
        Mat srcMat = BlurService.getMat(bufferedImage);
        ImgProcessor imgProcessor = new ImgProcessor(bufferedImage);

        // Area threshold can be set as a fixed value or relatively to image size.
        final double contourAreaThreshold = srcMat.size().width * srcMat.size().height * 0.005;

        // Turn on saving intermediate files if you want to see how each image processing stage works.
        imgProcessor.setIntermediateSaving(false)
//                .setSavingDirectory("tmp_result")
                .process("color-emp", ImgProcessingUtils::filterByRedColorEmpirically)
                .process("blur", ImgProcessingUtils::blur)
                .process("thr", ImgProcessingUtils::brightnessThreshold)
                .process("top-contours-thr", mat -> ImgProcessingUtils.topContoursOverImage(mat,
                        contourAreaThreshold, BlurService.getMat(getRgbBufferedImageFromFx(originalImage))));

        return SwingFXUtils.toFXImage(BlurService.getImage(imgProcessor.getMat()), null);
    }

    private Image processingPhotoPro(Image image) {
        BufferedImage bufferedImage2 = getRgbBufferedImageFromFx(image);
        int width = (int) image.getWidth(), height = (int) image.getHeight();
        int h = 10, w = 10;

        for (int y = 0; y < height; y += h) {
            for (int x = 0; x < width; x += w) {
                //кол-во подходящих и всех (можно перемножать стороны прямоугольника, но тогда нужны случаи)
                int c = 0, C = 0;
                for (int i = y; i < h + y && i < height; ++i) {
                    for (int j = x; j < w + x && j < width; ++j) {
                        C++;
                        javafx.scene.paint.Color _color = image.getPixelReader().getColor(j, i);
                        Color color = new Color(
                                (int) (_color.getRed() * 255),
                                (int) (_color.getGreen() * 255),
                                (int) (_color.getBlue() * 255)
                        );
                        Color greenGradientStart = new Color(0, 80, 0);
                        Color greenGradientEnd = new Color(200, 200, 100);
                        if (checkInGradient(greenGradientStart, greenGradientEnd, color)) {
                            c++;
                        }
                    }
                }
                for (int i = y; i < h + y && i < height; ++i) {
                    for (int j = x; j < w + x && j < width; ++j) {

                        javafx.scene.paint.Color _color = image.getPixelReader().getColor(j, i);
                        Color color = new Color(
                                (int) (_color.getRed() * 255),
                                (int) (_color.getGreen() * 255),
                                (int) (_color.getBlue() * 255)
                        );

                        Color greenGradientStart = new Color(0, 80, 0);
                        Color greenGradientEnd = new Color(200, 200, 100);
                        Color blueGradientStart = new Color(0, 0, 255);
                        Color blueGradientEnd = new Color(255, 0, 0);
                        if (/*1.5 * c >= C &&*/ checkInGradient(greenGradientStart, greenGradientEnd, color)) {
                            bufferedImage2.setRGB(j, i,
                                    mapColor(
                                            greenGradientStart, greenGradientEnd,
                                            blueGradientStart, blueGradientEnd,
                                            color
                                    ).getRGB()
                            );
                        } else {
                            bufferedImage2.setRGB(j, i, 0);
                        }
                    }
                }
            }
        }
        return SwingFXUtils.toFXImage(bufferedImage2, null);
    }

    private static Color mapColor(
            Color gradientFromStart, Color gradientFromEnd,
            Color gradientToStart, Color gradientToEnd,
            Color toMap
    ) {
        double pR = (double) (toMap.getRed() - gradientFromStart.getRed()) / (gradientFromEnd.getRed() - gradientFromStart.getRed());
        double pG = (double) (toMap.getGreen() - gradientFromStart.getGreen()) / (gradientFromEnd.getGreen() - gradientFromStart.getGreen());
        double pB = (double) (toMap.getBlue() - gradientFromStart.getBlue()) / (gradientFromEnd.getBlue() - gradientFromStart.getBlue());
        return new Color(
                (int) (gradientToStart.getRed() + pR * (gradientToEnd.getRed() - gradientToStart.getRed())),
                (int) (gradientToStart.getGreen() + pG * (gradientToEnd.getGreen() - gradientToStart.getGreen())),
                (int) (gradientToStart.getBlue() + pB * (gradientToEnd.getBlue() - gradientToStart.getBlue()))
        );
    }

    private static boolean checkInGradient(
            Color gradientFromStart, Color gradientFromEnd,
            Color toCheck
    ) {
        return
                ((toCheck.getRed() >= gradientFromStart.getRed()) && (toCheck.getRed() <= gradientFromEnd.getRed())) &&
                        ((toCheck.getGreen() >= gradientFromStart.getGreen()) && (toCheck.getGreen() <= gradientFromEnd.getGreen())) &&
                        ((toCheck.getBlue() >= gradientFromStart.getBlue()) && (toCheck.getBlue() <= gradientFromEnd.getBlue()));
    }

    /**
     * Needed because {@link SwingFXUtils#fromFXImage} method converts only to RGBA BufferedImage.
     *
     * @see <a href="http://google.com">https://bugs.openjdk.java.net/browse/JDK-8119048</a>
     */
    private BufferedImage getRgbBufferedImageFromFx(Image fxImage) {
        BufferedImage bufferedImg = SwingFXUtils.fromFXImage(fxImage, null);
        BufferedImage rgbImage = new BufferedImage(bufferedImg.getWidth(), bufferedImg.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        rgbImage.getGraphics().drawImage(bufferedImg, 0, 0, null);
        return rgbImage;
    }
}