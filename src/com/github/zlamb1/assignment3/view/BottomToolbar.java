package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.DrawableAdapter;
import com.github.zlamb1.assignment3.listener.IDrawableListener;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;
import com.github.zlamb1.view.listener.IColorListener;
import com.github.zlamb1.view.swing.RGBField;

import javax.swing.*;
import java.awt.*;

public class BottomToolbar extends JPanel implements IBottomToolbar {
    private final IDrawableListener drawableListener;
    private final IColorListener colorListener;

    private final ICanvasDrawableFactory drawableFactory;
    private final ICanvasArea canvasArea;
    private ICanvasDrawable activeDrawable;

    private final RGBField rgbField;

    private final JLabel positionLabel;

    public BottomToolbar(ICanvasDrawableFactory drawableFactory, ICanvasArea canvasArea) {
        super();

        this.drawableFactory = drawableFactory;
        this.canvasArea = canvasArea;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Options"));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        rgbField = new RGBField("Color: ");
        rgbField.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(rgbField);

        JCheckBox filled = new JCheckBox("Filled");
        filled.addActionListener(e -> {
            this.drawableFactory.setFilled(filled.isSelected());
        });

        leftPanel.add(filled);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        positionLabel = new JLabel("");
        rightPanel.add(positionLabel);

        JButton undoButton = new JButton("Undo");
        undoButton.setToolTipText("Ctrl+Z");
        undoButton.addActionListener(e -> {
            canvasArea.undo();
        });

        JButton undoAllButton = new JButton("Undo All");
        undoAllButton.setToolTipText("Ctrl+Shift+Z");
        undoAllButton.addActionListener(e -> {
            canvasArea.undoAll();
        });

        rightPanel.add(undoButton);
        rightPanel.add(undoAllButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        add(leftPanel, gbc);
        gbc.gridx++;
        add(rightPanel, gbc);

        drawableListener = new DrawableAdapter() {
            @Override
            public void onAddDrawable(ICanvasDrawable canvasDrawable) {
                activeDrawable = canvasDrawable;
            }
        };

        colorListener = drawableFactory::setColor;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        canvasArea.addDrawableListener(drawableListener);
        rgbField.addColorListener(colorListener);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        canvasArea.removeDrawableListener(drawableListener);
        rgbField.removeColorListener(colorListener);
    }

    @Override
    public void setPosition(Point position) {
        positionLabel.setText((int) position.getX() + ", " + (int) position.getY());
        positionLabel.repaint();
    }

    @Override
    public JComponent getComponent() {
        return this;
    }
}
