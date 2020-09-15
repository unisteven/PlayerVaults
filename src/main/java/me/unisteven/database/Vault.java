package me.unisteven.database;

import me.unisteven.Main;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Vault {

    public void saveInventory(Inventory inventory, Player p, int page){
        try(Connection connection = Main.database.getConnection()){
            try(PreparedStatement ps = connection.prepareStatement("INSERT INTO Vault(uuid, page, items) VALUES (?,?,?) ON DUPLICATE KEY UPDATE items = ?")){
                ps.setString(1, p.getUniqueId().toString());
                ps.setInt(2, page);
                ps.setString(3, this.kitToBase64(inventory.getStorageContents()));
                ps.setString(4, this.kitToBase64(inventory.getStorageContents()));
                ps.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ItemStack[] loadInventory(int page, Player p){
        ItemStack[] inventory = null;
        try(Connection connection = Main.database.getConnection()){
            try(PreparedStatement ps = connection.prepareStatement("SELECT * FROM Vault WHERE uuid = ? AND page = ?")){
                ps.setString(1, p.getUniqueId().toString());
                ps.setInt(2, page);
                try(ResultSet rs = ps.executeQuery()){
                    if(rs.next()){
                        inventory = this.kitFromBase64(rs.getString("items"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return inventory;
    }

    public String kitToBase64(ItemStack[] kit) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeObject(kit);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception var4) {
            throw new IllegalStateException("An Error has occurred! Contact Developer! Error: Manager_CreateKit_1", var4);
        }
    }
    public ItemStack[] kitFromBase64(String s) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(s));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] kit = (ItemStack[])dataInput.readObject();
            dataInput.close();
            return kit;
        } catch (Exception var4) {
            throw new IllegalStateException("An Error has occurred! Contact Developer! Error: Player_Kit_1", var4);
        }
    }
}
