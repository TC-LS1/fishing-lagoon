package com.drpicox.fishingLagoon.presentation;

import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.actions.ActionParser;
import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class RestServerController {

    private ActionParser actionParser;
    private GamePresentation game;
    private Gson gson;

    public RestServerController(ActionParser actionParser, GamePresentation game, Gson gson) {
        this.actionParser = actionParser;
        this.game = game;
        this.gson = gson;
    }

    public void start() {
        // http://sparkjava.com/tutorials/heroku
        port(getHerokuAssignedPort());

        get("/hello", (rq, rs) -> {
           var m = new HashMap();
           m.put("hello", "world");
           return m;
        }, gson::toJson);

        get("/", (a,b) -> "Hello");

        get("/bots", this::listBots, gson::toJson);
        post("/bots", this::createBot, gson::toJson);
        get("/bots/:botToken", this::getBotByToken, gson::toJson);
        put("/bots/:botToken", this::updateBot, gson::toJson);

        post("/rounds", this::createRound, gson::toJson);
        get("/rounds", this::listRounds, gson::toJson);
        get("/rounds/:roundId", this::getRound, gson::toJson);
        put("/rounds/:roundId/seats/:botToken", this::seatBot, gson::toJson);
        put("/rounds/:roundId/commands/:botToken", this::commandBot, gson::toJson);

        post("/tournaments", this::createTournament, gson::toJson);
        get("/tournaments", this::getTournament);
        get("/tournaments/fish-populations", this::getTournamentFishPopulations);

        exception(IllegalArgumentException.class, this::handle);
        exception(IllegalStateException.class, this::handle);
        exception(SQLException.class, this::handle);
    }

    private Object createTournament(Request request, Response response) throws SQLException {
        var adminToken = new AdminToken(request.queryParams("adminToken"));
        var tournamentId = new TournamentId(request.queryParams("tournamentId"));
        var rounds = request.body();
        return game.createTournamentRounds(tournamentId, rounds, adminToken, now());
    }

    private Object getTournament(Request request, Response response) throws SQLException {
        var adminToken = new AdminToken(request.queryParams("adminToken"));
        var tournamentId = new TournamentId(request.queryParams("tournamentId"));
        var result = game.getTournamentScores(tournamentId, adminToken);
        response.type("text/plain");
        response.body(result + '\n');
        return result + '\n';
    }

    private Object getTournamentFishPopulations(Request request, Response response) throws SQLException {
        var adminToken = new AdminToken(request.queryParams("adminToken"));
        var tournamentId = new TournamentId(request.queryParams("tournamentId"));
        var result = game.getTournamentFishPopulations(tournamentId, adminToken);
        response.type("text/plain");
        response.body(result + '\n');
        return result + '\n';
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    private <T extends Throwable> void handle(T error, Request request, Response response) {
        response.status(400);
        response.body(error.getClass().getSimpleName() + ": " + error.getMessage() + ".");
    }

    private Object commandBot(Request request, Response response) throws SQLException {
        var roundId = new RoundId(request.params("roundId"));
        var botToken = new BotToken(request.params("botToken"));
        var actions = actionParser.parse(request.body());
        return game.commandBot(roundId, botToken, actions, now());
    }

    private Object seatBot(Request request, Response response) throws SQLException {
        var roundId = new RoundId(request.params("roundId"));
        var botToken = new BotToken(request.params("botToken"));
        var lagoonIndex = Integer.parseInt(request.body());
        return game.seatBot(roundId, botToken, lagoonIndex, now());
    }

    private Object getRound(Request request, Response response) throws SQLException {
        var roundId = new RoundId(request.params("roundId"));
        var botToken = new BotToken(request.queryParams("botToken"));
        return game.getRound(roundId, botToken, now());
    }

    private Object listRounds(Request request, Response response) throws SQLException {
        var isActive = request.queryParams("isActive");
        if (isActive != null && isActive.length() > 0) {
            var botToken = new BotToken(request.params("botToken"));
            return game.listActiveRounds(botToken, now());
        } else {
            return game.listRounds();
        }
    }

    private Object createRound(Request request, Response response) throws SQLException {
        var botToken = new BotToken(request.queryParams("botToken"));
        var roundText = request.body();
        return game.createRound(roundText, botToken, now());
    }

    private Object listBots(Request request, Response response) throws SQLException {
        return game.listBots();
    }

    private Object createBot(Request request, Response response) throws SQLException {
        var botToken = new BotToken(request.queryParams("botToken"));
        var adminToken = new AdminToken(request.queryParams("adminToken"));
        return game.createBot(botToken, adminToken);
    }

    private Object getBotByToken(Request request, Response response) throws SQLException {
        var botToken = new BotToken(request.params("botToken"));
        return game.getBotByToken(botToken);
    }

    private Object updateBot(Request request, Response response) throws SQLException {
        var botToken = new BotToken(request.params("botToken"));
        var map = gson.fromJson(request.body(), Map.class);
        return game.updateBot(botToken, (String) map.get("name"));
    }

    private TimeStamp now() {
        return new TimeStamp(System.currentTimeMillis());
    }

}
