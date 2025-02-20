package com.github.zlamb1.view.animation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationTween {
    protected double defaultValue;
    protected double value;

    protected double target;

    protected int timerDelay = 20;
    protected double animationDuration = 1.0;

    protected int totalFrames;
    protected AtomicInteger framesElapsed = new AtomicInteger(0);

    protected Timer timer;
    protected AnimationDirection animationDirection = AnimationDirection.FORWARD;

    protected boolean paused;

    protected List<IAnimationValueListener> animationValueListeners = new ArrayList<>();

    public enum AnimationDirection {
        FORWARD,
        REVERSE,
    }

    public interface IAnimationValueListener {
        void onValueChange(AnimationTween animation, double oldValue, double newValue);
    }

    public AnimationTween(double value, double target) {
        defaultValue = value;
        this.value = value;

        this.target = target;

        calculateAnimateMeta();
    }

    public double getValue() {
        return value;
    }

    public void start() {
        if (timer == null) {
            constructTimer();
        }
        if (!paused) {
            timer.start();
        }
    }

    public void pause() {
        this.paused = true;
        if (timer != null) {
            timer.stop();
        } else {
            constructTimer();
        }
    }

    public void unpause() {
        this.paused = false;
        if (timer != null) {
            timer.start();
        } else {
            constructTimer();
        }
    }

    public void reset() {
        if (timer != null) {
            timer.stop();
        }

        value = defaultValue;
        paused = false;

        framesElapsed.set(0);
        calculateAnimateMeta();
    }

    public void restart() {
        reset();
        timer.start();
    }

    public void cancel() {
        reset();
        timer = null;
    }

    public void reverse() {
        setAnimationDirection((animationDirection == AnimationDirection.FORWARD) ?
            AnimationDirection.REVERSE : AnimationDirection.FORWARD);
        framesElapsed.set(0);
        if (timer != null) {
            timer.start();
        } else {
            constructTimer();
            timer.start();
        }
    }

    public double getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(double animationDuration) {
        this.animationDuration = animationDuration;
    }

    public int getTimerDelay() {
        return timerDelay;
    }

    public void setTimerDelay(int timerDelay) {
        this.timerDelay = timerDelay;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isComplete() {
        return value == getTarget();
    }

    public AnimationDirection getAnimationDirection() {
        return animationDirection;
    }

    public void setAnimationDirection(AnimationDirection animationDirection) {
        this.animationDirection = animationDirection;
    }

    public void addAnimationValueListener(IAnimationValueListener animationValueListener) {
        animationValueListeners.add(animationValueListener);
    }

    public boolean removeAnimationValueListener(IAnimationValueListener animationValueListener) {
        return animationValueListeners.remove(animationValueListener);
    }

    public double getTarget() {
        return switch (animationDirection) {
            case FORWARD -> target;
            case REVERSE -> defaultValue;
        };
    }

    public void setTarget(double target) {
        this.target = target;
        if (!isComplete()) {
            framesElapsed.set(0);
            if (timer == null) {
                constructTimer();
                timer.start();
            } else if (!timer.isRunning()) {
                timer.start();
            }
        }
    }

    protected void calculateAnimateMeta() {
        totalFrames = (int) Math.ceil((animationDuration * 1000) / timerDelay);
    }

    protected void constructTimer() {
        timer = new Timer(timerDelay, (ActionEvent e) -> {
            if (!isPaused() && !isComplete()) {
                double dx = (getTarget() - value) / (totalFrames - framesElapsed.getAndIncrement());

                double oldValue = value;
                value += dx;

                for (IAnimationValueListener animationValueListener : animationValueListeners) {
                    animationValueListener.onValueChange(this, oldValue, value);
                }
            } else {
                timer.stop();
            }
        });
    }
}
