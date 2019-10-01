package Problem_1;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    //1, 2, 2, 1, 10.1, 14.1
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public  static void createMapWindow(
        double focusDistance,
        double photoCensorHeight,
        double photoCensorWidth,
        double height,
        double fieldHeight,
        double fieldWidth,
        MapView.LatLng southWest,
        double diameter
    ) {
        double[] a = calculate(focusDistance, photoCensorHeight, photoCensorWidth, height);
        invokeMapWindow(
                calculateRoute(a[0], a[1], fieldHeight, fieldWidth), fieldHeight, fieldWidth, southWest, diameter
        );
    }


    public static double[] calculate(double focusDistance, double photoCensorHeight, double photoCensorWidth, double height) {
        double photoCensorDiameter = Math.sqrt(photoCensorHeight * photoCensorHeight + photoCensorWidth * photoCensorWidth);
        double groundDiameter = photoCensorDiameter * height / focusDistance;
        double widthHeightRatio = photoCensorWidth / photoCensorHeight;
        double groundHeight = Math.sqrt(groundDiameter * groundDiameter / (widthHeightRatio * widthHeightRatio + 1));
        double groundWidth = Math.sqrt(groundDiameter * groundDiameter / (1 / (widthHeightRatio * widthHeightRatio) + 1));
        return new double[] {groundHeight, groundWidth};
    }

    public static double[][] calculateRoute(double photoHeight, double photoWidth, double fieldHeight, double fieldWidth) {
        RouteCalculator routeCalculator = new RouteCalculator(photoHeight, photoWidth, fieldHeight, fieldWidth);
        int h = routeCalculator.getHeight();
        int w = routeCalculator.getWidth();

        boolean invert = false;

        if (h % 2 == 0 || w % 2 == 0) {
            if (w % 2 != 0 && h % 2 == 0) {
                routeCalculator = new RouteCalculator(photoWidth, photoHeight, fieldWidth, fieldHeight);
                h = routeCalculator.getHeight();
                w = routeCalculator.getWidth();
                invert = true;
            }

            for (int i = 0; i < w - 1; i++) routeCalculator.moveRight();
            if (h == 1) {
                routeCalculator.end();
            } else {
                routeCalculator.moveUp();
                for (int i = 0; i < w / 2; i++) {
                    if (i != 0) routeCalculator.moveLeft();
                    for (int j = 0; j < h - 2; j++) {
                        routeCalculator.moveUp();
                    }
                    routeCalculator.moveLeft();
                    for (int j = 0; j < h - 2; j++) {
                        if (i == w / 2 - 1) {
                            routeCalculator.moveDown(true);
                        } else {
                            routeCalculator.moveDown();
                        }
                    }
                }
                routeCalculator.end();
            }
        } else {
            for (int i = 0; i < w - 1; i++) {
                routeCalculator.moveRight();
            }
            if (h == 1) {
                routeCalculator.end();
            } else {
                routeCalculator.moveUp();
                for (int i = 0; i < (w - 3) / 2; i++) {
                    if (i != 0) routeCalculator.moveLeft();
                    for (int j = 0; j < h - 2; j++) {
                        routeCalculator.moveUp();
                    }
                    routeCalculator.moveLeft();
                    for (int j = 0; j < h - 2; j++) {
                        routeCalculator.moveDown();
                    }
                }
                routeCalculator.moveLeft();
                for (int j = 0; j < h - 2; j++) {
                    routeCalculator.moveUp();
                }
                routeCalculator.moveLeft();
                for (int i = 0; i < (h - 1) / 2; i++) {
                    if (i != 0) routeCalculator.moveDown();
                    routeCalculator.moveLeft();
                    routeCalculator.moveDown();
                    routeCalculator.moveRight();
                }
                routeCalculator.end();
            }
        }

        return invert ? invertCoords(routeCalculator.getRoute()) : routeCalculator.getRoute();
    }

    private static double[][] invertCoords(double[][] route) {
        double[][] result = new double[route.length][];
        for (int i = 0; i < route.length; i++) {
            result[i] = new double[] {
                    route[i][1],
                    route[i][0]
            };
        }
        return result;
    }

    public static void printRoute(double[][] route) {
        for (double[] coords: route) {
            System.out.println(coords[0] + " " + coords[1]);
        }
    }

    private static class RouteCalculator {
        private double photoHeight, photoWidth, fieldHeight, fieldWidth;
        private ArrayList<double[]> route = new ArrayList<>();
        private double x, y;

        public RouteCalculator(double photoHeight, double photoWidth, double fieldHeight, double fieldWidth) {
            this.photoHeight = photoHeight;
            this.photoWidth = photoWidth;
            this.fieldHeight = fieldHeight;
            this.fieldWidth = fieldWidth;
            x = y = 0;
            checkBoundsAndSave(null);
        }

        private void saveCurrentCoords() {
            route.add(new double[] {x, y});
        }

        private void checkBounds(Direction direction) {
            double[][] bounds = getBounds();
            double x1 = x, y1 = y;
            y = Math.max(
                    Math.min(y, bounds[1][0]),
                    bounds[0][0]
            );
            x = Math.max(
                    Math.min(x, bounds[1][1]),
                    bounds[0][1]
            );

            if (direction != null && x1 == x & y1 == y)
                for (double[] coords: route) {
                    if (
                            ((coords[0] - photoWidth / 2) < x && x < (coords[0] + photoWidth / 2)) &&
                            ((coords[1] - photoHeight / 2) < y && y < (coords[1] + photoHeight / 2))
                    ) {
                        if (direction == Direction.UP) {
                            y = coords[1] + photoHeight / 2;
                        } else if (direction == Direction.DOWN) {
                            y = coords[1] - photoHeight / 2;
                        } else if (direction == Direction.RIGHT) {
                            x = coords[0] + photoWidth / 2;
                        } else if (direction == Direction.LEFT) {
                            x = coords[0] - photoWidth / 2;
                        } // throw new RuntimeException("ty pidor");
                    }
                }
        }

        private void checkBoundsAndSave(Direction direction) {
            /*double[][] bounds = getBounds();
            route.add(new double[] {
                    Math.max(
                            Math.min(x, bounds[1][1]),
                            bounds[0][1]
                    ),
                    Math.max(
                            Math.min(y, bounds[1][0]),
                            bounds[0][0]
                    )
            });*/
            checkBounds(direction);
            saveCurrentCoords();
        }

        private double[][] getBounds() {
            return new double[][] {
                    new double[] {
                            photoHeight / 2, photoWidth / 2
                    },
                    new double[] {
                            fieldHeight - photoHeight / 2, fieldWidth - photoWidth / 2
                    },
            };
        }

        public void moveRight() {
            moveRight(false);
        }

        public void moveRight(boolean ignoreRoute) {
            x += photoWidth;
            checkBoundsAndSave(ignoreRoute ? null : Direction.LEFT);
        }

        public void moveLeft() {
            moveLeft(false);
        }

        public void moveLeft(boolean ignoreRoute) {
            x -= photoWidth;
            checkBoundsAndSave(ignoreRoute ? null : Direction.RIGHT);
        }

        public void moveUp() {
            moveUp(false);
        }

        public void moveUp(boolean ignoreRoute) {
            y += photoHeight;
            checkBoundsAndSave(ignoreRoute ? null : Direction.DOWN);
        }

        public void moveDown() {
            moveDown(false);
        }

        public void moveDown(boolean ignoreRoute) {
            y -= photoHeight;
            checkBoundsAndSave(ignoreRoute ? null : Direction.UP);
        }

        public double[][] getRoute() {
            double[][] result = new double[route.size()][];
            for (int i = 0; i < route.size(); i++) result[i] = route.get(i);
            return result;
        }

        public double getPhotoHeight() {
            return photoHeight;
        }

        public double getPhotoWidth() {
            return photoWidth;
        }

        public double getFieldHeight() {
            return fieldHeight;
        }

        public double getFieldWidth() {
            return fieldWidth;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public int getWidth() {
            return (int) Math.ceil(fieldWidth / photoWidth);
        }

        public int getHeight() {
            return (int) Math.ceil(fieldHeight / photoHeight);
        }

        public void end() {
            x = 0;
            y = 0;
            checkBoundsAndSave(null);
        }

        private enum Direction {
            LEFT, RIGHT, UP, DOWN
        }
    }

    private static void invokeMapWindow(
            double[][] route, double height, double width, MapView.LatLng southWest, double diameter
    ) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Controller.class.getResource("mapwindow.fxml")
            );
            Stage stage = new Stage();
            stage.setTitle("Map");
            Scene scene = new Scene(loader.load(), 800, 600);
            stage.setScene(scene);
            MapWindowController controller = loader.getController();
            stage.show();

            controller.getMapView().init(
                    southWest,
                    new MapView.LatLngBounds(
                            southWest, //new MapView.LatLng(50.466977, 31.211438),
                            new MapView.LatLng(
                                    southWest.getLatitude() + Math.sqrt(diameter * diameter * width * width / (width * width + height * height)),
                                    southWest.getLongitude() + Math.sqrt(diameter * diameter * height * height / (height * height + width * width))
                            )
                    ),
                    15
            );

            controller.getMapView().onload(() -> {
                controller.getMapView().setLineWidth(5);
                controller.getMapView().strokeRect(
                        new MapView.Coords(0, 0),
                        new MapView.Bounds(1, 1)
                );

                MapView.Coords prevCoords = null;

                for (int i = 0; i < route.length; i++) {
                    double[] coords = route[i];
                    double[] unitCoords = new double[] {
                            coords[0] / width, 1 - coords[1] / height
                    };
                    MapView.Coords realCoords = new MapView.Coords(
                            unitCoords[0],
                            unitCoords[1]
                    );


                    if (i == 0) {
                        controller.getMapView().beginPath();
                        controller.getMapView().moveTo(realCoords);
                        controller.getMapView().arcPathPixel(
                                controller.getMapView().unitCoordsToPixels(realCoords),
                                10, 0,2 * Math.PI);
                        controller.getMapView().closePath();
                        controller.getMapView().fillPath();

                        controller.getMapView().setFillPattern(
                                new MapView.ColoredPattern(
                                        new Color(
                                                255, 0, 0
                                        )
                                )
                        );
                    } else {
                        controller.getMapView().beginPath();
                        controller.getMapView().moveTo(realCoords);
                        controller.getMapView().arcPathPixel(
                                controller.getMapView().unitCoordsToPixels(realCoords),
                                10, 0,2 * Math.PI);
                        controller.getMapView().closePath();
                        controller.getMapView().fillPath();

                        double deltaX = (realCoords.getX() - prevCoords.getX());
                        double deltaY = (realCoords.getY() - prevCoords.getY());

                        double alpha;

                        /*if (deltaY == 0) {
                            alpha = deltaX > 0 ? 0 : Math.PI;
                        } else {
                            alpha = Math.atan(
                                deltaY / deltaX
                            );
                        }*/

                        alpha = Math.atan(
                                deltaY / deltaX
                        );

                        if (alpha < 0) alpha = Math.PI + alpha;

                        if (deltaX == 0) {
                            alpha = deltaY > 0 ? Math.PI / 2 : -Math.PI / 2;
                        } else if (deltaY == 0) {
                            alpha = deltaX > 0 ? 0 : Math.PI;
                        }

                        double arrowLength = Math.sqrt(
                                deltaX * deltaX + deltaY * deltaY
                        ) / 5;
                        double arrowAngle = Math.PI / 6;


                        MapView.Coords arrowCoords1 = new MapView.Coords(
                                realCoords.getX() - Math.cos(alpha - arrowAngle) * arrowLength,
                                realCoords.getY() - Math.sin(alpha - arrowAngle) * arrowLength
                        );
                        MapView.Coords arrowCoords2 = new MapView.Coords(
                                realCoords.getX() - Math.cos(alpha + arrowAngle) * arrowLength,
                                realCoords.getY() - Math.sin(alpha + arrowAngle) * arrowLength
                        );

                        controller.getMapView().beginPath();
                        controller.getMapView().moveTo(prevCoords);
                        controller.getMapView().lineTo(realCoords);
                        controller.getMapView().lineTo(arrowCoords1);
                        controller.getMapView().moveTo(realCoords);
                        controller.getMapView().lineTo(arrowCoords2);
                        controller.getMapView().strokePath();
                    }

                    /*
                    if (i == 0) {
                        controller.getMapView().beginPath();
                        controller.getMapView().moveTo(realCoords);
                        controller.getMapView().arcPathPixel(
                                controller.getMapView().unitCoordsToPixels(realCoords),
                                10, 0,2 * Math.PI);
                        controller.getMapView().closePath();
                        controller.getMapView().fillPath();

                        controller.getMapView().setFillPattern(
                                new MapView.ColoredPattern(
                                        new Color(
                                                255, 0, 0
                                        )
                                )
                        );

                        controller.getMapView().beginPath();
                        controller.getMapView().moveTo(realCoords);
                    } else {
                        controller.getMapView().lineTo(realCoords);
                        controller.getMapView().strokePath();

                        if (i != route.length - 1) {
                            controller.getMapView().beginPath();
                            controller.getMapView().moveTo(realCoords);
                            controller.getMapView().arcPathPixel(
                                    controller.getMapView().unitCoordsToPixels(realCoords),
                                    10, 0, 2 * Math.PI);
                            controller.getMapView().closePath();
                            controller.getMapView().fillPath();
                        }

                        controller.getMapView().beginPath();
                        controller.getMapView().moveTo(realCoords);
                    }
                    */

                    prevCoords = realCoords;
                }
                controller.getMapView().closePath();
                controller.getMapView().strokePath();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
