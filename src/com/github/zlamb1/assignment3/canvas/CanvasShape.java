package com.github.zlamb1.assignment3.canvas;

import java.awt.*;

public class CanvasShape implements ICanvasDrawable {
    protected Point origin;
    protected Shape shape;
    protected Color color;
    protected int strokeWidth = 1;
    protected boolean filled;

    public CanvasShape(Point origin, Shape shape, Color color, boolean filled) {
        this.origin = origin;
        this.shape = shape;
        this.color = color;
        this.filled = filled;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Point getOrigin() {
        return origin;
    }

    @Override
    public void draw(final Graphics graphics) {
        final Graphics2D g2d = (Graphics2D) graphics;
        g2d.setColor(color);

        if (filled) {
            g2d.fill(shape);
        } else {
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.draw(shape);
        }
    }
}
