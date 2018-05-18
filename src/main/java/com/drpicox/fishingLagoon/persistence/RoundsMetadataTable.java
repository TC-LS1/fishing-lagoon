package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.rounds.RoundMetadata;
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
                    "CREATE TABLE IF NOT EXISTS roundsStarts (" +
                    "  id VARCHAR(255) PRIMARY KEY," +
                    "  startTs DECIMAL(20)" +
                    ")"
            );
        }
    }

    public RoundMetadata get(RoundId id) throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT * FROM roundsStarts WHERE id = ?")) {
            pstmt.setString(1, id.getValue());
            try (var rs = pstmt.executeQuery()) {
                return nextStart(rs);
            }
        }
    }

    public List<RoundId> listIds() throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT id FROM roundsStarts")) {
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
    }

    public RoundMetadata save(RoundId id, RoundMetadata metadata) throws SQLException {
        try (var pstmt = connection.prepareStatement(
                "MERGE INTO roundsStarts(id, startTs) VALUES(?, ?)")
        ) {
            pstmt.setString(1, id.getValue());
            pstmt.setLong(2, start.getMilliseconds());
            pstmt.execute();
        };

        return start;
    }

    private TimeStamp nextStart(ResultSet rs) throws SQLException {
        if (rs.next()) {
            long startTs = rs.getLong("startTs");
            return new TimeStamp(startTs);
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
