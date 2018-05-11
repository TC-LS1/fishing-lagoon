package com.drpicox.fishingLagoon.common.actions;

import java.util.ArrayList;
import java.util.List;

public class ActionParser {

    public List<Action> parse(String actionsText) {
        var actions = new ArrayList<Action>();

        String[] lines = actionsText.split(",");
        for (String line: lines) {
            line = line.trim();
            var action = parseAction(line);
            if (action != null) actions.add(action);
        }

        return actions;
    }

    public Action parseAction(String actionText) {
        String[] parts = actionText.split(" +");
        switch (parts[0]) {
            case "fish":
                var fishValue = Long.parseLong(parts[1]);
                return new FishAction(fishValue);
            case "rest":
                return new RestAction();
            default:
                return null;
        }
    }

    public String toString(List<Action> actions) {
        StringBuilder sb = new StringBuilder();
        String comma = "";
        for (var action: actions) {
            sb.append(comma).append(action.toString());
            comma = ",";
        }
        return sb.toString();
    }
}
