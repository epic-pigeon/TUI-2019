package Problem_6;

import Problem_2.BlurService;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MainCli {

    private static final String IMG_PATH = "src/Problem_6/resources/field2.jpg";

    public static void main(String[] args) throws Exception {
        nu.pattern.OpenCV.loadShared();

        processImage();
    }

    private static void processImage() throws IOException {
        Mat srcMat = BlurService.getMat(ImageIO.read(new File(IMG_PATH)));
        ImgProcessor imgProcessor = new ImgProcessor(IMG_PATH);

        // Area threshold can be set as a fixed value or relatively to image size.
        final double contourAreaThreshold = srcMat.size().width * srcMat.size().height * 0.005;

        // Turn on saving intermediate files if you want to see how each image processing stage works.
        imgProcessor.setIntermediateSaving(true)
                .process("color-emp", ImgProcessingUtils::filterByGreenishColorEmpirically)
                .process("blur", ImgProcessingUtils::blur)
                .process("thr", ImgProcessingUtils::brightnessThreshold)
                .process("top-contours-thr", mat -> ImgProcessingUtils.topContoursOverImage(mat,
                        contourAreaThreshold, srcMat));

        ImageIO.write(BlurService.getImage(imgProcessor.getMat()), "JPG",
                new File(imgProcessor.generateFileName()));
    }
}
