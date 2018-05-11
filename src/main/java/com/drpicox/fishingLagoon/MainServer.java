package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.GameController;
import com.drpicox.fishingLagoon.business.bots.BotToken;

import java.sql.SQLException;

public class MainServer {

    public static void main(String[] args) throws SQLException {
        var adminTokenString = System.getenv("FISHING_LAGOON_ADMIN_TOKEN");
        var adminToken = new AdminToken(adminTokenString);

        var bootstrap = new Bootstrap(adminToken);
        var restController = bootstrap.getRestPresentation();

        generateBotTokens(bootstrap.getGameController(), adminToken);

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
