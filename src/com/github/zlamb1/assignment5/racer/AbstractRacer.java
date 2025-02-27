package com.github.zlamb1.assignment5.racer;

public class AbstractRacer implements IRacer {
    protected int DELAY_MS = 100;
    protected double position = 0;
    protected double speed = 0;
    protected boolean advance = true;

    protected String name;

    public AbstractRacer() {
        this(0.5, 1.0);
    }

    public AbstractRacer(double lowerBound, double upperBound) {
        randomizeSpeed(lowerBound, upperBound);
    }

    @Override
    public int getPosition() {
        return (int) Math.floor(position);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void tick() {
        try {
            Thread.sleep(DELAY_MS);
        } catch (InterruptedException exc) {
            throw new AssertionError(exc);
        }

        if (advance) {
            position += speed;
        }
    }

    protected void randomizeSpeed(double lowerBound, double upperBound) {
        speed = Math.random() * (upperBound - lowerBound) + lowerBound;
    }
}
