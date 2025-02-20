package com.github.zlamb1.view.animation;

public interface IComponentAnimation extends AnimationTween.IAnimationValueListener {
    double getValue();
    boolean isFinished();
    AnimationTween getTween();
}
