package Problem_2.Algorithm;

import Problem_2.BlurService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class PhotoCropper {

    private final int w = 30;
    private final int h = 30;

    public BufferedImage cropImage(List<BufferedImage> images) {
        BufferedImage result = images.get(0);
        Graphics g = result.getGraphics();
        BlurService service = new BlurService();
        int width = images.get(0).getWidth(), height = images.get(0).getHeight();

        for (int y = 0; y < height; y += h) {
            for (int x = 0; x < width; x += w) {
                BufferedImage bitFinishImage = null;
                Double value = 0.0;
                for (BufferedImage image : images) {
                    BufferedImage candidate = image.getSubimage(x, y, Math.min(w, width - 1 - x), Math.min(h, height - 1 - y));
                    Double temp = service.detectBlur(candidate);
                    if (temp > value) {
                        value = temp;
                        bitFinishImage = candidate;
                    }
                }
                g.drawImage(bitFinishImage, x, y, Math.min(w, width - 1 - x), Math.min(h, height - 1 - y), null);
            }
        }
        return result;
    }
}