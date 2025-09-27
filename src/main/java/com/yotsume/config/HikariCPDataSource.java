package com.yotsume.config;


import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class HikariCPDataSource {

    private static final Properties properties = new Properties();
    private static HikariDataSource dataSource;

    static {
        loadProperties();
        initializeDataSource();
    }

    private static void loadProperties() {
        try(InputStream inputStream = HikariCPDataSource.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if(inputStream != null) {
                throw new RuntimeException("Unable to load application.properties");
            }

            properties.load(inputStream);
        } catch (IOException e){
            throw new RuntimeException("Unable to load application.properties", e);
        }
    }

    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getProperty("db.url"));
        config.setUsername(getProperty("db.username"));
        config.setPassword(getProperty("db.password"));
        config.setDriverClassName(getProperty("db.driverClassName"));

        config.setMaximumPoolSize(getIntProperty("db.pool.size", 10));
        config.setMinimumIdle(getIntProperty("db.pool.min.idle", 5));
        config.setIdleTimeout(getLongProperty("db.pool.idle.timeout", 300000));
        config.setMaxLifetime(getLongProperty("db.pool.max.lifetime", 1200000));
        config.setConnectionTimeout(getLongProperty("db.pool.connection.timeout", 20000));
        config.setLeakDetectionThreshold(getLongProperty("db.pool.leak.detection.threshold", 30000));

        dataSource = new HikariDataSource(config);

    }

    private static String getProperty(String key) {
        String value = properties.getProperty(key);
        if(value == null) {
            throw new RuntimeException(key + " not found in application.properties");
        }
        return value.trim();
    }
    private static int getIntProperty(String key, int defaultValue) {

        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }

    private static long getLongProperty(String key, long defaultValue) {

        String value = properties.getProperty(key);
        return value != null ? Long.parseLong(value) : defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

}

