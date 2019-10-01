package Problem_1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {

    private static Pane pane;
    private static VBox vbox;
    private static ArrayList<Label> labels = new ArrayList<>();
    public static ArrayList<TextField> textFields = new ArrayList<>();
    private static final double vgap = 15.0;
    private static final double hgap = 10.0;
    public static Button okBtn;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Enter the data");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        pane = new Pane();
        labels.add(new Label("Enter focus Distance"));
        labels.add(new Label("Enter photo censor height"));
        labels.add(new Label("Enter photo censor width"));
        labels.add(new Label("Enter height"));
        labels.add(new Label("Enter field height"));
        labels.add(new Label("Enter field width"));
        labels.add(new Label("Enter field latitude"));
        labels.add(new Label("Enter field longitude"));
        labels.add(new Label("Enter field diagonal"));

        vbox = new VBox(vgap);
        vbox.setPadding(new Insets(10 ,10 ,10 ,10));

        for (int i = 0; i < 9; i++) {
            TextField temp = new TextField();
            if (i == 6) temp.setText("50.467977");
            if (i == 7) temp.setText("31.211438");
            if (i == 8) temp.setText("0.01");
            temp.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
                try {
                    Float.valueOf(temp.getText() + keyEvent.getCharacter());
                }catch (Exception e){
                    keyEvent.consume();
                }
            });
            textFields.add(temp);

            HBox hBox = new HBox(hgap, labels.get(i), textFields.get(i));
            vbox.getChildren().add(hBox);
        }
        okBtn = new Button("Enter");
        vbox.getChildren().add(okBtn);
        pane.getChildren().add(vbox);
        primaryStage.setScene(new Scene(pane));

        okBtn.setOnAction(event -> {
               Controller.createMapWindow(
                        Float.valueOf(textFields.get(0).getText()),
                        Float.valueOf(textFields.get(1).getText()),
                        Float.valueOf(textFields.get(2).getText()),
                        Float.valueOf(textFields.get(3).getText()),
                        Float.valueOf(textFields.get(4).getText()),
                        Float.valueOf(textFields.get(5).getText()),
                        new MapView.LatLng(
                                Float.valueOf(textFields.get(6).getText()),
                                Float.valueOf(textFields.get(7).getText())
                                ),
                        //new MapView.LatLng(50.466977, 31.211438), 0.01
                       Float.valueOf(textFields.get(8).getText())
               );
               /*double[] photoBounds = Controller.calculate(
                       Float.valueOf(textFields.get(0).getText()),
                       Float.valueOf(textFields.get(1).getText()),
                       Float.valueOf(textFields.get(2).getText()),
                       Float.valueOf(textFields.get(3).getText())
               );
               Controller.printRoute(
                       Controller.calculateRoute(
                               photoBounds[0],
                               photoBounds[1],
                               Float.valueOf(textFields.get(4).getText()),
                               Float.valueOf(textFields.get(5).getText())
                       )
               );*/
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
