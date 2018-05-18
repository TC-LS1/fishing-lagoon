package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.GameController;
import com.drpicox.fishingLagoon.business.RoundsController;
import com.drpicox.fishingLagoon.business.tournaments.SparringTournament;
import com.drpicox.fishingLagoon.business.tournaments.Tournament;
import com.drpicox.fishingLagoon.common.actions.ActionParser;
import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.BotsController;
import com.drpicox.fishingLagoon.persistence.*;
import com.drpicox.fishingLagoon.common.IdGenerator;
import com.drpicox.fishingLagoon.common.UuidIdGenerator;
import com.drpicox.fishingLagoon.common.parser.GsonFactory;
import com.drpicox.fishingLagoon.common.parser.PropsParser;
import com.drpicox.fishingLagoon.common.parser.RoundParser;
import com.drpicox.fishingLagoon.presentation.GamePresentation;
import com.drpicox.fishingLagoon.presentation.RestServerController;
import com.drpicox.fishingLagoon.business.rules.FishingLagoonRuleFishing;
import com.drpicox.fishingLagoon.business.rules.FishingLagoonRuleProcreation;
import com.drpicox.fishingLagoon.business.rules.FishingLagoonRules;
import com.drpicox.fishingLagoon.business.rules.FishingLagoonSetupRuleFishPopulation;
import com.drpicox.fishingLagoon.presentation.limits.BotsQueryLimits;
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

    private GamePresentation gamePresentation;
    public GamePresentation getGamePresentation() throws SQLException {
        if (gamePresentation == null) {
            gamePresentation = new GamePresentation(getGameController(), getFishingLagoonRules(), new BotsQueryLimits());
        }
        return gamePresentation;
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

    private RestServerController restServerController;
    public RestServerController getRestServerController() throws SQLException {
        if (restServerController == null) {
            restServerController = new RestServerController(getActionParser(), getGamePresentation(), getGson());
        }
        return restServerController;
    }

    private RoundsController roundsController;
    public RoundsController getRoundsController() throws SQLException {
        if (roundsController == null) {
            var roundsCommandsTable = new RoundsCommandsTable(getActionParser(), getConnection());
            var roundsDescriptionsTable = new RoundsDescriptorsTable(getConnection(), getRoundParser());
            var roundsSeatsTable = new RoundsSeatsTable(getConnection());
            var roundsStartsTable = new RoundsMetadataTable(getConnection());
            var roundsStore = new RoundsStore(roundsStartsTable, roundsDescriptionsTable, roundsSeatsTable, roundsCommandsTable);
            var idGenerator = getIdGenerator("round");
            roundsController = new RoundsController(idGenerator, roundsStore, getTournament());
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

    private Tournament tournament;
    public Tournament getTournament() {
        if (tournament == null) {
            tournament = new SparringTournament();
        }
        return tournament;
    }
    public void configureTournament(Tournament tournament) {
        if (this.tournament != null) throw new IllegalStateException("tournament already instanced");
        this.tournament = tournament;
    }
}
