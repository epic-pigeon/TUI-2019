package Problem_2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class photorez {
    private BufferedImage kar;

    public photorez(BufferedImage kar) {
        this.kar = kar;
    }

    public void res(){
        kar.getSubimage(0 , 0 , 100 , 100);
    }
}
