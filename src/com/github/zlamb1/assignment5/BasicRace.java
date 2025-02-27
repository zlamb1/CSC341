package com.github.zlamb1.assignment5;

import com.github.zlamb1.assignment5.racer.IRacer;

import java.util.ArrayList;
import java.util.List;

public class BasicRace implements IRace {
    protected IRaceView view;

    protected List<IRacer> racers = new ArrayList<>();
    protected int raceDistance;

    protected int maxNameLength;

    public BasicRace(IRaceView view, int raceDistance) {
        this.view = view;
        this.raceDistance = raceDistance;
    }

    @Override
    public List<IRacer> getRacers() {
        return racers;
    }

    @Override
    public void addRacer(IRacer racer) {
        racers.add(racer);
    }

    @Override
    public boolean removeRacer(IRacer racer) {
        return racers.remove(racer);
    }

    @Override
    public int getRaceDistance() {
        return raceDistance;
    }

    @Override
    public void setRaceDistance(int raceDistance) {
        this.raceDistance = raceDistance;
    }

    @Override
    public void startRace() {
        if (racers.isEmpty()) {
            view.drawEmptyRace(this);
            return;
        }

        while (getWinner() == null) {
            view.drawRace(this);
        }

        view.drawWinner(this);
    }

    @Override
    public IRacer getWinner() {
        for (IRacer racer : racers) {
            if (racer.getPosition() >= raceDistance) {
                return racer;
            }
        }

        return null;
    }
}
