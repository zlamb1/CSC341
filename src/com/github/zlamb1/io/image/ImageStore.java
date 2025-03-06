package com.github.zlamb1.io.image;

import com.github.zlamb1.assignment2.view.CardLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageStore implements IImageStore {
    protected String path;
    protected Dimension2D scaleInstance = new Dimension(1, 1);

    protected ImageIcon image;
    protected Map<Dimension, ImageIcon> imageStore = new HashMap<>();

    public ImageStore(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Dimension2D getScaleInstance() {
        return scaleInstance;
    }

    public void setScaleInstance(Dimension2D scaleInstance) {
        boolean changedScaleInstance = scaleInstance.getWidth() != this.scaleInstance.getWidth()
            || scaleInstance.getHeight() != this.scaleInstance.getHeight();
        this.scaleInstance = scaleInstance;
    }

    protected ImageIcon getScaledImageIcon(int width, int height) {
        return new ImageIcon(image.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
    }

    public ImageIcon getImage(Dimension size) {
        if (image == null) {
            URL imageUrl = CardLabel.class.getClassLoader().getResource(path);
            assert imageUrl != null;

            image = new ImageIcon(imageUrl);
            imageStore.put(new Dimension(image.getIconWidth(), image.getIconHeight()), image);

            ImageIcon scaledImageIcon = getScaledImageIcon(size.width, size.height);
            imageStore.put(size, scaledImageIcon);

            return scaledImageIcon;
        }

        if (imageStore.containsKey(size)) {
            return imageStore.get(size);
        } else {
            ImageIcon scaledImageIcon = getScaledImageIcon(size.width, size.height);
            imageStore.put(size, scaledImageIcon);
            return scaledImageIcon;
        }
    }
}
