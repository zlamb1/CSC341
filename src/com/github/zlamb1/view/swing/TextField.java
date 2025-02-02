package com.github.zlamb1.view.swing;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class TextField extends JTextField {
    private final Map<?, ?> desktopHints =
        (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");

    private String hint;
    private Color hintColor = Color.LIGHT_GRAY;

    public TextField() {
        super();
    }

    public TextField(String text) {
        super(text);
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
        setColumns(Math.max(getColumns(), hint.length()));
    }

    public Color getHintColor() {
        return hintColor;
    }

    public void setHintColor(Color hintColor) {
        this.hintColor = hintColor;
    }

    @Override
    protected void paintComponent(final Graphics pG) {
        super.paintComponent(pG);

        if (hint == null || hint.isEmpty() || (getText() != null && !getText().isEmpty())) {
            return;
        }

        final Graphics2D g = (Graphics2D) pG;

        if (desktopHints != null) {
            g.setRenderingHints(desktopHints);
        } else {
            g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
        }

        g.setColor(hintColor);
        g.drawString(hint, getInsets().left, pG.getFontMetrics()
            .getMaxAscent() + getInsets().top
        );
    }
}
