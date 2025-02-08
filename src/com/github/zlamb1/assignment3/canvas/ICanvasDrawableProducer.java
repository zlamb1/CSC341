package com.github.zlamb1.assignment3.canvas;

public interface ICanvasDrawableProducer<T extends ICanvasDrawable> {
    T buildDrawable(ICanvasDrawableFactory drawableFactory);
}
