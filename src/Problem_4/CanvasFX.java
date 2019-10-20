package Problem_4;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class CanvasFX extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(1800, 800);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        String imagePath = "/resources/birds.jpg";
        Image image = new Image(imagePath);
        // Draw the Image

        //drawRotatedImage(gc, image,  40,   0,   0);
   //     gc.drawImage(image, 10, 10, 200, 200);

        Rotate r = new Rotate(30, 220 + 50, 50 + 35);
       // r = new Rotate(0, 0, 0);
      //  gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
      //  gc.drawImage(image, 220, 50, 100, 70);

        for (int i = 0; i < 10; ++i) {
            r = new Rotate(i * 15, 50 + i*50, 35 + i*50);
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
            gc.drawImage(image, i*50, i*50, 100, 70);
        }
        ScrollPane root = new ScrollPane(canvas);
        root.setPrefWidth(1500);
        root.setPrefHeight(900);
       // Pane root = new Pane();
        /*root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");*/

      //  root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Kar");
        stage.show();
    }
}