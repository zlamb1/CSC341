package com.github.zlamb1.assignment5;

import com.github.zlamb1.assignment5.racer.HareRacer;
import com.github.zlamb1.assignment5.racer.IRacer;
import com.github.zlamb1.assignment5.racer.TurtleRacer;

public class RaceApp {
    protected IRace race;

    public RaceApp(IRace race) {
        this.race = race;

        for (int i = 0; i < 3; i++) {
            IRacer turtleRacer = new TurtleRacer();
            turtleRacer.setName("Turtle #" + (i + 1));
            race.addRacer(turtleRacer);
        }

        for (int i = 0; i < 3; i++) {
            IRacer hareRacer = new HareRacer(race.getRaceDistance());
            hareRacer.setName("Hare #" + (i + 1));
            race.addRacer(hareRacer);
        }
    }

    public void startRace() {
        race.startRace();
    }
}
