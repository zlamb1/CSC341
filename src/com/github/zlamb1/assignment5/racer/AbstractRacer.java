package com.github.zlamb1.assignment5.racer;

public class AbstractRacer implements IRacer {
    protected int delay = 100;
    protected volatile double position = 0;
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
    public synchronized double getPosition() {
        return position;
    }

    @Override
    public synchronized void setPosition(double position) {
        this.position = position;
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
    public double getSpeed() {
        if (advance) {
            return speed;
        }
        return 0;
    }

    @Override
    public int getDelay() {
        return delay;
    }

    @Override
    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public void delay() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException exc) {
            throw new AssertionError(exc);
        }
    }

    @Override
    public void beforeTick() {}

    @Override
    public void tick() {
        beforeTick();
        delay();
        setPosition(position + getSpeed());
        afterTick();
    }

    @Override
    public void afterTick() {}

    protected void randomizeSpeed(double lowerBound, double upperBound) {
        speed = Math.random() * (upperBound - lowerBound) + lowerBound;
    }
}
