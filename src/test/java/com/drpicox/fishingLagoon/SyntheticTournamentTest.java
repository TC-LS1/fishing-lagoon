package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.business.tournaments.SyntheticTournament;
import com.drpicox.fishingLagoon.business.tournaments.Tournament;
import com.drpicox.fishingLagoon.helpers.TestBootstrap;
import com.drpicox.fishingLagoon.presentation.GamePresentation;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static com.drpicox.fishingLagoon.helpers.Helpers.token;
import static com.drpicox.fishingLagoon.helpers.Helpers.ts;

public class SyntheticTournamentTest {

    private AdminToken adminToken;
    private TestBootstrap bootstrap;

    @Before
    public void instance() throws SQLException {
        adminToken = new AdminToken("admin123");
        bootstrap = new TestBootstrap(adminToken);
    }

    private GamePresentation getGame() throws SQLException {
        return getGame(new SyntheticTournament());
    }

    private GamePresentation getGame(Tournament tournament) throws SQLException {
        bootstrap.configureTournament(tournament);
        var game = bootstrap.getGamePresentation();
        game.createBot(token(1), adminToken);
        return game;
    }

    @Test(expected = IllegalStateException.class)
    public void does_not_allow_to_create_rounds() throws SQLException {
        var game = getGame();

        game.createRound("", token(1), ts(0));
    }

}
