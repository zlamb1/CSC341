package com.github.zlamb1.assignment2.view;

import com.github.zlamb1.card.Card;
import com.github.zlamb1.card.Hand;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class HandPanel extends JPanel {
    private final Hand hand;
    private CardLabel activeLabel;

    private boolean active = true;
    private final boolean renderBack;
    private final boolean showDuplicates;

    private final List<CardLabel> labels = new ArrayList<>();
    private final List<IHandActivateListener> cardActivateListeners = new ArrayList<>();
    private final List<IHandSelectListener> cardSelectionListeners = new ArrayList<>();

    public HandPanel(Hand hand) {
        this(hand, false);
    }

    public HandPanel(Hand hand, boolean renderBack) {
        this.hand = hand;
        this.renderBack = renderBack;
        showDuplicates = !renderBack;
        setLayout(new FlowLayout());
        instantiatePanels();
    }

    public void addActivateListener(IHandActivateListener listener) {
        cardActivateListeners.add(listener);
    }

    public void removeActivateListener(IHandActivateListener listener) {
        cardActivateListeners.remove(listener);
    }

    public void addSelectionListener(IHandSelectListener listener) {
        cardSelectionListeners.add(listener);
    }

    public void removeSelectionListener(IHandSelectListener listener) {
        cardSelectionListeners.remove(listener);
    }

    protected void instantiatePanels() {
        for (Card card : hand.getCards()) {
            CardLabel cardLabel = new CardLabel(card, true, renderBack);

            if (showDuplicates && hand.contains(card.getRank(), 2)) {
                cardLabel.setDefaultBorderColor(Color.PINK);
                cardLabel.setDefaultBorder();
            }

            cardLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    if (active && e.getButton() == MouseEvent.BUTTON1) {
                        if (activeLabel == null || activeLabel.getCard().getRank() != card.getRank()) {
                            setActiveLabel(cardLabel);
                        } else if (activeLabel.equals(cardLabel)) {
                            setActiveLabel(null);
                        } else {
                            // notify listeners
                            for (IHandSelectListener listener : cardSelectionListeners) {
                                listener.onSelect(activeLabel.getCard(), card);
                            }
                            setActiveLabel(null);
                        }
                    }
                }
            });

            labels.add(cardLabel);
            add(cardLabel);
        }
    }

    public void setActive(boolean active) {
        this.active = active;
        for (CardLabel cardLabel : labels) {
            cardLabel.setActive(active);
        }
    }

    protected void setActiveLabel(CardLabel cardLabel) {
        if (activeLabel != null) {
            activeLabel.setSelected(false);
        }

        activeLabel = cardLabel;
        if (activeLabel != null) {
            activeLabel.setSelected(true);
            for (IHandActivateListener listener : cardActivateListeners) {
                listener.onActivate(activeLabel.getCard());
            }
        }
    }
}
