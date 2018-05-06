package com.drpicox.fishingLagoon.engine;


import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.parser.PropsParser;
import com.drpicox.fishingLagoon.parser.RoundParser;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleFishing;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleProcreation;
import com.drpicox.fishingLagoon.rules.FishingLagoonRules;
import com.drpicox.fishingLagoon.rules.FishingLagoonSetupRuleFishPopulation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import static com.drpicox.fishingLagoon.JsonPathMatcher.jsonPath;
import static com.drpicox.fishingLagoon.actions.Actions.fish;
import static com.drpicox.fishingLagoon.actions.Actions.rest;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RoundEngineJsonTest {

    private static final FishingLagoonRules rules = new FishingLagoonRules(asList(
            new FishingLagoonSetupRuleFishPopulation()
    ), asList(
            new FishingLagoonRuleFishing(),
            new FishingLagoonRuleProcreation()
    ));

    private RoundEngine createRound() {
        return parse(10L,"",
                "maxDensity=2.0",
                "weekCount=2",
                "seatMilliseconds=20",
                "commandMilliseconds=30",
                "scoreMilliseconds=40",
                "lagoons=lagoon,lagoonBig",
                "lagoon.fishPopulation=9",
                "lagoonBig.fishPopulation=100");
    }

    private Gson gson;

    @Before
    public void make_gson() {
        var builder = new GsonBuilder();
        builder.setPrettyPrinting();
        gson = builder.create();
    }

    @Test
    public void round_json_descriptor() {
        var round = createRound();

        var json = gson.toJson(round.getDescriptor());
        assertThat(json, jsonPath("$.maxDensity", 2.0));
        assertThat(json, jsonPath("$.weekCount", is(2)));
        assertThat(json, jsonPath("$.seatMilliseconds", 20));
        assertThat(json, jsonPath("$.commandMilliseconds", 30));
        assertThat(json, jsonPath("$.scoreMilliseconds", 40));
        assertThat(json, jsonPath("$.lagoons", hasSize(2)));
        assertThat(json, jsonPath("$.lagoons[0].fishPopulation", 9));
        assertThat(json, jsonPath("$.lagoons[1].fishPopulation", 100));
    }

    @Test
    public void round_json_seats() {
        var round = createRound();
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 1);
        round.seatBot(bot(2), 1);

        var json = gson.toJson(round.getSeats().toMap());
        assertThat(json, jsonPath("$.bot1.lagoonIndex", 0));
        assertThat(json, jsonPath("$.bot2.lagoonIndex", 1));
        assertThat(json, jsonPath("$.bot3.lagoonIndex", 1));
    }

    @Test
    public void round_json_commands() {
        var round = createRound();
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 0);
        round.commandBot(bot(1), asList(fish(1), fish(2)));
        round.commandBot(bot(2), asList(rest(), fish(3)));

        var json = gson.toJson(round.getCommands().toMap());
        assertThat(json, jsonPath("$.bot1.actions", contains("fish 1", "fish 2")));
        assertThat(json, jsonPath("$.bot2.actions", contains("rest", "fish 3")));
    }

    @Test
    public void round_json_scores() {
        var round = createRound();
        round.seatBot(bot(1), 0);
        round.seatBot(bot(2), 0);
        round.seatBot(bot(3), 0);
        round.commandBot(bot(1), asList(fish(1), fish(2)));
        round.commandBot(bot(2), asList(rest(), fish(4)));

        var json = gson.toJson(round.getScores(rules).toMap());
        assertThat(json, jsonPath("$.lagoons[0].fishPopulation", 9));
        assertThat(json, jsonPath("$.lagoons[1].fishPopulation", 225));
        assertThat(json, jsonPath("$.bots.bot1.score", 3));
        assertThat(json, jsonPath("$.bots.bot2.score", 4));
    }



    private static BotId bot(int n) {
        return new BotId("bot" + n);
    }

    private static RoundEngine parse(long startTs, String... roundTextLines) {
        var roundText = String.join("\n", roundTextLines);
        var roundDescriptor = new RoundParser(new PropsParser()).parse(roundText);
        return new RoundEngine(startTs, roundDescriptor);
    }
}
