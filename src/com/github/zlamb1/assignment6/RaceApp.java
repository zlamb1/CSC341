package com.github.zlamb1.assignment6;

import com.github.zlamb1.assignment5.IRace;
import com.github.zlamb1.assignment5.racer.HareRacer;
import com.github.zlamb1.assignment5.racer.IRacer;
import com.github.zlamb1.assignment5.racer.TurtleRacer;

import java.awt.*;

public class RaceApp {
    protected IRace race;

    public RaceApp(IRace race) {
        this.race = race;

        for (int i = 0; i < 3; i++) {
            IRacer turtleRacer = new TurtleRacer();
            turtleRacer.setName("Turtle #" + (i + 1));
            Sprite sprite = SpriteFactory.createSprite(
                    "com/github/zlamb1/assignment6/images/turtle.gif", new Dimension(75, 50)
            );
            race.addRacer(new SpriteRacer(turtleRacer, sprite, false));
        }

        for (int i = 0; i < 3; i++) {
            HareRacer hareRacer = new HareRacer(race.getRaceDistance());
            hareRacer.setName("Hare #" + (i + 1));
            hareRacer.setCurveDescentRate(1.05);
            hareRacer.setMaximumMultiplier(1.75);
            Sprite sprite = SpriteFactory.createSprite(
                "com/github/zlamb1/assignment6/images/bunny.gif", new Dimension(50, 50)
            );
            race.addRacer(new SpriteRacer(hareRacer, sprite, true));
        }
    }

    public void startRace() {
        race.startRace();
    }
}
