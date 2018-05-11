package com.drpicox.fishingLagoon.business.scores;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.rounds.Round;

import java.util.Set;

public class LagoonCalculatorScoreView {
    private int lagoonIndex;
    private RoundScoresCalculator scoresCalculator;

    public LagoonCalculatorScoreView(int lagoonIndex, RoundScoresCalculator scoresCalculator) {
        this.lagoonIndex = lagoonIndex;
        this.scoresCalculator = scoresCalculator;
    }

    public long getFishPopulation() {
        return scoresCalculator.getFishPopulation(lagoonIndex);
    }

    public void sumFishPopulation(long amount) {
        scoresCalculator.sumFishPopulation(lagoonIndex, amount);
    }

    public void sumScore(BotId bot, long share) {
        scoresCalculator.sumScore(bot, share);
    }
}
