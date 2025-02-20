package com.github.zlamb1.view.animation;

import javax.swing.*;

public abstract class ComponentAnimation implements IComponentAnimation {
    protected JComponent component;
    protected AnimationTween animationTween;

    public ComponentAnimation(JComponent component, double value, double target) {
        this.component = component;
        animationTween = new AnimationTween(value, target);
        animationTween.addAnimationValueListener(this);
    }

    @Override
    public double getValue() {
        return animationTween.getValue();
    }

    @Override
    public void onValueChange(AnimationTween animation, double oldValue, double newValue) {
        if (isFinished()) {
            animationTween.cancel();
            return;
        }

        component.repaint();
    }

    @Override
    public AnimationTween getTween() {
        return animationTween;
    }
}
