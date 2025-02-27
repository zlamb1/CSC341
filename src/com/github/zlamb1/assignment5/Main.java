package com.github.zlamb1.assignment5;

public class Main {
    public static void main(String[] args) {
        IRaceView view = new ConsoleRaceView();

        IRace race = new BasicRace(view, 25);
        RaceApp raceApp = new RaceApp(race);
        raceApp.startRace();
    }
}
