package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.DrawableAdapter;
import com.github.zlamb1.assignment3.listener.IDrawableListener;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;
import com.github.zlamb1.view.listener.IColorListener;
import com.github.zlamb1.view.swing.RGBField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
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

        rgbField = new RGBField("Color");
        rgbField.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(rgbField);

        setupStrokeField(leftPanel);

        JCheckBox filled = new JCheckBox("Filled");
        filled.addActionListener(e -> {
            this.drawableFactory.setFilled(filled.isSelected());
        });

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

    protected void setupStrokeField(Container container) {
        JLabel strokeLabel = new JLabel("Stroke");

        JTextField strokeField = new JTextField(drawableFactory.getStrokeWidth() + "");
        strokeField.setPreferredSize(new Dimension(50, 30));

        strokeField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }

            private void onUpdate(DocumentEvent e) {
                try {
                    String newText = e.getDocument().getText(0, e.getDocument().getLength());
                    drawableFactory.setStrokeWidth(Integer.parseInt(newText));
                } catch (BadLocationException exc) {
                    throw new AssertionError(exc);
                } catch (NumberFormatException ignored)
                {
                }
            }
        });

        container.add(strokeLabel);
        container.add(strokeField);
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
