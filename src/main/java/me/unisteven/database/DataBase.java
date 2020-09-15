package me.unisteven.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;

public class DataBase {
    private HikariDataSource dataSource;
    private Plugin plugin;

    public void init(Plugin plugin) {
        this.plugin = plugin;
        try {
            openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openConnection() {
        FileConfiguration configuration = this.plugin.getConfig();
        System.out.println("Connecting to database as: " + configuration.getString("user"));
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + configuration.getString("host") + ":" + configuration.getInt("port") + "/" + configuration.getString("database") + "?allowMultiQueries=true");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(configuration.getString("user"));
        config.setPassword(configuration.getString("pass"));
        config.setMinimumIdle(configuration.getInt("minimumIdle"));
        config.setMaximumPoolSize(configuration.getInt("maximumPoolSize"));
        config.setIdleTimeout(configuration.getInt("idleTimeOut"));
        config.setConnectionTimeout(configuration.getInt("connectionTimeout"));
        config.setConnectionTestQuery(configuration.getString("connectionTestQuery"));
        this.dataSource = new HikariDataSource(config);
        System.out.println("Connection established with database!");
    }


    public void destroy() {
        try {
            if (this.dataSource != null) {
                this.dataSource.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Connection getConnection() {
        try {
            return this.dataSource.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get the {@link HikariDataSource}.
     */
    public HikariDataSource getDataSource() {
        return this.dataSource;
    }
}
