package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.IDrawModeListener;
import com.github.zlamb1.view.swing.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Toolbar extends JPanel implements IToolbar {
    private final List<IDrawModeListener> drawModeListeners = new ArrayList<>();

    private final Map<DrawMode, JButton> drawModeButtons = new HashMap<>();
    private DrawMode drawMode;

    protected void makeDrawModeButton(DrawMode drawMode, String text) {
        if (drawModeButtons.containsKey(drawMode) && drawModeButtons.get(drawMode) != null) {
            remove(drawModeButtons.get(drawMode));
        }

        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(e -> {
            for (IDrawModeListener drawModeListener : drawModeListeners) {
                drawModeListener.onChangeDrawMode(drawMode);
            }
        });

        add(button);

        drawModeButtons.put(drawMode, button);
    }

    public Toolbar() {
        super();
        setLayout(new WrapLayout(FlowLayout.LEFT));

        for (DrawMode drawMode : DrawMode.values()) {
            String modeName = drawMode.name();
            modeName = modeName.substring(0, 1).toUpperCase() + modeName.substring(1).toLowerCase();
            makeDrawModeButton(drawMode, modeName);
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
        this.drawMode = drawMode;
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
