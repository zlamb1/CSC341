package com.github.zlamb1.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

    public boolean contains(Card.Rank rank) {
        for (Card card : cards) {
            if (card.getRank() == rank) {
                return true;
            }
        }

        return false;
    }

    public boolean contains(Card.Rank rank, int atLeast) {
        int occurrences = 0;
        for (Card card : cards) {
            if (card.getRank() == rank) {
                if (++occurrences >= atLeast) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public List<Card> getCards() {
        return cards;
    }

    public Optional<Card.Rank> getFirstPair() {
        HashMap<Card.Rank, Integer> rankOccurrences = new HashMap<>();

        for (Card card : cards) {
            rankOccurrences.merge(card.getRank(), 1, Integer::sum);
            if (rankOccurrences.get(card.getRank()) > 1) {
                return Optional.of(card.getRank());
            }
        }

        return Optional.empty();
    }
}
