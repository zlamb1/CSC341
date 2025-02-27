package com.github.zlamb1.assignment5;

import com.github.zlamb1.assignment5.racer.IRacer;

public class ConsoleRaceView implements IRaceView {
    private int maxNameLength;

    @Override
    public void drawEmptyRace(IRace race) {
        System.out.println("There are no racers!");
    }

    @Override
    public void drawRoundStart(IRace race) {
        maxNameLength = 0;
        for (IRacer racer : race.getRacers()) {
            maxNameLength = Math.max(maxNameLength, racer.getName().length());
        }
    }

    @Override
    public void drawRoundEnd(IRace race) {
        System.out.println();
    }

    @Override
    public void drawWinner(IRace race) {
        System.out.println(race.getWinner().getName() + " wins!");
    }

    @Override
    public void drawRacer(IRace race, IRacer racer) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%" + -(maxNameLength + 3) + "s", racer.getName()));
        sb.append("[");

        for (int i = 0; i <= race.getRaceDistance(); i++) {
            sb.append("-");
            if (i == racer.getPosition()) {
                sb.append("\033[38;5;238m");
            }
        }

        sb.append("\033[0m]");
        System.out.println(sb);
    }
}
