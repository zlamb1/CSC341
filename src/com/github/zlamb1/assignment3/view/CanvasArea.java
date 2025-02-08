package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.IDrawableListener;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CanvasArea extends JPanel implements ICanvasArea {
    private final List<ICanvasDrawable> canvasDrawables = new ArrayList<>();
    private final List<IDrawableListener> drawableListeners = new ArrayList<>();
    private ICanvasDrawable ghostDrawable;

    public CanvasArea() {
        super();
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setBorder(BorderFactory.createTitledBorder("Canvas Area"));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        Insets insets = getInsets();

        int regionWidth = getWidth() - insets.left - insets.right;
        int regionHeight = getHeight() - insets.top - insets.bottom;

        // clip shapes past insets
        Shape clip = g2d.getClip();
        g2d.setClip(insets.left, insets.top, regionWidth, regionHeight);

        g2d.fillRect(insets.left, insets.top, regionWidth, regionHeight);
        g2d.setColor(Color.BLACK);

        for (ICanvasDrawable canvasDrawable : canvasDrawables) {
            canvasDrawable.draw(g);
        }

        if (ghostDrawable != null) {
            ghostDrawable.draw(g);
        }

        // restore clip
        g2d.setClip(clip);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void addDrawable(ICanvasDrawable canvasDrawable) {
        canvasDrawables.add(canvasDrawable);
        revalidate();
        repaint();

        for (IDrawableListener drawableListener : drawableListeners) {
            drawableListener.onAddDrawable(canvasDrawable);
        }
    }

    @Override
    public boolean removeDrawable(ICanvasDrawable canvasDrawable) {
        int index = canvasDrawables.indexOf(canvasDrawable), size = canvasDrawables.size();
        if (canvasDrawables.remove(canvasDrawable)) {
            revalidate();
            repaint();
            if (index == size - 1) {
                for (IDrawableListener drawableListener : drawableListeners) {
                    drawableListener.onRemoveDrawable(canvasDrawable);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public ICanvasDrawable getLastDrawable() {
        if (canvasDrawables.isEmpty()) {
            return null;
        }

        return canvasDrawables.getLast();
    }

    @Override
    public void addDrawableListener(IDrawableListener drawableListener) {
        drawableListeners.add(drawableListener);
    }

    @Override
    public boolean removeDrawableListener(IDrawableListener drawableListener) {
        return drawableListeners.remove(drawableListener);
    }

    @Override
    public void setGhostDrawable(ICanvasDrawable canvasDrawable) {
        ghostDrawable = canvasDrawable;
        revalidate();
        repaint();
    }

    @Override
    public void removeGhostDrawable() {
        ghostDrawable = null;
        revalidate();
        repaint();
    }

    @Override
    public void undo() {
        if (!canvasDrawables.isEmpty()) {
            canvasDrawables.removeLast();
            revalidate();
            repaint();
        }
    }

    @Override
    public void undoAll() {
        if (!canvasDrawables.isEmpty()) {
            canvasDrawables.clear();
            revalidate();
            repaint();
        }
    }
}
