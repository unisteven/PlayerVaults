package me.unisteven.command;

import me.unisteven.Main;
import me.unisteven.playervaults.VaultMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashMap;
import java.util.Map;

public class PlayerVault implements CommandExecutor {
    private Map<Player, VaultMenu> vaults = new HashMap<>();

    private Main plugin;

    public PlayerVault(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can execute this command.");
            return false;
        }
        Player p = (Player) sender;
        int max = 0;
        for(PermissionAttachmentInfo permis : p.getEffectivePermissions()){
            if(permis.getPermission().startsWith("playervaults.limit.")){
                int maxi = Integer.parseInt(permis.getPermission().split("\\.")[2]);
                if(maxi > max){
                    max = maxi;
                }
            }
        }
        if(max == 0){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSorry! it seems that you dont have access to that"));
            return true;
        }
        VaultMenu vaultMenu = this.vaults.get(p);
        if(vaultMenu == null){
            vaultMenu = new VaultMenu(max);
            Bukkit.getPluginManager().registerEvents(vaultMenu, this.plugin);
            this.vaults.put(p, vaultMenu);
        }
        vaultMenu.openInventory(p);
        return false;
    }
}
