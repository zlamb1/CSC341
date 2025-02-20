package com.github.zlamb1.view.utility;

import java.awt.*;

public class DimensionUtility {
    public static Dimension roundToNearestPowerOfTwo(Dimension dimension) {
        return new Dimension(
            dimension.getWidth() == 0 ? 0 : (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros((int) (dimension.getWidth() - 1))),
            dimension.getHeight() == 0 ? 0 : (int) Math.pow(2, 32 - Integer.numberOfLeadingZeros((int) (dimension.getHeight() - 1)))
        );
    }
}
