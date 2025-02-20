package com.github.zlamb1.assignment4.view;

import com.github.zlamb1.assignment4.ActiveCard;
import com.github.zlamb1.assignment4.ICrazyEightsView;
import com.github.zlamb1.card.Card;
import com.github.zlamb1.card.Hand;
import com.github.zlamb1.card.Player;
import com.github.zlamb1.view.swing.CardWidget;
import com.github.zlamb1.view.swing.NumericalField;
import com.github.zlamb1.view.swing.WrapLayout;
import com.github.zlamb1.view.utility.IntegerOperations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

public class CrazyEightsView extends JFrame implements ICrazyEightsView {
    protected GridBagLayout gridBagLayout = new GridBagLayout();

    protected JPanel contentPane;
    protected JPanel topPanel, centerPanel, bottomPanel;

    protected CardWidget discardPile;
    protected CardWidget stockPile;

    public CrazyEightsView() {
        super();
        setTitle("Crazy Eights");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel(gridBagLayout);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        gbc.anchor = GridBagConstraints.NORTH;
        contentPane.add(topPanel, gbc);

        centerPanel = new JPanel();

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weighty = 1.0;

        contentPane.add(centerPanel, gbc);
        gbc.fill = GridBagConstraints.NONE;

        bottomPanel = new JPanel();
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weighty = 0.0;
        contentPane.add(bottomPanel, gbc);

        setContentPane(contentPane);

        SwingUtilities.invokeLater(() -> {
            setSize(800, 600);
            setVisible(true);
        });
    }

    protected List<CardWidget> renderHand(Player player, Card discardCard, String title) {
        clearPanels();

        final Hand hand = player.getHand();

        topPanel.removeAll();

        JLabel titleLabel = new JLabel(player.getName() + " - " + title);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(titleLabel);

        topPanel.add(Box.createRigidArea(new Dimension(5, 20)));

        JPanel pilePanel = new JPanel();
        pilePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        discardPile = new CardWidget(discardCard);
        pilePanel.add(discardPile);

        pilePanel.add(Box.createRigidArea(new Dimension(5, 0)));

        stockPile = new CardWidget(new Card(Card.Suit.CLUBS, Card.Rank.TWO));
        stockPile.setRenderBack(true);
        pilePanel.add(stockPile);

        topPanel.add(pilePanel);

        List<CardWidget> cards = new ArrayList<>();

        for (Card card : hand.getCards()) {
            CardWidget cardWidget = new CardWidget(card);
            cards.add(cardWidget);
            centerPanel.add(cardWidget);
        }

        revalidate();
        repaint();

        return cards;
    }

    @Override
    public CrazyEightsSettings promptStart() {
        clearPanels();

        CrazyEightsSettings settings = new CrazyEightsSettings();
        settings.playerCount = 2;

        CountDownLatch latch = new CountDownLatch(1);

        NumericalField<Integer> playerCountField = new NumericalField<>(settings.playerCount, IntegerOperations.getInstance());

        playerCountField.setValidator((value) -> Math.min(Math.max(value, 2), 6));
        playerCountField.addValueChangeListener((oldValue, newValue) -> {
            settings.playerCount = newValue;
        });

        JButton startButton = new JButton("Start Game");
        startButton.addActionListener((e) -> latch.countDown());

        LayoutManager layoutManager = centerPanel.getLayout();

        centerPanel.setLayout(new GridLayout(3, 1, 5, 5));

        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        fieldPanel.add(new JLabel("Player Count"));
        fieldPanel.add(playerCountField);

        centerPanel.add(fieldPanel);
        centerPanel.add(startButton);

        revalidate();
        repaint();

        try {
            latch.await();
        } catch (InterruptedException exc) {
            throw new AssertionError(exc);
        }

        centerPanel.setLayout(layoutManager);

        return settings;
    }

    @Override
    public Card promptPlay(Player player, Card discardCard) {
        final Card[] selectedCard = new Card[1];
        CountDownLatch latch = new CountDownLatch(1);

        for (CardWidget cardWidget : renderHand(player, discardCard, "Select Card")) {
            if (cardWidget.getCard() instanceof ActiveCard) {
                boolean isActive = ((ActiveCard) cardWidget.getCard()).isActive();
                cardWidget.setInteractable(isActive);
                if (isActive) {
                    cardWidget.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                selectedCard[0] = cardWidget.getCard();
                                latch.countDown();
                            }
                        }
                    });
                }
            }

            cardWidget.setCardHoverBorderColor(new Color(215, 215, 0));
        }

        try {
            latch.await();
        } catch (InterruptedException exc) {
            throw new AssertionError(exc);
        }

        return selectedCard[0];
    }

    @Override
    public void promptDraw(Player player, Card discardCard) {
        renderHand(player, discardCard, "Draw Card");
        stockPile.setInteractable(true);

        CountDownLatch latch = new CountDownLatch(1);
        stockPile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    latch.countDown();
                }
            }
        });

        try {
            latch.await();
        } catch (InterruptedException exc) {
            throw new AssertionError(exc);
        }
    }

    @Override
    public Card.Suit promptSuit(Player player, Card discardCard) {
        AtomicReference<Card.Suit> selectedSuit = new AtomicReference<>(discardCard.getSuit());
        JDialog dialog = new JDialog(this);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 4, 1, 1));

        dialog.setTitle("Select Suit for Eight");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setLocationRelativeTo(dialog.getParent());
        dialog.setModal(true);

        for (Card.Suit suit : Card.Suit.values()) {
            if (suit != Card.Suit.JOKER) {
                JButton suitButton = new JButton(suit.toString());
                suitButton.addActionListener(e -> {
                    selectedSuit.set(suit);
                    dialog.setVisible(false);
                    dialog.dispose();
                });
                panel.add(suitButton);
            }
        }

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setResizable(false);
        dialog.setVisible(true);

        revalidate();
        repaint();

        return selectedSuit.get();
    }

    @Override
    public void promptNextTurn(Player player, Card discardCard) {
        renderHand(player, discardCard, "Turn Over");

        CountDownLatch latch = new CountDownLatch(1);
        JButton nextTurnButton = new JButton("Next Turn");

        nextTurnButton.addActionListener(e -> latch.countDown());

        bottomPanel.add(nextTurnButton);
        revalidate();
        repaint();

        try {
            latch.await();
        } catch (InterruptedException exc) {
            throw new AssertionError(exc);
        }
    }

    @Override
    public void promptWinner(Player player) {
        clearPanels();

        CountDownLatch latch = new CountDownLatch(1);

        JLabel winnerLabel = new JLabel(player.getName() + " Wins!");
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> latch.countDown());

        LayoutManager layoutManager = centerPanel.getLayout();

        centerPanel.setLayout(new GridLayout(2, 1, 5, 5));

        centerPanel.add(winnerLabel);
        centerPanel.add(newGameButton);

        bottomPanel.removeAll();

        revalidate();
        repaint();

        try {
            latch.await();
        } catch (InterruptedException exc) {
            throw new AssertionError(exc);
        }

        // restore layout
        centerPanel.setLayout(layoutManager);
    }

    protected void clearPanels() {
        topPanel.removeAll();
        centerPanel.removeAll();
        bottomPanel.removeAll();
    }
}
