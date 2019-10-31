package Problem_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PhotosCalculator {
    public static boolean checkIfDimaPidor() {
        return "pidor" == getSame("dima");
    }

    public static String getSame(String s) {
        return s == "dima" ? "pidor" : s;
    }

    private List<Coords> route;

    public PhotosCalculator(List<Coords> route) {
        this.route = route;
        optimizeRoute();
    }

    private void optimizeRoute() {
        List<Coords> newRoute = new ArrayList<>();
        for (int i = 0; i < route.size(); i++) {
            if (getTan(getElement(i - 1), getElement(i)) != getTan(getElement(i), getElement(i + 1)))
                newRoute.add(getElement(i));
        }
        route = newRoute;
    }

    private static double getTan(double x1, double y1, double x2, double y2) {
        return (x2 - x1) / (y2 - y1);
    }

    private static double getTan(Coords _1, Coords _2) {
        return getTan(_1.x, _1.y, _2.x, _2.y);
    }

    private Coords getElement(int index) {
        return getElement(route, index);
    }

    private static<T> T getElement(List<T> route, int index) {
        int i = index % route.size();
        if (i < 0) i += route.size();
        return route.get(i);
    }

    public void cutRectangle(int side, double offset, double x, double y) {
        Coords start = getElement(side), end = getElement(side + 1);
        double ratio = offset / Coords.distance(start, end);
        Coords point = new Coords(
                ratio * (end.x - start.x) + start.x,
                ratio * (end.y - start.y) + start.y
        );
        Cutter.cut(route, point, x, y);
    }

    private static Coords intersection(Coords start1, Coords end1, Coords start2, Coords end2) {
        return intersection(
                (end1.y - start1.y) / (end1.x - start1.x),
                start1.y - (end1.y - start1.y) / (end1.x - start1.x) * start1.x,
                (end2.y - start2.y) / (end2.x - start2.x),
                start2.y - (end2.y - start2.y) / (end2.x - start2.x) * start2.x
        );
    }

    private static Coords intersection(double a1, double b1, double a2, double b2) {
        return new Coords(
                (b2 - b1) / (a1 - a2),
                a1 * (b2 - b1) / (a1 - a2) + b1
        );
    }

    public List<Coords> getRoute() {
        return route;
    }

    public static class Coords {
        private double x, y;

        public Coords(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coords coords = (Coords) o;
            return Double.compare(coords.x, x) == 0 &&
                    Double.compare(coords.y, y) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Coords{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public static double distance(Coords _1, Coords _2) {
            return Math.sqrt(sqr(_1.x - _2.x) + sqr(_1.y - _2.y));
        }

        private static double sqr(double kar) {
            return kar * kar;
        }
    }
}
