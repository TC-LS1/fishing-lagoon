package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.bots.Bot;
import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.bots.BotToken;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class BotsStore {
    private final Connection connection;

    public BotsStore(Connection connection) throws SQLException {
        this.connection = connection;

        try (var stmt = connection.createStatement()) {
            stmt.execute("" +
                    "CREATE TABLE IF NOT EXISTS bots (" +
                    "  id VARCHAR(255) PRIMARY KEY," +
                    "  name VARCHAR(255)," +
                    "  token VARCHAR(255) UNIQUE" +
                    ")"
            );
        }
    }

    public Bot create(Bot bot, BotToken token) throws SQLException {
        try (var pstmt = connection.prepareStatement("INSERT INTO bots (id, token) VALUES (?, ?)")) {
            pstmt.setString(1, bot.getId().getValue());
            pstmt.setString(2, token.getValue());
            pstmt.execute();
        }

        return save(bot);
    }

    public Bot get(BotId id) throws SQLException {
        try (var pstmt = connection.prepareStatement("SELECT * FROM bots WHERE id = ?")) {
            pstmt.setString(1, id.getValue());
            try (ResultSet rs = pstmt.executeQuery()) {
                return nextBot(rs);
            }
        }
    }

    public Bot getByToken(BotToken token) throws SQLException {
        try (var pstmt = connection.prepareStatement("SELECT * FROM bots WHERE token = ?")) {
            pstmt.setString(1, token.getValue());
            try (ResultSet rs = pstmt.executeQuery()) {
                return nextBot(rs);
            }
        }
    }

    public List<Bot> list() throws SQLException {
        var result = new LinkedList<Bot>();

        try (var pstmt = connection.prepareStatement("SELECT * FROM bots")) {
            try (var rs = pstmt.executeQuery()) {
                var bot = nextBot(rs);
                while (bot != null) {
                    result.add(bot);
                    bot = nextBot(rs);
                }
            }
        }

        return result;
    }

    public Bot save(Bot bot) throws SQLException {
        try (var pstmt = connection.prepareStatement("UPDATE bots SET name = ? WHERE id = ?")) {
            pstmt.setString(1, bot.getName());
            pstmt.setString(2, bot.getId().toString());
            pstmt.execute();
        };

        return bot;
    }

    private Bot nextBot(ResultSet rs) throws SQLException {
        if (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            return new Bot(new BotId(id), name);
        } else {
            return null;
        }
    }

}
