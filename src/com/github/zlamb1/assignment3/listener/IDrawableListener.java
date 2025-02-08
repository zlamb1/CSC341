package com.github.zlamb1.assignment3.listener;

import com.github.zlamb1.assignment3.canvas.ICanvasDrawable;

public interface IDrawableListener {
    void onAddDrawable(ICanvasDrawable canvasDrawable);
    void onRemoveDrawable(ICanvasDrawable canvasDrawable);
}
