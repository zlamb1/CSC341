package com.github.zlamb1.assignment4;

import com.github.zlamb1.assignment4.view.CrazyEightsView;
import com.github.zlamb1.view.swing.LookAndFeel;

public class Main {
    public static void main(String[] args) {
        LookAndFeel.setSystemLookAndFeel();
        ICrazyEightsView view = new CrazyEightsView();
        new CrazyEightsApp(view);
    }
}
