package com.github.zlamb1.assignment5.racer;

public interface IRacer {
    double getPosition();
    void setPosition(double newPosition);

    String getName();

    void setName(String name);

    double getSpeed();

    int getDelay();
    void setDelay(int delay);

    void delay();

    void beforeTick();
    void tick();
    void afterTick();
}
