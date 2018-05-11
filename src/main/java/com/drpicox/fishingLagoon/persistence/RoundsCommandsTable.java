package com.drpicox.fishingLagoon.persistence;

import com.drpicox.fishingLagoon.business.bots.BotId;
import com.drpicox.fishingLagoon.business.rounds.RoundCommand;
import com.drpicox.fishingLagoon.business.rounds.RoundCommands;
import com.drpicox.fishingLagoon.business.rounds.RoundId;
import com.drpicox.fishingLagoon.common.actions.ActionParser;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

public class RoundsCommandsTable {
    private ActionParser actionParser;
    private Connection connection;

    public RoundsCommandsTable(ActionParser actionParser, Connection connection) throws SQLException {
        this.actionParser = actionParser;
        this.connection = connection;

        try (var stmt = connection.createStatement()) {
            stmt.execute("" +
                    "CREATE TABLE IF NOT EXISTS roundsCommands (" +
                    "  id VARCHAR(255)," +
                    "  botId VARCHAR(255)," +
                    "  actionsText TEXT," +
                    "  PRIMARY KEY (id, botId)" +
                    ")"
            );
        }
    }

    public RoundCommands get(RoundId id) throws SQLException {
        try (var pstmt = this.connection.prepareStatement("SELECT * FROM roundsCommands WHERE id = ?")) {
            pstmt.setString(1, id.getValue());
            try (var rs = pstmt.executeQuery()) {
                var commands = new HashMap<BotId, RoundCommand>();

                while (rs.next()) {
                    var botId = new BotId(rs.getString("botId"));
                    var actionsText = rs.getString("actionsText");
                    var actions = actionParser.parse(actionsText);

                    commands.put(botId, new RoundCommand(actions));
                }

                return new RoundCommands(commands);
            }
        }
    }

    public void save(RoundId id, RoundCommands commands) throws SQLException {
        try (var pstmt = connection.prepareStatement(
                "MERGE INTO roundsCommands(id, botId, actionsText) VALUES(?, ?, ?)")
        ) {
            pstmt.setString(1, id.getValue());

            for (var botId: commands.getBots()) {
                var command = commands.get(botId);
                String actionsText = actionParser.toString(command.getActions());
                pstmt.setString(2, botId.getValue());
                pstmt.setString(3, actionsText);
                pstmt.executeUpdate();
            }
        }
    }
}
