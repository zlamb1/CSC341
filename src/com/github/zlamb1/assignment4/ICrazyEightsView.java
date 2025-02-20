package com.github.zlamb1.assignment4;

import com.github.zlamb1.card.Card;
import com.github.zlamb1.card.Player;

public interface ICrazyEightsView {
    class CrazyEightsSettings {
        public int playerCount;
    }

    CrazyEightsSettings promptStart();

    Card promptPlay(Player player, Card discardCard);
    void promptDraw(Player player, Card discardCard);
    Card.Suit promptSuit(Player player, Card discardCard);
    void promptNextTurn(Player player, Card discardCard);
    void promptWinner(Player player);
}
