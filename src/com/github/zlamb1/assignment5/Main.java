package com.github.zlamb1.assignment5;

public class Main {
    public static void main(String[] args) {
        IRace race = new BasicRace(25);
        RaceApp raceApp = new RaceApp(race);
        raceApp.startRace();
    }
}
