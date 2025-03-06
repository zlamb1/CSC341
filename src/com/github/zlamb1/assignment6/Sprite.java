package com.github.zlamb1.assignment6;

import com.github.zlamb1.io.image.ImageStore;

import java.awt.*;

public class Sprite {
    protected ImageStore imageStore;
    protected Dimension size = new Dimension(50, 50);

    protected double rotation = 0;

    public Sprite(String path) {
        this(path, new Dimension(50, 50));
    }

    public Sprite(String path, Dimension size) {
        imageStore = new ImageStore(path);
        this.size = size;
        try {
            imageStore.getImage(size);
        } catch (Exception exc) {
            throw new SpriteLoadException("Failed to Load Sprite @: " + path, exc);
        }
    }

    public Sprite(ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension size) {
        this.size = size;
        // preload image size
        imageStore.getImage(size);
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
        Image image = imageStore.getImage(size).getImage();
        g2d.drawImage(image, centerX, centerY, null);
    }
}
