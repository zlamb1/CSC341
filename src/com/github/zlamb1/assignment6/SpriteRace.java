package com.github.zlamb1.assignment6;

import com.github.zlamb1.assignment5.IRace;
import com.github.zlamb1.assignment5.IRaceView;
import com.github.zlamb1.assignment5.racer.IRacer;

import java.util.ArrayList;
import java.util.List;

public class SpriteRace implements IRace {
    protected IRaceView view;
    protected List<IRacer> racers = new ArrayList<>();
    protected int raceDistance;

    public SpriteRace(IRaceView view, int raceDistance) {
        this.view = view;
        this.raceDistance = raceDistance;
    }

    @Override
    public List<IRacer> getRacers() {
        return racers;
    }

    @Override
    public void addRacer(IRacer racer) {
        assert racer instanceof IDrawableRacer;
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
            view.drawRoundStart(this);
            for (IRacer racer : racers) {
                racer.tick();
                view.drawRacer(this, racer);
            }
            view.drawRoundEnd(this);
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
