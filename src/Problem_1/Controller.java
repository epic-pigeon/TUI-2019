package Problem_1;

import Problem_1.linkerninghan.LinKernighan;
import Problem_1.linkerninghan.Point;
import Problem_4.Collection;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Controller implements Initializable {
    public static final int ITERATIONS = 100;

    //1, 2, 2, 1, 10.1, 14.1
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public static void createMapWindow(
        double focusDistance,
        double photoCensorHeight,
        double photoCensorWidth,
        double height,
        double fieldHeight,
        double fieldWidth,
        MapView.LatLng southWest,
        double diameter,
        double chargePerPhoto,
        double chargePerMeter,
        double possibleCharge
    ) {
        double[] a = calculate(focusDistance, photoCensorHeight, photoCensorWidth, height);
        invokeMapWindow(
                calculateRoute(a[0], a[1], fieldHeight, fieldWidth, chargePerMeter, chargePerPhoto, possibleCharge), fieldHeight, fieldWidth, southWest, diameter
        );

    }

    public static class Data {
        private double[][] route;
        private double fieldHeight, fieldWidth, diameter;
        private MapView.LatLng southWest;

        public Data(double[][] route, double fieldHeight, double fieldWidth, double diameter, MapView.LatLng southWest) {
            this.route = route;
            this.fieldHeight = fieldHeight;
            this.fieldWidth = fieldWidth;
            this.diameter = diameter;
            this.southWest = southWest;
        }

        public double[][] getRoute() {
            return route;
        }

        public double getFieldHeight() {
            return fieldHeight;
        }

        public double getFieldWidth() {
            return fieldWidth;
        }

        public double getDiameter() {
            return diameter;
        }

        public MapView.LatLng getSouthWest() {
            return southWest;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "route=" + Arrays.deepToString(route) +
                    ", fieldHeight=" + fieldHeight +
                    ", fieldWidth=" + fieldWidth +
                    ", diameter=" + diameter +
                    ", southWest=" + southWest +
                    '}';
        }
    }

    public static double calculateCharge(double[][] route, double chargePerPhoto, double chargePerMeter) {
        double[] prev = route[0];
        double result = 0;
        for (int i = 1; i < route.length; i++) {
            result += chargePerPhoto;
            result += chargePerMeter * Math.sqrt(
                    sqr(route[i][1] - prev[1]) +
                    sqr(route[i][0] - prev[0])
            );
            prev = route[i];
        }
        return result;
    }

    private static double sqr(double q) {
        return q * q;
    }

    public static void save(File file, Data data) {
        try {
            double[][] coords = data.getRoute();
            byte[] toWrite = new byte[coords.length * 16 + 40];
            for (int i = 0; i < coords.length; i++) {
                for (int j = 0; j < 2; j++) {
                    for (int k = 0; k < 8; k++) {
                        toWrite[i * 16 + j * 8 + k] = (byte) ((byte) (Double.doubleToLongBits(coords[i][j]) >>> k*8) & 0xFF);
                    }
                }
            }
            for (int i = 0; i < 8; i++) {
                toWrite[coords.length * 16 + i] = (byte) ((byte) (Double.doubleToLongBits(data.getFieldHeight()) >>> i*8) & 0xFF);
            }
            for (int i = 0; i < 8; i++) {
                toWrite[coords.length * 16 + 8 + i] = (byte) ((byte) (Double.doubleToLongBits(data.getFieldWidth()) >>> i*8) & 0xFF);
            }
            for (int i = 0; i < 8; i++) {
                toWrite[coords.length * 16 + 16 + i] = (byte) ((byte) (Double.doubleToLongBits(data.getDiameter()) >>> i*8) & 0xFF);
            }
            for (int i = 0; i < 8; i++) {
                toWrite[coords.length * 16 + 24 + i] = (byte) ((byte) (Double.doubleToLongBits(data.getSouthWest().getLatitude()) >>> i*8) & 0xFF);
            }
            for (int i = 0; i < 8; i++) {
                toWrite[coords.length * 16 + 32 + i] = (byte) ((byte) (Double.doubleToLongBits(data.getSouthWest().getLongitude()) >>> i*8) & 0xFF);
            }
            Files.write(file.toPath(), toWrite);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Data read(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            double[][] coords = new double[(bytes.length - 40) / 16][];
            for (int i = 0; i < (bytes.length - 40) / 16; i++) {
                double[] point = new double[2];
                for (int j = 0; j < 2; j++) {
                    long num = 0;
                    for (int k = 0; k < 8; k++) {
                        byte aByte = bytes[i * 16 + j * 8 + k];
                        num |= ((long) aByte & 0xFF) << k*8;
                    }
                    point[j] = Double.longBitsToDouble(num);
                }
                coords[i] = point;
            }
            long fieldHeight = 0;
            for (int i = 0; i < 8; i++) {
                byte aByte = bytes[bytes.length - 40 + i];
                fieldHeight |= ((long) aByte & 0xFF) << i*8;
            }
            long fieldWidth = 0;
            for (int i = 0; i < 8; i++) {
                byte aByte = bytes[bytes.length - 32 + i];
                fieldWidth |= ((long) aByte & 0xFF) << i*8;
            }
            long diameter = 0;
            for (int i = 0; i < 8; i++) {
                byte aByte = bytes[bytes.length - 24 + i];
                diameter |= ((long) aByte & 0xFF) << i*8;
            }
            long latitude = 0;
            for (int i = 0; i < 8; i++) {
                byte aByte = bytes[bytes.length - 16 + i];
                latitude |= ((long) aByte & 0xFF) << i*8;
            }
            long longitude = 0;
            for (int i = 0; i < 8; i++) {
                byte aByte = bytes[bytes.length - 8 + i];
                longitude |= ((long) aByte & 0xFF) << i*8;
            }
            return new Data(
                    coords,
                    Double.longBitsToDouble(fieldHeight),
                    Double.longBitsToDouble(fieldWidth),
                    Double.longBitsToDouble(diameter),
                    new MapView.LatLng(
                            Double.longBitsToDouble(latitude),
                            Double.longBitsToDouble(longitude)
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static double[] calculate(double focusDistance, double photoCensorHeight, double photoCensorWidth, double height) {
        double photoCensorDiameter = Math.sqrt(photoCensorHeight * photoCensorHeight + photoCensorWidth * photoCensorWidth);
        double groundDiameter = photoCensorDiameter * height / focusDistance;
        double widthHeightRatio = photoCensorWidth / photoCensorHeight;
        double groundHeight = Math.sqrt(groundDiameter * groundDiameter / (widthHeightRatio * widthHeightRatio + 1));
        double groundWidth = Math.sqrt(groundDiameter * groundDiameter / (1 / (widthHeightRatio * widthHeightRatio) + 1));
        return new double[] {groundHeight, groundWidth};
    }

    /*public static double[][] calculateRoute(double photoHeight, double photoWidth, double fieldHeight, double fieldWidth, double chargePerMeter, double chargePerPhoto, double possibleCharge) {
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


        double[][] result =  invert ? invertCoords(routeCalculator.getRoute()) : routeCalculator.getRoute();
        if (calculateCharge(result, chargePerPhoto, chargePerMeter) <= possibleCharge) {
            return result;
        } else {
            if (!invert ? h > w : w > h) {
                fieldHeight -= photoHeight;
            } else {
                fieldWidth -= photoWidth;
            }
            return calculateRoute(photoHeight, photoWidth, fieldHeight, fieldWidth, chargePerMeter, chargePerPhoto, possibleCharge);
        }
    }*/

    public static double[][] calculateRoute(double photoHeight, double photoWidth, double fieldHeight, double fieldWidth, double chargePerMeter, double chargePerPhoto, double possibleCharge) {
        Collection<Point> points = new Collection<>(calculatePhotoCoords(photoHeight, photoWidth, fieldHeight, fieldWidth, chargePerMeter, chargePerPhoto, possibleCharge))
                .map(arr -> new Point(arr[0], arr[1]));
        int maxLength = (int) (possibleCharge / chargePerPhoto);
        if (points.size() > maxLength) {
            points = new Collection<>(points.subList(0, maxLength));
        }
        LinKernighan bestResult = new LinKernighan(points);
        for (int i = 0; i < ITERATIONS; i++) {
            LinKernighan lk = new LinKernighan(points);
            lk.runAlgorithm();
            if (lk.getDistance() < bestResult.getDistance()) {
                bestResult.tour = lk.tour.clone();
            }
        }
        double[][] result = new double[points.size()][];
        for (int i = 0; i < points.size(); i++) {
            result[i] = new double[]{
                    points.get(bestResult.tour[i]).getX(),
                    points.get(bestResult.tour[i]).getY()
            };
        }
        return result;
    }

    public static double[][] calculatePhotoCoords(double photoHeight, double photoWidth, double fieldHeight, double fieldWidth, double chargePerMeter, double chargePerPhoto, double possibleCharge) {
        Collection<double[]> result = new Collection<>();
        Collection<double[]> rows = new Collection<>(
                new double[] { 0, fieldWidth, 0 }
        );
        //System.out.println(rows.toString(double[].class));
        while ((rows = rows.filter(val -> val[2] < fieldHeight)).size() > 0) {
            //System.out.println(rows.toString(double[].class));
            double[] toFill =
                    rows.qsort(Comparator.comparingDouble(o1 -> o1[2])).get(0)
                    //rows.get(0)
                    ;
            //System.out.println(Arrays.toString(toFill));
            int index = rows.indexOf(toFill);
            int[] splitRes = splitRow(toFill[1], photoHeight, photoWidth);
            //System.out.println(Arrays.toString(splitRes));
            rows = rows.splice(index, index + 1).insert(new Collection<>(
                    splitRes[0] > 0 ? new double[] { toFill[0], splitRes[0] * photoHeight, toFill[2] + photoWidth } : null,
                    splitRes[1] > 0 ? new double[] { toFill[0] + splitRes[0] * photoHeight, toFill[1] - splitRes[0] * photoHeight, toFill[2] + photoHeight } : null
            ).filter(Objects::nonNull), index);
            //System.out.println(Arrays.toString(splitRes));
            //System.out.println(rows.splice(index, index + 1));
            for (int i = 0; i < splitRes[0]; i++) {
                result.add(
                        new double[] {
                                toFill[0] + (i + 0.5) * photoHeight,
                                toFill[2] + photoWidth * 1/2,
                                0
                        }
                );
            }
            for (int i = 0; i < splitRes[1]; i++) {
                result.add(
                        new double[] {
                                toFill[0] + splitRes[0] * photoHeight + (i + 0.5) * photoWidth,
                                toFill[2] + photoHeight * 1/2,
                                1
                        }
                );
            }
            //System.out.println(rows.toString(double[].class));
        }
        //System.out.println(result);
        return result.array(double[].class);
    }

    private static int[] splitRow(double length, double __l1, double __l2) {
        double l1 = Math.max(__l1, __l2);
        double l2 = Math.min(__l1, __l2);
        int[] minSum = null;
        //System.out.println(length);
        //System.out.println(l1);
        //System.out.println(l2);
        for (int i = 0; (i - 1) * l1 < length; i++) {
            double l1Length = i * l1;
            if (l1Length >= length) {
                if ((minSum == null || i < (minSum[0] + minSum[1]))) {
                    minSum = new int[] {i, 0};
                }
            } else {
                int l2Count = (int) Math.ceil((length - l1Length) / l2);
                if ((minSum == null || (i + l2Count) < (minSum[0] + minSum[1]))) {
                    minSum = new int[] {i, l2Count};
                }
            }
            //System.out.println(i);
            //System.out.println(lost);
        }
        return l1 == __l1 ? minSum : (minSum != null ? new int[] { minSum[1], minSum[0] } : null);
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

    public static void invokeMapWindow(
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

            controller.getMapView().setPostUpdate(false);

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
                        if (i != route.length - 1) {
                            controller.getMapView().beginPath();
                            controller.getMapView().moveTo(realCoords);
                            controller.getMapView().arcPathPixel(
                                    controller.getMapView().unitCoordsToPixels(realCoords),
                                    10, 0, 2 * Math.PI);
                            controller.getMapView().closePath();
                            controller.getMapView().fillPath();
                        }

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
                controller.getMapView().update();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
