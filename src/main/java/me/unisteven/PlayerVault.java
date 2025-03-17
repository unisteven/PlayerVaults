package me.unisteven;

import me.unisteven.command.PlayerVaultCommand;
import me.unisteven.database.IDataBase;
import me.unisteven.database.MigrateData;
import me.unisteven.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
public class PlayerVault extends JavaPlugin {
    private IDataBase database;
    private String storageType;

    @Override
    public void onEnable(){
        Bukkit.getLogger().log(Level.FINE, "PlayerVaults is enabling");

        this.getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("playervault").setExecutor(new PlayerVaultCommand(this));

        try {
            String storageType = getConfig().getString("storageType");

            this.storageType = storageType;

            assert storageType != null;
            if(storageType.equalsIgnoreCase("mysql")){
                Bukkit.getLogger().log(Level.INFO, "Loading database type:" + storageType);

                this.database = new MySQL();
                this.database.init(this);

                Bukkit.getLogger().log(Level.FINE, "Database loaded succesfully");

                MigrateData dataMigration = new MigrateData(this);
                dataMigration.checkForUpdates();
            }

        }catch (Exception e){
            Bukkit.getLogger().log(Level.SEVERE, "Database connection failed! with the following error:");
            e.printStackTrace();

            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable(){
    }

    public IDataBase getDatabase() {
        return database;
    }

    public String getStorageType() {
        return storageType;
    }

    public static String translatePlaceholders(String input, int vaultLimit, int vaultPage){
        return ChatColor.translateAlternateColorCodes('&', input
                .replaceAll("%vault_limit%", "" + vaultLimit)
                .replaceAll("%vault_number%", "" + vaultPage));
    }
}