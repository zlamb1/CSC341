package com.github.zlamb1.card;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Card Must Not Be Null");
        }

        cards.add(card);
    }

    public Card getCardAt(int index) {
        return cards.get(index);
    }

    public boolean removeCard(Card card) {
        return cards.remove(card);
    }

    public void removeCardAt(int index) {
        if (index < 0 || index >= cards.size()) {
            throw new IllegalArgumentException("Card Index Out Of Bounds");
        }
        cards.remove(index);
    }

    public void clearHand() {
        cards.clear();
    }

    public int getHandSize() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public List<Card> getCards() {
        return cards;
    }
}
