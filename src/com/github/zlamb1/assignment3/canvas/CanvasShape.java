package com.github.zlamb1.assignment3.canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class CanvasShape implements ICanvasDrawable {
    protected Point origin;
    protected Shape shape;
    protected Color color;
    protected int strokeWidth;
    protected boolean filled;

    public CanvasShape(Point origin, Shape shape, Color color, int strokeWidth, boolean filled) {
        this.origin = origin;
        this.shape = shape;
        this.color = color;
        this.strokeWidth = strokeWidth;
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
    public void moveTo(Point point) {
        Point translate = new Point(point.x - origin.x, point.y - origin.y);
        shape = AffineTransform.getTranslateInstance(translate.x, translate.y).createTransformedShape(shape);
        origin = point;
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
