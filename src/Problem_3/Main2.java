package Problem_3;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main2 extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane rootMain = new Pane();
        rootMain.setPrefSize(500 , 500);

        BufferedImage startImg = ImageIO.read(new File("src/Problem_3/birds.jpg"));
        PhotoTransformer photoTransformer = new PhotoTransformer();
        startImg = photoTransformer.translateImage(startImg , 30, -700, 400);
        ImageView imageView = new ImageView(SwingFXUtils.toFXImage(startImg, null));
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setTranslateX(200);
        imageView.setTranslateY(200);

        rootMain.getChildren().addAll(imageView);


        Scene scene = new Scene(rootMain);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Problem_4");
        primaryStage.show();
    }
}
