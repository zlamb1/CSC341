package com.github.zlamb1.assignment3;

import com.github.zlamb1.assignment3.canvas.CanvasDrawableFactory;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;
import com.github.zlamb1.assignment3.view.*;
import com.github.zlamb1.view.swing.LookAndFeel;

import javax.swing.*;

public class CanvasView {
    private final JFrame frame;

    public CanvasView() {
        LookAndFeel.setSystemLookAndFeel();

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        ICanvasDrawableFactory drawableFactory = new CanvasDrawableFactory();
        ICanvasArea canvasArea = new CanvasArea();
        frame.add(new CanvasPanel(drawableFactory, new Toolbar(drawableFactory), canvasArea, new BottomToolbar(drawableFactory, canvasArea)));

        frame.revalidate();
        frame.repaint();
    }
}
