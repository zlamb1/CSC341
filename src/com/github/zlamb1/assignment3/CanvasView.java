package com.github.zlamb1.assignment3;

import com.github.zlamb1.assignment3.canvas.CanvasDrawableFactory;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;
import com.github.zlamb1.assignment3.view.*;
import com.github.zlamb1.assignment3.view.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.awt.event.InputEvent.*;

public class CanvasView extends JFrame {
    private final KeyEventPostProcessor keyEventPostProcessor;

    protected final static int undoTimerDuration = 300;
    protected final Timer undoTimer;
    protected final AtomicBoolean canUndo = new AtomicBoolean(true);

    protected ICanvasDrawableFactory drawableFactory;

    protected IToolbar toolbar;
    protected ICanvasArea canvasArea;
    protected IBottomToolbar bottomToolbar;

    public CanvasView() {
        super();

        undoTimer = new Timer(undoTimerDuration, e -> canUndo.set(true));

        keyEventPostProcessor = e -> {
            if (canUndo.get() && e.getKeyCode() == KeyEvent.VK_Z && (e.getModifiersEx() & CTRL_DOWN_MASK) == CTRL_DOWN_MASK) {
                canUndo.set(false);
                // Z and CTRL pressed
                if ((e.getModifiersEx() & SHIFT_DOWN_MASK) == SHIFT_DOWN_MASK) {
                    canvasArea.undoAll();
                } else {
                    canvasArea.undo();
                }
                undoTimer.start();
            }
            return true;
        };

        drawableFactory = new CanvasDrawableFactory();
        toolbar = new Toolbar(drawableFactory);
        canvasArea = new CanvasArea();
        bottomToolbar = new BottomToolbar(drawableFactory, canvasArea);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // this fixes artifacts with WrapLayout not being relaid out correctly on maximize/minimize
                SwingUtilities.invokeLater(() -> {
                    revalidate();
                    repaint();
                });
            }
        });

        SwingUtilities.invokeLater(() -> {
            setContentPane(new CanvasPanel(drawableFactory, toolbar, canvasArea, bottomToolbar));
            setJMenuBar(new MenuBar(canvasArea));

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(600, 800);
            setExtendedState(JFrame.MAXIMIZED_BOTH);

            setVisible(true);
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // global key listener for CTRL+Z/CTRL+ALT+Z
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventPostProcessor(keyEventPostProcessor);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventPostProcessor(keyEventPostProcessor);
    }
}
