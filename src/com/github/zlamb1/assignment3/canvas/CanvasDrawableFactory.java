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
    protected boolean filled;

    public CanvasDrawableFactory() {
        this.drawMode = DrawMode.TRIANGLE;
        this.origin = new Point(0, 0);
        this.color = Color.BLACK;
        this.width = MIN_SIZE;
        this.height = MIN_SIZE;
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

        int halfWidth = Math.abs(width) / 2;
        int halfHeight = Math.abs(height) / 2;
        int halfSize = Math.max(Math.max(halfWidth, halfHeight), MIN_SIZE);

        // default behaviors
        switch (drawMode) {
            case LINE: {
                Polygon polygon = new Polygon();
                polygon.addPoint(origin.x, origin.y);
                polygon.addPoint(origin.x - width, origin.y - height);
                // WARNING: lines don't render if filled
                return new CanvasShape(origin, polygon, color, false);
            }
            case TRIANGLE: {
                Polygon polygon = new Polygon();
                polygon.addPoint((int) origin.getX(), (int) (origin.getY() - halfSize));
                polygon.addPoint((int) (origin.getX() - halfSize), (int) (origin.getY() + halfSize));
                polygon.addPoint((int) (origin.getX() + halfSize), (int) (origin.getY() + halfSize));
                return new CanvasShape(origin, polygon, color, filled);
            }
            case CIRCLE: {
                return new CanvasShape(origin, buildEllipse(origin, halfSize, halfSize), color, filled);
            }
            case ELLIPSE: {
                return new CanvasShape(origin, buildEllipse(origin, halfWidth, halfHeight), color, filled);
            }
            case SQUARE: {
                return new CanvasShape(origin, buildRectangle(origin, halfSize, halfSize), color, filled);
            }
            case RECTANGLE: {
                return new CanvasShape(origin, buildRectangle(origin, halfWidth, halfHeight), color, filled);
            }
        }

        if (drawMode.isArbitraryPolygon()) {
            return new CanvasShape(origin, buildArbitraryPolygon(origin, drawMode.getNSides(), halfSize), color, filled);
        }

        return null;
    }

    @Override
    public ICanvasDrawable buildDrawable(ICanvasDrawableFactory drawableFactory) {
        return null;
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
