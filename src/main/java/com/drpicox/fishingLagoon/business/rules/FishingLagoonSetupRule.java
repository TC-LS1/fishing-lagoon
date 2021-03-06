package com.drpicox.fishingLagoon.business.rules;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.scores.RoundScoresCalculator;

public interface FishingLagoonSetupRule {

    void setup(RoundScoresCalculator scores, Round round);

}
