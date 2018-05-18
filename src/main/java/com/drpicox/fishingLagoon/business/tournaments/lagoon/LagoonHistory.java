package com.drpicox.fishingLagoon.business.tournaments.lagoon;


import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;

import java.util.*;

public class LagoonHistory {

    private int weekCount;
    private List<Lagoon> lagoonsByWeek;
    private Map<String, BotWeekAction> botWeekActions;

    public LagoonHistory(int weekCount, Lagoon initialLagoon) {
        this(weekCount, new LinkedList<>(), new HashMap<>());

        lagoonsByWeek.add(initialLagoon);
    }

    private LagoonHistory(int weekCount, List<Lagoon> lagoonsByWeek, Map<String, BotWeekAction> botWeekActions) {
        this.weekCount = weekCount;
        this.lagoonsByWeek = lagoonsByWeek;
        this.botWeekActions = botWeekActions;
    }

    public Action getActionOfAt(BotId botId, int weekIndex) {
        return getLagoonAt(weekIndex).getActionOf(botId);
    }

    public List<Action> getActionsOf(BotId botId) {
        List<Action> result = new ArrayList<>();
        for (int weekIndex = 0; weekIndex < weekCount; weekIndex++) {
            Action action = getActionOfAt(botId, weekIndex);
            result.add(action);
        }
        return result;
    }

    public List<BotId> getBots() {
        return getInitialLagoon().getBots();
    }

    public Lagoon getInitialLagoon() {
        return getLagoonAt(0);
    }

    public Lagoon getLagoonAt(int weekIndex) {
        weekIndex = Math.min(weekCount, weekIndex);
        cacheMissingLagoonsUntil(weekIndex);
        return lagoonsByWeek.get(weekIndex);
    }

    public Lagoon getFinalLagoon() {
        return getLagoonAt(weekCount);
    }

    public long getFishCountOf(BotId botId) {
        return getFishCountOfAt(botId, weekCount);
    }

    public long getFishCountOfAt(BotId botId, int weekIndex) {
        return getLagoonAt(weekIndex).getFishCountOf(botId);
    }

    public List<Long> getFishCountsOf(BotId botId) {
        List<Long> result = new ArrayList<>();
        for (int weekIndex = 0; weekIndex <= weekCount; weekIndex++) {
            long fishCount = getFishCountOfAt(botId, weekIndex);
            result.add(fishCount);
        }
        return result;
    }

    public long getScoreOf(BotId botId) {
        return getFishCountOf(botId);
    }

    public int getWeekCount() {
        return weekCount;
    }

    public LagoonHistory putAction(BotId botId, int weekIndex, Action action) {
        if (weekIndex >= weekCount) return this;
        if (!getInitialLagoon().hasBot(botId)) return this;

        BotWeekAction bwa = new BotWeekAction(botId, weekIndex, action);

        LagoonHistory result = copy();
        result.botWeekActions = new HashMap<>(botWeekActions);
        result.botWeekActions.put(bwa.key, bwa);
        result.lagoonsByWeek = result.copyLagoonsByWeekUntil(weekIndex);
        return result;
    }

    public LagoonHistory putActions(BotId botId, Action... actions) {
        if (!getInitialLagoon().hasBot(botId)) return this;

        LagoonHistory result = copy();
        result.botWeekActions = new HashMap<>(botWeekActions);
        for (int weekIndex = 0; weekIndex < actions.length && weekIndex < weekCount; weekIndex++) {
            BotWeekAction bwa = new BotWeekAction(botId, weekIndex, actions[weekIndex]);
            result.botWeekActions.put(bwa.key, bwa);
        }
        result.botWeekActions.values().removeIf(bwa -> bwa.weekIndex >= actions.length && bwa.botId.equals(botId));
        result.lagoonsByWeek = copyLagoonsByWeekUntil(0);
        return result;
    }

    private void cacheMissingLagoonsUntil(int targetWeekIndex) {
        targetWeekIndex = Math.min(targetWeekIndex, weekCount);
        int weekIndex = lagoonsByWeek.size();
        while (weekIndex - 1 <= targetWeekIndex) {

            int prevWeekIndex = weekIndex - 1;
            Lagoon prevLagoon = lagoonsByWeek.get(prevWeekIndex);
            prevLagoon = prevLagoon.cleanActions();
            prevLagoon = applyActionsAt(prevLagoon, prevWeekIndex);
            lagoonsByWeek.set(prevWeekIndex, prevLagoon);

            if (weekIndex <= targetWeekIndex) {
                Lagoon currentLagoon = prevLagoon.commitWeek();
                lagoonsByWeek.add(currentLagoon);
            }

            weekIndex = weekIndex + 1;
        }
    }

    private Lagoon applyActionsAt(Lagoon currentLagoon, int weekIndex) {
        for (BotWeekAction bwe: botWeekActions.values()) {
            if (bwe.weekIndex == weekIndex) {
                currentLagoon = currentLagoon.putAction(bwe.botId, bwe.action);
            }
        }

        return currentLagoon;
    }

    private List<Lagoon> copyLagoonsByWeekUntil(int weekIndex) {
        return new ArrayList<>(lagoonsByWeek.subList(0, Math.min(weekIndex + 1, lagoonsByWeek.size())));
    }

    private LagoonHistory copy() {
        return new LagoonHistory(weekCount, lagoonsByWeek, botWeekActions);
    }

    @Override
    public String toString() {
        cacheMissingLagoonsUntil(weekCount);
        return "LagoonHistory{\n  "
                + String.join("\n  ", lagoonsByWeek.stream().map(x -> x.toString()).toArray(String[]::new))
                + "\n}";
    }


    private static final class BotWeekAction {
        final String key;
        final BotId botId;
        final int weekIndex;
        final Action action;

        public BotWeekAction(BotId botId, int weekIndex, Action action) {
            this.key = botId.toString() + "#" + weekIndex;
            this.botId = botId;
            this.weekIndex = weekIndex;
            this.action = action;
        }
    }
}
