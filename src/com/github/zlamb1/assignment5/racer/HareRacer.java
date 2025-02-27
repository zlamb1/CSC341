package com.github.zlamb1.assignment5.racer;

public class HareRacer extends AbstractRacer {
    protected int elapsedTicks = 0;
    protected int restTicks = 0;
    protected int raceDistance;

    protected static double lowerBound = 1.5, upperBound = 2.0;

    public HareRacer(int raceDistance) {
        this.raceDistance = raceDistance;
        randomizeSpeed(lowerBound, upperBound);
        randomizeRestTicks();
    }

    @Override
    public void tick() {
        elapsedTicks++;
        advance = restTicks == 0;

        super.tick();

        if (advance) {
            randomizeRestTicks();
            randomizeSpeed(lowerBound, upperBound);
        } else {
            restTicks--;
        }
    }

    @Override
    protected void randomizeSpeed(double lowerBound, double upperBound) {
        super.randomizeSpeed(lowerBound, upperBound);
        double curveDescentRate = 1.1;
        double maximumMultiplier = 2.0;
        double minimumSpeedMultiplier = 0.1;
        // this has the effect of making the hare faster near the start but slowing down significantly near the end
        double speedMultiplier = Math.pow((double) raceDistance - Math.min(elapsedTicks, raceDistance), curveDescentRate)
                / ((Math.pow(raceDistance, curveDescentRate) * (1.0 / maximumMultiplier)));
        speed *= Math.max(speedMultiplier, minimumSpeedMultiplier);
    }

    protected void randomizeRestTicks() {
        restTicks = (int) Math.floor(Math.random() * 3);
    }
}
