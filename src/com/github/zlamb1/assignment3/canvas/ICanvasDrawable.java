package com.github.zlamb1.assignment3.canvas;

import java.awt.*;

public interface ICanvasDrawable {
    Color getColor();
    Point getOrigin();

    void moveTo(Point point);
    void draw(final Graphics graphics);
}
