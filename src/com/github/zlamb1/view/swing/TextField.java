package com.github.zlamb1.view.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Objects;

public class TextField extends JTextField {
    private String hint;
    private Color recordedColor;
    private final Color hintColor = Color.LIGHT_GRAY;

    public TextField() {
        super();
        recordedColor = getForeground();
        initializeFocusListener();
    }

    public TextField(String text) {
        super(text);
        recordedColor = getForeground();
        initializeFocusListener();
    }

    protected void clearHint() {
        setText("");
        setForeground(recordedColor);
    }

    protected void applyHint() {
        recordedColor = getForeground();
        setText(hint);
        setForeground(hintColor);
    }

    protected void initializeFocusListener() {
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (Objects.equals(getText(), hint) && getForeground() == hintColor) {
                    clearHint();
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty()) {
                    applyHint();
                }
            }
        });
    }

    public void setHint(String hint) {
        this.hint = hint;
        if (!hasFocus() && getText().isEmpty()) {
            applyHint();
        }
    }

    public String getHint() {
        return hint;
    }
}
