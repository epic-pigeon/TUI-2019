package Problem_3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static Problem_3.TestDataProvider.getImagePositions;
import static Problem_3.TestDataProvider.getImages;

public class Denoise {

    public BufferedImage DenoiseFromBits(List<File> inputFiles) {
        Date time = new Date();
        List<BufferedImage> bufferedImages = new ArrayList<>();
        for (File file : inputFiles) {
            try {
                bufferedImages.add(ImageIO.read(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<ImagePosition> positions = getPositionsFromCli(bufferedImages.size());
        BufferedImage result = new PhotoTransformer().cropImage(bufferedImages, positions);
        System.out.println(((double) (new Date().getTime() - time.getTime())) / 1000.000);
        return result;
    }

    private List<ImagePosition> getPositionsFromCli(int size) {
        List<ImagePosition> positions = new ArrayList<>();

        double angle, deltaX, deltaY;
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < size; i++) {
            angle = scanner.nextDouble();
            deltaX = scanner.nextDouble();
            deltaY = scanner.nextDouble();
            positions.add(new ImagePosition(angle, deltaX, deltaY));
        }

        return positions;
    }

    public BufferedImage Denoise(List<File> files){
        nu.pattern.OpenCV.loadShared();

        PhotoTransformer photoTransformer = new PhotoTransformer();
        String POSITIONS_PATH = "";
        List<String> IMG_NAMES = new ArrayList<>();
        String IMG_DIR = "src/Problem_3/resources/";
        String RESULT_DIR = "src/Problem_3/resources/tmp_result/";

        boolean fl = true;
        for (File file: files){
            if (fl){
                if (file.toURI().toString().endsWith(".csv")){
                    fl = false;
                    POSITIONS_PATH = file.getPath();
                    continue;
                }
            }
            IMG_NAMES.add(file.getName());
        }


        List<ImagePosition> imagePositions = getImagePositions(POSITIONS_PATH, IMG_NAMES);

        BufferedImage result = photoTransformer.cropImage(getImages(IMG_DIR, IMG_NAMES), imagePositions);

        if (!new File(RESULT_DIR).exists()) {
            new File(RESULT_DIR).mkdirs();
        }

        return result;
    }
}
