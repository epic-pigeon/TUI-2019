package Problem_1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class Tests {
    private Map<List<PhotosCalculator.Coords>, List<PhotosCalculator.Coords>> optimizationTests;

    @Before
    public void initOptimizationTest() {
        optimizationTests = new HashMap<>();
        optimizationTests.put(
                Arrays.asList(
                        new PhotosCalculator.Coords(0, 3),
                        new PhotosCalculator.Coords(2, 3),
                        new PhotosCalculator.Coords(1, 1),
                        new PhotosCalculator.Coords(4, 7)
                ),
                Arrays.asList(
                        new PhotosCalculator.Coords(0, 3),
                        new PhotosCalculator.Coords(2, 3),
                        new PhotosCalculator.Coords(4, 7)
                )
        );
        optimizationTests.put(
                Arrays.asList(
                        new PhotosCalculator.Coords(0, 3),
                        new PhotosCalculator.Coords(1, 1),
                        new PhotosCalculator.Coords(2, 3),
                        new PhotosCalculator.Coords(4, 7)
                ),
                Arrays.asList(
                        new PhotosCalculator.Coords(0, 3),
                        new PhotosCalculator.Coords(1, 1),
                        new PhotosCalculator.Coords(4, 7)
                )
        );
        optimizationTests.put(
                Arrays.asList(
                        new PhotosCalculator.Coords(0, 3),
                        new PhotosCalculator.Coords(1, 1),
                        new PhotosCalculator.Coords(2, 3),
                        new PhotosCalculator.Coords(4, 7),
                        new PhotosCalculator.Coords(5, 9)
                ),
                Arrays.asList(
                        new PhotosCalculator.Coords(0, 3),
                        new PhotosCalculator.Coords(1, 1),
                        new PhotosCalculator.Coords(5, 9)
                )
        );
    }

    @Test
    public void optimizationTest() {
        for (Map.Entry<List<PhotosCalculator.Coords>, List<PhotosCalculator.Coords>> entry: optimizationTests.entrySet()) {
            assertListEquals(entry.getValue(), new PhotosCalculator(entry.getKey()).getRoute());
            //System.out.println(entry.getKey());
            //System.out.println(new PhotosCalculator(entry.getKey()).getRoute());
        }
    }

    @Test
    public void dimaIsPidor() {
        Assert.assertTrue(PhotosCalculator.checkIfDimaPidor());
    }

    private<T> void assertListEquals(List<T> _1, List<T> _2) {
        Assert.assertEquals(_1.size(), _2.size());
        for (int i = 0; i < _1.size(); i++) Assert.assertEquals(_1.get(i), _2.get(i));
    }
}
