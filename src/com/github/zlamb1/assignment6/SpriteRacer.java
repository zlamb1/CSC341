package com.github.zlamb1.assignment6;

import com.github.zlamb1.assignment5.racer.IRacer;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpriteRacer implements IDrawableRacer {
    protected IRacer racer;
    protected Sprite sprite;

    public SpriteRacer(IRacer racer, Sprite sprite) {
        this.racer = racer;
        this.sprite = sprite;

        setDelay(300);
    }

    @Override
    public synchronized double getPosition() {
        return racer.getPosition();
    }

    @Override
    public synchronized void setPosition(double newPosition) {
        racer.setPosition(newPosition);
    }

    @Override
    public String getName() {
        return racer.getName();
    }

    @Override
    public void setName(String name) {
        racer.setName(name);
    }

    @Override
    public double getSpeed() {
        return racer.getSpeed();
    }

    @Override
    public int getDelay() {
        return racer.getDelay();
    }

    @Override
    public void setDelay(int delay) {
        racer.setDelay(delay);
    }

    @Override
    public void delay() {
        racer.delay();
    }

    @Override
    public void beforeTick() {
        racer.beforeTick();
    }

    @Override
    public void tick() {
        beforeTick();

        double originalPosition = racer.getPosition();
        double endPosition = originalPosition + racer.getSpeed();

        int totalDelay = racer.getDelay();
        AtomicInteger elapsedDelay = new AtomicInteger();
        int period = 10;

        try (ScheduledExecutorService executor = Executors.newScheduledThreadPool(1)) {
            ScheduledFuture<?> future = executor.scheduleAtFixedRate(() -> {
                int currentElapsedDelay = elapsedDelay.addAndGet(period);
                int remainingDelay = totalDelay - currentElapsedDelay;

                if (remainingDelay > 0) {
                    double dx = (endPosition - racer.getPosition()) / ((double) remainingDelay / period);
                    racer.setPosition(racer.getPosition() + dx);
                }
            }, 0, period, TimeUnit.MILLISECONDS);

            delay();
            future.cancel(true);
        }

        // update position to correct value in case something went wrong
        racer.setPosition(endPosition);

        afterTick();
    }

    @Override
    public void afterTick() {
        racer.afterTick();
    }

    @Override
    public void drawRacer(final Graphics graphics, int x, int y) {
        sprite.drawSprite(graphics, x, y);
    }
}
