package com.github.zlamb1.view.listener;

import java.awt.*;

public interface IColorListener extends IValueListener<Color> {
    @Override
    default void onValueChange(Color oldColor, Color newColor) {
        onColorChange(oldColor, newColor);
    }

    void onColorChange(Color oldColor, Color newColor);
}
