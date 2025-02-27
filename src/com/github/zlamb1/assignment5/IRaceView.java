package com.github.zlamb1.assignment5;

import com.github.zlamb1.assignment5.racer.IRacer;

public interface IRaceView {
    void drawEmptyRace(IRace race);
    void drawRoundStart(IRace race);
    void drawRacer(IRace race, IRacer racer);
    void drawRoundEnd(IRace race);
    void drawWinner(IRace race);
}
