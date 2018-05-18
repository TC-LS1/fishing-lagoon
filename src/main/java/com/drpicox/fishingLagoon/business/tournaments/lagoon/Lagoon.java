package com.drpicox.fishingLagoon.business.tournaments.lagoon;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.actions.FishAction;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Lagoon {
    private long lagoonFishCount;
    private Set<BotId> bots;
    private Map<BotId, Long> botsFishCount;
    private Map<BotId, BotAction> botsAction;

    public Lagoon(long lagoonFishCount) {
        this(lagoonFishCount, new HashSet<>(), new HashMap<>(), new HashMap<>());
    }

    private Lagoon(long lagoonFishCount, Set<BotId> bots, Map<BotId, Long> botsFishCount, Map<BotId, BotAction> botsAction) {
        this.bots = bots;
        this.lagoonFishCount = lagoonFishCount;
        this.botsFishCount = botsFishCount;
        this.botsAction = botsAction;
    }

    public Lagoon putAction(BotId botId, Action action) {
        if (!bots.contains(botId)) return this;

        Lagoon result = copy();
        result.botsAction = new HashMap<>(botsAction);
        result.botsAction.put(botId, new BotAction(botId, action));
        return result;
    }

    public Lagoon addBot(BotId botId) {
        if (bots.contains(botId)) return this;

        Lagoon result = copy();
        result.bots = new HashSet<>(bots);
        result.bots.add(botId);
        return result;
    }

    public Lagoon addBots(BotId... newBots) {
        if (Stream.of(newBots).allMatch(bot -> this.bots.contains(bot))) return this;

        Lagoon result = copy();
        result.bots = new HashSet<>(bots);
        result.bots.addAll(Arrays.asList(newBots));
        return result;
    }

    public Lagoon cleanActions() {
        Lagoon result = copy();
        result.botsAction = new HashMap<>();
        return result;
    }

    public Lagoon commitWeek() {
        return this
            .commitFishing()
            .commitFishReproduction()
            .cleanActions();
    }

    public Action getActionOf(BotId botId) {
        if (botsAction.containsKey(botId)) {
            return botsAction.get(botId).action;
        }
        return null;
    }

    public List<BotId> getBots() {
        return new LinkedList<>(bots);
    }

    public long getLagoonFishCount() {
        return lagoonFishCount;
    }

    public long getFishCountOf(BotId botId) {
        return botsFishCount.getOrDefault(botId, 0L);
    }

    public long getScoreOf(BotId botId) {
        return getFishCountOf(botId);
    }

    public boolean hasBot(BotId botId) {
        return bots.contains(botId);
    }

    public Lagoon removeBot(BotId botId) {
        if (!bots.contains(botId)) return this;

        Lagoon result = copy();
        result.bots = new HashSet<>(bots);
        result.bots.remove(botId);
        result.botsAction = new HashMap<>(botsAction);
        result.botsAction.keySet().remove(botId);
        result.botsFishCount = new HashMap<>(botsFishCount);
        result.botsFishCount.keySet().remove(botId);
        return result;
    }


    private Lagoon commitFishing() {
        List<BotAction> fishingBotActions = botsAction.values().stream()
                .filter(ba -> ba.isFishing())
                .collect(Collectors.toList());
        return commitFishing(fishingBotActions, 1);
    }

    private Lagoon commitFishing(List<BotAction> fishingActions, long startFishCount) {
        long minActionFishCount = Long.MAX_VALUE;
        for (BotAction currentAction: fishingActions) {
            long actionFishCount = currentAction.getFishingCount();
            if (startFishCount <= actionFishCount && actionFishCount < minActionFishCount) {
                minActionFishCount = actionFishCount;
            }
        }

        if (minActionFishCount == Long.MAX_VALUE) return this;
        Lagoon result = copy();
        result.botsFishCount = copyBotsFishCount();

        List<BotId> fishingBots = new LinkedList<>();
        for (BotAction fishAction: fishingActions) {
            long actionFishCount = fishAction.getFishingCount();
            if (minActionFishCount == actionFishCount) {
                fishingBots.add(fishAction.botId);
            }
        }

        long share = Math.min(lagoonFishCount / fishingBots.size(), minActionFishCount);
        for (BotId fishingBot : fishingBots) {
            result.botsFishCount.put(fishingBot, getFishCountOf(fishingBot) + share);
        }
        result.lagoonFishCount -= share * fishingBots.size();

        return result.commitFishing(fishingActions, minActionFishCount + 1);
    }


    private Lagoon commitFishReproduction() {
        Lagoon result = copy();
        result.lagoonFishCount = lagoonFishCount + (lagoonFishCount / 2);
        return result;
    }

    private Lagoon copy() {
        return new Lagoon(lagoonFishCount, bots, botsFishCount, botsAction);
    }

    private Map<BotId,Long> copyBotsFishCount() {
        return new HashMap<>(botsFishCount);
    }


    @Override
    public String toString() {
        Set<BotId> bots = new HashSet<>();
        bots.addAll(botsFishCount.keySet());
        bots.addAll(botsAction.keySet());

        return "Lagoon{"
                + "fish [" + lagoonFishCount + "], "
                + String.join(", ", bots.stream().map(
                        b -> b + "[" + getFishCountOf(b) + "]:" + getActionOf(b)
                ).toArray(String[]::new))
                + "}";
    }

    private final static class BotAction {
        private BotId botId;
        private Action action;

        public BotAction(BotId botId, Action action) {
            this.botId = botId;
            this.action = action;
        }

        public boolean isFishing() {
            return action instanceof FishAction;
        }

        public long getFishingCount() {
            return ((FishAction) action).getFishingCount();
        }
    }
}
