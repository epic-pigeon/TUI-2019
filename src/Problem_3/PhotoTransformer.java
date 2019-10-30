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

    private BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    //FIXME: величины delta идут как пиксели , а не как реальные координаты
    private BufferedImage transformImageByVector(BufferedImage img, double deltaX, double deltaY) {
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = w + (int) Math.abs(deltaX);
        int newHeight = h + (int) Math.abs(deltaY);

        BufferedImage transformed = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = transformed.createGraphics();
        AffineTransform at = new AffineTransform();
//        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        at.translate(deltaX, deltaY);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return transformed;
    }

    public BufferedImage translateImage(BufferedImage img, ImagePosition position) {
        img = rotateImageByDegrees(img, position.getAngle());
        return transformImageByVector(img, position.getDeltaX(), position.getDeltaY());
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
        for (int i = 0; i < images.size(); i++) {
            images.set(i, translateImage(images.get(i), positions.get(i)));
        }
        Size maxSize = new Size();
        for (BufferedImage image : images) {
            maxSize.height = Math.max(maxSize.height, image.getHeight());
            maxSize.width = Math.max(maxSize.width, image.getWidth());
        }
        for (int i = 0; i < images.size(); i++) {
            images.set(i, adaptToSize(images.get(i), maxSize));
        }
        save("src/Problem_3/resources/tmp_result/", images);

        System.out.println("Image size: " + images.get(0).getHeight() + "x" + images.get(0).getWidth());
        return cropImageAfterTransform(images);
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
                Rect rect = new Rect(x, y, Math.min(w, width - 1 - x), Math.min(h, height - 1 - y));
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
                BufferedImage bitFinishImage = images.get(bestImgId).getSubimage(x, y, Math.min(w, width - 1 - x), Math.min(h, height - 1 - y));
                for (int i = 0; i < bitFinishImage.getHeight(); i++) {
                    for (int j = 0; j < bitFinishImage.getWidth(); j++) {
                        int color = bitFinishImage.getRGB(j, i);
                        if (Color.black.equals(new Color(color))) {
                            bitFinishImage.setRGB(j, i, result.getRGB(x + j , y + i));
                        }
                    }
                }
                g.drawImage(bitFinishImage, x, y, Math.min(w, width - 1 - x), Math.min(h, height - 1 - y), null);
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
