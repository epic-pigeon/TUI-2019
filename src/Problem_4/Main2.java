package Problem_4;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main2 {
    public static void main(String[] args) {
        try {
            byte[] arr = Files.readAllBytes(Paths.get(new File("src/Problem_4/resources/file.tlog").toURI()));
            for (byte i : arr) {
                System.out.println((char) (i & 0xFF));
            }
            /*FileInputStream fstream = new FileInputStream("hs_err_pid10516.log");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null) {

                System.out.println(strLine);
            }
            fstream.close();*/
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
