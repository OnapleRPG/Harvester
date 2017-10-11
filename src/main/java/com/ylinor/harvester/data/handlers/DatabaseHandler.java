package com.ylinor.harvester.data.handlers;

import com.ylinor.harvester.Harvester;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DatabaseHandler {
    private static String JDBC_URL = "jdbc:h2:./harvester";

    private static SqlService sqlService;

    public static DataSource getDatasource() throws SQLException {
        if (sqlService == null) {
            sqlService = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sqlService.getDataSource(JDBC_URL);
    }
}
