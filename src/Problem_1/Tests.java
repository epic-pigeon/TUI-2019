package Problem_1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opencv.photo.Photo;

import javax.management.relation.RoleUnresolvedList;
import java.util.*;

public class Tests {
    private Map<List<PhotosCalculator.Coords>, List<PhotosCalculator.Coords>> optimizationTests;
    private Map<PhotosCalculator.Coords[], PhotosCalculator.Coords> intersectionTests;
    private Map<CutTest, List<PhotosCalculator.Coords>> cutTests;

    private static class CutTest {
        private List<PhotosCalculator.Coords> initialRoute;
        private double offset, w, h;
        private int sideId;

        public CutTest(List<PhotosCalculator.Coords> initialRoute, double offset, double w, double h, int sideId) {
            this.initialRoute = initialRoute;
            this.offset = offset;
            this.w = w;
            this.h = h;
            this.sideId = sideId;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            CutTest cutTest = (CutTest) object;
            return Double.compare(cutTest.offset, offset) == 0 &&
                    Double.compare(cutTest.w, w) == 0 &&
                    Double.compare(cutTest.h, h) == 0 &&
                    sideId == cutTest.sideId &&
                    initialRoute.equals(cutTest.initialRoute);
        }

        @Override
        public int hashCode() {
            return Objects.hash(initialRoute, offset, w, h, sideId);
        }

        @Override
        public String toString() {
            return "CutTest{" +
                    "initialRoute=" + initialRoute +
                    ", offset=" + offset +
                    ", w=" + w +
                    ", h=" + h +
                    ", sideId=" + sideId +
                    '}';
        }
    }

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

        intersectionTests = new HashMap<>();
        intersectionTests.put(
                new PhotosCalculator.Coords[] {
                        new PhotosCalculator.Coords(-1, -1),
                        new PhotosCalculator.Coords(1, 1),
                        new PhotosCalculator.Coords(1, -1),
                        new PhotosCalculator.Coords(-1, 1)
                },
                new PhotosCalculator.Coords(0, 0)
        );
        intersectionTests.put(
                new PhotosCalculator.Coords[] {
                        new PhotosCalculator.Coords(3, 4),
                        new PhotosCalculator.Coords(4, 4),
                        new PhotosCalculator.Coords(3, 3),
                        new PhotosCalculator.Coords(0, 0)
                },
                null
        );
        intersectionTests.put(
                new PhotosCalculator.Coords[] {
                        new PhotosCalculator.Coords(0, 1),
                        new PhotosCalculator.Coords(9, 1),
                        new PhotosCalculator.Coords(1, 80),
                        new PhotosCalculator.Coords(1, -10)
                },
                new PhotosCalculator.Coords(1, 1)
        );
        intersectionTests.put(
                new PhotosCalculator.Coords[] {
                        new PhotosCalculator.Coords(0, 1),
                        new PhotosCalculator.Coords(9, 1),
                        new PhotosCalculator.Coords(2, 1),
                        new PhotosCalculator.Coords(5, 1)
                },
                null
        );
        intersectionTests.put(
                new PhotosCalculator.Coords[] {
                        new PhotosCalculator.Coords(0, 0),
                        new PhotosCalculator.Coords(1, 1),
                        new PhotosCalculator.Coords(1, 0),
                        new PhotosCalculator.Coords(2, 1)
                },
                null
        );
        cutTests = new HashMap<>();
        cutTests.put(
                new CutTest(
                        Arrays.asList(
                                new PhotosCalculator.Coords(0, 0),
                                new PhotosCalculator.Coords(4, 0),
                                new PhotosCalculator.Coords(4, 4),
                                new PhotosCalculator.Coords(3, 4),
                                new PhotosCalculator.Coords(0, 1)
                        ),
                        1,
                        2,
                        3,
                        0
                ),
                Arrays.asList(
                        new PhotosCalculator.Coords(0, 0),
                        new PhotosCalculator.Coords(1, 0),
                        new PhotosCalculator.Coords(1, 2),
                        new PhotosCalculator.Coords(2, 3),
                        new PhotosCalculator.Coords(3, 3),
                        new PhotosCalculator.Coords(3, 0),
                        new PhotosCalculator.Coords(4, 0),
                        new PhotosCalculator.Coords(4, 4),
                        new PhotosCalculator.Coords(3, 4),
                        new PhotosCalculator.Coords(0, 1)
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
    public void intersectionTest() {
        for (Map.Entry<PhotosCalculator.Coords[], PhotosCalculator.Coords> entry: intersectionTests.entrySet()) {
            Assert.assertEquals(
                    entry.getValue(),
                    PhotosCalculator.intersection(entry.getKey()[0], entry.getKey()[1], entry.getKey()[2], entry.getKey()[3])
            );
            //System.out.println(entry.getKey());
            //System.out.println(new PhotosCalculator(entry.getKey()).getRoute());
        }
    }

    @Test
    public void cutTest() {
        for (Map.Entry<CutTest, List<PhotosCalculator.Coords>> entry: cutTests.entrySet()) {
            CutTest cutTest = entry.getKey();
            PhotosCalculator calculator = new PhotosCalculator(cutTest.initialRoute);
            calculator.cutRectangle(cutTest.sideId, cutTest.offset, cutTest.h, cutTest.w);
            assertListEquals(
                    entry.getValue(),
                    calculator.getRoute()
            );
        }
    }

    @Test
    public void dimaIsPidor() {
        Assert.assertTrue(PhotosCalculator.checkIfDimaPidor());
    }

    private static <T> void assertListEquals(List<T> _1, List<T> _2) {
        Assert.assertEquals(_1, _2);
    }
}
