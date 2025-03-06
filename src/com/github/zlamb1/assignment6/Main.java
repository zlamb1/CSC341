package com.github.zlamb1.assignment6;

import com.github.zlamb1.assignment5.IRace;
import com.github.zlamb1.assignment5.IRaceView;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        IRaceView view = new SwingRaceView();

        IRace race = new SpriteRace(view, 25);

        com.github.zlamb1.assignment6.RaceApp raceApp = new com.github.zlamb1.assignment6.RaceApp(race);
        raceApp.startRace();
    }
}
