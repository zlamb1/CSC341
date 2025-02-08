package com.github.zlamb1.assignment3.view;

public enum DrawMode {
    LINE,
    TRIANGLE,
    RIGHT_TRIANGLE,
    CIRCLE,
    ELLIPSE,
    SQUARE,
    RECTANGLE,
    PENTAGON(5),
    HEXAGON(6),
    HEPTAGON(7),
    OCTAGON(8),
    NONAGON(9),
    DECAGON(10);

    private final boolean arbitraryPolygon;
    private final int nSides;

    DrawMode() {
        arbitraryPolygon = false;
        nSides = 0;
    }

    DrawMode(int nSides) {
        arbitraryPolygon = true;
        this.nSides = nSides;
    }

    public boolean isArbitraryPolygon() {
        return arbitraryPolygon;
    }

    public int getNSides() {
        return nSides;
    }
}
