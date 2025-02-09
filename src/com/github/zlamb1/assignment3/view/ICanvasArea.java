package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.IDrawableListener;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;
import com.github.zlamb1.view.swing.ISwingComponent;

import javax.swing.*;
import java.awt.*;

public interface ICanvasArea extends ISwingComponent {
    void addDrawable(ICanvasDrawable canvasDrawable);
    boolean removeDrawable(ICanvasDrawable canvasDrawable);
    ICanvasDrawable getLastDrawable();
    void addDrawableListener(IDrawableListener drawableListener);
    boolean removeDrawableListener(IDrawableListener drawableListener);

    void setGhostDrawable(ICanvasDrawable canvasDrawable);
    void removeGhostDrawable();
    void undo();
    void undoAll();
    void clear();

    JComponent getCanvas();
    Dimension getCanvasSize();
    void setCanvasSize(Dimension canvasSize);
    void setCanvasImage(Image image);

    void drawCanvas(final Graphics g);
    void invalidateCanvas();
}
