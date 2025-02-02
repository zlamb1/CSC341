package com.github.zlamb1.card;

public class Card {
    public enum Color {
        BLACK,
        RED
    }

    public enum Suit {
        JOKER, SPADES, CLUBS, HEARTS, DIAMONDS;

        @Override
        public String toString() {
            return switch (this) {
                case JOKER -> "Joker";
                case SPADES -> "Spades";
                case CLUBS -> "Clubs";
                case HEARTS -> "Hearts";
                case DIAMONDS -> "Diamonds";
            };
        }
    }

    public enum Rank {
        JOKER(0),
        ACE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13);

        private final int value;

        Rank(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public String getShorthand() {
            return switch (this) {
                case JOKER -> "JOKER";
                case ACE -> "A";
                case TWO -> "2";
                case THREE -> "3";
                case FOUR -> "4";
                case FIVE -> "5";
                case SIX -> "6";
                case SEVEN -> "7";
                case EIGHT -> "8";
                case NINE -> "9";
                case TEN -> "10";
                case JACK -> "J";
                case QUEEN -> "Q";
                case KING -> "K";
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case JOKER -> "Joker";
                case ACE -> "Ace";
                case TWO -> "Two";
                case THREE -> "Three";
                case FOUR -> "Four";
                case FIVE -> "Five";
                case SIX -> "Six";
                case SEVEN -> "Seven";
                case EIGHT -> "Eight";
                case NINE -> "Nine";
                case TEN -> "Ten";
                case JACK -> "Jack";
                case QUEEN -> "Queen";
                case KING -> "King";
            };
        }
    }

    private final Color color;
    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this(Color.BLACK, suit, rank);
    }

    public Card(Color color, Suit suit, Rank rank) {
        if (suit == Suit.JOKER && rank != Rank.JOKER) {
            throw new IllegalArgumentException("Card With Joker Suit Must Have A Joker Rank");
        } else if (rank == Rank.JOKER && suit != Suit.JOKER) {
            throw new IllegalArgumentException("Card With Joker Rank Must Have A Joker Suit");
        }

        this.color = color;
        this.suit = suit;
        this.rank = rank;
    }

    public Color getColor() {
        return color;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    @Override
    public String toString() {
        if (rank == Rank.JOKER) {
            return "Joker";
        }

        return rank.toString() + " of " + suit.toString();
    }
}
