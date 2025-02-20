package com.github.zlamb1.view.swing;

import com.github.zlamb1.view.listener.IValueListener;
import com.github.zlamb1.view.utility.DimensionUtility;
import com.github.zlamb1.view.utility.INumericalOperations;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class NumericalField<T extends Number> extends JPanel {
    public interface IValidator<T> {
        T validate(T newValue);
    }

    protected final INumericalOperations<T> numericalOperations;
    protected IValidator<T> validator = null;

    protected T value;
    protected int minimumFieldWidth = 100;

    protected JTextField textField;

    protected final List<IValueListener<T>> valueChangeListeners = new ArrayList<>();

    public NumericalField(T defaultValue, INumericalOperations<T> numericalOperations) {
        this.numericalOperations = numericalOperations;
        this.value = defaultValue;

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;

        textField = new JTextField(String.valueOf(defaultValue)) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(
                    Math.max(super.getPreferredSize().width, minimumFieldWidth),
                    super.getPreferredSize().height
                );
            }
        };

        textField.getDocument().addDocumentListener(new DocumentListener() {
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
                try {
                    String textValue = e.getDocument().getText(0, e.getDocument().getLength());

                    T oldValue = value;
                    T newValue = numericalOperations.parse(textValue);

                    if (validator != null) {
                        value = validator.validate(newValue);
                    } else {
                        value = newValue;
                    }

                    for (IValueListener<T> valueChangeListener : valueChangeListeners) {
                        valueChangeListener.onValueChange(oldValue, value);
                    }
                } catch (BadLocationException exc) {
                    throw new AssertionError(exc);
                } catch (NumberFormatException ignored)
                {
                }
            }
        });

        // reset text to current value if invalid
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
            textField.setText(String.valueOf(value));
            }
        });

        JPanel buttonPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx = 0;
        gbc2.gridy = 0;
        gbc2.fill = GridBagConstraints.VERTICAL;

        gbc2.weightx = 0.5;
        gbc2.weighty = 0.5;

        JButton incrementButton = new JButton("+");
        incrementButton.addActionListener(e -> setValue(numericalOperations.increment(value)));

        JButton decrementButton = new JButton("-");
        decrementButton.addActionListener(e -> setValue(numericalOperations.decrement(value)));

        int maxWidth = Math.max(incrementButton.getPreferredSize().width, decrementButton.getPreferredSize().width);
        int maxHeight = Math.max(incrementButton.getPreferredSize().height, decrementButton.getPreferredSize().height);
        int maxExtent = Math.max(maxWidth, maxHeight);

        Dimension preferredSize = new Dimension(maxExtent, maxExtent);
        incrementButton.setPreferredSize(preferredSize);
        decrementButton.setPreferredSize(preferredSize);

        buttonPanel.add(incrementButton, gbc2);

        gbc2.gridx++;
        buttonPanel.add(decrementButton, gbc2);

        add(textField, gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(buttonPanel, gbc);
    }

    public void addValueChangeListener(IValueListener<T> valueChangeListener) {
        valueChangeListeners.add(valueChangeListener);
    }


    public boolean removeValueChangeListener(IValueListener<T> valueChangeListener) {
        return valueChangeListeners.remove(valueChangeListener);
    }

    public int getMinimumFieldWidth() {
        return minimumFieldWidth;
    }

    public void setMinimumFieldWidth(int minimumFieldWidth) {
        this.minimumFieldWidth = minimumFieldWidth;
        revalidate();
        repaint();
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        if (validator != null) {
            value = validator.validate(value);
        }

        this.value = value;
        textField.setText(String.valueOf(value));
        revalidate();
        repaint();
    }

    public void setValidator(IValidator<T> validator) {
        this.validator = validator;
    }
}
