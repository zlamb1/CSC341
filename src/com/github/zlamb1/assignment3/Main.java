package com.github.zlamb1.assignment3;

import com.github.zlamb1.view.swing.LookAndFeel;

public class Main {
    public static void main(String[] args) {
        LookAndFeel.setSystemLookAndFeel();
        new CanvasView();
    }
}
