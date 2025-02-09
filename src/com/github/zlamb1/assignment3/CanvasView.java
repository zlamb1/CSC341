package com.github.zlamb1.assignment3;

import com.github.zlamb1.assignment3.canvas.CanvasDrawableFactory;
import com.github.zlamb1.assignment3.canvas.ICanvasDrawableFactory;
import com.github.zlamb1.assignment3.view.*;
import com.github.zlamb1.assignment3.view.MenuBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.awt.event.InputEvent.*;

public class CanvasView extends JFrame {
    private final KeyEventPostProcessor keyEventPostProcessor;

    protected final static int undoTimerDuration = 250;
    protected final Timer undoTimer;
    protected final AtomicBoolean canUndo = new AtomicBoolean(true);

    protected ICanvasDrawableFactory drawableFactory;
    protected ICanvasArea canvasArea;

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

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        drawableFactory = new CanvasDrawableFactory();
        canvasArea = new CanvasArea();
        add(new CanvasPanel(drawableFactory, new Toolbar(drawableFactory), canvasArea, new BottomToolbar(drawableFactory, canvasArea)));

        setJMenuBar(new MenuBar(canvasArea));

        setVisible(true);

        revalidate();
        repaint();
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
