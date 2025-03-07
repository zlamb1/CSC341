package com.github.zlamb1.assignment6;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Sprite {
    protected List<Image> frames = new ArrayList<>();
    protected Dimension size = new Dimension(50, 50);
    protected volatile int frame;

    protected double rotation = 0;

    protected volatile boolean animating = false;

    public Sprite(List<Image> frames) {
        this(frames, new Dimension(50, 50));
    }

    public Sprite(List<Image> frames, Dimension size) {
        this.size = size;
        this.frames = frames;

        Timer timer = new Timer(50, e -> {
            if (isAnimating() || frame != 0) {
                frame = (frame + 1) % frames.size();
            }
        });

        timer.setInitialDelay(0);
        timer.start();
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public void setSize(int width, int height) {
        setSize(new Dimension(width, height));
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public void drawSprite(final Graphics g, int x, int y) {
        int centerX = x - size.width / 2;
        int centerY = y - size.height / 2;

        Graphics2D g2d = (Graphics2D) g;
        Image image = frames.get(frame);
        g2d.drawImage(image, centerX, centerY, null);
    }

    public synchronized boolean isAnimating() {
        return animating;
    }

    public synchronized void setAnimating(boolean animating) {
        this.animating = animating;
    }
}
