package me.unisteven.database;
import me.unisteven.PlayerVault;

import java.sql.Connection;

public interface IDataBase {
    void init(PlayerVault plugin);
    Connection getConnection();
}
