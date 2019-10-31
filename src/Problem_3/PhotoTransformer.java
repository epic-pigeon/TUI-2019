package Problem_3;

import Problem_2.BlurService;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PhotoTransformer {

    private BufferedImage rotateImageByDegrees(BufferedImage img, ImagePosition position) {
        double rads = Math.toRadians(position.getAngle());
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();

        // Move rotated image so that image crop is prevented.
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);
        position.setDeltaX(position.getDeltaX() - (newWidth - w) / 2);
        position.setDeltaY(position.getDeltaY() - (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    //FIXME: величины delta идут как пиксели , а не как реальные координаты
    private BufferedImage transformImageByVector(BufferedImage img, ImagePosition position) {
        double deltaX = position.getDeltaX();
        double deltaY = position.getDeltaY();
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = w + (int) Math.abs(deltaX);
        int newHeight = h + (int) Math.abs(deltaY);

        BufferedImage transformed = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = transformed.createGraphics();
        AffineTransform at = new AffineTransform();

        at.translate(deltaX, deltaY);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return transformed;
    }

    public BufferedImage translateImage(BufferedImage img, ImagePosition position) {
        img = rotateImageByDegrees(img, position);
        return transformImageByVector(img, position);
    }

    private BufferedImage adaptToSize(BufferedImage img, Size maxSize) {
        BufferedImage transformed = new BufferedImage((int) maxSize.width, (int) maxSize.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = transformed.createGraphics();
        g2d.drawImage(img, 0, 0, null);
        return transformed;
    }

    private final int w = 30;
    private final int h = 30;

    public BufferedImage cropImage(List<BufferedImage> images, List<ImagePosition> positions) {
        translateImages(images, positions);
        save("src/Problem_3/resources/tmp_result/", images);

        System.out.println("Image size: " + images.get(0).getHeight() + "x" + images.get(0).getWidth());
        return cropImageAfterTransform(images);
    }

    private void translateImages(List<BufferedImage> images, List<ImagePosition> positions) {
        // Rotate images.
        for (int i = 0; i < images.size(); i++) {
            BufferedImage translatedImage = rotateImageByDegrees(images.get(i), positions.get(i));
            images.set(i, translatedImage);
        }
        // Adapt deltaX and deltaY to exclude negative deltas and make deltas as small as possible.
        changeDeltasStartingPoint(images, positions);
        // Move each image to specified position.
        for (int i = 0; i < images.size(); i++) {
            BufferedImage translatedImage = transformImageByVector(images.get(i), positions.get(i));
            images.set(i, translatedImage);
        }
        // Adapt images to have the same size.
        Size maxSize = new Size();
        for (BufferedImage image : images) {
            maxSize.height = Math.max(maxSize.height, image.getHeight());
            maxSize.width = Math.max(maxSize.width, image.getWidth());
        }
        for (int i = 0; i < images.size(); i++) {
            images.set(i, adaptToSize(images.get(i), maxSize));
        }
    }

    private void changeDeltasStartingPoint(List<BufferedImage> images, List<ImagePosition> positions) {
        ImagePosition minDeltas = new ImagePosition(0, images.get(0).getWidth(), images.get(0).getHeight());
        for (ImagePosition position : positions) {
            minDeltas.setDeltaX(Math.min(minDeltas.getDeltaX(), position.getDeltaX()));
            minDeltas.setDeltaY(Math.min(minDeltas.getDeltaY(), position.getDeltaY()));
        }
        for (ImagePosition position : positions) {
            position.setDeltaX(position.getDeltaX() - minDeltas.getDeltaX());
            position.setDeltaY(position.getDeltaY() - minDeltas.getDeltaY());
        }
    }

    private BufferedImage cropImageAfterTransform(List<BufferedImage> images) {
        int width = images.get(0).getWidth(), height = images.get(0).getHeight();
        BufferedImage result = getImageAll(images);
        save("src/Problem_3/resources/tmp_result/", new ArrayList<BufferedImage>() {{
            add(result);
        }});

        Graphics g = result.getGraphics();
        BlurService service = new BlurService();
        // Apply Laplacian operator to each image.
        List<Mat> laplacianMats = new ArrayList<>();
        for (BufferedImage image : images) {
            laplacianMats.add(service.getLaplacianMat(image));
        }
        for (int y = 0; y < height; y += h) {
            for (int x = 0; x < width; x += w) {
                // Finding best image id for current region defined by (x,y). Best region has highest variance
                // after Laplacian operator.
                Rect rect = new Rect(x, y, Math.min(w, width - x), Math.min(h, height - y));
                double maxVariance = 0;
                int bestImgId = 0;
                for (int imgId = 0; imgId < images.size(); imgId++) {
                    Double variance = service.getVariance(new Mat(laplacianMats.get(imgId), rect));
                    if (variance > maxVariance) {
                        maxVariance = variance;
                        bestImgId = imgId;
                    }
                }
                // Inserting best image region to the result image.
                BufferedImage bitFinishImage = images.get(bestImgId).getSubimage(x, y, Math.min(w, width - x),
                        Math.min(h, height - y));
                for (int i = 0; i < bitFinishImage.getHeight(); i++) {
                    for (int j = 0; j < bitFinishImage.getWidth(); j++) {
                        int color = bitFinishImage.getRGB(j, i);
                        if (Color.black.equals(new Color(color))) {
                            bitFinishImage.setRGB(j, i, result.getRGB(x + j, y + i));
                        }
                    }
                }
                g.drawImage(bitFinishImage, x, y, bitFinishImage.getWidth(), bitFinishImage.getHeight(), null);
            }
        }
        return result;
    }

    private void save(String directoryPath, List<BufferedImage> images) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            boolean dirCreated = dir.mkdirs();
            assert dirCreated;
        }
        for (int i = 0; i < images.size(); i++) {
            File file = Paths.get(directoryPath, System.currentTimeMillis() + "_image" + i + ".jpg").toFile();
            try {
                ImageIO.write(images.get(i), "JPG", file);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private BufferedImage getImageAll(List<BufferedImage> images) {
        boolean fl;
        BufferedImage res = images.get(0);
        for (int i = 0; i < res.getHeight(); i++) {
            for (int j = 0; j < res.getWidth(); j++) {
                fl = true;
                for (int k = 0; k < images.size(); k++) {
                    int color = images.get(k).getRGB(j, i);
                    if (!Color.black.equals(new Color(color))) {
                        res.setRGB(j, i, color);
                        fl = false;
                        break;
                    }
                }
                if (fl) {
                    res.setRGB(j, i, images.get(0).getRGB(j, i));
                }
            }
        }
        return res;
    }
}
