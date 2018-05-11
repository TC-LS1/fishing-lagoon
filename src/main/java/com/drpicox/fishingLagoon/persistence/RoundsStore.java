package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoundsStore {

    private RoundsStartsTable startsTable;
    private RoundsDescriptorsTable descriptorsTable;
    private RoundsSeatsTable seatsTable;
    private RoundsCommandsTable commandsTable;

    public RoundsStore(RoundsStartsTable startsTable, RoundsDescriptorsTable descriptorsTable, RoundsSeatsTable seatsTable, RoundsCommandsTable commandsTable) throws SQLException {
        this.startsTable = startsTable;
        this.descriptorsTable = descriptorsTable;
        this.seatsTable = seatsTable;
        this.commandsTable = commandsTable;
    }

    public Round get(RoundId id) throws SQLException {
        var start = startsTable.get(id);
        var descriptor = descriptorsTable.get(id);
        var seats = seatsTable.get(id);
        var commands = commandsTable.get(id);

        if (start == null) return null;
        return new Round(id, start, descriptor, seats, commands);
    }

    public List<Round> list() throws SQLException {
        List<Round> result = new ArrayList<>();

        var ids = startsTable.listIds();
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
        startsTable.save(id, round.getStartTs());
        return round;
    }
}
