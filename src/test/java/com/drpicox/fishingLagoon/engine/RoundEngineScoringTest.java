package com.drpicox.fishingLagoon.engine;


import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.parser.PropsParser;
import com.drpicox.fishingLagoon.parser.RoundParser;
import com.drpicox.fishingLagoon.rounds.RoundId;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleFishing;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleProcreation;
import com.drpicox.fishingLagoon.rules.FishingLagoonSetupRuleFishPopulation;
import com.drpicox.fishingLagoon.rules.FishingLagoonRules;
import org.junit.Test;

import static com.drpicox.fishingLagoon.actions.Actions.fish;

import static com.drpicox.fishingLagoon.engine.RoundTestHelper.setTimeForCommand;
import static com.drpicox.fishingLagoon.engine.RoundTestHelper.timeForSeat;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RoundEngineScoringTest {

    private static final FishingLagoonRules rules = new FishingLagoonRules(asList(
            new FishingLagoonSetupRuleFishPopulation()
    ), asList(
            new FishingLagoonRuleFishing(),
            new FishingLagoonRuleProcreation()
    ));

    private RoundEngine createRound(String... extraLines) {
        return parse("",
                "maxDensity=2.0",
                "weekCount=2",
                "lagoons=lagoon,lagoonBig",
                "lagoon.fishPopulation=5",
                "lagoonBig.fishPopulation=100",
                String.join("\n", extraLines));
    }

    @Test
    public void round_scoring_computes_lagoon_fish_population() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 0);

        var scores = round.getScores(rules);
        assertThat(scores.getLagoonCount(), is(1));
        assertThat(scores.getFishPopulation(0), is(10L));
        assertThat(scores.getBots(), is(empty()));
        assertThat(scores.getBotCount(), is(0));
        assertThat(scores.getScore(bot(1)), is(0L));
    }

    @Test
    public void round_scoring_computes_lagoon_fish_population_adjusting_lagoons_because_seating() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 0);

        var scores = round.getScores(rules);
        assertThat(scores.getLagoonCount(), is(2));
        assertThat(scores.getFishPopulation(0), is(10L));
        assertThat(scores.getFishPopulation(1), is(225L));
        assertThat(scores.getBots(), is(empty()));
        assertThat(scores.getBotCount(), is(0));
        assertThat(scores.getScore(bot(1)), is(0L));
    }

    @Test
    public void round_scoring_computes_population_fishing() {
        var round = createRound();

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 1);

        setTimeForCommand(round);
        round.commandBot(bot(1), asList(fish(1L), fish(2L)));
        round.commandBot(bot(3), asList(fish(200L), fish(200L)));

        var scores = round.getScores(rules);
        assertThat(scores.getFishPopulation(0), is(6L));
        assertThat(scores.getFishPopulation(1), is(0L));
    }

    @Test
    public void round_scoring_computes_bot_score() {
        var round = createRound("lagoon.fishPopulation=9");

        timeForSeat(round);
        round.seatBot(bot(1), 0);

        setTimeForCommand(round);
        round.commandBot(bot(1), asList(fish(1L), fish(2L)));

        var scores = round.getScores(rules);
        assertThat(scores.getBots(), containsInAnyOrder(bot(1)));
        assertThat(scores.getBotCount(), is(1));
        assertThat(scores.getScore(bot(1)), is(3L));
    }

    @Test
    public void round_scoring_computes_bot_scores_in_different_lagoons() {
        var round = createRound("lagoon.fishPopulation=9");

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(2), 1);

        setTimeForCommand(round);
        round.commandBot(bot(1), asList(fish(1L), fish(2L)));
        round.commandBot(bot(2), asList(fish(3L), fish(4L)));

        var scores = round.getScores(rules);
        assertThat(scores.getBots(), containsInAnyOrder(bot(1), bot(2)));
        assertThat(scores.getBotCount(), is(2));
        assertThat(scores.getScore(bot(1)), is(3L));
        assertThat(scores.getScore(bot(2)), is(7L));
    }

    @Test
    public void round_scoring_considres_lagoon_fish_depletation() {
        var round = createRound("lagoon.fishPopulation=9");

        timeForSeat(round);
        round.seatBot(bot(1), 0);

        setTimeForCommand(round);
        round.commandBot(bot(1), asList(fish(11L), fish(2L)));

        var scores = round.getScores(rules);
        assertThat(scores.getScore(bot(1)), is(9L));
    }

    @Test
    public void round_scoring_fish_when_lagoon_will_depleted_first_who_fish_less() {
        var round = createRound("lagoon.fishPopulation=9");

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);

        setTimeForCommand(round);
        round.commandBot(bot(1), asList(fish(8L), fish(2L)));
        round.commandBot(bot(2), asList(fish(9L), fish(2L)));

        var scores = round.getScores(rules);
        assertThat(scores.getScore(bot(1)), is(8L));
        assertThat(scores.getScore(bot(2)), is(1L));
    }

    @Test
    public void round_scoring_fish_when_lagoon_will_depleted_same_fishing_share_fishes_equally() {
        var round = createRound("lagoon.fishPopulation=9", "weekCount=1");

        timeForSeat(round);
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 0);

        setTimeForCommand(round);
        round.commandBot(bot(1), asList(fish(8L)));
        round.commandBot(bot(2), asList(fish(8L)));
        round.commandBot(bot(3), asList(fish(9L)));

        var scores = round.getScores(rules);
        assertThat(scores.getScore(bot(1)), is(4L));
        assertThat(scores.getScore(bot(2)), is(4L));
        assertThat(scores.getScore(bot(3)), is(1L));
    }

    private static RoundEngine parse(String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        return new RoundEngine(new RoundId("r0"), new TimeStamp(0L), roundDescriptor);
    }

    private static BotId bot(int n) {
        return new BotId("bot" + n);
    }


}
