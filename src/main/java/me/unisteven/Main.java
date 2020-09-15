package me.unisteven;

import me.unisteven.command.PlayerVault;
import me.unisteven.database.DataBase;
import me.unisteven.database.MigrateData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static Logger logger;
    public static DataBase database = new DataBase();

    @Override
    public void onEnable(){
        Main.logger = Bukkit.getLogger();
        logger.log(Level.FINE, "PlayerVaults is enabling");
        // configs
        this.getConfig().options().copyDefaults(true);
        saveConfig();
        // commands
        Objects.requireNonNull(getCommand("playervault")).setExecutor(new PlayerVault(this));

        // database
        try {
            Main.database.init(this);
            logger.log(Level.FINE, "Database loaded succesfully");
        }catch (Exception e){
            logger.log(Level.SEVERE, "Database connection failed! with the following error:");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        MigrateData dataMigration = new MigrateData(this);
        dataMigration.checkForUpdates();
    }

    @Override
    public void onDisable(){

    }
}
