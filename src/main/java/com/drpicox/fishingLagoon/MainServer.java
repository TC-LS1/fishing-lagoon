package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.GameController;
import com.drpicox.fishingLagoon.business.bots.BotToken;

import java.sql.SQLException;

public class MainServer {

    public static void main(String[] args) throws SQLException {
        var adminTokenString = System.getenv("FISHING_LAGOON_ADMIN_TOKEN");
        var databaseFile = System.getenv("FISHING_LAGOON_DATABASE_FILE");
        var tournamentMode = System.getenv("FISHING_LAGOON_TOURNAMENT_MODE");
        var adminToken = new AdminToken(adminTokenString);

        var bootstrap = new Bootstrap(adminToken, databaseFile);
        var restController = bootstrap.getRestServerController();

        generateBotTokens(bootstrap.getGameController(), adminToken);
        setTournamentMode(bootstrap, tournamentMode);

        restController.start();
    }

    private static void generateBotTokens(GameController gameController, AdminToken adminToken) {
        var common = System.getenv("FISHING_LAGOON_GENERATE_COMMON");
        var tokensString = System.getenv("FISHING_LAGOON_GENERATE_TOKENS");

        if (common == null || tokensString == null) return;

        var tokens = tokensString.split(",");
        for (var token: tokens) {
            generateBotToken(common, token + "-0", gameController, adminToken);
            generateBotToken(common, token + "-1", gameController, adminToken);
            generateBotToken(common, token + "-2", gameController, adminToken);
        }
    }

    private static void setTournamentMode(Bootstrap bootstrap, String tournamentMode) throws SQLException {
        var mode = false;

        if (tournamentMode != null) {
            switch (tournamentMode.toLowerCase()) {
                case "on":
                case "1":
                case "true":
                case "t":
                case "yes":
                case "y":
                    mode = true;
            }
        }

        bootstrap.getGameController().setTournamentMode(mode);
        bootstrap.getQueryLimits().setSkipLimits(mode);
    }

    private static void generateBotToken(String common, String token, GameController gameController, AdminToken adminToken) {
        try {
            var botToken = new BotToken(common + "-" + token);
            if (gameController.getBotByToken(botToken) == null) {
                gameController.createBot(botToken, adminToken);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
