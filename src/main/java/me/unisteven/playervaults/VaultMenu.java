package me.unisteven.playervaults;

import me.unisteven.database.Vault;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class VaultMenu implements Listener {

    private Inventory inv;
    private int page = 1;
    private int nextPage = 1;
    private Player p;
    private Vault vault;
    private int maxVaults = 0;

    public VaultMenu(int max) {
        this.maxVaults = max;
        this.vault = new Vault();
    }

    public void createInv() {
        if(nextPage != page){
            page = nextPage;
        }
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 54, ChatColor.translateAlternateColorCodes('&', "&f&lPlayer vault &c&l(&f&l" + this.page + "&c&l)"));
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
        inv.setItem(45, createGuiItem(Material.PAPER, "Back to previous page", "Click me to get back", "to the previous page"));
        for (int i = 46; i < 53; i++) {
            inv.setItem(i, createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.translateAlternateColorCodes('&', "&c-=-=-=-")));
        }
        inv.setItem(53, createGuiItem(Material.PAPER, "Next page", "Click me to get", "to the next page"));
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        this.p = (Player) ent;
        createInv();
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if(e.getInventory() != inv){
            return;
        }
        if (e.getRawSlot() >= 45 && e.getRawSlot() <= 53) {
            e.setCancelled(true); // players can only take items at the top.
        }
        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();

        if (e.getRawSlot() == 45) {
            if (!(page <= 1)) {
                nextPage--;
//                page--;
            }
            this.recreateInventory(p);
            return;
        }
        if (e.getRawSlot() == 53) {
            if((this.page + 1) > maxVaults){
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou are limited to &f" + this.maxVaults + " vaults!"));
                return;
            }else {
                nextPage++;
                this.recreateInventory(p);
                return;
            }
        }
    }

    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent e) {
        if (e.getInventory() != this.inv) {
            return;
        }
        this.vault.saveInventory(this.inv, this.p, this.page);
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        if (e.getInventory() != this.inv) {
            return;
        }
        this.vault.saveInventory(this.inv, this.p, this.page);
    }

    private void recreateInventory(Player p) {
        p.closeInventory();
        this.openInventory(p);
    }
}
