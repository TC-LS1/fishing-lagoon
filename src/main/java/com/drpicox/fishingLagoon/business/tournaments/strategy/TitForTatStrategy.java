package com.drpicox.fishingLagoon.business.tournaments.strategy;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.actions.FishAction;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.Lagoon;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.LagoonHistory;
import com.drpicox.fishingLagoon.business.tournaments.lagoon.LagoonRound;

import java.util.*;

import static com.drpicox.fishingLagoon.common.actions.Actions.fish;
import static com.drpicox.fishingLagoon.common.actions.Actions.rest;

public class TitForTatStrategy extends Strategy {

    private Set<BotId> traitors = new HashSet<>();

    @Override
    public int seat(BotId botId, LagoonRound round) {
        int lagoonIndexWithoutTraitors = getLagoonIndexWithoutTraitors(botId, round);
        if (lagoonIndexWithoutTraitors == -1) {
            // there are traitors everywhere
            return getRandomSeatIndex(round);
        }
        return lagoonIndexWithoutTraitors;
    }

    private int getLagoonIndexWithoutTraitors(BotId botId, LagoonRound round) {
        int lagoonIndexWithoutTraitors = -1;
        for (int lagoonIndex = 0; lagoonIndex < round.getLagoonCount(); lagoonIndex++) {
            Lagoon lagoon = round.getLagoonHistory(lagoonIndex).getInitialLagoon();
            List<BotId> traitors = getLagoonTraitors(botId, lagoon);

            if (traitors.isEmpty()) {
                lagoonIndexWithoutTraitors = lagoonIndex;
            }
        }
        return lagoonIndexWithoutTraitors;
    }

    private int getRandomSeatIndex(LagoonRound round) {
        return new Random().nextInt(round.getLagoonCount());
    }

    private List<BotId> getLagoonTraitors(BotId botId, Lagoon lagoon) {
        List<BotId> lagoonBots = lagoon.getBots();
        List<BotId> lagoonTraitors = new ArrayList<>(lagoonBots);
        lagoonTraitors.retainAll(traitors);
        lagoonTraitors.remove(botId);

        return lagoonTraitors;
    }


    @Override
    public Action[] getOrders(BotId myBotId, LagoonRound round) {
        List<BotId> myLagoonTraitors = getMyLagoonTraitors(myBotId, round);
        boolean thereAreTraitors = !myLagoonTraitors.isEmpty();

        if (thereAreTraitors) return getTraitorOrders(myBotId, round, myLagoonTraitors);
        else return getCollaborativeOrders(myBotId, round);
    }

    private Action[] getTraitorOrders(BotId myBotId, LagoonRound round, List<BotId> myLagoonTraitors) {
        List<Action> actions = new ArrayList<>();

        LagoonHistory myImaginaryHistory = round.getLagoonHistoryOf(myBotId);
        for (int weekIndex = 0; weekIndex < round.getWeekCount(); weekIndex++) {
            Lagoon lagoon = myImaginaryHistory.getLagoonAt(weekIndex);

            long traitorFishes = getTraitorFishesFor(lagoon);

            Action action = fish(traitorFishes);
            actions.add(action);

            myImaginaryHistory = myImaginaryHistory.putAction(myBotId, weekIndex, action);
            for (BotId traitor: myLagoonTraitors) {
                myImaginaryHistory = myImaginaryHistory.putAction(myBotId, weekIndex, action);
            }
        }

        return toArray(actions);
    }

    private Action[] getCollaborativeOrders(BotId myBotId, LagoonRound round) {
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

    private List<BotId> getMyLagoonTraitors(BotId botId, LagoonRound round) {
        Lagoon myLagoon = round.getStartingLagoonOf(botId);
        return getLagoonTraitors(botId, myLagoon);
    }

    private long getTraitorFishesFor(Lagoon lagoon) {
        long availableFish = lagoon.getLagoonFishCount();
        double botCount = lagoon.getBots().size();
        return (long) Math.ceil(availableFish / botCount);
    }

    private Action[] toArray(List<Action> actions) {
        return actions.toArray(new Action[actions.size()]);
    }



    @Override
    public void learnFromRound(BotId myBotId, LagoonRound lagoonRound) {
        List<BotId> competitors = lagoonRound.getCompetitors();
        competitors.remove(myBotId);

        for (BotId competitorId: competitors) {
            boolean isTraitor = isTraitor(lagoonRound, competitorId);
            if (isTraitor) {
                traitors.add(competitorId);
            }
        }
    }

    private boolean isTraitor(LagoonRound lagoonRound, BotId competitorId) {
        LagoonHistory history = lagoonRound.getLagoonHistoryOf(competitorId);
        Lagoon lagoon = history.getLagoonAt(0);

        long traitorFishes = getTraitorFishesFor(lagoon);
        long fished = getFishedByAt(competitorId, lagoon);

        return fished >= traitorFishes;
    }

    private long getFishedByAt(BotId botId, Lagoon lagoon) {
        long fished = 0;
        Action action = lagoon.getActionOf(botId);
        if (action instanceof FishAction) {
            fished = ((FishAction) action).getFishingCount();
        }
        return fished;
    }
}
