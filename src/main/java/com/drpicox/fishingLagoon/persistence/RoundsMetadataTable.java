package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.rounds.RoundMetadata;
import com.drpicox.fishingLagoon.business.tournaments.TournamentId;
import com.drpicox.fishingLagoon.common.TimeStamp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class RoundsMetadataTable {
    private Connection connection;

    public RoundsMetadataTable(Connection connection) throws SQLException {
        this.connection = connection;

        try (var stmt = connection.createStatement()) {
            stmt.execute("" +
                    "CREATE TABLE IF NOT EXISTS roundsMetadatas (" +
                    "  id VARCHAR(255) PRIMARY KEY," +
                    "  start DECIMAL(20), " +
                    "  end DECIMAL(20), " +
                    "  tournamentId VARCHAR(255), " +
                    ")"
            );
        }
    }

    public RoundMetadata get(RoundId id) throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT * FROM roundsMetadatas WHERE id = ?")) {
            pstmt.setString(1, id.getValue());
            try (var rs = pstmt.executeQuery()) {
                return next(rs);
            }
        }
    }

    public RoundId getLastRoundId() throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT id FROM roundsMetadatas WHERE start = SELECT MAX(start) FROM roundsMetadatas")) {
            try (var rs = pstmt.executeQuery()) {
                return nextRoundId(rs);
            }
        }
    }

    public List<RoundId> listIds() throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT id FROM roundsMetadatas")) {
            return getRoundIds(pstmt);
        }
    }

    public List<RoundId> listActiveIds(TimeStamp now) throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT id FROM roundsMetadatas WHERE start <= ? AND ? < end")) {
            pstmt.setLong(1, now.getMilliseconds());
            pstmt.setLong(2, now.getMilliseconds());

            return getRoundIds(pstmt);
        }
    }

    private List<RoundId> getRoundIds(PreparedStatement pstmt) throws SQLException {
        var result = new LinkedList<RoundId>();
        try (var rs = pstmt.executeQuery()) {
            RoundId id = nextRoundId(rs);
            while (id != null) {
                result.add(id);
                id = nextRoundId(rs);
            }
        }

        return result;
    }

    public RoundMetadata save(RoundMetadata metadata) throws SQLException {
        try (var pstmt = connection.prepareStatement(
                "MERGE INTO roundsMetadatas(id, start, end, tournamentId) VALUES(?, ?, ?, ?)")
        ) {
            pstmt.setString(1, metadata.getId().getValue());
            pstmt.setLong(2, metadata.getStartTs().getMilliseconds());
            pstmt.setLong(3, metadata.getEndTs().getMilliseconds());
            pstmt.setString(4, metadata.getTournamentId().getValue());
            pstmt.execute();
        };

        return metadata;
    }

    private RoundMetadata next(ResultSet rs) throws SQLException {
        if (rs.next()) {
            var id = rs.getString("id");
            var start = rs.getLong("start");
            var end = rs.getLong("end");
            var tournamentId = rs.getString("tournamentId");
            return new RoundMetadata(
                    new RoundId(id),
                    new TournamentId(tournamentId),
                    new TimeStamp(start),
                    new TimeStamp(end)
            );
        } else {
            return null;
        }
    }

    private RoundId nextRoundId(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String id = rs.getString("id");
            return new RoundId(id);
        } else {
            return null;
        }
    }
}
