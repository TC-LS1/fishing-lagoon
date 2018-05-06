package com.drpicox.fishingLagoon.rules;

import com.drpicox.fishingLagoon.engine.*;

public class FishingLagoonRuleFishing implements FishingLagoonRule {


    @Override
    public void apply(int weekIndex, RoundScoresCalculator scores, RoundEngine round) {
        var seats = round.getSeats();
        var commands = round.getCommands();
        for (var lagoonIndex: seats.getLagoonIndices()) {
            var scoresView = scores.getLagoonView(lagoonIndex, seats);
            var commandsView = commands.getLagoonWeekView(lagoonIndex, seats, weekIndex, round);
            applyLagoon(commandsView, scoresView);
        }
    }

    public void applyLagoon(LagoonWeekCommandsView commandsView, LagoonCalculatorScoreView scoresView) {
        var fishValue = getNextFishValue(0L, commandsView);
        while (haveNextFishValue(fishValue) && haveFishPopulation(scoresView)) {
            var fishActionCount = getFishActionCount(fishValue, commandsView);
            var share = getFishShare(fishActionCount, fishValue, scoresView);

            scoresView.sumFishPopulation(-share * fishActionCount);
            updateScores(fishValue, share, commandsView, scoresView);

            fishValue = getNextFishValue(fishValue, commandsView);
        }
    }

    private long getNextFishValue(long fishValue, LagoonWeekCommandsView commandsView) {
        var result = Long.MAX_VALUE;

        for (var bot: commandsView.getBots()) {
            var action = commandsView.getAction(bot);
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

    private int getFishActionCount(long fishValue, LagoonWeekCommandsView commandsView) {
        var result = 0;

        for (var bot: commandsView.getBots()) {
            var action = commandsView.getAction(bot);
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

    private void updateScores(long fishValue, long share, LagoonWeekCommandsView commandsView, LagoonCalculatorScoreView scoresView) {
        for (var bot: commandsView.getBots()) {
            var action = commandsView.getAction(bot);
            if (action.getFishValue() == fishValue) {
                scoresView.sumScore(bot, share);
            }
        }
    }

}
