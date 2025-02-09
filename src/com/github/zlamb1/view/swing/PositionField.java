package com.github.zlamb1.view.swing;

import com.github.zlamb1.view.listener.IPositionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PositionField extends JPanel {
    protected int nComponents;
    protected int[] components;
    protected JTextField[] componentFields;
    protected AtomicInteger internallyUpdatingText = new AtomicInteger(0);

    protected final List<IPositionListener> positionListeners = new ArrayList<>();

    public PositionField(String label) {
        this(label,2);
    }

    public PositionField(String label, int nComponents) {
        this.nComponents = nComponents;
        setLayout(new GridBagLayout());

        components = new int[nComponents];
        componentFields = new JTextField[nComponents];

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 5);

        JLabel _label = new JLabel(label);
        add(_label, gbc);

        setupComponents();
    }

    public int getNumberOfComponents() {
        return nComponents;
    }

    public int[] getPositionComponents() {
        return components;
    }

    public void setPositionComponents(int[] components) {
        if (nComponents != components.length) {
            throw new AssertionError("Expected " + nComponents + " components but got " + components.length);
        }

        internallyUpdatingText.set(nComponents);
        for (int i = 0; i < nComponents; i++) {
            this.components[i] = components[i];
            componentFields[i].setText(String.valueOf(components[i]));
        }
    }

    public void setNumberOfComponents(int nComponents) {
        int[] newComponents = new int[nComponents];
        for (int i = 0; i < Math.min(this.nComponents, nComponents); i++) {
            newComponents[i] = components[i];
        }

        components = newComponents;

        for (JTextField componentField : componentFields) {
            remove(componentField);
        }

        this.nComponents = nComponents;
        componentFields = new JTextField[nComponents];

        setupComponents();
    }

    public void addPositionListener(IPositionListener listener) {
        positionListeners.add(listener);
    }

    public boolean removePositionListener(IPositionListener listener) {
        return positionListeners.remove(listener);
    }

    protected void setupComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 5);

        for (int i = 0; i < nComponents; i++) {
            components[i] = 0;
            componentFields[i] = new JTextField(components[i]) {
                @Override
                public Dimension getPreferredSize() {
                    Dimension preferredSize = super.getPreferredSize();
                    preferredSize.width = Math.max(preferredSize.width, getMinimumSize().width);
                    return preferredSize;
                }
            };

            componentFields[i].setMinimumSize(new Dimension(50, 30));
            componentFields[i].getDocument().putProperty("positionComponent", i);

            componentFields[i].getDocument().addDocumentListener(new DocumentListener() {
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

                private void onUpdate(DocumentEvent e) {
                    if (internallyUpdatingText.get() == 0) {
                        Document document = e.getDocument();
                        try {
                            String newText = document.getText(0, document.getLength());
                            components[(int) document.getProperty("positionComponent")] = Integer.parseInt(newText);
                            for (IPositionListener positionListener : positionListeners) {
                                positionListener.onPositionChange(components);
                            }
                        } catch (BadLocationException exc)
                        {
                            throw new AssertionError(exc);
                        } catch (NumberFormatException ignored)
                        {
                        }
                    } else {
                        internallyUpdatingText.decrementAndGet();
                    }
                }
            });

            if (i == nComponents - 1) {
                gbc.insets = new Insets(0, 0, 0, 0);
            }

            add(componentFields[i], gbc);
            gbc.gridx++;
        }
    }
}
