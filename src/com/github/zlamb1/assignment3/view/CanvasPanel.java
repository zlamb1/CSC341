package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.IDrawModeListener;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CanvasPanel extends JPanel {
    private final ICanvasDrawableFactory shapeFactory;
    private final IToolbar toolbar;
    private final ICanvasArea canvasArea;
    private final IDrawModeListener drawModeListener;
    private final MouseAdapter canvasMouseListener;

    private DrawMode drawMode = DrawMode.TRIANGLE;
    private boolean isDrawing = false;
    private DrawingContext drawingContext;

    public CanvasPanel(ICanvasDrawableFactory shapeFactory, IToolbar toolbar, ICanvasArea canvasArea, Container bottom) {
        super();

        this.shapeFactory = shapeFactory;
        this.toolbar = toolbar;
        this.canvasArea = canvasArea;

        drawModeListener = (drawMode) -> {
            this.drawMode = drawMode;
            this.toolbar.setDrawMode(drawMode);
        };

        canvasMouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                if (!isDrawing && evt.getButton() == MouseEvent.BUTTON1) {
                    setDrawing(true, evt);
                }
            }
            @Override
            public void mouseReleased(MouseEvent evt) {
                if (evt.getButton() == MouseEvent.BUTTON1 && isDrawing) {
                    setDrawing(false, evt);
                }
            }
            @Override
            public void mouseDragged(MouseEvent evt) {
                if (isDrawing && drawingContext != null) {
                    setDrawing(true, evt);
                }
            }
        };

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.NORTH;

        add(toolbar.getComponent(), gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(canvasArea.getComponent(), gbc);

        gbc.gridy++;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(bottom, gbc);
    }

    protected void setDrawing(boolean isDrawing, MouseEvent evt) {
        if (this.isDrawing && isDrawing) {
            drawingContext.drag(evt.getPoint());
            canvasArea.setGhostDrawable(drawingContext.createDrawable());
        } else if (this.isDrawing) {
            canvasArea.addDrawable(drawingContext.createDrawable());
            drawingContext = null;
            canvasArea.removeGhostDrawable();
        } else if (isDrawing) {
            drawingContext = new DrawingContext(shapeFactory, drawMode, evt.getPoint());
            canvasArea.setGhostDrawable(drawingContext.createDrawable());
        }

        this.isDrawing = isDrawing;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        toolbar.addDrawModeListener(drawModeListener);
        toolbar.setDrawMode(drawMode);
        canvasArea.getComponent().addMouseListener(canvasMouseListener);
        canvasArea.getComponent().addMouseMotionListener(canvasMouseListener);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        toolbar.removeDrawModeListener(drawModeListener);
        canvasArea.getComponent().removeMouseListener(canvasMouseListener);
        canvasArea.getComponent().removeMouseMotionListener(canvasMouseListener);
        isDrawing = false;
    }
}
