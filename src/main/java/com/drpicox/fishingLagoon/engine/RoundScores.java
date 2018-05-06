package com.drpicox.fishingLagoon.engine;

import com.drpicox.fishingLagoon.bots.BotId;

import java.util.*;
import java.util.stream.Collectors;

public class RoundScores {
    private List<RoundLagoonScore> lagoons = new ArrayList<>();
    private Map<BotId, RoundBotScore> bots = new HashMap<>();

    public RoundScores() {
    }

    public RoundScores(RoundScores sample) {
        this.lagoons.addAll(sample.lagoons.stream().map(l -> new RoundLagoonScore(l)).collect(Collectors.toList()));
        this.bots.putAll(sample.bots);
    }
    

    // lagoons

    public int getLagoonCount() {
        return lagoons.size();
    }

    public Set<Integer> getLagoonIndices() {
        var result = new LinkedHashSet<Integer>();
        for (int lagoonIndex = 0; lagoonIndex < getLagoonCount(); lagoonIndex++) {
            result.add(lagoonIndex);
        }
        return result;
    }

    public long getFishPopulation(int lagoonIndex) {
        if (lagoons.size() <= lagoonIndex) return 0L;
        return lagoons.get(lagoonIndex).getFishPopulation();
    }

    void putFishPopulation(int lagoonIndex, long fishPopulation) {
        ensureLagoonCount(lagoonIndex);
        lagoons.get(lagoonIndex).setFishPopulation(fishPopulation);
    }

    private void ensureLagoonCount(int lagoonIndex) {
        while (lagoons.size() <= lagoonIndex) {
            lagoons.add(new RoundLagoonScore());
        }
    }

    // bots

    public int getBotCount() {
        return bots.size();
    }

    public long getScore(BotId bot) {
        if (bots.containsKey(bot)) return bots.get(bot).getScore();
        return 0;
    }

    void putScore(BotId bot, long score) {
        ensureBot(bot);
        bots.get(bot).setScore(score);
    }

    private void ensureBot(BotId bot) {
        if (!bots.containsKey(bot)) {
            bots.put(bot, new RoundBotScore());
        }
    }

    public Set<BotId> getBots() {
        return new HashSet<>(bots.keySet());
    }
}
