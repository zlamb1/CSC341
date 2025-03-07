package com.github.zlamb1.io.gif;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GIFFrame {
    protected BufferedImage image;
    protected int delay;

    protected boolean hasTransparency;
    protected boolean requiresUserInput;

    public GIFFrame(BufferedImage image, int delay, boolean hasTransparency, boolean requiresUserInput) {
        this.image = image;
        this.delay = delay;
        this.hasTransparency = hasTransparency;
        this.requiresUserInput = requiresUserInput;
    }

    public Image getImage() {
        return image;
    }

    public int getDelay() {
        return delay;
    }

    public boolean hasTransparency() {
        return hasTransparency;
    }

    public boolean requiresUserInput() {
        return requiresUserInput;
    }
}
