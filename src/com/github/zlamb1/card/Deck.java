package com.github.zlamb1.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class Deck {
    private final static int NUM_JOKERS = 2;

    private final List<Card> cards = new ArrayList<>();
    private int cardsDealt = 0;
    private final boolean useJokers;

    public Deck() {
        this(false);
    }

    public Deck(boolean useJokers) {
        this.useJokers = useJokers;

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                if (suit != Card.Suit.JOKER && rank != Card.Rank.JOKER) {
                    cards.add(new Card(suit, rank));
                }
            }
        }

        for (int i = 0; useJokers && i < NUM_JOKERS; i++) {
            cards.add(new Card(Card.Suit.JOKER, Card.Rank.JOKER));
        }
    }

    public boolean removeCard(Card.Rank rank) {
        for (Card card : cards) {
            if (card.getRank() == rank) {
                cards.remove(card);
                return true;
            }
        }

        return false;
    }

    public boolean removeCard(Card.Rank rank, Card.Suit suit) {
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            if (card.getRank() == rank && card.getSuit() == suit) {
                cards.remove(i);
                return true;
            }
        }

        return false;
    }

    public void shuffle() {
        for (int i = 0; i < cards.size(); i++) {
            int index;
            do {
                index = (int) (Math.random() * cards.size());
            } while (index == i);
            Card temp = cards.get(i);
            cards.set(i, cards.get(index));
            cards.set(index, temp);
        }

        cardsDealt = 0;
    }

    public Card dealCard() {
        if (cardsDealt >= cards.size()) {
            throw new IllegalStateException("No Cards Remaining In Deck");
        }

        return cards.get(cardsDealt++);
    }

    public int getCardsDealt() {
        return cardsDealt;
    }

    public int getCardsRemaining() {
        return cards.size() - cardsDealt;
    }

    public boolean doesUseJokers() {
        return useJokers;
    }
}
