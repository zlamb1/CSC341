package com.github.zlamb1.assignment2.view;

import com.github.zlamb1.assignment2.OldMaidSettings;
import com.github.zlamb1.card.Card;
import com.github.zlamb1.card.Hand;
import com.github.zlamb1.card.Player;
import com.github.zlamb1.view.IView;

import java.util.List;

public interface IOldMaidView extends IView {
    OldMaidSettings promptStart();
    List<Card> promptDiscard(Player player);
    Card promptDraw(Player drawPlayer);
    void promptEndTurn(Player player);
}
