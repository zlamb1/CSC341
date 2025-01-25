package com.github.zlamb1.view;

public abstract class AbstractView implements IView {
    public boolean promptBoolean(String prompt) {
        return Boolean.parseBoolean(promptString(prompt));
    }

    public int promptInt(String prompt) {
        return Integer.parseInt(promptString(prompt));
    }

    public float promptFloat(String prompt) {
        return Float.parseFloat(promptString(prompt));
    }

    public double promptDouble(String prompt) {
        return Double.parseDouble(promptString(prompt));
    }

    public void disposeView() {

    }
}
