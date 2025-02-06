package com.github.zlamb1.card;

public abstract class Player {
    protected Hand hand;
    protected String name;

    public Player() {
        this(new Hand());
    }

    public Player(Hand hand) {
        this.hand = hand;
    }

    public Hand getHand() {
        return hand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
