package com.drpicox.fishingLagoon.business.rules;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.scores.RoundScoresCalculator;

public class FishingLagoonRuleProcreation implements FishingLagoonRule {

    @Override
    public void apply(int weekIndex, RoundScoresCalculator scores, Round round) {
        for (var lagoonIndex: scores.getLagoonIndices()) {
            var fishPopulation = scores.getFishPopulation(lagoonIndex);
            scores.sumFishPopulation(lagoonIndex, fishPopulation / 2);
        }
    }
}
