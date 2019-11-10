package Problem_1;

import Problem_4.Collection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.internal.org.objectweb.asm.tree.LabelNode;

import java.io.File;
import java.util.ArrayList;

public class Main extends Application {

    private static Pane pane;
    private static VBox vbox;
    private static ArrayList<Label> labels = new ArrayList<>();
    public static ArrayList<TextField> textFields = new ArrayList<>();
    private static final double vgap = 15.0;
    private static final double hgap = 10.0;
    public static Button okBtn, saveBtn, loadBtn;
    public static FileChooser.ExtensionFilter drsFilter = new FileChooser.ExtensionFilter(
            "Dronio Route Save files",
            "*.drs"
    );

    private static abstract class Field {
        private String name;
        private String defaultValue;

        public abstract boolean verifyInput(String input);

        public String getName() {
            return name;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public Field(String name, String defaultValue) {
            this.name = name;
            this.defaultValue = defaultValue;
        }
    }

    private static abstract class NumberField extends Field {
        public NumberField(String name, Double defaultValue) {
            super(name, defaultValue == null ? null : String.valueOf(defaultValue));
        }

        @Override
        public boolean verifyInput(String input) {
            try {
                return verifyNumber(Double.parseDouble(input));
            } catch (Exception e) {
                return false;
            }
        }

        protected abstract boolean verifyNumber(double num);
    }

    private final static Collection<Field> fields = new Collection<>(
            new NumberField("Focus distance(m): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num && num <= 5;
                }
            },
            new NumberField("Photo censor height(m): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num && num <= 5;
                }
            },
            new NumberField("Photo censor width(m): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num && num <= 5;
                }
            },
            new NumberField("Drone height(m): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num && num <= 500;
                }
            },
            new NumberField("Field height(m): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num && num <= 10000;
                }
            },
            new NumberField("Field width(m): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num && num <= 10000;
                }
            },
            new NumberField("Field latitude(m): ", 50.467977) {
                @Override
                protected boolean verifyNumber(double num) {
                    return -180 < num && num <= 180;
                }
            },
            new NumberField("Field longitude(m): ", 31.211438) {
                @Override
                protected boolean verifyNumber(double num) {
                    return -180 < num && num <= 180;
                }
            },
            new NumberField("Field diagonal(km): ", 0.01) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num && num <= 15000;
                }
            },
            new NumberField("Charge per photo(u): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num;
                }
            },
            new NumberField("Charge per meter(u): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 < num;
                }
            },
            new NumberField("Max total charge(u): ", null) {
                @Override
                protected boolean verifyNumber(double num) {
                    return 0 <= num;
                }
            }
    );

    public static void showAlert(String message) {
        Dialog<Void> alert = new Dialog<>();
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Enter the data");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        pane = new Pane();
        /*labels.add(new Label("Enter focus Distance"));
        labels.add(new Label("Enter photo censor height"));
        labels.add(new Label("Enter photo censor width"));
        labels.add(new Label("Enter height"));
        labels.add(new Label("Enter field height"));
        labels.add(new Label("Enter field width"));
        labels.add(new Label("Enter field latitude"));
        labels.add(new Label("Enter field longitude"));
        labels.add(new Label("Enter field diagonal"));
        labels.add(new Label("Charge per photo"));
        labels.add(new Label("Charge per meter"));
        labels.add(new Label("Max energy"));*/

        vbox = new VBox(vgap);
        vbox.setPadding(new Insets(10, 10, 10, 10));


        for (Field field : fields) {
            Label label = new Label(field.getName());
            labels.add(label);
            TextField temp = new TextField();
            if (field.getDefaultValue() != null)
                temp.setText(field.getDefaultValue());
            temp.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
                if (!field.verifyInput(temp.getText() + keyEvent.getCharacter())) keyEvent.consume();
            });
            textFields.add(temp);
            HBox hBox = new HBox(hgap, label, temp);
            vbox.getChildren().add(hBox);
        }

        /*for (int i = 0; i < 12; i++) {
            TextField temp = new TextField();
            if (i == 6) temp.setText("50.467977");
            if (i == 7) temp.setText("31.211438");
            if (i == 8) temp.setText("0.01");
            temp.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
                try {
                    Float.valueOf(temp.getText() + keyEvent.getCharacter());
                } catch (Exception e) {
                    keyEvent.consume();
                }
            });
            textFields.add(temp);

            HBox hBox = new HBox(hgap, labels.get(i), textFields.get(i));
            vbox.getChildren().add(hBox);
        }*/
        okBtn = new Button("Enter");
        loadBtn = new Button("Load");
        saveBtn = new Button("Save");
        vbox.getChildren().add(okBtn);
        vbox.getChildren().add(loadBtn);
        vbox.getChildren().add(saveBtn);
        pane.getChildren().add(vbox);
        primaryStage.setScene(new Scene(pane));

        okBtn.setOnAction(event -> {
            if (Float.valueOf(textFields.get(9).getText()) > Float.valueOf(textFields.get(11).getText())) {
                showAlert("Cannot take a single photo");
            } else {
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
                        Float.valueOf(textFields.get(8).getText()),
                        Float.valueOf(textFields.get(9).getText()),
                        Float.valueOf(textFields.get(10).getText()),
                        Float.valueOf(textFields.get(11).getText())
                );
            }
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
        saveBtn.setOnAction(event -> {
            if (Float.valueOf(textFields.get(9).getText()) > Float.valueOf(textFields.get(11).getText())) {
                showAlert("Cannot take a single photo");
            } else {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Choose a file to save to");
                chooser.getExtensionFilters().add(drsFilter);
                File file = chooser.showSaveDialog(primaryStage);
                double[] a = Controller.calculate(
                        Float.valueOf(textFields.get(0).getText()),
                        Float.valueOf(textFields.get(1).getText()),
                        Float.valueOf(textFields.get(2).getText()),
                        Float.valueOf(textFields.get(3).getText())
                );
                System.out.println(file);
                Controller.save(file,
                        new Controller.Data(
                                Controller.calculateRoute(
                                        a[0], a[1],
                                        Float.valueOf(textFields.get(4).getText()),
                                        Float.valueOf(textFields.get(5).getText()),
                                        Float.valueOf(textFields.get(9).getText()),
                                        Float.valueOf(textFields.get(10).getText()),
                                        Float.valueOf(textFields.get(11).getText())
                                ),
                                Float.valueOf(textFields.get(4).getText()),
                                Float.valueOf(textFields.get(5).getText()),
                                Float.valueOf(textFields.get(8).getText()),
                                new MapView.LatLng(
                                        Float.valueOf(textFields.get(6).getText()),
                                        Float.valueOf(textFields.get(7).getText())
                                )
                        )
                );
            }
        });
        loadBtn.setOnAction(event -> {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Choose a file to load");
                chooser.getExtensionFilters().add(drsFilter);
                File file = chooser.showOpenDialog(primaryStage);
                Controller.Data data = Controller.read(file);
                Controller.invokeMapWindow(
                        data.getRoute(),
                        data.getFieldHeight(),
                        data.getFieldWidth(),
                        data.getSouthWest(),
                        data.getDiameter()
                );
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
