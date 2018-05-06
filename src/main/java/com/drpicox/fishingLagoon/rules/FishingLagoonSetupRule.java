package com.drpicox.fishingLagoon.rules;

import com.drpicox.fishingLagoon.engine.RoundEngine;
import com.drpicox.fishingLagoon.engine.RoundScoresCalculator;

public interface FishingLagoonSetupRule {

    void setup(RoundScoresCalculator scores, RoundEngine round);

}
