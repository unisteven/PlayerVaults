package me.unisteven.database;

import com.zaxxer.hikari.HikariDataSource;
import me.unisteven.Main;

import java.sql.Connection;

public interface IDataBase {
    void init(Main plugin);
    void destroy();
    Connection getConnection();
}
