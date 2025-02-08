package com.github.zlamb1.assignment3.view;

import com.github.zlamb1.assignment3.listener.IDrawModeListener;
import com.github.zlamb1.view.swing.ISwingComponent;

public interface IToolbar extends ISwingComponent {
    void setDrawMode(DrawMode drawMode);

    void addDrawModeListener(IDrawModeListener drawModeListener);
    boolean removeDrawModeListener(IDrawModeListener drawModeListener);
}
