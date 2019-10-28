package Problem_2.Algorithm;

import Problem_2.BlurService;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class PhotoCropper {

    private final int w = 30;
    private final int h = 30;

    public BufferedImage cropImage(List<BufferedImage> images){
        BufferedImage result = images.get(0);
        Graphics g = result.getGraphics();
        BlurService service = new BlurService();
        int width = images.get(0).getWidth(), height = images.get(0).getHeight();

        for (int y = 0 ; y < height; y += h) {
            for (int x = 0 ; x < width; x += w) {
                BufferedImage bitFinishImage = null;
                Double value = 0.0;
                for (BufferedImage image : images) {
                    BufferedImage candidate = null;
                    if (x + w >= width){
                        if (y + h >= height){
                            candidate =  image.getSubimage(x , y , width - 1 - x, height - 1 - y);
                        }else{
                            candidate =  image.getSubimage(x , y , width - 1 - x, h);
                        }
                    }else if (y + h >= height){
                        if (x + w >= width){
                            candidate =  image.getSubimage(x , y , width - 1 - x, height - 1 - y);
                        }else{
                            candidate =  image.getSubimage(x , y , w, height - 1 - y);
                        }
                    }else {
                        candidate = image.getSubimage(x, y, w, h);
                    }
                    Double temp = service.detectBlur(candidate);
                    if (temp > value){
                        value = temp;
                        bitFinishImage = candidate;
                    }
                }
                g.drawImage(bitFinishImage , x , y, w , h ,null);
            }
        }
        return result;
    }
}
