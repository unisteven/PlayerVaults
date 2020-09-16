package me.unisteven.database;

import me.unisteven.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;

public class MigrateData {
    private Main plugin;

    public MigrateData(Main plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        try (Connection connection = Main.database.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("SHOW TABLES LIKE 'pv_Version'")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        // Default tables do not yet exist so version 1.
                        this.updateToVersion(1);
                    } else {
                        try(PreparedStatement ps2 = connection.prepareStatement("SELECT MAX(version) AS version FROM pv_Version")){
                            try(ResultSet rs2 = ps2.executeQuery()){
                                if(rs2.next()){
                                    int version = rs2.getInt("version");
                                    Main.logger.log(Level.INFO, "Rebelwar is on version: " + version + " trying to update to version: " + (version + 1));
                                    if (this.loadVersion(version + 1) != null) {
                                        this.updateToVersion(version + 1);
                                        this.checkForUpdates();
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

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
    }

    public void updateToVersion(int version) {
        Main.logger.log(Level.CONFIG, "Loading version " + version + " into the database");
        String sql = this.loadVersion(version);
        if(sql == null){
            return;
        }
        try (Connection connection = Main.database.getConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try(PreparedStatement ps = connection.prepareStatement("INSERT INTO pv_Version VALUES (?)")){
                ps.setInt(1, version);
                ps.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadVersion(int version) {
        InputStream inputStream = plugin.getClass().getClassLoader().getResourceAsStream("version-" + version + ".sql");
        if(inputStream != null) {
            try {
                InputStreamReader isReader = new InputStreamReader(inputStream);
                //Creating a BufferedReader object
                BufferedReader reader = new BufferedReader(isReader);
                StringBuilder sb = new StringBuilder();
                String str;
                while ((str = reader.readLine()) != null) {
                    sb.append(str);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
