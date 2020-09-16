package me.unisteven.database;

import me.unisteven.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Vault {

    private Main plugin;

    public Vault(Main plugin) {
        this.plugin = plugin;
    }

    public void saveInventory(Inventory inventory, Player p, int page) {
        if (Main.storageType.equalsIgnoreCase("flat")) {
            try {
                File vault = new File(plugin.getDataFolder() + "/vaults/" + p.getUniqueId().toString() + "-vault-" + page);
                FileWriter fw = new FileWriter(vault.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(this.inventoryToBase64(inventory.getStorageContents()));
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try (Connection connection = Main.database.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO pv_Vault(uuid, page, items) VALUES (?,?,?) ON DUPLICATE KEY UPDATE items = ?")) {
                ps.setString(1, p.getUniqueId().toString());
                ps.setInt(2, page);
                ps.setString(3, this.inventoryToBase64(inventory.getStorageContents()));
                ps.setString(4, this.inventoryToBase64(inventory.getStorageContents()));
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ItemStack[] loadInventory(int page, Player p) {
        if (Main.storageType.equalsIgnoreCase("flat")) {
            try {
                File dir = new File(plugin.getDataFolder() + "/vaults/");
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File vault = new File(plugin.getDataFolder() + "/vaults/" + p.getUniqueId().toString() + "-vault-" + page);
                if(!vault.exists()){
                    vault.createNewFile();
                }
                InputStream is = new FileInputStream(new File(plugin.getDataFolder() + "/vaults/" + p.getUniqueId().toString() + "-vault-" + page));
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));

                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();

                while (line != null) {
                    sb.append(line).append("\n");
                    line = buf.readLine();
                }

                String fileAsString = sb.toString();
                if(fileAsString.equalsIgnoreCase("")){
                    return null;
                }
                return this.inventoryFromBase64(fileAsString);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        ItemStack[] inventory = null;
        try (Connection connection = Main.database.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM pv_Vault WHERE uuid = ? AND page = ?")) {
                ps.setString(1, p.getUniqueId().toString());
                ps.setInt(2, page);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        inventory = this.inventoryFromBase64(rs.getString("items"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inventory;
    }

    public String inventoryToBase64(ItemStack[] kit) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(kit);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception var4) {
            throw new IllegalStateException("An Error has occurred! Contact Developer!", var4);
        }
    }

    public ItemStack[] inventoryFromBase64(String s) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] kit = (ItemStack[]) dataInput.readObject();
            dataInput.close();
            return kit;
        } catch (Exception var4) {
            throw new IllegalStateException("An Error has occurred! Contact Developer!", var4);
        }
    }
}
