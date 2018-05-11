package com.drpicox.fishingLagoon.business.rules;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.scores.*;

public class FishingLagoonRuleFishing implements FishingLagoonRule {


    @Override
    public void apply(int weekIndex, RoundScoresCalculator scores, Round round) {
        var lagoonCount = round.countLagoons();
        for (var lagoonIndex = 0; lagoonIndex < lagoonCount; lagoonIndex++) {
            var scoresView = new LagoonCalculatorScoreView(lagoonIndex, scores);
            var roundView = new LagoonWeekRoundView(lagoonIndex, weekIndex, round);
            applyLagoon(roundView, scoresView);
        }
    }

    public void applyLagoon(LagoonWeekRoundView roundView, LagoonCalculatorScoreView scoresView) {
        var fishValue = getNextFishValue(0L, roundView);
        while (haveNextFishValue(fishValue) && haveFishPopulation(scoresView)) {
            var fishActionCount = getFishActionCount(fishValue, roundView);
            var share = getFishShare(fishActionCount, fishValue, scoresView);

            scoresView.sumFishPopulation(-share * fishActionCount);
            updateScores(fishValue, share, roundView, scoresView);

            fishValue = getNextFishValue(fishValue, roundView);
        }
    }

    private long getNextFishValue(long fishValue, LagoonWeekRoundView roundView) {
        var result = Long.MAX_VALUE;

        for (var bot: roundView.getBots()) {
            var action = roundView.getAction(bot);
            var actionFishValue = action.getFishValue();
            if (fishValue < actionFishValue && actionFishValue < result) {
                result = actionFishValue;
            }
        }
        return result;
    }

    private boolean haveNextFishValue(long fishValue) {
        return fishValue != Long.MAX_VALUE;
    }

    private boolean haveFishPopulation(LagoonCalculatorScoreView scoresView) {
        return scoresView.getFishPopulation() > 0;
    }

    private int getFishActionCount(long fishValue, LagoonWeekRoundView roundView) {
        var result = 0;

        for (var bot: roundView.getBots()) {
            var action = roundView.getAction(bot);
            if (action.getFishValue() == fishValue) {
                result += 1;
            }
        }
        return result;
    }

    private long getFishShare(int fishActionCount, long fishValue, LagoonCalculatorScoreView scoresView) {
        var fishPopulation = scoresView.getFishPopulation();
        var share = Math.min(fishPopulation / fishActionCount, fishValue);
        return share;
    }

    private void updateScores(long fishValue, long share, LagoonWeekRoundView roundView, LagoonCalculatorScoreView scoresView) {
        for (var bot: roundView.getBots()) {
            var action = roundView.getAction(bot);
            if (action.getFishValue() == fishValue) {
                scoresView.sumScore(bot, share);
            }
        }
    }

}
