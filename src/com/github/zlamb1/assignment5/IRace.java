package com.github.zlamb1.assignment5;

import com.github.zlamb1.assignment5.racer.IRacer;

import java.util.List;

public interface IRace {
    List<IRacer> getRacers();
    void addRacer(IRacer racer);
    boolean removeRacer(IRacer racer);

    int getRaceDistance();
    void setRaceDistance(int raceDistance);

    void startRace();
    IRacer getWinner();
}
