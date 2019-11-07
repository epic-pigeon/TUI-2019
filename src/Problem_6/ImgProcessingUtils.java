package Problem_6;

import Problem_2.BlurService;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import javafx.util.Pair;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ImgProcessingUtils {

    // Blur radius can be tuned.
    private static final int BLUR_RADIUS = 15;

    public static Mat filterByGreenishColorEmpirically(Mat srcMat) {
        BufferedImage srcImg = BlurService.getImage(srcMat);
        WritableImage srcFxImg = SwingFXUtils.toFXImage(srcImg, null);

        BufferedImage filteredImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), ColorSpace.TYPE_RGB);

        for (int x = 0; x < srcImg.getWidth(); ++x) {
            for (int y = 0; y < srcImg.getHeight(); ++y) {
                javafx.scene.paint.Color color = srcFxImg.getPixelReader().getColor(x, y);
                //FIXME: подкорректировать фильтр
                boolean pixelMatches = color.getGreen() > 0.5 && color.getRed() < color.getGreen()
                        && color.getBlue() < color.getRed();
                filteredImg.setRGB(x, y, pixelMatches ? Color.WHITE.getRGB() : Color.BLACK.getRGB());
            }
        }
        return BlurService.getMat(filteredImg);
    }

    /**
     * Performs Gaussian blur.
     *
     * @param srcMat
     * @return
     */
    public static Mat blur(Mat srcMat) {
        Mat blurredMat = new Mat(srcMat.rows(), srcMat.cols(),
                srcMat.type());
        Imgproc.GaussianBlur(srcMat, blurredMat, new Size(BLUR_RADIUS, BLUR_RADIUS), 0);
        return blurredMat;
    }

    /**
     * Selects the pixels that are brighter than average (determined by fixed threshold).
     *
     * @param srcMat
     * @return
     */
    public static Mat brightnessThreshold(Mat srcMat) {
        // Brightness threshold can be tuned.
        Scalar grayStart = new Scalar(127, 127, 127);
        Scalar whiteEnd = new Scalar(255, 255, 255);
        Mat whiteFilteredMat = new Mat(srcMat.rows(), srcMat.cols(), srcMat.type());
        Core.inRange(srcMat, grayStart, whiteEnd, whiteFilteredMat);
        return whiteFilteredMat;
    }

    /**
     * Selects top contours depending on their area and draws these contours.
     *
     * @param srcMat        image, from which contours are extracted
     * @param areaThreshold minimal allowed area for the contour
     * @param outputMat     image, on which best contours will be drawn
     * @return
     */
    public static Mat topContoursOverImage(Mat srcMat, double areaThreshold, Mat outputMat) {
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(srcMat, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

        // Obtain list of pairs, where each value is a contour, each key is its contour area, and list is sorted by area
        // from largest to smallest.
        List<Pair<Double, MatOfPoint>> areaToContour = mapAreaToContoursSorted(contours);

        List<Double> areas = areaToContour.stream().map(Pair::getKey).collect(Collectors.toList());
        int bestContoursNumber = Math.toIntExact(areas.stream().filter(a -> a > areaThreshold).count());

        List<MatOfPoint> bestContours = areaToContour.stream().map(Pair::getValue).limit(bestContoursNumber)
                .collect(Collectors.toList());
//        Use fillPoly if you want to highlight inner part of contours as well.
        Imgproc.fillPoly(outputMat, bestContours, new Scalar(255, 0, 0));

        for (int contourIdx = 0; contourIdx < bestContours.size(); contourIdx++) {
            Imgproc.drawContours(outputMat, bestContours, contourIdx, new Scalar(255, 255, 255));
        }
        return outputMat;
    }

    private static List<Pair<Double, MatOfPoint>> mapAreaToContoursSorted(ArrayList<MatOfPoint> contours) {
        List<Pair<Double, MatOfPoint>> areaToContour = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            areaToContour.add(new Pair<>(area, contour));
        }
        Comparator<Pair<Double, MatOfPoint>> areaComparator = Comparator.comparing(Pair::getKey);
        areaToContour.sort(areaComparator.reversed());
        return areaToContour;
    }
}
