package com.github.zlamb1.view.swing;

import com.github.zlamb1.view.AbstractView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SwingView extends AbstractView {
    protected static final int FRAME_WIDTH = 500;
    protected static final int FRAME_HEIGHT = 500;

    protected final JFrame frame;

    public SwingView() {
        frame = new JFrame();
        defaultLayout();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        // center frame in screen
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.requestFocusInWindow();
            }
        });
        frame.setVisible(true);
    }

    public SwingView(JFrame frame) {
        this.frame = frame;
    }

    protected Container getContainer() {
        return frame.getContentPane();
    }

    protected void repaint() {
        frame.revalidate();
        frame.repaint();
    }

    protected void defaultLayout() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.setContentPane(panel);
    }

    protected static class InfoLayout {
        public JComponent container, top, bottom;
    }

    protected InfoLayout infoLayout() {
        InfoLayout infoLayout = new InfoLayout();
        infoLayout.container = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        infoLayout.container.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        infoLayout.top = new JPanel();
        infoLayout.top.setLayout(new FlowLayout(FlowLayout.CENTER));
        constraints.weighty = 0.9;
        constraints.gridx = 0;
        constraints.gridy = 0;
        infoLayout.container.add(infoLayout.top, constraints);

        infoLayout.bottom = new JPanel();
        infoLayout.bottom.setLayout(new FlowLayout(FlowLayout.CENTER));
        constraints.weighty = 0.1;
        constraints.gridy = 1;
        infoLayout.container.add(infoLayout.bottom, constraints);

        return infoLayout;
    }

    protected InfoLayout infoLayout(JComponent container, JComponent top, JComponent bottom) {
        InfoLayout infoLayout = new InfoLayout();
        infoLayout.container = container;
        GridBagLayout layout = new GridBagLayout();
        infoLayout.container.setLayout(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        infoLayout.top = top;
        constraints.weighty = 0.9;
        constraints.gridx = 0;
        constraints.gridy = 0;
        infoLayout.container.add(infoLayout.top, constraints);

        infoLayout.bottom = bottom;
        constraints.weighty = 0.1;
        constraints.gridy = 1;
        infoLayout.container.add(infoLayout.bottom, constraints);

        return infoLayout;
    }

    @Override
    public void displayInfo(String info) {
        InfoLayout layout = infoLayout();

        CountDownLatch latch = new CountDownLatch(1);
        JLabel label = new JLabel(info);
        layout.top.add(label);

        JButton button = new JButton("Continue");
        button.addActionListener(e -> {
            latch.countDown();
        });

        layout.bottom.add(button);

        frame.setContentPane(layout.container);

        repaint();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new AssertionError("Unexpected InterruptedException", e);
        }

        defaultLayout();
        repaint();
    }

    @Override
    public String promptString(String prompt) {
        CountDownLatch latch = new CountDownLatch(1);
        TextField textField = new TextField();
        textField.setHint(prompt);
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    latch.countDown();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        getContainer().add(textField);

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> {
            latch.countDown();
        });

        getContainer().add(submitBtn);

        repaint();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new AssertionError("Unexpected InterruptedException", e);
        }

        getContainer().remove(textField);
        getContainer().remove(submitBtn);
        repaint();

        return textField.getText();
    }

    @Override
    public int promptChoice(List<String> choices) {
        CountDownLatch latch = new CountDownLatch(1);
        List<JButton> buttons = new ArrayList<>();
        AtomicInteger choiceIndex = new AtomicInteger(-1);

        for (int i = 0; i < choices.size(); i++) {
            final int finalI = i;
            JButton button = new JButton(choices.get(i));
            button.addActionListener(e -> {
                choiceIndex.compareAndExchange(-1, finalI);
                latch.countDown();
            });
            buttons.add(button);
            getContainer().add(button);
        }

        repaint();

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new AssertionError("Unexpected InterruptedException", e);
        }

        for (JButton button : buttons) {
            getContainer().remove(button);
        }

        repaint();

        return choiceIndex.get();
    }

    @Override
    public void disposeView() {
        frame.setVisible(false);
        frame.dispose();
    }
}
