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
    private Mat mat;

    private boolean intermediateSaving;
    private String savingDirectory;
    private List<String> performedStages = new ArrayList<>();

    public ImgProcessor(BufferedImage image) {
        mat = BlurService.getMat(image);
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

    public String getSavingDirectory() {
        return savingDirectory;
    }

    public ImgProcessor setSavingDirectory(String savingDirectory) {
        this.savingDirectory = savingDirectory;
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
                ImageIO.write(BlurService.getImage(mat), "JPG", new File(generateFilePath()));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return this;
    }

    public String generateFilePath() {
        if (savingDirectory == null) {
            throw new IllegalStateException("Directory should be set to generate file path.");
        }

        if (!new File(savingDirectory).exists()) {
            new File(savingDirectory).mkdirs();
        }
        String fileName = System.currentTimeMillis() + "_" + String.join("_", performedStages) + ".jpg";
        return Paths.get(savingDirectory, fileName).toString();
    }
}