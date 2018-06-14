package com.drpicox.fishingLagoon.business.rounds;

import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.business.bots.BotId;

import java.util.List;

public class RoundCommand {
    private List<Action> actions;

    public RoundCommand(List<Action> actions) {
        this.actions = actions;
    }

    public RoundCommand() {
    }

    public Action getAction(int weekIndex) {
        return actions.get(weekIndex);
    }

    public List<Action> getActions() {
        return actions;
    }

    public void commandBot(BotId bot, List<Action> actions, int weekCount) {
        if (actions.size() != weekCount) throw new IllegalArgumentException("Actions length must match weekCount, it has " + actions.size() + " actions but there are " + weekCount + " weeks");

        this.actions = actions;
    }

    @Override
    public String toString() {
        return "RoundCommand{" +
                "actions=" + actions +
                '}';
    }
}
