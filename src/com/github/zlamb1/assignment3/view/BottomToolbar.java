package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.DrawableAdapter;
import com.github.zlamb1.assignment3.listener.IDrawableListener;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;
import com.github.zlamb1.view.listener.IColorListener;
import com.github.zlamb1.view.listener.IPositionListener;
import com.github.zlamb1.view.swing.PositionField;
import com.github.zlamb1.view.swing.RGBField;

import javax.swing.*;
import java.awt.*;

public class BottomToolbar extends JPanel implements IBottomToolbar {
    private final IDrawableListener drawableListener;
    private final IColorListener colorListener;
    private final IPositionListener positionListener;

    private final ICanvasDrawableFactory drawableFactory;
    private final ICanvasArea canvasArea;
    private ICanvasDrawable activeDrawable;

    private final RGBField rgbField;
    private final PositionField positionField;
    private final PositionField strokeField;

    private final JLabel positionLabel;

    public BottomToolbar(ICanvasDrawableFactory drawableFactory, ICanvasArea canvasArea) {
        super();

        this.drawableFactory = drawableFactory;
        this.canvasArea = canvasArea;

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Options"));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 0));

        rgbField = new RGBField("Color");
        rgbField.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(rgbField);

        positionField = new PositionField("Position");
        positionField.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(positionField);

        strokeField = new PositionField("Stroke", 1);
        strokeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        strokeField.setPositionComponents(new int[] { drawableFactory.getStrokeWidth() });
        strokeField.addPositionListener(stroke -> {
            drawableFactory.setStrokeWidth(stroke[0]);
        });
        leftPanel.add(strokeField);

        JCheckBox filled = new JCheckBox("Filled");
        filled.addActionListener(e -> this.drawableFactory.setFilled(filled.isSelected()));

        leftPanel.add(filled);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        positionLabel = new JLabel("");
        rightPanel.add(positionLabel);

        setupUndoButtons(rightPanel);

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
                int[] i = new int[] { (int) canvasDrawable.getOrigin().getX(), (int) canvasDrawable.getOrigin().getY() };
                positionField.setPositionComponents(i);
            }
        };

        colorListener = drawableFactory::setColor;

        positionListener = (components) -> {
            if (activeDrawable != null) {
                activeDrawable.moveTo(new Point(components[0], components[1]));
                canvasArea.invalidateCanvas();
            }
        };
    }

    @Override
    public void addNotify() {
        super.addNotify();
        canvasArea.addDrawableListener(drawableListener);
        rgbField.addColorListener(colorListener);
        positionField.addPositionListener(positionListener);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        canvasArea.removeDrawableListener(drawableListener);
        rgbField.removeColorListener(colorListener);
        positionField.removePositionListener(positionListener);
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

    protected void setupUndoButtons(Container container) {
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

        container.add(undoButton);
        container.add(undoAllButton);
    }
}
