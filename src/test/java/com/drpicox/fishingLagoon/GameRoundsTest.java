package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.actions.Action;
import com.drpicox.fishingLagoon.admin.AdminToken;
import com.drpicox.fishingLagoon.bots.BotId;
import com.drpicox.fishingLagoon.bots.BotToken;
import com.drpicox.fishingLagoon.common.TimeOffset;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.rounds.RoundId;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleFishing;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleProcreation;
import com.drpicox.fishingLagoon.rules.FishingLagoonRules;
import com.drpicox.fishingLagoon.rules.FishingLagoonSetupRuleFishPopulation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.drpicox.fishingLagoon.JsonPathMatcher.jsonPath;
import static com.drpicox.fishingLagoon.actions.Actions.fish;
import static com.drpicox.fishingLagoon.actions.Actions.rest;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GameRoundsTest {

    private AdminToken adminToken;
    private TestBootstrap bootstrap;
    private Gson gson;
    private GameController gameController;

    private static final long SEAT_MILLISECONDS = 20000L;
    private static final long COMMAND_MILLISECONDS = 20000L;
    private static final long SCORE_MILLISECONDS = 20000L;
    private static final long TOTAL_MILLISECONDS = SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS;

    private static final String ROUND_TEXT = String.join("\n", "",
            "maxDensity=2.0",
            "weekCount=2",
            "seatMilliseconds=20000",
            "commandMilliseconds=20000",
            "scoreMilliseconds=20000",
            "lagoons=lagoon0,lagoon1",
            "lagoon0.fishPopulation=9",
            "lagoon1.fishPopulation=100"
            ) + "\n";

    @Before
    public void instance_bootstrap() throws SQLException {
        adminToken = new AdminToken("admin123");
        bootstrap = new TestBootstrap(adminToken);
        gson = bootstrap.getGson();
        gameController = bootstrap.getGameController();

        gameController.createBot(botToken("token1"), adminToken);
        gameController.createBot(botToken("token2"), adminToken);
        gameController.createBot(botToken("token3"), adminToken);
        gameController.createBot(botToken("token4"), adminToken);
    }

    // ROUND CREATION

    @Test
    public void rounds_create() throws SQLException {
        var round1 = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).withSelfId(null);
        var round2 = gameController.createRound(ROUND_TEXT, botToken("token2"), ts(TOTAL_MILLISECONDS)).withSelfId(null);
        var rounds = gameController.listRounds();

        assertThat(round1, hasProperty("id", is(round(1))));
        assertThat(round1, hasProperty("startTs", is(ts(0))));
        assertThat(round1, hasProperty("endTs", is(ts(TOTAL_MILLISECONDS))));
        assertThat(round2, hasProperty("id", is(round(2))));
        assertThat(round2, hasProperty("startTs", is(ts(TOTAL_MILLISECONDS))));
        assertThat(round2, hasProperty("endTs", is(ts(TOTAL_MILLISECONDS + TOTAL_MILLISECONDS))));
        assertThat(rounds, hasSize(2));
        assertThat(rounds, hasItem(samePropertyValuesAs(round1)));
        assertThat(rounds, hasItem(samePropertyValuesAs(round2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_cannot_be_called_by_unexisting_bots() throws SQLException {
        gameController.createRound(ROUND_TEXT, botToken("unexistingToken"), ts(TOTAL_MILLISECONDS));
    }

    @Test
    public void rounds_create_cannot_work_with_less_than_one_minute_round() throws SQLException {
        var roundText = ROUND_TEXT + "\nseatMilliseconds=" + (SEAT_MILLISECONDS - 1);

        IllegalArgumentException exceptionFound = null;
        try {
            gameController.createRound(roundText, botToken("token1"), ts(0L));
        } catch (IllegalArgumentException e) {
            exceptionFound = e;
        }

        var rounds = gameController.listRounds();
        assertThat(rounds, is(empty()));
        assertThat(exceptionFound, is(not(nullValue())));
    }

    @Test
    public void rounds_create_cannot_be_called_if_already_exists_a_working_round() throws SQLException {
        var round1 = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L));

        IllegalArgumentException exceptionFound = null;
        try {
            gameController.createRound(ROUND_TEXT, botToken("token2"), ts(TOTAL_MILLISECONDS - 1));
        } catch (IllegalArgumentException e) {
            exceptionFound = e;
        }


        var rounds = gameController.listRounds();
        assertThat(rounds, hasSize(1));
        assertThat(rounds, hasItem(samePropertyValuesAs(round1.withSelfId(null))));
        assertThat(exceptionFound, is(not(nullValue())));
    }

    // ROUND SEATING

    @Test
    public void round_seating() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();

        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gameController.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gameController.seatBot(roundId, botToken("token3"), 1, ts(3L));

        var round = gameController.getRound(roundId, botToken("token3"), ts(3L));
        var json = gson.toJson(round.getSeats());
        assertThat(json, jsonPath("$.bot1.lagoonIndex", 0));
        assertThat(json, jsonPath("$.bot2.lagoonIndex", 0));
        assertThat(json, jsonPath("$.bot3.lagoonIndex", 1));
    }

    @Test
    public void round_seating_allows_change_seat() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT + "maxDensity=1", botToken("token1"), ts(0L)).getId();

        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gameController.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gameController.seatBot(roundId, botToken("token2"), 1, ts(3L));

        var round = gameController.getRound(roundId, ts(4L));
        var json = gson.toJson(round.getSeats());
        assertThat(json, jsonPath("$.bot1.lagoonIndex", 0));
        assertThat(json, jsonPath("$.bot2.lagoonIndex", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_seating_reports_illegal_seats_negative() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token2"), -1, ts(2L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_seating_reports_illegal_seats_not_existing() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token2"), -1, ts(2L));
    }

    @Test(expected = IllegalStateException.class)
    public void round_seating_fails_seats_outside_time() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(10L)).getId();
        gameController.seatBot(roundId, botToken("token2"), 0, ts(SEAT_MILLISECONDS + 10L));
    }

    @Test
    public void round_seating_is_not_shown_after_round_seating_commanding_scoring() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));

        var seats = gameController.getRound(roundId, ts(TOTAL_MILLISECONDS)).getSeats();
        assertThat(seats, is(nullValue()));
    }

    // ROUND COMMANDS

    @Test
    public void round_commanding() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gameController.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gameController.seatBot(roundId, botToken("token3"), 1, ts(3L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        gameController.commandBot(roundId, botToken("token2"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + 1L));
        gameController.commandBot(roundId, botToken("token3"), asList(rest(), fish(5)), ts(SEAT_MILLISECONDS + 2L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS ));
        var json = gson.toJson(round.getCommands());
        assertThat(json, jsonPath("$.bot1.actions", contains("fish 1", "fish 2")));
        assertThat(json, jsonPath("$.bot2.actions", contains("fish 3", "fish 4")));
        assertThat(json, jsonPath("$.bot3.actions", contains("rest", "fish 5")));
    }

    @Test
    public void round_commanding_allows_change_commands() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        gameController.commandBot(roundId, botToken("token1"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + 1L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS ));

        var json = gson.toJson(round.getCommands());
        assertThat(json, jsonPath("$.bot1.actions", contains("fish 3", "fish 4")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_commanding_not_accept_not_seated_bots() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
    }

    @Test
    public void round_commanding_not_accept_not_wrong_size_lists() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gameController.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gameController.seatBot(roundId, botToken("token3"), 0, ts(3L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        try {
            gameController.commandBot(roundId, botToken("token2"), asList(fish(3), fish(4), fish(5)), ts(SEAT_MILLISECONDS + 1L));
        } catch (IllegalArgumentException iea) {}
        try {
            gameController.commandBot(roundId, botToken("token3"), asList(fish(1)), ts(SEAT_MILLISECONDS + 2L));
        } catch (IllegalArgumentException iea) {}

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS ));

        var json = gson.toJson(round.getCommands());
        assertThat(json, jsonPath("$.bot1.actions", contains("fish 1", "fish 2")));
    }

    @Test(expected = IllegalStateException.class)
    public void round_commanding_not_accept_before_time() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gameController.commandBot(roundId, botToken("token1"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS - 1L));
    }

    @Test(expected = IllegalStateException.class)
    public void round_commanding_not_accept_after_time() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gameController.commandBot(roundId, botToken("token1"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS));
    }

    @Test
    public void round_commanding_not_showing_commands_before_time() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS - 1L));
        var commands = round.getCommands();

        assertThat(commands, is(nullValue()));
    }

    @Test
    public void round_commanding_not_showing_commands_after_time() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS));
        var commands = round.getCommands();

        assertThat(commands, is(nullValue()));
    }

    @Test
    public void round_commanding_accepts_very_large_chains_of_commands() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT + "\nweekCount=100\n", botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));

        var actions = new ArrayList<Action>(100);
        for (var weekIndex = 0; weekIndex < 100; weekIndex++) {
            actions.add(fish(Long.MAX_VALUE));
        }
        gameController.commandBot(roundId, botToken("token1"), actions, ts(SEAT_MILLISECONDS + 0L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS));

        var json = gson.toJson(round.getCommands());
        assertThat(json, jsonPath("$.bot1.actions", hasSize(100)));
    }

    // SCORES

    @Test
    public void round_scoring() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gameController.seatBot(roundId, botToken("token2"), 0, ts(2L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        gameController.commandBot(roundId, botToken("token2"), asList(fish(5), fish(6)), ts(SEAT_MILLISECONDS + 1L));
        gameController.commandBot(roundId, botToken("token2"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + 2L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS));

        var json = gson.toJson(round.getScores());
        assertThat(json, jsonPath("$.lagoons[0].fishPopulation", 1));
        assertThat(json, jsonPath("$.bots.bot1.score", 3));
        assertThat(json, jsonPath("$.bots.bot2.score", 7));
    }

    @Test
    public void round_scoring_not_showing_scores_before_time() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS - 1));
        var scores = round.getScores();

        assertThat(scores, is(nullValue()));
    }

    @Test
    public void round_scoring_shows_scores_after_time() throws SQLException {
        var roundId = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gameController.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gameController.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gameController.getRound(roundId, ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS));

        var json = gson.toJson(round.getScores());
        assertThat(json, jsonPath("$.lagoons[0].fishPopulation", 15));
        assertThat(json, jsonPath("$.bots.bot1.score", 3));
    }

    @Test
    public void round_creating_seating_commanding_and_getting_returns_selfId() throws SQLException {
        var create = gameController.createRound(ROUND_TEXT, botToken("token1"), ts(0L));
        var roundId = create.getId();

        var seat = gameController.seatBot(roundId, botToken("token2"), 0, ts(1L));
        gameController.seatBot(roundId, botToken("token3"), 0, ts(1L));

        var command = gameController.commandBot(roundId, botToken("token3"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        var get = gameController.getRound(roundId, botToken("token4"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS));

        assertThat(create.getSelfId(), is(bot(1)));
        assertThat(seat.getSelfId(), is(bot(2)));
        assertThat(command.getSelfId(), is(bot(3)));
        assertThat(get.getSelfId(), is(bot(4)));
    }


    private static BotId bot(int n) {
        return new BotId("bot" + n);
    }
    private static RoundId round(int n) {
        return new RoundId("round" + n);
    }

    private static TimeStamp ts(long n) {
        return new TimeStamp(n);
    }
    private static TimeOffset to(long n) {
        return new TimeOffset(n);
    }

    private BotToken botToken(String n) {
        return new BotToken(n);
    }
}
