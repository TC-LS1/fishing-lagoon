package com.drpicox.fishingLagoon.rules;

import com.drpicox.fishingLagoon.engine.RoundEngine;
import com.drpicox.fishingLagoon.engine.RoundScoresCalculator;

public class FishingLagoonRuleProcreation implements FishingLagoonRule {

    @Override
    public void apply(int weekIndex, RoundScoresCalculator scores, RoundEngine round) {
        for (var lagoonIndex: scores.getLagoonIndices()) {
            var fishPopulation = scores.getFishPopulation(lagoonIndex);
            scores.sumFishPopulation(lagoonIndex, fishPopulation / 2);
        }
    }
}
