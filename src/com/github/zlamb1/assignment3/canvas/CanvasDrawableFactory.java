package com.github.zlamb1.assignment3.canvas;

import com.github.zlamb1.assignment3.view.DrawMode;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

public class CanvasDrawableFactory implements ICanvasDrawableFactory {
    protected static int MIN_SIZE = 5;

    /**
     * The idea behind this is to make the factory extensible.
     * We could just have a switch statement, but we want others to be able to extend the factory based
     * on new modes or custom behaviors.
     */
    protected Map<DrawMode, ICanvasDrawableProducer<?>> drawableProducers = new HashMap<>();

    protected DrawMode drawMode;
    protected Point origin;
    protected Color color;
    protected int width, height;
    protected int strokeWidth;
    protected boolean filled;

    public CanvasDrawableFactory() {
        this.drawMode = DrawMode.TRIANGLE;
        this.origin = new Point(0, 0);
        this.color = Color.BLACK;
        this.width = MIN_SIZE;
        this.height = MIN_SIZE;
        this.strokeWidth = 1;
        this.filled = false;
    }

    @Override
    public ICanvasDrawableFactory setOrigin(Point origin) {
        this.origin = origin;
        return this;
    }

    @Override
    public ICanvasDrawableFactory setColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public ICanvasDrawableFactory setSize(int size) {
        this.width = size;
        this.height = size;
        return this;
    }

    @Override
    public ICanvasDrawableFactory setSize(Dimension dimension) {
        this.width = dimension.width;
        this.height = dimension.height;
        return this;
    }

    @Override
    public ICanvasDrawableFactory setDrawMode(DrawMode drawMode) {
        this.drawMode = drawMode;
        return this;
    }

    @Override
    public ICanvasDrawableFactory setFilled(boolean filled) {
        this.filled = filled;
        return this;
    }

    @Override
    public ICanvasDrawableFactory setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    @Override
    public Point getOrigin() {
        return origin;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Dimension getSize() {
        return new Dimension(width, height);
    }

    @Override
    public DrawMode getDrawMode() {
        return drawMode;
    }

    @Override
    public boolean isFilled() {
        return filled;
    }

    @Override
    public int getStrokeWidth() {
        return strokeWidth;
    }

    @Override
    public void addCanvasDrawableProducer(DrawMode drawMode, ICanvasDrawableProducer<?> producer) {
        drawableProducers.put(drawMode, producer);
    }

    @Override
    public ICanvasDrawableProducer<?> removeCanvasDrawableProducer(DrawMode drawMode) {
        return drawableProducers.remove(drawMode);
    }

    public ICanvasDrawable buildDrawable() {
        // custom behavior
        if (drawableProducers.containsKey(drawMode)) {
            ICanvasDrawableProducer<?> producer = drawableProducers.get(drawMode);
            if (producer != null) {
                return producer.buildDrawable(this);
            }
        }

        boolean filled = this.filled;
        int halfWidth = Math.abs(width) / 2;
        int halfHeight = Math.abs(height) / 2;
        int halfSize = Math.max(Math.max(halfWidth, halfHeight), MIN_SIZE);

        Shape shape = null;

        // default behaviors
        switch (drawMode) {
            case LINE: {
                Polygon polygon = new Polygon();
                polygon.addPoint(origin.x, origin.y);
                polygon.addPoint(origin.x - width, origin.y - height);
                // WARNING: lines don't render if filled
                filled = false;
                shape = polygon;
                break;
            }
            case TRIANGLE: {
                Polygon polygon = new Polygon();
                polygon.addPoint((int) origin.getX(), (int) (origin.getY() - halfSize));
                polygon.addPoint((int) (origin.getX() - halfSize), (int) (origin.getY() + halfSize));
                polygon.addPoint((int) (origin.getX() + halfSize), (int) (origin.getY() + halfSize));
                shape = polygon;
                break;
            }
            case RIGHT_TRIANGLE: {
                Polygon polygon = new Polygon();
                polygon.addPoint((int) origin.getX() - halfSize, (int) (origin.getY() - halfSize));
                polygon.addPoint((int) origin.getX() - halfSize, (int) (origin.getY() + halfSize));
                polygon.addPoint((int) origin.getX() + halfSize, (int) (origin.getY() + halfSize));
                shape = polygon;
                break;
            }
            case CIRCLE: {
                shape = buildEllipse(origin, halfSize, halfSize);
                break;
            }
            case ELLIPSE: {
                shape = buildEllipse(origin, halfWidth, halfHeight);
                break;
            }
            case SQUARE: {
                shape = buildRectangle(origin, halfSize, halfSize);
                break;
            }
            case RECTANGLE: {
                shape = buildRectangle(origin, halfWidth, halfHeight);
                break;
            }
        }

        if (shape != null) {
            return new CanvasShape(origin, shape, color, strokeWidth, filled);
        }

        if (drawMode.isArbitraryPolygon()) {
            return new CanvasShape(origin, buildArbitraryPolygon(origin, drawMode.getNSides(), halfSize), color, strokeWidth, filled);
        }

        return null;
    }

    @Override
    public ICanvasDrawableFactory clone() {
        try {
            ICanvasDrawableFactory drawableFactory = (ICanvasDrawableFactory) super.clone();
            // WARNING: ensure setOrigin so that no mutable references are preserved across clones
            return drawableFactory
                .setOrigin(new Point((int) origin.getX(), (int) origin.getY()))
                .setColor(color)
                .setSize(new Dimension(width, height))
                .setDrawMode(drawMode)
                .setFilled(filled);
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    protected Shape buildEllipse(Point origin, int halfWidth, int halfHeight) {
        return new Ellipse2D.Double(origin.x - halfWidth, origin.y - halfHeight, halfWidth * 2, halfHeight * 2);
    }

    protected Shape buildRectangle(Point origin, int halfWidth, int halfHeight) {
        Polygon polygon = new Polygon();
        polygon.addPoint((int) (origin.getX() - halfWidth), (int) (origin.getY() - halfHeight));
        polygon.addPoint((int) (origin.getX() + halfWidth), (int) (origin.getY() - halfHeight));
        polygon.addPoint((int) (origin.getX() + halfWidth), (int) (origin.getY() + halfHeight));
        polygon.addPoint((int) (origin.getX() - halfWidth), (int) (origin.getY() + halfHeight));
        return polygon;
    }

    protected Shape buildArbitraryPolygon(Point origin, int nSides, int radius) {
        assert nSides > 1;

        Path2D.Double polygon = new Path2D.Double();
        for (int i = 0; i < nSides; i++) {
            double angle = 2 * Math.PI * i / nSides;
            int x = (int) (origin.getX() + radius * Math.cos(angle));
            int y = (int) (origin.getY() + radius * Math.sin(angle));
            if (i == 0) {
                polygon.moveTo(x, y);
            } else {
                polygon.lineTo(x, y);
            }
        }

        polygon.closePath();
        return polygon;
    }
}
