package com.github.zlamb1.view.swing;

import com.github.zlamb1.view.listener.IColorListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RGBField extends JPanel {
    protected List<IColorListener> colorListeners = new ArrayList<>();
    protected Color color = Color.BLACK;

    protected List<JTextField> componentFields = new ArrayList<>();

    public RGBField(String label) {
        super();

        for (int i = 0; i < 3; i++) {
            JTextField componentField = getDefaultTextField();
            componentField.getDocument().putProperty("rgbaComponent", i);
            componentFields.add(componentField);
        }

        DocumentListener documentListener = new DocumentListener() {
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

            private int getComponent(int component, int rgbaComponent, String text, int defaultValue) throws NumberFormatException {
                if (rgbaComponent == component) {
                    return Integer.parseInt(text);
                } else if (componentFields.size() > component) {
                    return Integer.parseInt(componentFields.get(component).getText());
                } else {
                    return defaultValue;
                }
            }

            private void onUpdate(DocumentEvent e) {
                try {
                    String newText = e.getDocument().getText(0, e.getDocument().getLength());
                    int rgbaComponent = (int) e.getDocument().getProperty("rgbaComponent");

                    int r = getComponent(0, rgbaComponent, newText, 0);
                    int g = getComponent(1, rgbaComponent, newText, 0);
                    int b = getComponent(2, rgbaComponent, newText, 0);
                    int a = getComponent(3, rgbaComponent, newText, 255);

                    Color newColor = new Color(r, g, b, a);
                    if (!newColor.equals(color)) {
                        color = newColor;
                        for (IColorListener listener : colorListeners) {
                            listener.onChangeColor(newColor);
                        }
                    }
                } catch (NumberFormatException ignored)
                {
                } catch (BadLocationException exc) {
                    exc.printStackTrace();
                }
            }
        };

        add(new JLabel(label));
        for (int i = 0; i < componentFields.size(); i++) {
            JTextField field = componentFields.get(i);
            field.getDocument().addDocumentListener(documentListener);
            add(field);
        }
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        componentFields.get(0).setText(String.valueOf(color.getRed()));
        componentFields.get(1).setText(String.valueOf(color.getGreen()));
        componentFields.get(2).setText(String.valueOf(color.getBlue()));
        if (componentFields.size() > 3) {
            componentFields.get(3).setText(String.valueOf(color.getAlpha()));
        }
    }

    public void addColorListener(IColorListener listener) {
        colorListeners.add(listener);
    }

    public boolean removeColorListener(IColorListener listener) {
        return colorListeners.remove(listener);
    }

    protected JTextField getDefaultTextField() {
        JTextField field = new JTextField("0");
        field.setPreferredSize(new Dimension(50, 30));
        return field;
    }
}
