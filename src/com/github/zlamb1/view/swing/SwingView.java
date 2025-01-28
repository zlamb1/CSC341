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
    protected static final int FRAME_WIDTH = 1280;
    protected static final int FRAME_HEIGHT = 720;

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
        defaultLayout();
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
        panel.setLayout(new GridBagLayout());
        frame.setContentPane(panel);
    }

    protected void useGridBagConstraints(Container parent, Container child, int gridx, int gridy) {
        useGridBagConstraints(parent, child, gridx, gridy, 1, 1);
    }

    protected void useGridBagConstraints(Container parent, Container child, int gridx, int gridy, int gridwidth, int gridheight) {
        useGridBagConstraints(parent, child, new GridBagConstraints(), gridx, gridy, gridwidth, gridheight);
    }

    protected void useGridBagConstraints(Container parent, Container child, GridBagConstraints constraints, int gridx, int gridy) {
        useGridBagConstraints(parent, child, constraints, gridx, gridy, 1, 1);
    }

    protected void useGridBagConstraints(Container parent, Container child, GridBagConstraints constraints, int gridx, int gridy, int gridwidth, int gridheight) {
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        parent.add(child, constraints);
    }

    protected static class InfoLayout {
        public JComponent container, top, bottom;
    }

    protected InfoLayout infoLayout() {
        InfoLayout infoLayout = new InfoLayout();
        infoLayout.container = new JPanel();
        infoLayout.container.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        infoLayout.top = new JPanel();
        infoLayout.top.setLayout(new FlowLayout(FlowLayout.CENTER));
        constraints.weighty = 0.9;
        useGridBagConstraints(infoLayout.container, infoLayout.top, constraints, 0, 0);

        infoLayout.bottom = new JPanel();
        infoLayout.bottom.setLayout(new FlowLayout(FlowLayout.CENTER));
        constraints.weighty = 0.1;
        useGridBagConstraints(infoLayout.container, infoLayout.bottom, constraints, 0, 1);

        return infoLayout;
    }

    protected InfoLayout infoLayout(JComponent container, JComponent top, JComponent bottom) {
        InfoLayout infoLayout = new InfoLayout();
        infoLayout.container = container;
        infoLayout.container.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        infoLayout.top = top;
        constraints.weighty = 0.9;
        useGridBagConstraints(infoLayout.container, infoLayout.top, constraints, 0, 0);

        infoLayout.bottom = bottom;
        constraints.weighty = 0.1;
        useGridBagConstraints(infoLayout.container, infoLayout.bottom, constraints, 0, 1);

        return infoLayout;
    }

    @Override
    public void displayInfo(String info) {
        GridBagConstraints constraints = new GridBagConstraints();
        CountDownLatch latch = new CountDownLatch(1);
        JLabel label = new JLabel(info);
        getContainer().add(label);

        JButton button = new JButton("Continue");
        button.addActionListener(e -> {
            latch.countDown();
        });

        useGridBagConstraints(getContainer(), button, 0, 1);
        constraints.gridx = 0;
        constraints.gridy = 1;
        getContainer().add(button, constraints);

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

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.weightx = 1.0d / choices.size();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 5, 0, 5);

        for (int i = 0; i < choices.size(); i++) {
            final int finalI = i;
            JButton button = new JButton(choices.get(i));
            button.setAlignmentY(Component.CENTER_ALIGNMENT);
            button.addActionListener(e -> {
                choiceIndex.compareAndExchange(-1, finalI);
                latch.countDown();
            });
            buttons.add(button);
            constraints.gridx++;
            getContainer().add(button, constraints);
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
