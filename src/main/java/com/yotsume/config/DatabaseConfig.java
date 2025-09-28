package com.yotsume.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public class DatabaseConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;

    static {
        try {
            initDataSource();
            LOGGER.info("✅ Пул соединений HikariCP успешно инициализирован");
        }catch(Exception e) {
            LOGGER.error("❌ Ошибка инициализации пула соединений", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void initDataSource() throws IOException {
        Properties props = loadProperties();
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getRequiredProperty(props, "db.url"));
        config.setUsername(getRequiredProperty(props, "db.username"));
        config.setPassword(getRequiredProperty(props, "db.password"));

        // Настройки пула
        config.setMaximumPoolSize(getIntProperty(props, "db.pool.maximum-size", 10));
        config.setMinimumIdle(getIntProperty(props, "db.pool.minimum-idle", 2));
        config.setMaxLifetime(getLongProperty(props, "db.pool.max-lifetime", 1800_000L));
        config.setIdleTimeout(getLongProperty(props, "db.pool.idle-timeout", 600_000L));
        config.setConnectionTimeout(getLongProperty(props, "db.pool.connection-timeout", 30_000L));
        config.setLeakDetectionThreshold(getLongProperty(props, "db.pool.leak-detection-threshold", 0L));

        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("PaymentSystem-Pool");

        dataSource = new HikariDataSource(config);
    }

    private static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        try(InputStream inputStream = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new FileNotFoundException("Файл application.properties не найден в classpath");
            }
            properties.load(inputStream);
        }
        return properties;
    }

    private static String getRequiredProperty(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Обязательный параметр отсутствует: " + key);
        }
        return value.trim();
    }

    private static int getIntProperty(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        return (value != null) ? Integer.parseInt(value.trim()) : defaultValue;
    }

    private static long getLongProperty(Properties props, String key, long defaultValue) {
        String value = props.getProperty(key);
        return (value != null) ? Long.parseLong(value.trim()) : defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new IllegalStateException("Пул соединений не инициализирован или закрыт");
        }
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            LOGGER.info("Закрытие пула соединений HikariCP...");
            dataSource.close();
        }
    }
}
