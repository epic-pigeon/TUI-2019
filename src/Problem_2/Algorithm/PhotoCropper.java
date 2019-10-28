package Problem_2.Algorithm;

import Problem_2.BlurService;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PhotoCropper {

    private final int w = 30;
    private final int h = 30;

    public BufferedImage cropImage(List<BufferedImage> images) {
        BufferedImage result = images.get(0);
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