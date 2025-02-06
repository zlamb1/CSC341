package com.github.zlamb1.assignment2;

import com.github.zlamb1.assignment2.view.IOldMaidView;
import com.github.zlamb1.card.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OldMaidApp implements ICardControl {
    private final IOldMaidView view;
    private final List<Player> players = new ArrayList<>();
    private int activePlayer;

    public OldMaidApp(IOldMaidView view) {
        this.view = view;
    }

    @Override
    public void startGame() {
        OldMaidSettings settings = view.promptStart();

        players.clear();
        Deck deck = new Deck(settings.useJokers);
        deck.shuffle();
        // remove a single queen so one player ends up with it at end
        deck.removeCard(Card.Rank.QUEEN);

        for (int i = 0; i < settings.numPlayers; i++) {
            players.add(new OldMaidPlayer(i));
        }

        activePlayer = (int) (Math.random() * settings.numPlayers);

        int i = 0;
        while (deck.getCardsRemaining() > 0) {
            Player player = players.get(i++ % players.size());
            player.getHand().addCard(deck.dealCard());
        }

        runGame();
    }

    @Override
    public void playRound() {
        int lastPlayer = activePlayer - 1;
        if (lastPlayer < 0) {
            lastPlayer = players.size() - 1;
        }

        Player player = players.get(activePlayer), drawPlayer = players.get(lastPlayer);

        if (!player.getHand().isEmpty()) {
            doDiscardStage(player);
        }

        if (!drawPlayer.getHand().isEmpty()) {
            Card card = view.promptDraw(drawPlayer);
            player.getHand().addCard(card);
            drawPlayer.getHand().removeCard(card);
        }

        // clean up any pairs formed from draw
        doDiscardStage(player);

        view.promptEndTurn(player);

        // set next player
        activePlayer = (activePlayer + 1) % players.size();
    }

    protected void doDiscardStage(Player player) {
        Optional<Card.Rank> rank = player.getHand().getFirstPair();
        while (rank.isPresent()) {
            List<Card> cards = view.promptDiscard(player);
            for (Card card : cards) {
                player.getHand().removeCard(card);
            }
            rank = player.getHand().getFirstPair();
        }
    }

    @Override
    public void runGame() {
        while (!isGameOver()) {
            playRound();
        }
        view.displayInfo(getLoser().getName() + " is an Old Maid!");
        // back to start
        startGame();
    }

    protected boolean isGameOver() {
        int loser = -1;

        for (int i = 0; i < players.size(); i++) {
            if (!players.get(i).getHand().isEmpty()) {
                if (loser != -1) return false;
                loser = i;
            }
        }

        if (loser == -1) return false;

        Hand lastHand = players.get(loser).getHand();

        return lastHand.getHandSize() == 1 && lastHand.contains(Card.Rank.QUEEN);
    }

    protected Player getLoser() {
        // precondition of isGameOver
        assert isGameOver();

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (!player.getHand().isEmpty()) {
                return player;
            }
        }

        return null;
    }
}
