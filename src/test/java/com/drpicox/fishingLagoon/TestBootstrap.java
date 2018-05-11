package com.drpicox.fishingLagoon;

import com.drpicox.fishingLagoon.business.AdminToken;
import com.drpicox.fishingLagoon.common.IdGenerator;
import com.drpicox.fishingLagoon.common.SequenceIdGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class TestBootstrap extends Bootstrap {

    public TestBootstrap(AdminToken adminToken) {
        super(adminToken);
    }

    private Connection connection;
    public Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:h2:mem:", "sa", "");
        }
        return connection;
    }

    private Map<String,IdGenerator> idGenerators = new HashMap<>();
    public IdGenerator getIdGenerator(String type) {
        var idGenerator = idGenerators.get(type);
        if (idGenerator == null) {
            idGenerator = new SequenceIdGenerator(type);
            idGenerators.put(type, idGenerator);
        }
        return idGenerator;
    }

    public void dumpTable(String tableName) throws SQLException {
        try (var result = getConnection().createStatement().executeQuery("SELECT * FROM " + tableName)) {
            var meta = result.getMetaData();

            for (int i = 0; i < meta.getColumnCount(); i++) {
                System.out.print(meta.getColumnName(i + 1));
                System.out.print(",");
            }
            System.out.println("");

            while (result.next()) {
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    System.out.print(result.getString(i + 1));
                    System.out.print(",");
                }
                System.out.println("");
            }
            System.out.println("-----");
            System.out.println("");
        }
    }
}
