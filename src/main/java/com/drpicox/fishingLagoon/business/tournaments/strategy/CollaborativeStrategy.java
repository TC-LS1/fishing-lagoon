package com.drpicox.fishingLagoon.business.tournaments.strategy;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.Lagoon;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.LagoonHistory;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.LagoonRound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.drpicox.fishingLagoon.common.actions.Actions.fish;
import static com.drpicox.fishingLagoon.common.actions.Actions.rest;

public class CollaborativeStrategy extends Strategy {

    @Override
    public int seat(BotId botId, LagoonRound round) {
        return new Random().nextInt(round.getLagoonCount());
    }

    public Action[] getOrders(BotId myBotId, LagoonRound round) {
        List<Action> actions = new ArrayList<>();

        LagoonHistory myImaginaryHistory = round.getLagoonHistoryOf(myBotId);
        int weekCount = round.getWeekCount();
        int lastWeekIndex = weekCount - 1;
        for (int weekIndex = 0; weekIndex < lastWeekIndex; weekIndex++) {
            actions.add(rest());
        }

        Lagoon lastWeekLagoon = myImaginaryHistory.getLagoonAt(lastWeekIndex);
        long lagoonFishCount = lastWeekLagoon.getLagoonFishCount();
        int botCount = myImaginaryHistory.getBots().size();
        actions.add(fish(lagoonFishCount / botCount));

        return toArray(actions);
    }

    private Action[] toArray(List<Action> actions) {
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void learnFromRound(BotId botId, LagoonRound lagoonRound) {

    }
}
