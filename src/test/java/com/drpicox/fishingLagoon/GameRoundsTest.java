package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.common.TimeOffset;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.presentation.GamePresentation;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.drpicox.fishingLagoon.JsonPathMatcher.jsonPath;
import static com.drpicox.fishingLagoon.common.actions.Actions.fish;
import static com.drpicox.fishingLagoon.common.actions.Actions.rest;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GameRoundsTest {

    private AdminToken adminToken;
    private TestBootstrap bootstrap;
    private Gson gson;
    private GamePresentation gamePresentation;

    private static final long SECOND = 1000L;
    private static final long MINUTE = 60 * SECOND;
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
        gamePresentation = bootstrap.getGamePresentation();

        gamePresentation.createBot(botToken("token1"), adminToken);
        gamePresentation.createBot(botToken("token2"), adminToken);
        gamePresentation.createBot(botToken("token3"), adminToken);
        gamePresentation.createBot(botToken("token4"), adminToken);
    }

    // ROUND CREATION

    @Test
    public void rounds_create() throws SQLException {
        var round1 = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L));
        var round2 = gamePresentation.createRound(ROUND_TEXT, botToken("token2"), ts(TOTAL_MILLISECONDS));
        var rounds = gamePresentation.listRounds();

        var json1 = gson.toJson(round1);
        var json2 = gson.toJson(round2);
        var jsons = gson.toJson(rounds);
        assertThat(json1, jsonPath("$.id", is("round1")));
        assertThat(json1, jsonPath("$.startTs", is(0)));
        assertThat(json1, jsonPath("$.endTs", is((int) TOTAL_MILLISECONDS)));
        assertThat(json2, jsonPath("$.id", is("round2")));
        assertThat(json2, jsonPath("$.startTs", is((int)TOTAL_MILLISECONDS)));
        assertThat(json2, jsonPath("$.endTs", is((int)(TOTAL_MILLISECONDS + TOTAL_MILLISECONDS))));
        assertThat(jsons, jsonPath("$", hasSize(2)));
        assertThat(jsons, jsonPath("$[0].id", is("round1")));
        assertThat(jsons, jsonPath("$[1].id", is("round2")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_cannot_be_called_by_unexisting_bots() throws SQLException {
        gamePresentation.createRound(ROUND_TEXT, botToken("unexistingToken"), ts(TOTAL_MILLISECONDS));
    }

    @Test
    public void rounds_create_fastest_round_with_all_phases_lasting_5_seconds() throws SQLException {
        var roundText = "" +
                "seatMilliseconds=" + (5 * SECOND) + "\n" +
                "commandMilliseconds=" + (5 * SECOND) + "\n" +
                "scoreMilliseconds=" + (5 * SECOND) + "\n";

        var round = gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
        assertThat(round, hasProperty("totalMilliseconds", is(3 * 5 * SECOND)));
    }

    @Test
    public void rounds_create_slowest_round_with_all_phases_lasting_1_minute() throws SQLException {
        var roundText = "" +
                "seatMilliseconds=" + (1 * MINUTE) + "\n" +
                "commandMilliseconds=" + (1 * MINUTE) + "\n" +
                "scoreMilliseconds=" + (1 * MINUTE) + "\n";

        var round = gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
        assertThat(round, hasProperty("totalMilliseconds", is(3 * MINUTE)));
    }
    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_fails_if_seat_phase_lasts_less_than_5_seconds() throws SQLException {
        var roundText = "seatMilliseconds=" + (5 * SECOND - 1L);
        gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_fails_if_command_phase_lasts_less_than_5_seconds() throws SQLException {
        var roundText = "commandMilliseconds=" + (5 * SECOND - 1L);
        gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_fails_if_score_phase_lasts_less_than_5_seconds() throws SQLException {
        var roundText = "scoreMilliseconds=" + (5 * SECOND - 1L);
        gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_fails_if_seat_phase_lasts_more_than_one_minute() throws SQLException {
        var roundText = "seatMilliseconds=" + (MINUTE + 1L);
        gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_fails_if_command_phase_lasts_more_than_one_minute() throws SQLException {
        var roundText = "commandMilliseconds=" + (MINUTE + 1L);
        gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rounds_create_fails_if_score_phase_lasts_more_than_one_minute() throws SQLException {
        var roundText = "scoreMilliseconds=" + (MINUTE + 1L);
        gamePresentation.createRound(roundText, botToken("token1"), ts(0L));
    }

    @Test
    public void rounds_create_cannot_be_called_if_already_exists_a_working_round() throws SQLException {
        var round1 = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L));

        IllegalArgumentException exceptionFound = null;
        try {
            gamePresentation.createRound(ROUND_TEXT, botToken("token2"), ts(TOTAL_MILLISECONDS - 1));
        } catch (IllegalArgumentException e) {
            exceptionFound = e;
        }


        var rounds = gamePresentation.listRounds();
        assertThat(rounds, hasSize(1));
        assertThat(rounds, hasItem(hasProperty("id", is(round1.getId()))));
        assertThat(exceptionFound, is(not(nullValue())));
    }

    // ROUND SEATING

    @Test
    public void round_seating() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();

        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gamePresentation.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gamePresentation.seatBot(roundId, botToken("token3"), 1, ts(3L));

        var round = gamePresentation.getRound(roundId, botToken("token3"), ts(3L));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.seats.bot1.lagoonIndex", 0));
        assertThat(json, jsonPath("$.seats.bot2.lagoonIndex", 0));
        assertThat(json, jsonPath("$.seats.bot3.lagoonIndex", 1));
    }

    @Test
    public void round_seating_allows_change_seat() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT + "maxDensity=1", botToken("token1"), ts(0L)).getId();

        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gamePresentation.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gamePresentation.seatBot(roundId, botToken("token2"), 1, ts(3L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(4L));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.seats.bot1.lagoonIndex", 0));
        assertThat(json, jsonPath("$.seats.bot2.lagoonIndex", 1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_seating_reports_illegal_seats_negative() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token2"), -1, ts(2L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_seating_reports_illegal_seats_not_existing() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token2"), -1, ts(2L));
    }

    @Test(expected = IllegalStateException.class)
    public void round_seating_fails_seats_outside_time() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(10L)).getId();
        gamePresentation.seatBot(roundId, botToken("token2"), 0, ts(SEAT_MILLISECONDS + 10L));
    }

    @Test
    public void round_seating_is_not_shown_after_round_seating_commanding_scoring() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(TOTAL_MILLISECONDS));
        var json = gson.toJson(round);
        // TODO check first fail
        assertThat(json, is(not(jsonPath("$.seats.bot1.lagoonIndex", 0))));
    }

    // ROUND COMMANDS

    @Test
    public void round_commanding() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gamePresentation.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gamePresentation.seatBot(roundId, botToken("token3"), 1, ts(3L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        gamePresentation.commandBot(roundId, botToken("token2"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + 1L));
        gamePresentation.commandBot(roundId, botToken("token3"), asList(rest(), fish(5)), ts(SEAT_MILLISECONDS + 2L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS ));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.commands.bot1.actions", contains("fish 1", "fish 2")));
        assertThat(json, jsonPath("$.commands.bot2.actions", contains("fish 3", "fish 4")));
        assertThat(json, jsonPath("$.commands.bot3.actions", contains("rest", "fish 5")));
    }

    @Test
    public void round_commanding_allows_change_commands() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + 1L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS ));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.commands.bot1.actions", contains("fish 3", "fish 4")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void round_commanding_not_accept_not_seated_bots() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
    }

    @Test
    public void round_commanding_not_accept_not_wrong_size_lists() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gamePresentation.seatBot(roundId, botToken("token2"), 0, ts(2L));
        gamePresentation.seatBot(roundId, botToken("token3"), 0, ts(3L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        try {
            gamePresentation.commandBot(roundId, botToken("token2"), asList(fish(3), fish(4), fish(5)), ts(SEAT_MILLISECONDS + 1L));
        } catch (IllegalArgumentException iea) {}
        try {
            gamePresentation.commandBot(roundId, botToken("token3"), asList(fish(1)), ts(SEAT_MILLISECONDS + 2L));
        } catch (IllegalArgumentException iea) {}

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS ));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.commands.bot1.actions", contains("fish 1", "fish 2")));
    }

    @Test(expected = IllegalStateException.class)
    public void round_commanding_not_accept_before_time() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS - 1L));
    }

    @Test(expected = IllegalStateException.class)
    public void round_commanding_not_accept_after_time() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS));
    }

    @Test
    public void round_commanding_not_showing_commands_before_time() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS - 1L));
        var json = gson.toJson(round);
        assertThat(json, is(not(jsonPath("$.commands.bot1.actions", contains("fish 1", "fish 2")))));
    }

    @Test
    public void round_commanding_not_showing_commands_after_time() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS));
        var json = gson.toJson(round);
        assertThat(json, is(not(jsonPath("$.commands.bot1.actions", contains("fish 1", "fish 2")))));
    }

    @Test
    public void round_commanding_accepts_very_large_chains_of_commands() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT + "\nweekCount=100\n", botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));

        var actions = new ArrayList<Action>(100);
        for (var weekIndex = 0; weekIndex < 100; weekIndex++) {
            actions.add(fish(Long.MAX_VALUE));
        }
        gamePresentation.commandBot(roundId, botToken("token1"), actions, ts(SEAT_MILLISECONDS + 0L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.commands.bot1.actions", hasSize(100)));
    }

    // SCORES

    @Test
    public void round_scoring() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));
        gamePresentation.seatBot(roundId, botToken("token2"), 0, ts(2L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        gamePresentation.commandBot(roundId, botToken("token2"), asList(fish(5), fish(6)), ts(SEAT_MILLISECONDS + 1L));
        gamePresentation.commandBot(roundId, botToken("token2"), asList(fish(3), fish(4)), ts(SEAT_MILLISECONDS + 2L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.scores.lagoons[0].fishPopulation", 1));
        assertThat(json, jsonPath("$.scores.bots.bot1.score", 3));
        assertThat(json, jsonPath("$.scores.bots.bot2.score", 7));
    }

    @Test
    public void round_scoring_not_showing_scores_before_time() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS - 1));
        var json = gson.toJson(round);
        assertThat(json, is(not(jsonPath("$.scores.lagoons[0].fishPopulation", 15))));
    }

    @Test
    public void round_scoring_shows_scores_after_time() throws SQLException {
        var roundId = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L)).getId();
        gamePresentation.seatBot(roundId, botToken("token1"), 0, ts(1L));

        gamePresentation.commandBot(roundId, botToken("token1"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));

        var round = gamePresentation.getRound(roundId, botToken("token1"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS));
        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.scores.lagoons[0].fishPopulation", 15));
        assertThat(json, jsonPath("$.scores.bots.bot1.score", 3));
    }

    @Test
    public void round_creating_seating_commanding_and_getting_returns_selfId() throws SQLException {
        var create = gamePresentation.createRound(ROUND_TEXT, botToken("token1"), ts(0L));
        var roundId = create.getId();

        var seat = gamePresentation.seatBot(roundId, botToken("token2"), 0, ts(1L));
        gamePresentation.seatBot(roundId, botToken("token3"), 0, ts(1L));

        var command = gamePresentation.commandBot(roundId, botToken("token3"), asList(fish(1), fish(2)), ts(SEAT_MILLISECONDS + 0L));
        var get = gamePresentation.getRound(roundId, botToken("token4"), ts(SEAT_MILLISECONDS + COMMAND_MILLISECONDS + SCORE_MILLISECONDS));

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
