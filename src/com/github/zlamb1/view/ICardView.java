package com.github.zlamb1.view;

import com.github.zlamb1.card.Card;
import com.github.zlamb1.card.Hand;

public interface ICardView extends IView {
    default void displayCard(Card card) {
        displayCard(card, false);
    }
    void displayCard(Card card, boolean showBack);
    default void promptHand(Hand hand) {
        promptHand(hand, true);
    }
    void promptHand(Hand hand, boolean showCards);
}
