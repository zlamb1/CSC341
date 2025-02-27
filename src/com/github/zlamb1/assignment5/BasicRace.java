package com.github.zlamb1.assignment5;

import com.github.zlamb1.assignment5.racer.IRacer;

import java.util.ArrayList;
import java.util.List;

public class BasicRace implements IRace {
    protected List<IRacer> racers = new ArrayList<>();
    protected int raceDistance;

    protected int maxNameLength;

    public BasicRace(int raceDistance) {
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
            System.out.println("There are no racers!");
            return;
        }

        maxNameLength = 0;
        for (IRacer racer : racers) {
            maxNameLength = Math.max(maxNameLength, racer.getName().length());
        }

        IRacer winner;
        while ((winner = getWinner()) == null) {
            for (IRacer racer : racers) {
                racer.tick();
                drawRacer(racer);
            }
            System.out.println();
        }

        System.out.println(winner.getName() + " wins!");
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

    protected void drawRacer(IRacer racer) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%" + -(maxNameLength + 3) + "s", racer.getName()));
        sb.append("[");

        for (int i = 0; i <= raceDistance; i++) {
            sb.append("-");
            if (i == racer.getPosition()) {
                sb.append("\033[38;5;238m");
            }
        }

        sb.append("\033[0m]");
        System.out.println(sb);
    }
}
