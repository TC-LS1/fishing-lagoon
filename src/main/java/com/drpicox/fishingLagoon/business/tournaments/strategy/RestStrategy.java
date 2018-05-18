package com.drpicox.fishingLagoon.business.tournaments.strategy;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.LagoonRound;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import static com.drpicox.fishingLagoon.common.actions.Actions.fish;

public class RestStrategy extends Strategy {

    @Override
    public int seat(BotId botId, LagoonRound round) {
        return new Random().nextInt(round.getLagoonCount());
    }

    @Override
    public Action[] getOrders(BotId botId, LagoonRound round) {
        List<Action> actions = new ArrayList<>();

        for (int weekIndex = 0; weekIndex < round.getWeekCount(); weekIndex++) {
            actions.add(fish(weekIndex * weekIndex));
        }

        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void learnFromRound(BotId botId, LagoonRound lagoonRound) {
    }
}
