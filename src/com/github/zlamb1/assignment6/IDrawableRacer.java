package com.github.zlamb1.assignment6;

import com.github.zlamb1.assignment5.racer.IRacer;

import java.awt.*;

public interface IDrawableRacer extends IRacer {
    void drawRacer(final Graphics g, int x, int y);
}
