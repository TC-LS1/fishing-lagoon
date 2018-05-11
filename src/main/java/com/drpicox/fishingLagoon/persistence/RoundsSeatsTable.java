package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.rounds.Round;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.business.rounds.RoundSeat;
import com.drpicox.fishingLagoon.business.rounds.RoundSeats;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class RoundsSeatsTable {
    private Connection connection;

    public RoundsSeatsTable(Connection connection) throws SQLException {
        this.connection = connection;

        try (var stmt = connection.createStatement()) {
            stmt.execute("" +
                    "CREATE TABLE IF NOT EXISTS roundsSeats (" +
                    "  id VARCHAR(255)," +
                    "  botId VARCHAR(255)," +
                    "  lagoonIndex DECIMAL(20)," +
                    "  PRIMARY KEY (id, botId)" +
                    ")"
            );
        }
    }

    public RoundSeats get(RoundId id) throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT * FROM roundsSeats WHERE id = ?")) {
            pstmt.setString(1, id.getValue());
            try (var rs = pstmt.executeQuery()) {
                var seats = new HashMap<BotId, RoundSeat>();

                while (rs.next()) {
                    var botId = new BotId(rs.getString("botId"));
                    var lagoonIndex = rs.getInt("lagoonIndex");
                    var seat = new RoundSeat(lagoonIndex);
                    seats.put(botId, seat);
                }

                return new RoundSeats(seats);
            }
        }
    }

    public void save(RoundId id, RoundSeats seats) throws SQLException {
        try (var pstmt = connection.prepareStatement(
                "MERGE INTO roundsSeats(id, botId, lagoonIndex) VALUES(?, ?, ?)")
        ) {
            pstmt.setString(1, id.getValue());
            for (var botId: seats.getBots()) {
                var seat = seats.get(botId);

                pstmt.setString(2, botId.getValue());
                pstmt.setInt(3, seat.getLagoonIndex());
                pstmt.execute();
            }
        };
    }
}
