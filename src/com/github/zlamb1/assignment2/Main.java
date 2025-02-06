package com.github.zlamb1.assignment2;

import com.github.zlamb1.assignment2.view.OldMaidView;
import com.github.zlamb1.card.ICardControl;

public class Main {
    public static void main(String[] args) {
        ICardControl app = new OldMaidApp(new OldMaidView());
        for (;;) {
            app.startGame();
        }
    }
}
