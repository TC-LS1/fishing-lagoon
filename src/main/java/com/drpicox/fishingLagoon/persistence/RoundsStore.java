package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoundsStore {

    private RoundsMetadataTable metadataTable;
    private RoundsDescriptorsTable descriptorsTable;
    private RoundsSeatsTable seatsTable;
    private RoundsCommandsTable commandsTable;

    public RoundsStore(RoundsMetadataTable metadataTable, RoundsDescriptorsTable descriptorsTable, RoundsSeatsTable seatsTable, RoundsCommandsTable commandsTable) throws SQLException {
        this.metadataTable = metadataTable;
        this.descriptorsTable = descriptorsTable;
        this.seatsTable = seatsTable;
        this.commandsTable = commandsTable;
    }

    public Round get(RoundId id) throws SQLException {
        var metadata = metadataTable.get(id);
        if (metadata == null) return null;

        var descriptor = descriptorsTable.get(id);
        var seats = seatsTable.get(id);
        var commands = commandsTable.get(id);

        return new Round(metadata, descriptor, seats, commands);
    }

    public Round getLastRound() throws SQLException {
        var id = metadataTable.getLastRoundId();
        if (id == null) return null;

        return get(id);
    }

    public List<Round> list() throws SQLException {
        var ids = metadataTable.listIds();
        return getRounds(ids);
    }

    public List<Round> listActives(TimeStamp now) throws SQLException {
        var ids = metadataTable.listActiveIds(now);
        return getRounds(ids);
    }

    public List<Round> listTournamentRounds(TournamentId tournamentId) throws SQLException {
        var ids = metadataTable.listTournamentIds(tournamentId);
        return getRounds(ids);
    }

    private List<Round> getRounds(List<RoundId> ids) throws SQLException {
        List<Round> result = new ArrayList<>();
        for (var id: ids) {
            result.add(get(id));
        }

        return result;
    }

    public Round save(Round round) throws SQLException {
        var id = round.getId();
        descriptorsTable.save(id, round.getDescriptor());
        seatsTable.save(id, round.getSeats());
        commandsTable.save(id, round.getCommands());
        metadataTable.save(round.getMetadata());
        return round;
    }
}
