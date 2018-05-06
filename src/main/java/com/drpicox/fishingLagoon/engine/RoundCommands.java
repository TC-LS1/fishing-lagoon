package com.drpicox.fishingLagoon.engine;

import com.drpicox.fishingLagoon.actions.Action;
import com.drpicox.fishingLagoon.actions.RestAction;
import com.drpicox.fishingLagoon.bots.BotId;

import java.util.*;

public class RoundCommands {

    private Map<BotId, RoundCommand> commands = new HashMap<>();
    private Map<BotId, List<Action>> botCommands = new HashMap<>();

    public RoundCommands() {
    }

    RoundCommands(RoundCommands sample) {
        commands.putAll(sample.commands);
        botCommands.putAll(sample.botCommands);
    }

    public Map<BotId, RoundCommand> getCommands() {
        return commands;
    }

    public Set<BotId> getBots() {
        return new HashSet<>(commands.keySet());
    }

    public Object getBotsCount() {
        return commands.size();
    }

    public List<Action> getActions(BotId botId) {
        return commands.get(botId).getActions();
    }

    public Action getAction(BotId botId, int weekIndex) {
        if (!commands.containsKey(botId)) return RestAction.DEFAULT;
        return commands.get(botId).getAction(weekIndex);
    }

    public LagoonWeekCommandsView getLagoonWeekView(Integer lagoonIndex, RoundSeats seats, int weekIndex) {
        return new LagoonWeekCommandsView(seats.getLagoonBots(lagoonIndex), weekIndex, this);
    }

    boolean commandBot(BotId botId, List<Action> actions) {
        forceCommandBot(botId, actions);
        return true;
    }

    private void ensureBot(BotId botId) {
        if (!commands.containsKey(botId)) {
            commands.put(botId, new RoundCommand());
        }
    }

    public void forceCommandBot(BotId botId, List<Action> actions) {
        ensureBot(botId);
        commands.get(botId).setActions(actions);
        botCommands.put(botId, new ArrayList<>(actions));
    }
}
