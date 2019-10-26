package Problem_4;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main3 {
    public static void main(String[] args) throws IOException, InterruptedException {
        List<TLogParser.TLogPoint> list = TLogParser.parseTLog(new File("./src/Problem_4/file.tlog"));
        for (TLogParser.TLogPoint kar: list) {
            System.out.println(kar.getLongitude() + " " + kar.getLatitude() + " " + kar.getImgId());
        }
    }
}
