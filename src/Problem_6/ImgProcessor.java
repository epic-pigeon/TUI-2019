package Problem_6;

import Problem_2.BlurService;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Can be used to perform a sequence of image processing stages.
 */
public class ImgProcessor {
    private String srcPath;
    private Mat mat;

    private boolean intermediateSaving;
    private List<String> performedStages = new ArrayList<>();

    public ImgProcessor(String path) {
        try {
            srcPath = path;
            mat = BlurService.getMat(ImageIO.read(new File(path)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public ImgProcessor(BufferedImage image, String path) {
        try {
            srcPath = path;
            mat = BlurService.getMat(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Mat getMat() {
        return mat;
    }

    public boolean isIntermediateSaving() {
        return intermediateSaving;
    }

    public ImgProcessor setIntermediateSaving(boolean intermediateSaving) {
        this.intermediateSaving = intermediateSaving;
        return this;
    }

    public List<String> getPerformedStages() {
        return performedStages;
    }

    public ImgProcessor process(String processingStage, Function<Mat, Mat> func) {
        return process(processingStage, func, true);
    }

    public ImgProcessor process(String processingStage, Function<Mat, Mat> func, boolean verbose) {
        long startTime = System.currentTimeMillis();
        mat = func.apply(mat);
        if (verbose) {
            System.out.println("Applied stage " + processingStage + ". Time: " + (System.currentTimeMillis() - startTime));
        }
        performedStages.add(processingStage);
        if (intermediateSaving) {
            try {
                ImageIO.write(BlurService.getImage(mat), "JPG", new File(generateFileName()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return this;
    }

    public String generateFileName() {
        File srcFile = new File(srcPath);
        String dirPath = Paths.get(srcFile.getAbsoluteFile().getParentFile().getAbsolutePath(), "tmp_result").toString();
        if (!new File(dirPath).exists()) {
            new File(dirPath).mkdirs();
        }
        String fileNamePrefix = System.currentTimeMillis() + "_" + String.join("_", performedStages);
        return Paths.get(dirPath, fileNamePrefix + "_" + srcFile.getName()).toString();
    }
}