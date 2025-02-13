package com.github.zlamb1.view.swing;

import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorder implements Border {
    private final Color color;
    private final Insets borderInsets;
    private final int borderRadius;
    private final int borderWidth;

    public RoundedBorder(int borderRadius) {
        this(borderRadius, null);
    }

    public RoundedBorder(int borderRadius, Color color) {
        this(borderRadius, 1, color);
    }

    public RoundedBorder(int borderRadius, int borderWidth, Color color) {
        this(borderRadius, borderWidth, new Insets(borderWidth, borderWidth, borderWidth, borderWidth), color);
    }

    public RoundedBorder(int borderRadius, int borderWidth, Insets borderInsets, Color color) {
        this.borderRadius = borderRadius;
        this.color = color;
        this.borderInsets = borderInsets;
        this.borderWidth = borderWidth;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color == null ? c.getForeground() : color);
        g2d.setStroke(new BasicStroke(borderWidth));
        g2d.drawRoundRect(x, y, width - 1, height - 1, borderRadius, borderRadius);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return borderInsets;
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }
}
