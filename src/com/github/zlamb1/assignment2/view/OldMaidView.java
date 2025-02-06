package com.github.zlamb1.assignment2.view;

import com.github.zlamb1.assignment2.OldMaidSettings;
import com.github.zlamb1.card.Card;
import com.github.zlamb1.card.Hand;
import com.github.zlamb1.card.Player;
import com.github.zlamb1.view.swing.SwingView;
import com.github.zlamb1.view.swing.TextField;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class OldMaidView extends SwingView implements IOldMaidView {
    public OldMaidView() {
        super();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public OldMaidSettings promptStart() {
        OldMaidSettings settings = new OldMaidSettings();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);

        CountDownLatch latch = new CountDownLatch(1);

        JCheckBox useJokers = new JCheckBox("Use Jokers");
        TextField numPlayers = new TextField();
        numPlayers.setHint("Enter Number of Players");
        JButton button = new JButton("Start Game");

        button.addActionListener(e -> {
            latch.countDown();
        });

        getContainer().add(useJokers, gbc);
        gbc.gridy++;
        getContainer().add(numPlayers, gbc);
        gbc.gridy++;
        getContainer().add(button, gbc);
        repaint();

        try {
            latch.await();
        } catch (InterruptedException exc) {
            throw new RuntimeException("Unexpected InterruptedException", exc);
        }

        try {
            settings.useJokers = useJokers.isSelected();
            settings.numPlayers = Integer.parseInt(numPlayers.getText());
            if (settings.numPlayers < 2) {
                return invalidStartPromptInput();
            }
        } catch (NumberFormatException exc) {
            return invalidStartPromptInput();
        }

        defaultLayout();
        repaint();

        return settings;
    }

    protected OldMaidSettings invalidStartPromptInput() {
        defaultLayout();
        displayInfo("Please enter a valid number of players (>2).");
        return promptStart();
    }

    @Override
    public List<Card> promptDiscard(final Player player) {
        assert player != null;
        assert player.getHand() != null;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel instructionLabel = new JLabel("Discard Pairs - " + player.getName());
        instructionLabel.setFont(instructionLabel.getFont().deriveFont(Font.BOLD, 24));
        getContainer().add(instructionLabel, gbc);
        gbc.gridy++;

        HandPanel playerHandPanel = new HandPanel(player.getHand());

        getContainer().add(playerHandPanel, gbc);
        repaint();

        List<Card> cards = new ArrayList<>();

        CountDownLatch latch = new CountDownLatch(1);

        IHandSelectListener listener = (first, second) -> {
            cards.add(first);
            cards.add(second);
            latch.countDown();
        };

        playerHandPanel.addSelectionListener(listener);

        try {
            latch.await();
            playerHandPanel.removeSelectionListener(listener);
        } catch (InterruptedException exc) {
            throw new RuntimeException("Unexpected InterruptedException", exc);
        }

        defaultLayout();
        repaint();

        return cards;
    }

    @Override
    public Card promptDraw(final Player drawPlayer) {
        assert drawPlayer != null;
        assert drawPlayer.getHand() != null;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel instructionLabel = new JLabel("Draw Card From " + drawPlayer.getName());
        instructionLabel.setFont(instructionLabel.getFont().deriveFont(Font.BOLD, 24));
        getContainer().add(instructionLabel, gbc);
        gbc.gridy++;

        HandPanel drawHandPanel = new HandPanel(drawPlayer.getHand(), true);
        getContainer().add(drawHandPanel, gbc);

        repaint();

        CountDownLatch latch = new CountDownLatch(1);

        final Card[] drawCard = new Card[1];
        IHandActivateListener listener = (card) -> {
            drawCard[0] = card;
            latch.countDown();
        };

        drawHandPanel.addActivateListener(listener);

        try {
            latch.await();
            drawHandPanel.removeActivateListener(listener);
        } catch (InterruptedException exc) {
            throw new RuntimeException("Unexpected InterruptedException", exc);
        }

        defaultLayout();
        repaint();

        return drawCard[0];
    }

    @Override
    public void promptEndTurn(Player player) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel label = new JLabel(player.getName() + "'s Hand");
        label.setFont(label.getFont().deriveFont(Font.BOLD, 24));
        getContainer().add(label, gbc);
        gbc.gridy++;

        HandPanel playerHandPanel = new HandPanel(player.getHand());
        playerHandPanel.setActive(false);
        getContainer().add(playerHandPanel, gbc);
        gbc.gridy++;

        CountDownLatch latch = new CountDownLatch(1);
        JButton button = new JButton("End Turn");

        button.addActionListener(e -> {
            latch.countDown();
        });

        getContainer().add(button, gbc);
        repaint();

        try {
            latch.await();
        } catch (InterruptedException exc) {
            throw new RuntimeException("Unexpected InterruptedException", exc);
        }

        defaultLayout();
        repaint();
    }
}
