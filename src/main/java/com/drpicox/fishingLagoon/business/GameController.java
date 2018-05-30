package com.drpicox.fishingLagoon.business;

import com.drpicox.fishingLagoon.business.bots.Bot;
import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.rules.FishingLagoonRules;
import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.parser.RoundParser;

import java.sql.SQLException;
import java.util.*;

public class GameController {
    private AdminToken adminToken;
    private BotsController botsController;
    private RoundsController roundsController;
    private RoundParser roundParser;
    private FishingLagoonRules rules;
    private boolean tournamentMode;

    public GameController(AdminToken adminToken, BotsController botsController, RoundsController roundsController, RoundParser roundParser, FishingLagoonRules rules) {
        this.adminToken = adminToken;
        this.botsController = botsController;
        this.roundsController = roundsController;
        this.roundParser = roundParser;
        this.rules = rules;
    }

    public synchronized void setTournamentMode(boolean tournamentMode) {
        this.tournamentMode = tournamentMode;
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
        if (tournamentMode == true) throw new IllegalStateException("Cannot create sparring rounds because tournament mode is ON");

        var descriptor = roundParser.parse(roundText);
        return roundsController.create(descriptor, now);
    }

    public synchronized List<Round> createTournamentRounds(TournamentId tournamentId, String tournamentText, AdminToken adminToken, TimeStamp now) throws SQLException {
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

    public synchronized List<Round> listActiveRounds(BotId botId, TimeStamp now) throws SQLException {
        return roundsController.listActives(botId, now);
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

    public synchronized String getTournamentScores(TournamentId tournamentId, AdminToken adminToken) throws SQLException {
        if (!this.adminToken.validate(adminToken)) throw new IllegalArgumentException("Invalid adminToken");

        var tournamentRounds = roundsController.listTournamentRounds(tournamentId);

        var bots = new HashSet<BotId>();
        for (var round: tournamentRounds) {
            var roundScore = rules.score(round);
            for (var bot: round.getBots()) {
                bots.add(bot);
            }
        }

        var totals = new HashMap<BotId, Long>();
        var partials = new HashMap<BotId, String>();
        for (var round: tournamentRounds) {
            var roundScore = rules.score(round);
            for (var bot: bots) {
                var score = roundScore.containsBot(bot) ? roundScore.getScore(bot) : -1;

                var total = totals.getOrDefault(bot, 0L);
                total += score;
                totals.put(bot, total);

                var partial = partials.getOrDefault(bot, "");
                partial += ";" + score;
                partials.put(bot, partial);
            }
        }

        var results = new ArrayList<String>();
        for (var botId: totals.keySet()) {
            var total = totals.get(botId);
            var partial = partials.get(botId);
            var token = botsController.getBotToken(botId);
            results.add(tournamentId + ";" + token + partial + ";" + total);
        }

        return String.join("\n", results.stream().toArray(String[]::new));
    }
}
