package com.github.zlamb1.assignment4;

import com.github.zlamb1.card.Card;

public class ActiveCard extends Card {
    boolean active = false;

    public ActiveCard(Suit suit, Rank rank) {
        super(suit, rank);
    }

    public ActiveCard(Card card, boolean active) {
        super(card.getSuit(), card.getRank());
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
