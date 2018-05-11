package com.drpicox.fishingLagoon.business.scores;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.business.bots.BotId;

import java.util.HashSet;
import java.util.Set;

public class LagoonWeekRoundView {
    private int lagoonIndex;
    private int weekIndex;
    private Round round;

    public LagoonWeekRoundView(int lagoonIndex, int weekIndex, Round round) {
        this.lagoonIndex = lagoonIndex;
        this.weekIndex = weekIndex;
        this.round = round;
    }

    public Set<BotId> getBots() {
        return round.getLagoonBots(lagoonIndex);
    }

    public Action getAction(BotId bot) {
        return round.getAction(bot, weekIndex);
    }
}
