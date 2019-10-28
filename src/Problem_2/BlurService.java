package Problem_2;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class BlurService {
    // Change to target image name.
    private static final String IMG_NAME = "kar.jpg";

    private static final String BLURRED_IMG_PREFIX = "blurred";
    private static final String IMG_FORMAT = "jpg";
    private static final int BLUR_THRESHOLD = 100;

    public static void main(String[] args) throws Exception {
        nu.pattern.OpenCV.loadShared();

        BlurService blurservice = new BlurService();

        // OpenCV accepts only odd radius.
        int radius = 7;
        String blurryImgName = composeBlurredImgName(IMG_NAME, radius);
        blurryImgName = "src/resources/birds.jpg";
      //  blurservice.blurTask("src/Problem_2/kar.jpg", radius, blurryImgName);
      //  blurservice.detectBlurTask(IMG_NAME);
        blurservice.detectBlurTask(blurryImgName);
    }

    private static String composeBlurredImgName(String name, int radius) {
        return BLURRED_IMG_PREFIX + radius + name;
    }

    /**
     * Performs Gaussian blur to the given image, saves result to a new file.
     *
     * @param path       location of the image
     * @param radius     radius parameter for Gaussian blur, defines strength of blur. Should be an odd number.
     * @param resultPath location of result image
     * @throws IOException
     */
    public void blurTask(String path, int radius, String resultPath) throws IOException {
        BufferedImage source = ImageIO.read(new File(path));
        BufferedImage result = blur(source, radius);
        ImageIO.write(result, IMG_FORMAT, new File(resultPath));
        System.out.println(resultPath + " created");
    }

    public BufferedImage blur(BufferedImage source, int radius) throws IOException {
        Mat sourceMat = getMat(source);
        Mat destination = new Mat(sourceMat.rows(), sourceMat.cols(), sourceMat.type());
        Imgproc.GaussianBlur(sourceMat, destination, new Size(radius, radius), 0);
        return getImage(destination);
    }

    public static Mat getMat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, IMG_FORMAT, byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    public static BufferedImage getImage(Mat matrix) throws IOException {
        MatOfByte mob = new MatOfByte();
        Imgcodecs.imencode("." + IMG_FORMAT, matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }

    /**
     * Detects if provided image is blurry.
     *
     * @param path location of the image
     */
    public void detectBlurTask(String path) {
        Double blur = detectBlur(path);
        boolean isBlurry = blur < BLUR_THRESHOLD;
        System.out.format("File %s. %s: %f\n", path, isBlurry ? "Blurry" : "Not blurry", blur);
    }

    public Double detectBlur(String path) {
        Mat image = Imgcodecs.imread(path, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        if (image.empty()) {
            return null;
        } else {
            Mat destination = new Mat();
            Mat matGray = new Mat();

            Imgproc.cvtColor(image, matGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Laplacian(matGray, destination, 3);
            return getVariance(destination);
        }
    }

    public Mat getLaplacianMat(BufferedImage image) {
        Mat imageNew = null;
        try {
            imageNew = getMat(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageNew.empty()) {
            return null;
        } else {
            Mat destination = new Mat();
            Mat matGray = new Mat();

            Imgproc.cvtColor(imageNew, matGray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.Laplacian(matGray, destination, 3);
            return destination;
        }
    }

    public Double getVariance(Mat destination) {
        MatOfDouble std = new MatOfDouble();
        Core.meanStdDev(destination, new MatOfDouble(), std);
        return Math.pow(std.get(0, 0)[0], 2.0);
    }
}
