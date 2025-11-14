package com.mas6y6.masworld.Commands.PersonalVault;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Vault {
    private final Connection conn;
    private final UUID uuid;

    public Vault(String path, UUID uuid) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        this.uuid = uuid;

        try (Statement stmt = this.conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS items (slot INT PRIMARY KEY, item TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS meta (key TEXT PRIMARY KEY, value TEXT)");

            stmt.execute("INSERT OR IGNORE INTO meta VALUES ('version', '1.00')");
            stmt.execute("INSERT OR IGNORE INTO meta VALUES ('tier', '0')");
            stmt.execute("INSERT OR IGNORE INTO meta VALUES ('maxslots','9')");
            stmt.execute("INSERT OR IGNORE INTO meta VALUES ('maxpages','1')");
        }
    }

    public int getMaxSlots() {
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT value FROM meta WHERE key='maxslots'")) {
            if (rs.next()) {
                return Integer.parseInt(rs.getString("value"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 9; // fallback
    }

    public Map<Integer, ItemStack> loadItems() {
        Map<Integer, ItemStack> items = new HashMap<>();
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT slot, item FROM items")) {

            while (rs.next()) {
                int slot = rs.getInt("slot");
                String serialized = rs.getString("item");

                if (serialized != null && !serialized.isEmpty()) {
                    YamlConfiguration cfg = new YamlConfiguration();
                    cfg.loadFromString(serialized);
                    ItemStack item = cfg.getItemStack("item");
                    if (item != null) items.put(slot, item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    public void saveItems(Map<Integer, ItemStack> items) {
        try {
            // Clear existing items
            try (PreparedStatement clear = this.conn.prepareStatement("DELETE FROM items")) {
                clear.executeUpdate();
            }

            // Insert new items
            try (PreparedStatement insert = this.conn.prepareStatement("INSERT INTO items (slot, item) VALUES (?, ?)")) {
                for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                    ItemStack item = entry.getValue();
                    if (item != null) {
                        YamlConfiguration cfg = new YamlConfiguration();
                        cfg.set("item", item);
                        String serialized = cfg.saveToString();

                        insert.setInt(1, entry.getKey());
                        insert.setString(2, serialized);
                        insert.addBatch();
                    }
                }
                insert.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        this.conn.close();
    }
}