package com.drpicox.fishingLagoon.business.rules;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundDescriptor;
import com.drpicox.fishingLagoon.business.scores.RoundScoresCalculator;

public class FishingLagoonSetupRuleFishPopulation implements FishingLagoonSetupRule {


    @Override
    public void setup(RoundScoresCalculator scores, Round round) {
        var descriptor = round.getDescriptor();
        var lagoonCount = round.countLagoons();
        for (int lagoonIndex = 0; lagoonIndex < lagoonCount; lagoonIndex++) {
            var lagoonDescriptor = descriptor.getLagoonDescriptor(lagoonIndex);
            var fishPopulation = lagoonDescriptor.getFishPopulation();
            scores.sumFishPopulation(lagoonIndex, fishPopulation);
        }
    }
}
