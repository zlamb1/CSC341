package com.github.zlamb1.view.swing;

import javax.swing.*;

public class LookAndFeel {
    public static boolean setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean setLookAndFeel(String expectedName) {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getName().equals(expectedName)) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                    return true;
                } catch (Exception exc) {
                    return false;
                }
            }
        }
        return false;
    }
}
