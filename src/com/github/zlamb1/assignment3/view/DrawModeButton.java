package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DrawModeButton extends JButton {
    private final ICanvasDrawableFactory drawableFactory;
    private final DrawMode drawMode;

    private static final int ICON_SIZE = 25;

    protected static String getButtonText(DrawMode drawMode) {
        String modeName = drawMode.name();

        StringBuilder sb = new StringBuilder();

        String[] parts = modeName.split("[ \\-_]+");
        for (int i = 0; i < parts.length; i++) {
            String s = parts[i];
            sb.append(s.substring(0, 1).toUpperCase())
              .append(s.substring(1).toLowerCase());
            if (i != parts.length - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    public DrawModeButton(DrawMode drawMode, ICanvasDrawableFactory drawableFactory) {
        super(DrawModeButton.getButtonText(drawMode));

        this.drawableFactory = drawableFactory;
        this.drawMode = drawMode;

        setIcon();
    }

    @Override
    public void setForeground(Color color) {
        super.setForeground(color);
        // regenerate icon when foreground color changes
        setIcon();
    }

    protected void setIcon() {
        if (drawMode != null) {
            try {
                super.setIcon(generateIcon(ICON_SIZE, ICON_SIZE));
            } catch (UnsupportedOperationException e) {
                // factory does not support draw mode
                setVisible(false);
            }
        }
    }

    protected ImageIcon generateIcon(int width, int height) throws UnsupportedOperationException {
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

        ICanvasDrawable canvasDrawable = factoryInstance.buildDrawable();

        if (canvasDrawable == null) {
            throw new UnsupportedOperationException();
        }

        canvasDrawable.draw(g2d);
        g2d.dispose();

        return new ImageIcon(image);
    }
}
