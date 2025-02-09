package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.IDrawableListener;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CanvasArea extends JPanel implements ICanvasArea {
    protected final List<ICanvasDrawable> canvasDrawables = new ArrayList<>();
    protected final List<IDrawableListener> drawableListeners = new ArrayList<>();
    protected ICanvasDrawable ghostDrawable;
    protected Image canvasImage;

    protected final JScrollPane canvasScrollPane;
    protected final JPanel canvas;
    protected Dimension canvasSize;

    public CanvasArea() {
        super();

        setBorder(BorderFactory.createTitledBorder("Canvas Area"));
        setLayout(new GridBagLayout());

        canvas = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.WHITE);
                drawCanvas(g2d);
                if (ghostDrawable != null) {
                    ghostDrawable.draw(g);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return getCanvasSize();
            }
        };

        canvas.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        canvasScrollPane = new JScrollPane(canvas);
        canvasScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        add(canvasScrollPane, gbc);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
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

    @Override
    public void clear() {
        canvasDrawables.clear();
        ghostDrawable = null;
        canvasImage = null;
        setCanvasSize(null);
        revalidate();
        repaint();
    }

    @Override
    public JComponent getCanvas() {
        return canvas;
    }

    @Override
    public Dimension getCanvasSize() {
        return canvasSize != null ? canvasSize : getInnerSize();
    }

    @Override
    public void setCanvasSize(Dimension canvasSize) {
        this.canvasSize = canvasSize;
        canvas.revalidate();
        canvas.repaint();
    }

    @Override
    public void setCanvasImage(Image image) {
        clear();

        setCanvasSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        canvasImage = image;

        revalidate();
        repaint();
    }

    @Override
    public void drawCanvas(Graphics g) {
        Dimension canvasSize = getCanvasSize();
        g.setColor(Color.WHITE);

        if (canvasImage != null) {
            g.drawImage(canvasImage, 0, 0, (int) canvasSize.getWidth(), (int) canvasSize.getHeight(), null);
        } else {
            g.fillRect(0, 0, (int) canvasSize.getWidth(), (int) canvasSize.getHeight());
        }

        for (ICanvasDrawable canvasDrawable : canvasDrawables) {
            canvasDrawable.draw(g);
        }
    }

    protected Dimension getInnerSize() {
        return new Dimension(
            getWidth() - getInsets().left - getInsets().right - 2,
            getHeight() - getInsets().top - getInsets().bottom - 2
        );
    }
}
