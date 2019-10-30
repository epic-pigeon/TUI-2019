package Problem_3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.*;

public class TestDataProvider {

    public static List<BufferedImage> getImages(String dirPath, List<String> imgNames) {
        List<BufferedImage> images = new ArrayList<>();
        for (String imgName : imgNames) {
            File file = Paths.get(dirPath, imgName).toFile();
            try {
                images.add(ImageIO.read(file));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return images;
    }

    public static List<ImagePosition> getImagePositions(String positionsFilePath, List<String> imgNames) {
        Map<String, ImagePosition> imageNameToPosition = getImageNameToPositionMap(positionsFilePath);
        List<ImagePosition> positions = new ArrayList<>();
        for (String imgName : imgNames) {
            assert imageNameToPosition.containsKey(imgName);
            positions.add(imageNameToPosition.get(imgName));
        }
        return positions;
    }

    private static Map<String, ImagePosition> getImageNameToPositionMap(String positionsFilePath) {
        Map<String, ImagePosition> imageNameToPosition = new HashMap<>();
        try (Scanner sc = new Scanner(new File(positionsFilePath))) {
            sc.nextLine();
            while (sc.hasNextLine()) {
                String[] positionData = sc.nextLine().split(",");
                String imageName = positionData[0];
                double angle = Double.parseDouble(positionData[1]);
                double deltaX = Double.parseDouble(positionData[2]);
                double deltaY = Double.parseDouble(positionData[3]);
                imageNameToPosition.put(imageName, new ImagePosition(angle, deltaX, deltaY));
            }
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
        return imageNameToPosition;
    }


}
