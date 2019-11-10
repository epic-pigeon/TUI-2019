package Problem_1.linkerninghan;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainCli {

    private static final int ITERATIONS_NUM = 100;

    public static void main(String[] args) {
        // Generate points that are located on each node of a grid of given width and height.
        // If either width or height is even, min distance to visit all points is [width * height].
        // If both width and height are odd, min distance is [width * height - 1 + sqrt(2)].
        ArrayList<Point> coordinates = generateGridPoints(5, 5);
        //ArrayList<Integer> ids = Stream.iterate(1, i -> i + 1).limit(coordinates.size()).collect(Collectors.toCollection(ArrayList::new));

        // Time tracking.
        long startTime = System.currentTimeMillis();

        LinKernighan result = runLinKernighan(coordinates);

        System.out.printf("The solution took: %dms\n", System.currentTimeMillis() - startTime);
        System.out.println("The solution is: ");
        System.out.println(result);
    }

    public static LinKernighan runLinKernighan(ArrayList<Point> coordinates) {
        LinKernighan bestResult = new LinKernighan(coordinates);
        for (int i = 0; i < ITERATIONS_NUM; i++) {
            LinKernighan lk = new LinKernighan(coordinates);
            lk.runAlgorithm();
            if (lk.getDistance() < bestResult.getDistance()) {
                bestResult.setTour(lk.getTour().clone());
            }
        }
        return bestResult;
    }

    public static ArrayList<Point> generateGridPoints(int height, int width) {
        ArrayList<Point> coords = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                coords.add(new Point(i, j));
            }
        }
        return coords;
    }
}

