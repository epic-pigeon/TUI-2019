package Problem_3;

import Problem_2.BlurService;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PhotoTransformer {

    private BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
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
        deltaX = -deltaX;
        deltaY = -deltaY;
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = w - (int) Math.abs(deltaX);
        int newHeight = h - (int) Math.abs(deltaY);

        BufferedImage transformed = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transformed.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        at.translate(deltaX, deltaY);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return transformed;
    }

    public BufferedImage translateImage(BufferedImage img, double angle, double deltaX, double deltaY) {
        img = rotateImageByDegrees(img, angle);
        return transformImageByVector(img, deltaX, deltaY);
    }

    private final int w = 30;
    private final int h = 30;

    public BufferedImage cropImage(List<BufferedImage> images) {
        double angle, deltaX, deltaY;
        Scanner scanner = new Scanner(System.in);
        System.out.println(images.get(0).getHeight());
        for (int i = 0; i < images.size(); i++) {
            angle = scanner.nextDouble();
            deltaX = scanner.nextDouble();
            deltaY = scanner.nextDouble();
            images.set(i, translateImage(images.get(i), angle, deltaX, deltaY));
        }

        System.out.println(images.get(0).getHeight());

        BufferedImage result = new BufferedImage(2000 , 2000 , BufferedImage.TYPE_INT_ARGB);
        Graphics g = result.getGraphics();
        BlurService service = new BlurService();
        int width = images.get(0).getWidth(), height = images.get(0).getHeight();
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
                g.drawImage(bitFinishImage, x, y, Math.min(w, width - 1 - x), Math.min(h, height - 1 - y), null);
            }
        }
        return result;
    }
}
