package com.github.zlamb1.view.swing;

import com.github.zlamb1.view.listener.IColorListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RGBField extends JPanel {
    protected static final Map<Color, ImageIcon> iconCache = new HashMap<>();

    protected List<IColorListener> colorListeners = new ArrayList<>();
    protected Color color = Color.BLACK;

    protected List<JTextField> componentFields = new ArrayList<>();
    protected JLabel fieldLabel;

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
                        internalSetColor(newColor);
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

        fieldLabel = new JLabel(label);
        add(fieldLabel);

        internalSetColor(color);

        for (JTextField field : componentFields) {
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
        internalSetColor(color);
    }

    public void addColorListener(IColorListener listener) {
        colorListeners.add(listener);
    }

    public boolean removeColorListener(IColorListener listener) {
        return colorListeners.remove(listener);
    }

    protected void internalSetColor(Color color) {
        this.color = color;

        if (iconCache.containsKey(color)) {
            fieldLabel.setIcon(iconCache.get(color));
        } else {
            SwingUtilities.invokeLater(() -> {
                // batch task for later; ensure color remains same
                if (this.color == color) {
                    BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D g2d = (Graphics2D) image.getGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Ellipse2D circle = new Ellipse2D.Double(1, 1, 8, 8);
                    g2d.setColor(color);
                    g2d.fill(circle);
                    g2d.dispose();

                    ImageIcon icon = new ImageIcon(image);
                    iconCache.put(color, icon);
                    fieldLabel.setIcon(icon);
                }
            });
        }
    }

    protected JTextField getDefaultTextField() {
        JTextField field = new JTextField("0");
        field.setPreferredSize(new Dimension(50, 30));
        return field;
    }
}
