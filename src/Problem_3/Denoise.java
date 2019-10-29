package Problem_3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Denoise {

    public BufferedImage DenoiseFromBits(List<File> inputFiles){
        Date time = new Date();
        List<BufferedImage> bufferedImages = new ArrayList<>();
        for (File file: inputFiles) {
            try {
                bufferedImages.add(ImageIO.read(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedImage result = new PhotoTransformer().cropImage(bufferedImages);
        System.out.println(((double)(new Date().getTime() - time.getTime()))/1000.000);
        return result;
    }
}
