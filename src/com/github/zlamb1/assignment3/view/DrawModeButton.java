package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawModeButton extends JButton {
    private final ICanvasDrawableFactory drawableFactory;
    private final DrawMode drawMode;

    private static final int ICON_SIZE = 25;

    public DrawModeButton(DrawMode drawMode, ICanvasDrawableFactory drawableFactory) {
        super(drawMode.name().substring(0, 1).toUpperCase() + drawMode.name().substring(1).toLowerCase());

        this.drawableFactory = drawableFactory;
        this.drawMode = drawMode;

        setIcon(generateIcon(ICON_SIZE, ICON_SIZE));
    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        if (drawMode != null) {
            // regenerate icon with correct foreground
            setIcon(generateIcon(ICON_SIZE, ICON_SIZE));
        }
    }

    protected ImageIcon generateIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        int halfWidth = width / 2;
        int quarterWidth = width / 4;
        int halfHeight = height / 2;
        int quarterHeight = height / 4;

        Point origin = drawMode == DrawMode.LINE ?
            new Point(quarterWidth, quarterHeight) : new Point(halfWidth, halfHeight);

        Dimension size = switch (drawMode) {
            case LINE -> new Dimension(-2 * quarterWidth, -2 * quarterHeight);
            case ELLIPSE, RECTANGLE -> new Dimension(halfWidth + (int) (quarterWidth / 1.5), halfHeight);
            default -> new Dimension(halfWidth, halfHeight);
        };

        ICanvasDrawableFactory factoryInstance = drawableFactory.clone();

        factoryInstance
            .setDrawMode(drawMode)
            .setOrigin(origin)
            .setColor(getForeground())
            .setSize(size)
            .setFilled(false);

        factoryInstance.buildDrawable().draw(g2d);

        g2d.dispose();

        return new ImageIcon(image);
    }
}
