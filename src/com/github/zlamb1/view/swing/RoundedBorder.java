package com.github.zlamb1.view.swing;

import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorder implements Border {
    private final Color color;
    private final int radius;
    private final int borderWidth;

    public RoundedBorder(int radius) {
        this(radius, null);
    }

    public RoundedBorder(int radius, Color color) {
        this(radius, 1, color);
    }

    public RoundedBorder(int radius, int borderWidth, Color color) {
        this.color = color;
        this.radius = radius;
        this.borderWidth = borderWidth;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color == null ? c.getForeground() : color);
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(this.borderWidth, this.borderWidth, this.borderWidth, this.borderWidth);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
