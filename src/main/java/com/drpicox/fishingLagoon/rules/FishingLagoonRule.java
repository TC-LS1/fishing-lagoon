package com.drpicox.fishingLagoon.rules;

import com.drpicox.fishingLagoon.engine.RoundEngine;
import com.drpicox.fishingLagoon.engine.RoundScoresCalculator;

public interface FishingLagoonRule {

    void apply(int weekIndex, RoundScoresCalculator scores, RoundEngine round);

}
