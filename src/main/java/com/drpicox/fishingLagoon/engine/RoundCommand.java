package com.drpicox.fishingLagoon.engine;

import com.drpicox.fishingLagoon.actions.Action;

import java.util.List;

public class RoundCommand {
    private List<Action> actions;

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Action getAction(int weekIndex) {
        return actions.get(weekIndex);
    }

    public List<Action> getActions() {
        return actions;
    }
}
