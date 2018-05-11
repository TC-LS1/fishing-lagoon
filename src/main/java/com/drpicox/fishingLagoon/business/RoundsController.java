package com.drpicox.fishingLagoon.business;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundDescriptor;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.persistence.RoundsStore;
import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.common.actions.Action;
import com.drpicox.fishingLagoon.common.IdGenerator;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.sql.SQLException;
import java.util.List;

public class RoundsController {
    private static final long MIN_ROUND_DURATION = 60000L;

    private IdGenerator idGenerator;
    private RoundsStore roundsStore;

    public RoundsController(IdGenerator idGenerator, RoundsStore roundsStore) {
        this.idGenerator = idGenerator;
        this.roundsStore = roundsStore;
    }

    public Round create(RoundDescriptor descriptor, TimeStamp now) throws SQLException {
        if (descriptor.getTotalMilliseconds() < MIN_ROUND_DURATION) throw new IllegalArgumentException("Round too fast");
        if (hasActiveRound(now)) throw new IllegalArgumentException("There is an active round");

        var id = new RoundId(idGenerator.next());
        var round = new Round(id, now, descriptor);

        return roundsStore.save(round);
    }

    public List<Round> list() throws SQLException {
        return roundsStore.list();
    }

    public Round getRound(RoundId id) throws SQLException {
        return roundsStore.get(id);
    }

    public Round seatBot(RoundId id, BotId botId, int lagoonIndex, TimeStamp ts) throws SQLException {
        var round = roundsStore.get(id);

        if (!round.getState(ts).isAcceptingSeats()) throw new IllegalStateException("It is not time for seating");

        round.seatBot(botId, lagoonIndex);
        return roundsStore.save(round);
    }

    public Round commandBot(RoundId id, BotId botId, List<Action> actions, TimeStamp ts) throws SQLException {
        var round = roundsStore.get(id);

        if (!round.getState(ts).isAcceptingCommands()) throw new IllegalStateException("It is not time for commanding");
        round.commandBot(botId, actions);
        return roundsStore.save(round);
    }

    private Round getActiveRound(TimeStamp ts) throws SQLException {
        for (var round: roundsStore.list()) {
            if (round.getState(ts).isActive()) return round;
        }
        return null;
    }

    private boolean hasActiveRound(TimeStamp now) throws SQLException {
        return getActiveRound(now) != null;
    }
}
