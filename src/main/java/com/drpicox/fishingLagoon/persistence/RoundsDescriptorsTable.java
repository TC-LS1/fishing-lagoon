package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundDescriptor;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.common.parser.RoundParser;

import java.sql.Connection;
import java.sql.SQLException;

public class RoundsDescriptorsTable {
    private Connection connection;
    private RoundParser roundParser;

    public RoundsDescriptorsTable(Connection connection, RoundParser roundParser) throws SQLException {
        this.connection = connection;
        this.roundParser = roundParser;

        try (var stmt = connection.createStatement()) {
            stmt.execute("" +
                    "CREATE TABLE IF NOT EXISTS roundsDescrs (" +
                    "  id VARCHAR(255) PRIMARY KEY," +
                    "  descriptorText TEXT" +
                    ")"
            );
        }
    }

    public RoundDescriptor get(RoundId id) throws SQLException {
        try (var pstmt = connection.prepareStatement("SELECT * FROM roundsDescrs WHERE id = ?")) {
            pstmt.setString(1, id.getValue());
            try (var rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                var descriptorText = rs.getString("descriptorText");
                return roundParser.parse(descriptorText);
            }
        }
    }

    public void save(RoundId id, RoundDescriptor roundDescriptor) throws SQLException {
        var descriptorText = roundParser.stringify(roundDescriptor);
        if (descriptorText.length() > 4000) {
            throw new IllegalArgumentException("Round description too long");
        }

        try (var pstmt = connection.prepareStatement(
                "MERGE INTO roundsDescrs(id, descriptorText) VALUES(?, ?)")
        ) {
            pstmt.setString(1, id.getValue());
            pstmt.setString(2, descriptorText);
            pstmt.execute();
        };
    }
}
