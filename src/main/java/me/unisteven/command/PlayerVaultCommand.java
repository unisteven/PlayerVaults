package me.unisteven.command;

import me.unisteven.PlayerVault;
import me.unisteven.database.Vault;
import me.unisteven.playervaults.VaultMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PlayerVaultCommand implements CommandExecutor {
    private final PlayerVault plugin;

    public PlayerVaultCommand(PlayerVault plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = PlayerVault.translatePlaceholders(plugin.getConfig().getString("prefix"), 0, 0);

        if (!(sender instanceof Player p)) {
            sender.sendMessage(prefix + " Only players can execute this command.");
            return false;
        }

        if (args.length > 0) {
            try {
                int page = Integer.parseInt(args[0]);
                openPlayerVault(p, p, page);

                return true;
            } catch (NumberFormatException ignored) {}

            if (args[0].equalsIgnoreCase("admin")) {
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("open")) {
                        if (p.hasPermission("playervaults.admin.*") || p.hasPermission("playervaults.admin.open")) {
                            if (args.length < 3) {
                                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&cTo few arguments! &f/pv admin open {playername}"));

                                return false;
                            }

                            openPlayerVault(p, Bukkit.getPlayer(args[2]), 1);
                        } else {
                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to execute this command &f(&b&lplayervaults.admin.open&f)"));
                        }
                    }

                    if (args[1].equalsIgnoreCase("clear")) {
                        if (p.hasPermission("playervaults.admin.*") || p.hasPermission("playervaults.admin.clear")) {
                            if (args.length < 4) {
                                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&cTo few arguments! &f/pv admin clear {playername} {page}"));

                                return false;
                            }

                            Vault vault = new Vault(this.plugin);
                            vault.saveInventory(Bukkit.createInventory(null, 54, ""), Bukkit.getPlayer(""), Integer.parseInt(args[3]));
                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&aCleared playervault page " + args[3] + " for player " + args[2]));
                        } else {
                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to execute this command &f(&b&lplayervaults.admin.clear&f)"));
                        }
                    }

                    if (args[1].equalsIgnoreCase("reload")) {
                        if (p.hasPermission("playervaults.admin.*") || p.hasPermission("playervaults.admin.reload")) {
                            this.plugin.reloadConfig();
                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&aYou have reloaded the config!"));
                        } else {
                            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to execute this command &f(&b&lplayervaults.admin.reload&f)"));
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l=-=-=-=-=-=-=-=-=-=-="));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l/pv admin open {player}"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l/pv admin clear {player} {vault_num}"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l/pv admin reload (reloads config)"));
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l=-=-=-=-=-=-=-=-=-=-="));
                }
            }

            return true;
        }

        openPlayerVault(p, p, 1);

        return false;
    }

    private void openPlayerVault(Player requester, Player target, int page) {
        String prefix = PlayerVault.translatePlaceholders(plugin.getConfig().getString("prefix"), 0, 0);
        int maxVaults = 0;

        if(requester.isOp()){
            maxVaults = Integer.MAX_VALUE;
        }

        for (PermissionAttachmentInfo permis : target.getEffectivePermissions()) {
            if (permis.getPermission().startsWith("playervaults.limit.")) {
                String maxString = permis.getPermission().split("\\.")[2];

                if (maxString.equalsIgnoreCase("*")) {
                    maxVaults = Integer.MAX_VALUE;
                    break;
                }

                int maxi = Integer.parseInt(maxString);

                if (maxi > maxVaults) {
                    maxVaults = maxi;
                }
            }
        }

        if (maxVaults == 0) {
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.plugin.getConfig().getString("noVaults")));

            return;
        }

        if(page < 1){
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.plugin.getConfig().getString("noVaults")));

            return;
        }

        if (page > maxVaults) {
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + PlayerVault.translatePlaceholders(this.plugin.getConfig().getString("vaultLimitReached"), maxVaults, page)));

            return;
        }

        VaultMenu vaultMenu = new VaultMenu(maxVaults, this.plugin);
        Bukkit.getPluginManager().registerEvents(vaultMenu, this.plugin);

        if (vaultMenu.getRequester() != null) {
            target.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + this.plugin.getConfig().getString("inUse")));

            return;
        }

        vaultMenu.setPage(page);
        vaultMenu.openInventory(target, requester);
    }
}
