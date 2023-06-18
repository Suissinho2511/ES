package eu.europa.esig.dss.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionPool {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/esDB";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "0804";
    private static HikariDataSource dataSource = setupDataSource();

    public static HikariDataSource setupDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl(DB_URL);
        hikariConfig.setUsername(DB_USERNAME);
        hikariConfig.setPassword(DB_PASSWORD);
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setMaxLifetime(900000);
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(hikariConfig);
    }

    public static Connection establishConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static Statement createStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    public static void terminate(Statement statement, Connection connection) throws SQLException {
        statement.close();
        connection.close();
    }

    public static void terminateStatement(Statement statement) throws SQLException {
        statement.close();
    }
}
