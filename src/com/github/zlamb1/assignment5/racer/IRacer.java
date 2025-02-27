package com.github.zlamb1.assignment5.racer;

public interface IRacer {
    int getPosition();
    String getName();

    void setName(String name);

    void tick();
}
