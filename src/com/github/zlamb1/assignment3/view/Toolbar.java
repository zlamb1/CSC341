package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;
import com.github.zlamb1.assignment3.listener.IDrawModeListener;
import com.github.zlamb1.view.swing.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Toolbar extends JPanel implements IToolbar {
    private final List<IDrawModeListener> drawModeListeners = new ArrayList<>();

    private ICanvasDrawableFactory drawableFactory;

    private final Map<DrawMode, DrawModeButton> drawModeButtons = new HashMap<>();
    private DrawMode drawMode;

    protected void makeDrawModeButton(DrawMode drawMode) {
        if (drawModeButtons.containsKey(drawMode) && drawModeButtons.get(drawMode) != null) {
            remove(drawModeButtons.get(drawMode));
        }

        DrawModeButton button = new DrawModeButton(drawMode, drawableFactory);

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(e -> {
            for (IDrawModeListener drawModeListener : drawModeListeners) {
                drawModeListener.onChangeDrawMode(drawMode);
            }
        });

        add(button);

        drawModeButtons.put(drawMode, button);
    }

    public Toolbar(ICanvasDrawableFactory drawableFactory) {
        super();

        this.drawableFactory = drawableFactory;

        setLayout(new WrapLayout(FlowLayout.LEFT));

        for (DrawMode drawMode : DrawMode.values()) {
            makeDrawModeButton(drawMode);
        }

        setBorder(BorderFactory.createTitledBorder("Toolbar"));
        setDrawMode(DrawMode.TRIANGLE);
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void setDrawMode(DrawMode drawMode) {
        if (drawModeButtons.containsKey(this.drawMode) && drawModeButtons.get(this.drawMode) != null) {
            drawModeButtons.get(this.drawMode).setSelected(false);
        }

        this.drawMode = drawMode;

        if (drawModeButtons.containsKey(drawMode) && drawModeButtons.get(drawMode) != null) {
            drawModeButtons.get(drawMode).setSelected(true);
        }
    }

    @Override
    public void addDrawModeListener(IDrawModeListener drawModeListener) {
        drawModeListeners.add(drawModeListener);
    }

    @Override
    public boolean removeDrawModeListener(IDrawModeListener drawModeListener) {
        return drawModeListeners.remove(drawModeListener);
    }
}
