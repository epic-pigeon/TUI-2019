package Problem_3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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
}
