package com.drpicox.fishingLagoon.rounds;

import com.drpicox.fishingLagoon.engine.RoundEngine;
import com.drpicox.fishingLagoon.parser.RoundDescriptor;
import com.drpicox.fishingLagoon.parser.RoundParser;

import java.sql.Connection;
import java.sql.SQLException;

public class RoundsDescriptorsStore {
    private Connection connection;
    private RoundParser roundParser;

    public RoundsDescriptorsStore(Connection connection, RoundParser roundParser) throws SQLException {
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

    public void create(Round round, RoundDescriptor roundDescriptor) throws SQLException {
        var descriptorText = roundParser.stringify(roundDescriptor);
        if (descriptorText.length() > 4000) {
            throw new IllegalArgumentException("Round description too long");
        }

        try (var pstmt = connection.prepareStatement(
                "MERGE INTO roundsDescrs(id, descriptorText) VALUES(?, ?)")
        ) {
            pstmt.setString(1, round.getId().getValue());
            pstmt.setString(2, descriptorText);
            pstmt.execute();
        };
    }

    public RoundEngine get(Round round) throws SQLException {
        try (var pstmt = connection.prepareStatement("SELECT * FROM roundsDescrs WHERE id = ?")) {
            pstmt.setString(1, round.getId().getValue());
            try (var rs = pstmt.executeQuery()) {
                if (!rs.next()) return null;
                var descriptorText = rs.getString("descriptorText");
                var roundDescriptor = roundParser.parse(descriptorText);
                var roundEngine = new RoundEngine(round.getId(), round.getStartTs(), roundDescriptor);
                return roundEngine;
            }
        }
    }
}
