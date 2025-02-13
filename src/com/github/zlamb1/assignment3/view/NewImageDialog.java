package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.view.swing.NumericalField;
import com.github.zlamb1.view.swing.RoundedBorder;
import com.github.zlamb1.view.swing.RoundedButtonUI;
import com.github.zlamb1.view.utility.IntegerOperations;

import javax.swing.*;
import java.awt.*;

public class NewImageDialog extends JDialog {
    private final int horizontalPadding = 5;
    private final int verticalPadding = 5;

    public NewImageDialog(Frame parent, ICanvasArea canvasArea) {
        super(parent);

        setResizable(false);
        setTitle("New Image With Custom Size");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(5, 5));
        contentPane.setBorder(BorderFactory.createEmptyBorder(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        Dimension canvasSize = canvasArea.getCanvasSize();
        NumericalField.IValidator<Integer> validator = (value) -> Math.max(value, 1);

        NumericalField<Integer> widthField = new NumericalField<>((int) canvasSize.getWidth(), IntegerOperations.getInstance());
        widthField.setValidator(validator);

        NumericalField<Integer> heightField = new NumericalField<>((int) canvasSize.getHeight(), IntegerOperations.getInstance());
        heightField.setValidator(validator);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;

        JLabel widthLabel = new JLabel("Width");
        widthLabel.setHorizontalAlignment(JLabel.RIGHT);

        Insets emptyInsets = new Insets(0, 0, 0, 0);

        gbc.insets = new Insets(0, 0, 20, 20);
        centerPanel.add(widthLabel, gbc);

        gbc.gridx++;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 20, 0);
        centerPanel.add(widthField, gbc);

        JLabel heightLabel = new JLabel("Height");
        heightLabel.setHorizontalAlignment(JLabel.RIGHT);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 20);
        centerPanel.add(heightLabel, gbc);

        gbc.gridx++;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = emptyInsets;
        centerPanel.add(heightField, gbc);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(Box.createHorizontalGlue());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(70, 35));
        cancelButton.addActionListener(e -> {
            setVisible(false);
            dispose();
        });

        JButton createButton = new JButton("Create");

        createButton.setPreferredSize(new Dimension(70, 35));
        createButton.addActionListener(e -> {
            Dimension newCanvasSize = new Dimension(widthField.getValue(), heightField.getValue());
            setVisible(false);
            dispose();
            canvasArea.clear(newCanvasSize);
        });

        Color buttonColor = new Color(61, 204, 61);
        // createButton.setBackground(buttonColor);

        bottomPanel.add(cancelButton);
        bottomPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        bottomPanel.add(createButton);

        contentPane.add(centerPanel, BorderLayout.CENTER);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
        pack();
    }
}
