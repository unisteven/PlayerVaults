package me.unisteven;

import me.unisteven.command.PlayerVault;
import me.unisteven.database.MySQL;
import me.unisteven.database.IDataBase;
import me.unisteven.database.MigrateData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    public static Logger logger;
    public static IDataBase database;
    public static String storageType;

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
            String storageType = getConfig().getString("storageType");
            Main.storageType = storageType;
            if(storageType.equalsIgnoreCase("mysql")){
                Main.database = new MySQL();
                logger.log(Level.INFO, "Loading database type:" + storageType);
                Main.database.init(this);
                logger.log(Level.FINE, "Database loaded succesfully");
                MigrateData dataMigration = new MigrateData(this);
                dataMigration.checkForUpdates();
            }

        }catch (Exception e){
            logger.log(Level.SEVERE, "Database connection failed! with the following error:");
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

    }

    @Override
    public void onDisable(){

    }

    public static String translatePlaceholders(String input, int vaultLimit, int vaultPage){
        return ChatColor.translateAlternateColorCodes('&', input
                .replaceAll("%vault_limit%", "" + vaultLimit)
                .replaceAll("%vault_number%", "" + vaultPage));
    }
}
