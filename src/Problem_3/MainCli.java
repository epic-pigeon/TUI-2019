package Problem_3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static Problem_3.TestDataProvider.getImagePositions;
import static Problem_3.TestDataProvider.getImages;

public class MainCli {

    private static final String IMG_DIR = "src/Problem_3/resources/";
    private static final List<String> IMG_NAMES = Arrays.asList("building_x0y0_blur.JPG", "building_x0y300_blur.JPG",
            "building_x100y300.JPG");
    private static final String POSITIONS_PATH = "src/Problem_3/resources/positions.csv";
    private static final String RESULT_DIR = "src/Problem_3/resources/tmp_result/";

    public static void main(String[] args) throws IOException {
        nu.pattern.OpenCV.loadShared();

        PhotoTransformer photoTransformer = new PhotoTransformer();
        List<ImagePosition> imagePositions = getImagePositions(POSITIONS_PATH, IMG_NAMES);

        BufferedImage result = photoTransformer.cropImage(getImages(IMG_DIR, IMG_NAMES), imagePositions);

        if (!new File(RESULT_DIR).exists()) {
            new File(RESULT_DIR).mkdirs();
        }
        ImageIO.write(result, "JPG", Paths.get(RESULT_DIR, System.currentTimeMillis() + "_result.jpg").toFile());
    }
}
