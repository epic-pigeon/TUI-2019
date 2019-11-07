package Problem_1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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

    public PhotosCalculator(double w, double h) {
        this(Arrays.asList(
                new Coords(0, 0),
                new Coords(w, 0),
                new Coords(w, h),
                new Coords(0, h)
        ));
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
        return (y2 - y1) / (x2 - x1);
    }

    private static double getTan(Coords _1, Coords _2) {
        return getTan(_1.x, _1.y, _2.x, _2.y);
    }

    private static double getTan(Segment segment) {
        return getTan(segment.start, segment.end);
    }

    private Coords getElement(int index) {
        return getElement(route, index);
    }

    private static <T> T getElement(List<T> route, int index) {
        int i = index % route.size();
        if (i < 0) i += route.size();
        return route.get(i);
    }

    private static double getRatio(Segment segment, Coords coords) {
        assert segmentContains(segment, coords);
        if (segment.start.x == segment.end.x) {
            return (coords.y - segment.start.y) / (segment.end.y - segment.start.y);
        } else {
            return (coords.x - segment.start.x) / (segment.end.x - segment.start.x);
        }
    }

    public void cutRectangle(int sideId, double offset, double h, double w) {
        Coords start = getElement(sideId), end = getElement(sideId + 1);
        double ratio = offset / Coords.distance(start, end);
        Coords point = new Coords(
                ratio * (end.x - start.x) + start.x,
                ratio * (end.y - start.y) + start.y
        );
        Segment segment = new Segment(start, end);
        //Cutter.cut(route, point, x, y);
        double tan = getTan(segment);
        Coords point1 = new Segment(
                point,
                1 / tan,
                h
        ).getEnd();
        Coords point2 = new Segment(
                point1,
                tan,
                w
        ).getEnd();
        Coords point3 = new Segment(
                point,
                tan,
                w
        ).getEnd();
        Coords[] points = new Coords[]{
                point1, point2, point3
        };
        Segment side1 = new Segment(point, point1);
        Segment side2 = new Segment(point1, point2);
        Segment side3 = new Segment(point2, point3);
        Segment[] sides = new Segment[]{
                side1, side2, side3
        };
        List<Coords> intersections = intersections(route, sideId, Arrays.asList(sides));
        List<Coords> side1Intersections = new ArrayList<>(),
                side2Intersections = new ArrayList<>(),
                side3Intersections = new ArrayList<>();
        for (Coords in : intersections) {
            if (segmentContains(side1, in)) {
                side1Intersections.add(in);
            }
            if (segmentContains(side2, in)) {
                side2Intersections.add(in);
            }
            if (segmentContains(side3, in)) {
                side3Intersections.add(in);
            }
        }
        side1Intersections.sort(Comparator.comparingDouble(o -> getRatio(side1, o)));
        side2Intersections.sort(Comparator.comparingDouble(o -> getRatio(side2, o)));
        side3Intersections.sort(Comparator.comparingDouble(o -> getRatio(side3, o)));
        List<List<Coords>> sideIntersections = Arrays.asList(
                side1Intersections,
                side2Intersections,
                side3Intersections
        );
        //System.out.println(intersections);
        List<Coords> newRoute = new ArrayList<>();
        for (int i = 0; i < route.size(); i++) {
            newRoute.add(route.get(i));
            if (i == sideId) {
                newRoute.add(point);
                boolean lastOdd = false;
                for (int j = 0; j < 3; j++) {
                    List<Coords> currentIntersections = sideIntersections.get(j);
                    for (int k = 0; k < currentIntersections.size(); k++) {
                        if (k % 2 == (lastOdd ? 1 : 0)) {
                            newRoute.add(currentIntersections.get(k));
                        } else {
                            int prevSide = getContainingSide(newRoute.get(newRoute.size() - 1));
                            int currentSide = getContainingSide(currentIntersections.get(k));
                            for (int p = prevSide + 1; p != currentSide + 1; p = (p + 1) % route.size()) {
                                newRoute.add(getElement(p));
                            }
                            newRoute.add(currentIntersections.get(k));
                        }
                    }
                    if (currentIntersections.size() % 2 == (lastOdd ? 1 : 0)) {
                        newRoute.add(points[j]);
                    }
                    lastOdd = currentIntersections.size() % 2 != (lastOdd ? 1 : 0);
                }
                //newRoute.add(point3);
            }
        }
        route = newRoute;
        optimizeRoute();
    }

    private int getContainingSide(Coords coords) {
        for (int i = 0; i < route.size(); i++) {
            Segment segment = new Segment(
                    getElement(i),
                    getElement(i + 1)
            );
            if (segmentContains(segment, coords)) return i;
        }
        return -1;
    }

    private List<Coords> intersections(List<Coords> route, int ignoreSide, List<Segment> sides) {
        List<Coords> intersections = new ArrayList<>();
        for (int i = (ignoreSide + 1) % route.size(); i != ignoreSide; i = (i + 1) % route.size()) {
            Segment currentSide = new Segment(
                    getElement(i), getElement(i + 1)
            );
            for (Segment side : sides) {
                Coords intersection = intersection(currentSide, side);
                if (intersection != null) intersections.add(intersection);
            }
        }
        return intersections;
    }

    public static boolean segmentContains(Segment segment, Coords coords) {
        if (segment.start.x == segment.end.x) {
            return coords.x == segment.start.x && contains(segment.start.y, segment.end.y, coords.y);
        }
        if (segment.start.y == segment.end.y) {
            return coords.y == segment.start.y && contains(segment.start.x, segment.end.x, coords.x);
        }
        return contains(segment.start.x, segment.end.x, coords.x) && map(segment.start.x, segment.end.x, segment.start.y, segment.end.y, coords.x) == coords.y;
    }

    public static Coords intersection(Segment _1, Segment _2) {
        return intersection(_1.start, _1.end, _2.start, _2.end);
    }

    public static Coords intersection(Coords start1, Coords end1, Coords start2, Coords end2) {
        Coords result = __intersection(start1, end1, start2, end2);
        if (result == null) return null;
        if (segmentContains(new Segment(start1, end1), result) && segmentContains(new Segment(start2, end2), result)) {
            /*System.out.println("kar");
            System.out.println(start1);
            System.out.println(end1);
            System.out.println(start2);
            System.out.println(end2);
            System.out.println(result);*/
            return result;
        }
        return null;
    }

    public static Coords __intersection(Coords start1, Coords end1, Coords start2, Coords end2) {
        double a1 = (end1.y - start1.y) / (end1.x - start1.x),
                a2 = (end2.y - start2.y) / (end2.x - start2.x),
                b1 = start1.y - (end1.y - start1.y) / (end1.x - start1.x) * start1.x,
                b2 = start2.y - (end2.y - start2.y) / (end2.x - start2.x) * start2.x;
        if (Double.isInfinite(a1)) {
            double x = end1.x;
            if (contains(start2.x, end2.x, x)) {
                return new Coords(
                        x,
                        map(start2.x, end2.x, start2.y, end2.y, x)
                );
            } else return null;
        }
        if (Double.isInfinite(a2)) {
            double x = end2.x;
            if (contains(start1.x, end1.x, x)) {
                return new Coords(
                        x,
                        map(start1.x, end1.x, start1.y, end1.y, x)
                );
            } else return null;
        }
        if (a1 == a2) return null;
        return intersection(a1, b1, a2, b2);
    }

    private static double map(double startRange, double endRange, double startTo, double endTo, double num) {
        return startTo + (endTo - startTo) * ((num - startRange) / (endRange - startRange));
    }

    private static boolean contains(double start, double end, double a) {
        if (start > end) {
            double k = end;
            end = start;
            start = k;
        }
        return a >= start && a <= end;
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

    public static class Segment {
        private Coords start, end;

        public Segment(Coords start, Coords end) {
            this.start = start;
            this.end = end;
        }

        public Segment(Coords start, double k, double l) {
            this.start = start;
            if (Double.isInfinite(k)) {
                this.end = new Coords(
                        start.x,
                        start.y + (k == Double.POSITIVE_INFINITY ? 1 : -1) * l
                );
            } else {
                this.end = new Coords(
                        start.x + l / Math.sqrt(k * k + 1),
                        start.y + k * l / Math.sqrt(k * k + 1)
                );
            }
        }

        public Coords getStart() {
            return start;
        }

        public Coords getEnd() {
            return end;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Segment segment = (Segment) object;
            return Objects.equals(start, segment.start) &&
                    Objects.equals(end, segment.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return "Segment{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }
}
