package com.drpicox.fishingLagoon.rules;

import com.drpicox.fishingLagoon.engine.*;

import java.util.ArrayList;
import java.util.List;

public class FishingLagoonRules {

    List<FishingLagoonSetupRule> setupRules;
    List<FishingLagoonRule> rules;

    public FishingLagoonRules(List<FishingLagoonSetupRule> setupRules, List<FishingLagoonRule> rules) {
        this.setupRules = new ArrayList<>(setupRules);
        this.rules = new ArrayList<>(rules);
    }

    public RoundScores score(RoundEngine round) {
        var scoresCalculator = new RoundScoresCalculator();

        setup(scoresCalculator, round);
        applyWeeks(scoresCalculator, round);

        return scoresCalculator.getScores();
    }

    private void setup(RoundScoresCalculator scores, RoundEngine round) {
        for (var setupRule: setupRules) {
            setupRule.setup(scores, round);
        }
    }

    private void applyWeeks(RoundScoresCalculator scores, RoundEngine round) {
        var weekCount = round.getDescriptor().getWeekCount();

        for (int weekIndex = 0; weekIndex < weekCount; weekIndex++) {
            applyWeek(weekIndex, scores, round);
        }
    }

    private void applyWeek(int weekIndex, RoundScoresCalculator scores, RoundEngine round) {
        for (var rule: rules) {
            rule.apply(weekIndex, scores, round);
        }
    }

}
