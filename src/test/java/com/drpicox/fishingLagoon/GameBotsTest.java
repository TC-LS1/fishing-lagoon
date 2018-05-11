package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.presentation.GamePresentation;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class GameBotsTest {

    private AdminToken adminToken;
    private TestBootstrap bootstrap;
    private GamePresentation gamePresentation;

    @Before
    public void instance_bootstrap() throws SQLException {
        adminToken = new AdminToken("admin123");
        bootstrap = new TestBootstrap(adminToken);
        gamePresentation = bootstrap.getGamePresentation();
    }

    @Test
    public void bots_create() throws SQLException {
        var bot1 = gamePresentation.createBot(botToken("token1"), adminToken);
        var bot2 = gamePresentation.createBot(botToken("token2"), adminToken);
        var bots = gamePresentation.listBots();

        assertThat(bot1, hasProperty("id", is(bot(1))));
        assertThat(bot1, hasProperty("name", is("NoName")));
        assertThat(bot2, hasProperty("id", is(bot(2))));
        assertThat(bot2, hasProperty("name", is("NoName")));
        assertThat(bots, hasSize(2));
        assertThat(bots, hasItem(samePropertyValuesAs(bot1)));
        assertThat(bots, hasItem(samePropertyValuesAs(bot2)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void bots_create_fails_if_admin_token_does_not_match() throws SQLException {
        gamePresentation.createBot(botToken("token1"), new AdminToken("wrongToken"));
    }

    @Test
    public void bots_update() throws SQLException {
        var bot1c = gamePresentation.createBot(botToken("token1"), adminToken);
        var bot2c = gamePresentation.createBot(botToken("token2"), adminToken);
        var bot1u = gamePresentation.updateBot(botToken("token1"), "GERTY");

        var bot1g = gamePresentation.getBot(bot(1));
        var bot2g = gamePresentation.getBot(bot(2));
        var bots = gamePresentation.listBots();

        assertThat(bot1c, not(samePropertyValuesAs(bot1u)));
        assertThat(bot1u, hasProperty("id", is(bot(1))));
        assertThat(bot1u, hasProperty("name", is("GERTY")));
        assertThat(bot1u, samePropertyValuesAs(bot1g));
        assertThat(bot2c, samePropertyValuesAs(bot2g));

        assertThat(bots, hasSize(2));
        assertThat(bots, hasItem(samePropertyValuesAs(bot1g)));
        assertThat(bots, hasItem(samePropertyValuesAs(bot2g)));
    }


    private static BotId bot(int n) {
        return new BotId("bot" + n);
    }

    private BotToken botToken(String v) {
        return new BotToken(v);
    }
}
