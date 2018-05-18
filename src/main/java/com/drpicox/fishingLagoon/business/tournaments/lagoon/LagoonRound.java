package com.drpicox.fishingLagoon.business.tournaments.lagoon;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LagoonRound {
    private int weekCount;
    private List<Lagoon> lagoons;
    private Map<BotId, Action[]> botActions;
    private LagoonHistory[] lagoonHistoryCache;

    public LagoonRound(int weekCount) {
        this(weekCount, new ArrayList<>(), new HashMap<>());
    }

    private LagoonRound(int weekCount, List<Lagoon> lagoons, Map<BotId, Action[]> botActions) {
        this.weekCount = weekCount;
        this.lagoons = lagoons;
        this.botActions = botActions;
    }

    public LagoonRound addCompetitors(BotId... additionalCompetitors) {
        LagoonRound result = copy();
        result.botActions = new HashMap<>(botActions);
        for (BotId botId : additionalCompetitors) {
            result.botActions.put(botId, new Action[]{});
        }
        return result;
    }

    public LagoonRound addLagoons(Lagoon... additionalLagoons) {
        LagoonRound result = copy();
        result.lagoons = new LinkedList<>(lagoons);
        result.lagoons.addAll(Arrays.asList(additionalLagoons));
        return result;
    }

    public LagoonRound addLagoons(int times, Lagoon lagoon) {
        LagoonRound result = copy();
        result.lagoons = new LinkedList<>(lagoons);
        for (int time = 0; time < times; time++) {
            result.lagoons.add(lagoon);
        }
        return result;
    }

    public LagoonRound clearCompetitors() {
        LagoonRound result = copy();
        result.botActions = new HashMap<>();
        return result;
    }

    public List<BotId> getCompetitors() {
        return new ArrayList<>(botActions.keySet());
    }

    public Lagoon getStartingLagoon(int lagoonIndex) {
        return lagoons.get(lagoonIndex);
    }

    public int getLagoonCount() {
        return lagoons.size();
    }

    public LagoonHistory getLagoonHistory(int lagoonIndex) {
        if (lagoonHistoryCache == null) {
            lagoonHistoryCache = new LagoonHistory[lagoons.size()];
        }

        if (lagoonIndex < 0 || lagoonHistoryCache.length <= lagoonIndex) {
            return null;
        }

        LagoonHistory result = lagoonHistoryCache[lagoonIndex];
        if (result == null) {
            result = computeLagoonHistory(getStartingLagoon(lagoonIndex));
            lagoonHistoryCache[lagoonIndex] = result;
        }
        return result;
    }

    public LagoonHistory getLagoonHistoryOf(BotId botId) {
        Lagoon lagoon = getStartingLagoonOf(botId);
        int lagoonIndex = lagoons.indexOf(lagoon);
        return getLagoonHistory(lagoonIndex);
    }

    public List<LagoonHistory> getLagoonHistories() {
        return IntStream.range(0, getLagoonCount())
                .mapToObj(lagoonIndex -> getLagoonHistory(lagoonIndex))
                .collect(Collectors.toList());
    }

    public int getLagoonIndexOf(BotId botId) {
        for (int lagoonIndex = 0; lagoonIndex < lagoons.size(); lagoonIndex++) {
            Lagoon lagoon = lagoons.get(lagoonIndex);
            if (lagoon.hasBot(botId)) {
                return lagoonIndex;
            }
        }
        return -1;
    }

    public Lagoon getStartingLagoonOf(BotId botId) {
        int lagoonIndex = getLagoonIndexOf(botId);
        if (lagoonIndex < 0) return null;
        return lagoons.get(lagoonIndex);
    }

    public long getScoreOf(BotId botId) {
        LagoonHistory history = getLagoonHistoryOf(botId);
        if (history == null) return 0;
        return history.getFishCountOf(botId);
    }

    public int getWeekCount() {
        return weekCount;
    }

    public LagoonRound seatBotAt(BotId botId, int lagoonIndex) {
        if (!botActions.containsKey(botId)) return this;

        LagoonRound result = copy();
        result.lagoons = lagoons.stream().map(l -> l.removeBot(botId)).collect(Collectors.toList());


        Lagoon lagoon = getStartingLagoon(lagoonIndex).addBot(botId);
        result.lagoons.set(lagoonIndex, lagoon);
        return result;
    }

    public LagoonRound putBotActions(BotId botId, Action... replaceActions) {
        if (!botActions.containsKey(botId)) return this;

        LagoonRound result = copy();
        result.botActions = new HashMap<>(botActions);
        result.botActions.put(botId, replaceActions);
        return result;
    }

    private LagoonRound copy() {
        return new LagoonRound(weekCount, lagoons, botActions);
    }

    private LagoonHistory computeLagoonHistory(Lagoon startingLagoon) {
        LagoonHistory history = new LagoonHistory(weekCount, startingLagoon);
        for (BotId botId: botActions.keySet()) {
            Action[] actions = botActions.get(botId);
            history = history.putActions(botId, actions);
        }
        return history;
    }

    @Override
    public String toString() {
        return "LagoonRound{" +
                "weekCount=" + weekCount +
                ", lagoons=" + lagoons +
                ", botActions=" + botActions +
                '}';
    }
}
