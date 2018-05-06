package com.drpicox.fishingLagoon.rules;

import com.drpicox.fishingLagoon.engine.RoundEngine;
import com.drpicox.fishingLagoon.engine.RoundScoresCalculator;

public class FishingLagoonSetupRuleFishPopulation implements FishingLagoonSetupRule {


    @Override
    public void setup(RoundScoresCalculator scores, RoundEngine round) {
        var descriptor = round.getDescriptor();
        var seats = round.getSeats();
        var maxDensity = descriptor.getMaxDensity();
        var lagoonCount = seats.getLagoonCount(null, maxDensity);

        for (int lagoonIndex = 0; lagoonIndex < lagoonCount; lagoonIndex++) {
            var lagoonDescriptor = descriptor.getLagoonDescriptor(lagoonIndex);
            var fishPopulation = lagoonDescriptor.getFishPopulation();
            scores.sumFishPopulation(lagoonIndex, fishPopulation);
        }
    }
}
