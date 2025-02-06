package com.github.zlamb1.view;

import java.util.List;

public interface IView {
    void displayInfo(String info);

    boolean promptBoolean(String prompt);
    String promptString(String prompt);
    int promptInt(String prompt);
    float promptFloat(String prompt);
    double promptDouble(String prompt);
    int promptChoice(List<String> choices);
    default void promptAlert(String prompt) {
        displayInfo(prompt);
    }

    void disposeView();
}
