package com.github.zlamb1.view.swing;

import com.github.zlamb1.view.listener.IValueListener;
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

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

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

        JPanel buttonPanel = new JPanel(new GridLayout());

        JButton incrementButton = new JButton("+");
        incrementButton.addActionListener(e -> {
            setValue(numericalOperations.increment(value));
        });

        JButton decrementButton = new JButton("-");
        decrementButton.addActionListener(e -> {
            setValue(numericalOperations.decrement(value));
        });

        buttonPanel.add(incrementButton);
        buttonPanel.add(decrementButton);

        add(textField);
        add(buttonPanel);
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
        this.value = value;
        textField.setText(String.valueOf(value));
        revalidate();
        repaint();
    }

    public void setValidator(IValidator<T> validator) {
        this.validator = validator;
    }
}
