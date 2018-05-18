package com.drpicox.fishingLagoon.business;

import com.drpicox.fishingLagoon.business.bots.Bot;
import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.parser.RoundParser;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class GameController {
    private AdminToken adminToken;
    private BotsController botsController;
    private RoundsController roundsController;
    private RoundParser roundParser;

    public GameController(AdminToken adminToken, BotsController botsController, RoundsController roundsController, RoundParser roundParser) {
        this.adminToken = adminToken;
        this.botsController = botsController;
        this.roundsController = roundsController;
        this.roundParser = roundParser;
    }

    public synchronized Bot createBot(BotToken botToken, AdminToken adminToken) throws SQLException {
        if (!this.adminToken.validate(adminToken)) throw new IllegalArgumentException("Invalid adminToken");

        return botsController.create(botToken);
    }

    public synchronized Bot getBot(BotId botId) throws SQLException {
        return botsController.getBot(botId);
    }

    public synchronized Bot getBotByToken(BotToken botToken) throws SQLException {
        return botsController.getBotByToken(botToken);
    }

    public synchronized List<Bot> listBots() throws SQLException {
        return botsController.list();
    }

    public synchronized Bot updateBot(BotToken token, String name) throws SQLException {
        return botsController.update(token, name);
    }

    public synchronized Round createRound(String roundText, TimeStamp now) throws SQLException {
        var descriptor = roundParser.parse(roundText);
        return roundsController.create(descriptor, now);
    }

    public synchronized List<Round> createTournamentRounds(TournamentId tournamentId, String tournamentText, AdminToken adminToken, TimeStamp now) {
        if (!this.adminToken.validate(adminToken)) throw new IllegalArgumentException("Invalid adminToken");

        var descriptors = roundParser.parseRounds(tournamentText);
        return roundsController.createTournamentRounds(tournamentId, descriptors, now);
    }

    public Round getRound(RoundId id) throws SQLException {
        return roundsController.getRound(id);
    }

    public synchronized List<Round> listRounds() throws SQLException {
        return roundsController.list();
    }

    public synchronized Round seatBot(RoundId roundId, BotId botId, int lagoonIndex, TimeStamp ts) throws SQLException {
        return roundsController.seatBot(roundId, botId, lagoonIndex, ts);
    }

    public synchronized Round commandBot(RoundId roundId, BotId botId, List<Action> actions, TimeStamp ts) throws SQLException {
        return roundsController.commandBot(roundId, botId, actions, ts);
    }

    private BotId getBotId(BotToken token) throws SQLException {
        Bot bot = botsController.getBotByToken(token);
        if (bot == null) throw new IllegalArgumentException("Invalid bot token");
        return bot.getId();
    }

}
