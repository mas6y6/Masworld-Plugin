package com.mas6y6.masworld.Commands.PersonalVault;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Vault {
    public Connection conn;

    public Vault(String path) throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + path);

        Statement stmt = this.conn.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS items (slot INT PRIMARY KEY, item TEXT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS meta (key TEXT PRIMARY KEY, value TEXT)");

        stmt.execute("INSERT OR IGNORE INTO meta VALUES ('version', '1.00')");
        stmt.execute("INSERT OR IGNORE INTO meta VALUES ('tier', '0')");
        stmt.close();
    }

    public void close() throws SQLException {
        this.conn.close();
    }
}
