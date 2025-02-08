package com.github.zlamb1.assignment3.canvas;

import com.github.zlamb1.assignment3.view.DrawMode;

import java.awt.*;

public interface ICanvasDrawableFactory {
    ICanvasDrawableFactory setOrigin(Point origin);
    ICanvasDrawableFactory setColor(Color color);
    ICanvasDrawableFactory setSize(int size);
    ICanvasDrawableFactory setSize(Dimension dimension);
    ICanvasDrawableFactory setDrawMode(DrawMode drawMode);
    ICanvasDrawableFactory setFilled(boolean filled);

    Point getOrigin();
    Color getColor();
    Dimension getSize();
    DrawMode getDrawMode();
    boolean isFilled();

    void addCanvasDrawableProducer(DrawMode drawMode, ICanvasDrawableProducer<?> producer);
    ICanvasDrawableProducer<?> removeCanvasDrawableProducer(DrawMode drawMode);

    ICanvasDrawable buildDrawable();
    ICanvasDrawable buildDrawable(ICanvasDrawableFactory drawableFactory);
}
