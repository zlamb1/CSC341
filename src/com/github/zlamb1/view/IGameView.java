package com.github.zlamb1.view;

public interface IGameView extends IView {
    void getResult(String prompt);
    void display(String message);
    <T> T getInput();
}
