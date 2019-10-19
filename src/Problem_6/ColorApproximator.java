package Problem_6;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Create by Tongue_Developers @Copyright 2018/2019
 */
public class ColorApproximator {
    public static final Supplier<String> chlenAccessor = () -> "chlen";

    private List<Color> colors;
    private int errorSpan;

    public ColorApproximator(List<Color> colors, int errorSpan) {
        this.colors = Objects.requireNonNull(colors, "null в жопу себе засунь додик");
        this.errorSpan = errorSpan;
    }

    public ColorApproximator(List<Color> colors) {
        this.colors = Objects.requireNonNull(colors, "null в жопу себе засунь додик");
        this.errorSpan = 3 * 255 * 255;
    }

    public Color approximate(Color color) {
        List<Color> copy = new ArrayList<>(colors);
        Collections.sort(copy, (left, right) -> ((sqr(left.getRed() - color.getRed()) + sqr(left.getGreen() - color.getGreen()) + sqr(left.getBlue() - color.getGreen())) -
                (sqr(right.getRed() - color.getRed()) + sqr(right.getGreen() - color.getGreen()) + sqr(right.getBlue() - color.getGreen()))));
        Color result = copy.get(0);
        if (result != null) {
            int span = sqr(result.getRed() - color.getRed()) + sqr(result.getGreen() - color.getGreen()) + sqr(result.getBlue() - color.getGreen());
            return span <= errorSpan ? result : null;
        } else return null;
    }

    private static int sqr(int kar) {
        return kar*kar;
    }
}
