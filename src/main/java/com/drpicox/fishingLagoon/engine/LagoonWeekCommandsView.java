package com.drpicox.fishingLagoon.engine;

import com.drpicox.fishingLagoon.actions.Action;
import com.drpicox.fishingLagoon.bots.BotId;

import java.util.HashSet;
import java.util.Set;

public class LagoonWeekCommandsView {
    private Set<BotId> lagoonBots;
    private int weekIndex;
    private RoundEngine roundEngine;

    public LagoonWeekCommandsView(Set<BotId> lagoonBots, int weekIndex, RoundEngine roundEngine) {
        this.lagoonBots = lagoonBots;
        this.weekIndex = weekIndex;
        this.roundEngine = roundEngine;
    }

    public Set<BotId> getBots() {
        return new HashSet<>(lagoonBots);
    }

    public Action getAction(BotId bot) {
        return roundEngine.getAction(bot, weekIndex);
    }
}
