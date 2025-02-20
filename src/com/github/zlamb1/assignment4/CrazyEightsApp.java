package com.github.zlamb1.assignment4;

import com.github.zlamb1.card.*;

import java.util.ArrayList;
import java.util.List;

public class CrazyEightsApp implements ICardControl {
    protected ICrazyEightsView view;
    protected List<Player> players;
    protected int currentPlayer;

    protected Hand discardPile, stockPile;

    public CrazyEightsApp(ICrazyEightsView view) {
        this.view = view;

        discardPile = new Hand();
        stockPile = new Hand();

        startGame();
    }

    @Override
    public void startGame() {
        Deck deck = new Deck();
        deck.shuffle();

        assert discardPile != null;
        discardPile.clearHand();

        assert stockPile != null;
        stockPile.clearHand();

        if (players == null) {
            players = new ArrayList<>();
        }

        ICrazyEightsView.CrazyEightsSettings settings = view.promptStart();

        players.clear();
        for (int i = 0; i < settings.playerCount; i++) {
            final int finalI = i;

            Hand hand = new Hand() {
                @Override
                public void addCard(Card card) {
                    cards.add(new ActiveCard(card, true));
                }
            };

            Player player = new Player(hand) {
                @Override
                public String getName() {
                    return "Player #" + (finalI + 1);
                }
            };

            for (int j = 0; j < 7; j++) {
                hand.addCard(deck.dealCard());
            }

            players.add(player);
        }

        while (deck.getCardsRemaining() > 0) {
            stockPile.addCard(deck.dealCard());
        }

        discardPile.addCard(stockPile.removeTopCard());

        currentPlayer = 0;
        runGame();
    }

    @Override
    public void playRound() {
        Player player = players.get(currentPlayer);

        Card discardCard = discardPile.getTopCard();

        int activeCards = 0;

        for (Card card : player.getHand().getCards()) {
            ActiveCard activeCard = (ActiveCard) card;
            boolean isActive = isActive(activeCard, discardCard);
            if (isActive) activeCards++;
            activeCard.setActive(isActive);
        }

        if (activeCards == 0) {
            while (true) {
                view.promptDraw(player, discardCard);

                Card drawnCard = refreshOrGetStockCard();
                stockPile.removeCard(drawnCard);

                boolean isActive = isActive(drawnCard, discardCard);

                player.getHand().addCard(drawnCard);
                ((ActiveCard) player.getHand().getTopCard()).setActive(isActive);

                if (isActive) {
                    break;
                }
            }
        }

        playStage(player);

        view.promptNextTurn(player, discardPile.getTopCard());

        currentPlayer = ++currentPlayer % players.size();
    }

    @Override
    public void runGame() {
        Player winner;
        while ((winner = getWinner()) == null) {
            playRound();
        }
        view.promptWinner(winner);
        startGame();
    }

    protected void playStage(Player player) {
        Card discardCard = discardPile.getTopCard();
        Card discardedCard;
        int activeCards;

        do {
            discardedCard = view.promptPlay(player, discardCard);
            player.getHand().removeCard(discardedCard);

            if (discardedCard.getRank() == Card.Rank.EIGHT) {
                Card.Suit suit = view.promptSuit(player, discardedCard);
                discardedCard = new Card(discardedCard.getColor(), suit, Card.Rank.EIGHT);
            }

            discardPile.addCard(discardedCard);

            activeCards = 0;
            for (Card card : player.getHand().getCards()) {
                ActiveCard activeCard = (ActiveCard) card;
                boolean isActive = card.getRank() == discardCard.getRank();
                activeCard.setActive(isActive);
                if (isActive) activeCards++;
            }

            if (discardedCard.getRank() != discardCard.getRank()) {
                break;
            }
        } while (activeCards > 0);
    }

    protected boolean isActive(Card card, Card discardCard) {
        return (card.getSuit() == discardCard.getSuit()) || (card.getRank() == discardCard.getRank()) || (card.getRank() == Card.Rank.EIGHT);
    }

    protected Card refreshOrGetStockCard() {
        if (stockPile.isEmpty()) {
            Card topCard = discardPile.getTopCard();
            List<Card> discardCards = discardPile.getCards();
            for (int i = 0; i < discardCards.size(); i++) {
                if (i != discardCards.size() - 1) {
                    stockPile.addCard(discardCards.get(i));
                }
            }
            discardPile.clearHand();
            discardPile.addCard(topCard);
            stockPile.shuffle();
        }

        return stockPile.getTopCard();
    }

    protected Player getWinner() {
        for (Player player : players) {
            if (player.getHand().isEmpty()) {
                return player;
            }
        }
        return null;
    }
}
