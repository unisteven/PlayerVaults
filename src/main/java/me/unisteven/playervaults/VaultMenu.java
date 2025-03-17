package me.unisteven.playervaults;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.unisteven.PlayerVault;
import me.unisteven.database.Vault;

public class VaultMenu implements Listener {
    private Inventory inv;
    private int page = 1;
    private int nextPage = 1;
    private Player p;
    private Player requester = null;
    private final Vault vault;
    private final int rowSize = 5;
    private final int invSize = ((rowSize + 1) * 9);
    private final int maxVaults;
    private final PlayerVault plugin;

    public VaultMenu(int max, PlayerVault plugin) {
        this.plugin = plugin;
        this.maxVaults = max;
        this.vault = new Vault(plugin);
    }

    public void createInv() {
        if(nextPage != page){
            page = nextPage;
        }

        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, invSize, PlayerVault.translatePlaceholders(this.plugin.getConfig().getString("menuName"), this.maxVaults, this.page));

        ItemStack[] inventory = this.vault.loadInventory(this.page, this.p);

        inv.clear();

        if (inventory != null) {
            inv.setContents(inventory);
        }

        // Put the items into the inventory
        initializeItems();
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        final ItemStack greyStainedGlassPane = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.translateAlternateColorCodes('&', "&c-=-=-=-"));
        final String[] backLores = this.plugin.getConfig().getStringList("backButtonDescription").stream().map(s -> PlayerVault.translatePlaceholders(s, this.maxVaults, this.page)).toArray(String[]::new);
        final ItemStack backButtonPaper = createGuiItem(Material.PAPER, PlayerVault.translatePlaceholders(this.plugin.getConfig().getString("backButtonName"), this.maxVaults, this.page), backLores);
        final String[] nextLores = this.plugin.getConfig().getStringList("nextButtonDescription").stream().map(s -> PlayerVault.translatePlaceholders(s, this.maxVaults, this.page)).toArray(String[]::new);
        final ItemStack nextButtonPaper = createGuiItem(Material.PAPER, PlayerVault.translatePlaceholders(this.plugin.getConfig().getString("nextButtonName"), this.maxVaults, this.page), nextLores);

        inv.setItem(invSize - 9, backButtonPaper);

        for (int i = (invSize - 8); i < (invSize - 1); i++) {
            inv.setItem(i, greyStainedGlassPane);
        }

        inv.setItem((invSize - 1), nextButtonPaper);

        if(page == 1){
            inv.setItem(invSize - 9, greyStainedGlassPane); // no back button on the first page
        }

        if(page >= maxVaults){
            inv.setItem((invSize - 1), greyStainedGlassPane); // no next button if there are no next pages.
        }
    }

    // Nice little method to create a GUI item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta metaData = item.getItemMeta();

        metaData.setDisplayName(name);
        metaData.setLore(Arrays.asList(lore));

        item.setItemMeta(metaData);

        return item;
    }

    public void openInventory(final HumanEntity ent, final HumanEntity requester) {
        if(requester != ent && requester != null){
            this.requester = (Player) requester;
            this.p = (Player) ent;

            ent.closeInventory(); // close inventory in case they still got one open

            createInv();
            requester.openInventory(inv);
        }else {
            this.p = (Player) ent;

            createInv();
            ent.openInventory(inv);
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if(e.getInventory() != inv){
            return;
        }

        if (e.getRawSlot() >= (invSize - 9) && e.getRawSlot() <= (invSize - 1)) {
            e.setCancelled(true); // players can only take items at the top.
        }

        String prefix = PlayerVault.translatePlaceholders(plugin.getConfig().getString("prefix"), 0, 0);

        // if it is an admin check if he has perm to alter the inv
        if(this.requester != null){
            if(!(this.requester.hasPermission("playervaults.admin.*") || this.requester.hasPermission("playervaults.admin.alter"))){
                e.setCancelled(true);

                this.requester.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "&cYou do not have permission to alter this inventory &f(&b&lplayervaults.admin.alter&f)"));
            }
        }
        
        if (e.getAction() != InventoryAction.NOTHING) {
            this.vault.saveInventory(this.inv, this.p, this.page);
        } else {
            return;
        }

        if (e.getRawSlot() == (invSize - 9)) {
            if (page > 1) {
                nextPage--;
            }else{
                e.setCancelled(true);

                return;
            }

            this.recreateInventory(p);

            return;
        }

        if (e.getRawSlot() == (invSize - 1)) {
            if((this.page + 1) > maxVaults){
                String message = PlayerVault.translatePlaceholders(this.plugin.getConfig().getString("vaultLimitReached"), this.maxVaults, this.page);

                p.sendMessage(prefix + message);
            }else {
                nextPage++;

                this.recreateInventory(p);
            }
        }
    }

    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent event) {
        if (event.getInventory() != this.inv) {
            return;
        }

        this.vault.saveInventory(this.inv, this.p, this.page);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() != this.inv) {
            return;
        }

        this.vault.saveInventory(this.inv, this.p, this.page);
        this.requester = null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().getOpenInventory().getTopInventory() != this.inv) {
            return;
        }

        this.vault.saveInventory(this.inv, this.p, this.page);
        this.requester = null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getOpenInventory().getTopInventory() != this.inv) {
            return;
        }

        this.vault.saveInventory(this.inv, this.p, this.page);
        this.requester = null;
    }

    private void recreateInventory(Player player) {
        this.vault.saveInventory(this.inv, this.p, this.page);
        this.openInventory(this.p, this.requester);
    }

    public Player getRequester() {
        return this.requester;
    }

    public void setPage(int page) {
        if(page != -1){
            this.page = page;
            this.nextPage = page;
        }
    }
}
