package com.drpicox.fishingLagoon.business.rounds;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.actions.RestAction;

import java.util.*;

public class RoundCommands {

    private Map<BotId, RoundCommand> commands;

    public RoundCommands() {
        commands = new HashMap<>();
    }

    public RoundCommands(HashMap<BotId,RoundCommand> commands) {
        this.commands = commands;
    }

    public Action getAction(BotId bot, int weekIndex) {
        if (!commands.containsKey(bot)) return RestAction.NOOP;
        return commands.get(bot).getAction(weekIndex);
    }

    public void commandBot(BotId bot, List<Action> actions, int weekCount) {
        var command = commands.get(bot);
        if (command == null) {
            command = new RoundCommand();
            commands.put(bot, command);
        }

        command.commandBot(bot, actions, weekCount);
    }

    public RoundCommand get(BotId bot) {
        return commands.get(bot);
    }

    public Set<BotId> getBots() {
        return commands.keySet();
    }

    @Override
    public String toString() {
        return "RoundCommands{" +
                "commands=" + commands +
                '}';
    }
}
