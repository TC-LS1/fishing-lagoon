package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.actions.ActionParser;
import com.drpicox.fishingLagoon.admin.AdminToken;
import com.drpicox.fishingLagoon.bots.BotsController;
import com.drpicox.fishingLagoon.bots.BotsStore;
import com.drpicox.fishingLagoon.common.IdGenerator;
import com.drpicox.fishingLagoon.common.UuidIdGenerator;
import com.drpicox.fishingLagoon.parser.GsonFactory;
import com.drpicox.fishingLagoon.parser.PropsParser;
import com.drpicox.fishingLagoon.parser.RoundParser;
import com.drpicox.fishingLagoon.rounds.*;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleFishing;
import com.drpicox.fishingLagoon.rules.FishingLagoonRuleProcreation;
import com.drpicox.fishingLagoon.rules.FishingLagoonRules;
import com.drpicox.fishingLagoon.rules.FishingLagoonSetupRuleFishPopulation;
import com.google.gson.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.util.Arrays.asList;

public class Bootstrap {

    private AdminToken adminToken;

    public Bootstrap(AdminToken adminToken) {
        this.adminToken = adminToken;
    }

    private Connection connection;
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            var databaseFile = System.getenv("FISHING_LAGOON_DATABASE_FILE");
            connection = DriverManager.getConnection("jdbc:h2:" + databaseFile, "sa", "");
        }
        return connection;
    }

    private ActionParser actionParser;
    public ActionParser getActionParser() {
        if (actionParser == null) {
            actionParser = new ActionParser();
        }
        return actionParser;
    }

    public AdminToken getAdminToken() {
        return adminToken;
    }

    private BotsController botsController;
    public BotsController getBotsController() throws SQLException {
        if (botsController == null) {
            var botsStore = new BotsStore(getConnection());
            var idGenerator = getIdGenerator("bot");
            botsController = new BotsController(botsStore, idGenerator);
        }
        return botsController;
    }

    private IdGenerator idGenerator;
    public IdGenerator getIdGenerator(String type) {
        if (idGenerator == null) {
            idGenerator = new UuidIdGenerator();
        }
        return idGenerator;
    }

    private GameController gameController;
    public GameController getGameController() throws SQLException {
        if (gameController == null) {
            gameController = new GameController(adminToken, getBotsController(), getRoundsController(), getRoundParser());
        }
        return gameController;
    }

    private GsonFactory gsonFactory;
    public Gson getGson() {
        if (gsonFactory == null) {
            gsonFactory = new GsonFactory(getActionParser());
        }
        return gsonFactory.get();
    }

    private FishingLagoonRules fishingLagoonRules;
    public FishingLagoonRules getFishingLagoonRules() {
        if (fishingLagoonRules == null) {
            fishingLagoonRules = new FishingLagoonRules(asList(
                    new FishingLagoonSetupRuleFishPopulation()
            ), asList(
                    new FishingLagoonRuleFishing(),
                    new FishingLagoonRuleProcreation()
            ));
        }
        return fishingLagoonRules;
    }

    private RestController restController;
    public RestController getRestController() throws SQLException {
        if (restController == null) {
            restController = new RestController(getActionParser(), getGameController(), getGson());
        }
        return restController;
    }

    private RoundsController roundsController;
    public RoundsController getRoundsController() throws SQLException {
        if (roundsController == null) {
            var roundsCommandsStore = new RoundsCommandsStore(getActionParser(), getConnection());
            var roundsDescriptionsStore = new RoundsDescriptorsStore(getConnection(), getRoundParser());
            var roundsSeatsStore = new RoundsSeatsStore(getConnection());
            var roundsStore = new RoundsStore(getConnection());
            var idGenerator = getIdGenerator("round");
            roundsController = new RoundsController(idGenerator, getFishingLagoonRules(), roundsCommandsStore, roundsDescriptionsStore, roundsSeatsStore, roundsStore);
        }
        return roundsController;
    }

    private RoundParser roundParser;
    public RoundParser getRoundParser() {
        if (roundParser == null) {
            roundParser = new RoundParser(new PropsParser());
        }
        return roundParser;
    }


}
