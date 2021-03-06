package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.helpers.TestBootstrap;
import com.drpicox.fishingLagoon.presentation.GamePresentation;
import com.drpicox.fishingLagoon.presentation.RoundPresentation;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static com.drpicox.fishingLagoon.common.actions.Actions.fish;
import static com.drpicox.fishingLagoon.common.actions.Actions.rest;
import static com.drpicox.fishingLagoon.helpers.Helpers.*;
import static com.drpicox.fishingLagoon.helpers.JsonPathMatcher.jsonPath;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GameTournamentsTest {

    private AdminToken adminToken;
    private TestBootstrap bootstrap;
    private Gson gson;
    private GamePresentation gamePresentation;

    private static final String ROUND_TEXT = String.join("\n", "",
            "maxDensity=2.0",
            "weekCount=2",
            "seatMilliseconds=" + SEAT_MILLISECONDS,
            "commandMilliseconds=" + COMMAND_MILLISECONDS,
            "scoreMilliseconds=" + SCORE_MILLISECONDS,
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

        gamePresentation.createBot(token(1), adminToken);
        gamePresentation.createBot(token(2), adminToken);
        gamePresentation.createBot(token(3), adminToken);
        gamePresentation.createBot(token(4), adminToken);
    }

    @Test
    public void rounds_have_sparring_default_tournament() throws SQLException {
        var round = gamePresentation.createRound(ROUND_TEXT, token(1), ts(0L));

        var json = gson.toJson(round);
        assertThat(json, jsonPath("$.tournamentId", "SPARRING"));
    }

    @Test
    public void tournament_create_rounds() throws SQLException {
        gamePresentation.createTournamentRounds(tournament("demo"), "---", adminToken, ts(0L));

        var round0 = gamePresentation.listActiveRounds(token(1), ts(0L * MINUTE)).get(0);
        var round1 = gamePresentation.listActiveRounds(token(1), ts(1L * MINUTE)).get(0);

        assertThat(json(round0), jsonPath("$.tournamentId", "demo"));
        assertThat(json(round1), jsonPath("$.tournamentId", "demo"));
    }

    @Test
    public void tournament_create_rounds_does_it_after_existing_rounds() throws SQLException {
        gamePresentation.createRound(ROUND_TEXT, token(1), ts(0L));
        gamePresentation.createTournamentRounds(tournament("demo"), "", adminToken, ts(0L));

        var round0 = gamePresentation.listActiveRounds(token(1), ts(0L * MINUTE)).get(0);
        var round1 = gamePresentation.listActiveRounds(token(1), ts(1L * MINUTE)).get(0);

        assertThat(json(round0), jsonPath("$.tournamentId", "SPARRING"));
        assertThat(json(round1), jsonPath("$.tournamentId", "demo"));
    }

    @Test(expected = IllegalStateException.class)
    public void tournament_mode_on_inhibits_creation_of_sparring_rounds() throws SQLException {
        gamePresentation.setTournamentMode(true);

        gamePresentation.createRound("", token(1), ts(0L));
    }

    @Test
    public void tournament_recovers_score_of_bot_id_in_csv() throws SQLException {
        gamePresentation.createRound(".fishPopulation=10\nweekCount=1", token(1), ts(0L));
        gamePresentation.createTournamentRounds(tournament("demo"), ".fishPopulation=10\nweekCount=1\n---\n.fishPopulation=10\nweekCount=1", adminToken, ts(0L));

        gamePresentation.seatBot(round(1), token(1), 0, ts(0 * MINUTE));
        gamePresentation.seatBot(round(1), token(2), 0, ts(0 * MINUTE));
        gamePresentation.seatBot(round(2), token(1), 0, ts(1 * MINUTE));
        gamePresentation.seatBot(round(3), token(1), 0, ts(2 * MINUTE));
        gamePresentation.seatBot(round(3), token(3), 0, ts(2 * MINUTE));
        gamePresentation.commandBot(round(1), token(1), asList(fish(1)), ts(0 * MINUTE + 20 * SECOND));
        gamePresentation.commandBot(round(1), token(2), asList(fish(2)), ts(0 * MINUTE + 20 * SECOND));
        gamePresentation.commandBot(round(2), token(1), asList(fish(3)), ts(1 * MINUTE + 20 * SECOND));
        gamePresentation.commandBot(round(3), token(1), asList(fish(4)), ts(2 * MINUTE + 20 * SECOND));
        gamePresentation.commandBot(round(3), token(3), asList(fish(5)), ts(2 * MINUTE + 20 * SECOND));

        var csv = gamePresentation.getTournamentScores(tournament("demo"), adminToken);
        var lines = csv.split("\n");
        assertThat(asList(lines), containsInAnyOrder(
                is("demo;token1;3;4;7"),
                is("demo;token3;-2;5;3")));
    }

    @Test
    public void tournament_recovers_score_of_bot_id_in_right_order() throws SQLException {
        gamePresentation.createTournamentRounds(tournament("demo"), ".fishPopulation=10\nweekCount=1\n---\n.fishPopulation=100\nweekCount=1", adminToken, ts(0L));

        gamePresentation.seatBot(round(1), token(1), 0, ts(0 * MINUTE));
        gamePresentation.seatBot(round(2), token(1), 0, ts(1 * MINUTE));
        gamePresentation.commandBot(round(1), token(1), asList(fish(100)), ts(0 * MINUTE + 20 * SECOND));
        var csv1 = gamePresentation.getTournamentScores(tournament("demo"), adminToken);

        gamePresentation.commandBot(round(2), token(1), asList(fish(100)), ts(1 * MINUTE + 20 * SECOND));
        var csv2 = gamePresentation.getTournamentScores(tournament("demo"), adminToken);

        assertThat(csv1, is("demo;token1;10;-1;9"));
        assertThat(csv2, is("demo;token1;10;100;110"));
    }

    @Test
    public void tournament_returns_0_if_fished_nothing_n1_if_seated_but_not_commanded_n2_if_no_seated() throws SQLException {
        gamePresentation.createTournamentRounds(tournament("demo"), ".fishPopulation=10\nweekCount=1\n---\n.fishPopulation=10\nweekCount=1", adminToken, ts(0L));

        gamePresentation.seatBot(round(1), token(1), 0, ts(0 * MINUTE));
        gamePresentation.seatBot(round(1), token(2), 0, ts(0 * MINUTE));
        gamePresentation.seatBot(round(1), token(3), 0, ts(0 * MINUTE));
        gamePresentation.commandBot(round(1), token(1), asList(fish(10)), ts(0 * MINUTE + 20 * SECOND));
        gamePresentation.commandBot(round(1), token(2), asList(fish(10)), ts(0 * MINUTE + 20 * SECOND));
        gamePresentation.commandBot(round(1), token(3), asList(fish(10)), ts(0 * MINUTE + 20 * SECOND));

        gamePresentation.seatBot(round(2), token(1), 0, ts(1 * MINUTE));
        gamePresentation.seatBot(round(2), token(2), 0, ts(1 * MINUTE));
        gamePresentation.commandBot(round(2), token(1), asList(fish(0)), ts(1 * MINUTE + 20 * SECOND));

        var csv = gamePresentation.getTournamentScores(tournament("demo"), adminToken);
        var lines = csv.split("\n");
        assertThat(asList(lines), containsInAnyOrder(
                is("demo;token1;3;0;3"),
                is("demo;token2;3;-1;2"),
                is("demo;token3;3;-2;1")));
    }

    @Test
    public void tournament_lagoons_returns_final_fishes_count_of_all_lagoons() throws SQLException {
        gamePresentation.createTournamentRounds(tournament("demo"), ".fishPopulation=10\nweekCount=2\nmaxDensity=1\n---\n.fishPopulation=10\nweekCount=2", adminToken, ts(0L));

        gamePresentation.seatBot(round(1), token(1), 0, ts(0 * MINUTE));
        gamePresentation.seatBot(round(1), token(2), 0, ts(0 * MINUTE));
        gamePresentation.commandBot(round(1), token(1), asList(fish(8), rest()), ts(0 * MINUTE + 20 * SECOND));

        gamePresentation.seatBot(round(2), token(1), 0, ts(1 * MINUTE));

        var csv = gamePresentation.getTournamentFishPopulations(tournament("demo"), adminToken);
        assertThat(csv, is("fish-populations;26;22;48"));
    }


    private String json(Object ob) {
        return gson.toJson(ob);
    }

}
