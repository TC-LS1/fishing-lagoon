package com.drpicox.fishingLagoon.business.tournaments.strategy;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.LagoonRound;

public abstract class Strategy {

    public abstract int seat(BotId botId, LagoonRound round);
    public abstract Action[] getOrders(BotId botId, LagoonRound round);
    public abstract void learnFromRound(BotId botId, LagoonRound lagoonRound);

}
