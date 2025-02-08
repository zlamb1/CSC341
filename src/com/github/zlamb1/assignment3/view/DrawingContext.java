package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;

import java.awt.*;

public class DrawingContext {
    private final ICanvasDrawableFactory drawableFactory;
    private final DrawMode drawMode;
    private final Point origin;
    private Point current;

    public DrawingContext(ICanvasDrawableFactory drawableFactory, DrawMode drawMode, Point origin) {
        this.drawableFactory = drawableFactory;
        this.drawMode = drawMode;
        this.origin = origin;
        this.current = origin;
    }

    public void drag(Point point) {
        current = point;
    }

    public ICanvasDrawable createDrawable() {
        Dimension dimensions = new Dimension(origin.x - current.x, origin.y - current.y);
        return drawableFactory
                .setDrawMode(drawMode)
                .setOrigin(origin)
                .setSize(dimensions)
                .buildDrawable();
    }
}
