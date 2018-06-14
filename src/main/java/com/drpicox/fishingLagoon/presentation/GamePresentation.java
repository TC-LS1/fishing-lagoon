package com.drpicox.fishingLagoon.presentation;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.business.GameController;
import com.drpicox.fishingLagoon.business.bots.Bot;
import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.bots.BotToken;
import com.drpicox.fishingLagoon.business.rounds.RoundCommands;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.rules.FishingLagoonRules;
import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeStamp;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.presentation.limits.BotsQueryLimits;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class GamePresentation {

    private GameController game;
    private FishingLagoonRules rules;
    private BotsQueryLimits limits;

    public GamePresentation(GameController game, FishingLagoonRules rules, BotsQueryLimits limits) {
        this.game = game;
        this.rules = rules;
        this.limits = limits;
    }

    public BotPresentation createBot(BotToken botToken, AdminToken adminToken) throws SQLException {
        return BotPresentation.from(game.createBot(botToken, adminToken));
    }

    public BotPresentation getBot(BotId botId) throws SQLException {
        return BotPresentation.from(game.getBot(botId));
    }

    public BotPresentation getBotByToken(BotToken botToken) throws SQLException {
        return BotPresentation.from(game.getBotByToken(botToken));
    }

    public List<BotPresentation> listBots() throws SQLException {
        return game.listBots().stream().map(bot -> BotPresentation.from(bot)).collect(Collectors.toList());
    }

    public BotPresentation updateBot(BotToken token, String name) throws SQLException {
        return BotPresentation.from(game.updateBot(token, name));
    }

    public RoundPresentation createRound(String roundText, BotToken token, TimeStamp now) throws SQLException {
        var botId = getBotId(token, now);
        return RoundPresentation.from(game.createRound(roundText, now), now, botId, rules);
    }

    public List<RoundPresentation> createTournamentRounds(TournamentId tournamentId, String tournamentText, AdminToken adminToken, TimeStamp now) throws SQLException {
        return game
                .createTournamentRounds(tournamentId, tournamentText, adminToken, now)
                .stream()
                .map(round -> RoundPresentation.from(round))
                .collect(Collectors.toList());
    }

    public RoundPresentation getRound(RoundId id, BotToken token, TimeStamp now) throws SQLException {
        var botId = mayGetBotId(token, now);
        return RoundPresentation.from(game.getRound(id), now, botId, rules);
    }

    public List<RoundPresentation> listRounds() throws SQLException {
        return game.listRounds().stream().map(round -> RoundPresentation.from(round)).collect(Collectors.toList());
    }

    public List<RoundPresentation> listActiveRounds(BotToken token, TimeStamp now) throws SQLException {
        BotId botId = mayGetBotId(token, now);
        return game.listActiveRounds(botId, now).stream()
                .map(round -> RoundPresentation.from(round, now, botId, rules))
                .collect(Collectors.toList());
    }

    public RoundPresentation seatBot(RoundId roundId, BotToken token, int lagoonIndex, TimeStamp now) throws SQLException {
        var botId = getBotId(token, now);
        return RoundPresentation.from(game.seatBot(roundId, botId, lagoonIndex, now), now, botId, rules);
    }

    public RoundPresentation commandBot(RoundId roundId, BotToken token, List<Action> actions, TimeStamp now) throws SQLException {
        var botId = getBotId(token, now);
        return RoundPresentation.from(game.commandBot(roundId, botId, actions, now), now, botId, rules);
    }

    private BotId getBotId(BotToken token, TimeStamp now) throws SQLException {
        verifyLimitAccess(token, now);

        Bot bot = game.getBotByToken(token);
        if (bot == null) throw new IllegalArgumentException("Invalid bot token");

        limits.trackAccess(token, now);
        return bot.getId();
    }

    private void verifyLimitAccess(BotToken token, TimeStamp now) {
        boolean isEmptyToken = token != null && token.equals(new BotToken(""));
        if (isEmptyToken) return;

        limits.verifyAccess(token, now);
    }

    private BotId mayGetBotId(BotToken token, TimeStamp now) throws SQLException {
        verifyLimitAccess(token, now);
        boolean isEmptyToken = token != null && token.equals(new BotToken(""));
        if (isEmptyToken) return null;

        var bot = game.getBotByToken(token);
        if (bot == null) return null;

        limits.trackAccess(token, now);
        return bot.getId();
    }

    public void setTournamentMode(boolean tournamentMode) {
        game.setTournamentMode(tournamentMode);
    }

    public String getTournamentScores(TournamentId tournamentId, AdminToken adminToken) throws SQLException {
        return game.getTournamentScores(tournamentId, adminToken);
    }

    public String getTournamentFishPopulations(TournamentId tournamentId, AdminToken adminToken) throws SQLException {
        return game.getTournamentFishPopulations(tournamentId, adminToken);
    }
}
