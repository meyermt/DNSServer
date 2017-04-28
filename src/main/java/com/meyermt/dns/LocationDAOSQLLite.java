package com.meyermt.dns;

//import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaelmeyer on 4/23/17.
 */
//@Component
public class LocationDAOSQLLite implements LocationDAO {

    public LocationDAOSQLLite() {}

    public void createDatabaseIfNeeded(String fileName) {

        File db = new File("./" + fileName);
        if (!db.isFile()) {
            String url = "jdbc:sqlite:./" + fileName;
            System.out.println("No database found for this server, so creating one.");
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    DatabaseMetaData meta = conn.getMetaData();
                    System.out.println("The driver name is " + meta.getDriverName());
                    System.out.println("New database has been created.");
                }

            } catch (SQLException e) {
                throw new RuntimeException("Unable to create new database and tables.", e);
            }

            String sqlPeers = "CREATE TABLE IF NOT EXISTS peers (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + "	name text NOT NULL,\n"
                    + "	ip text NOT NULL,\n"
                    + " port integer NOT NULL,\n"
                    + " root boolean NOT NULL\n"
                    + ");";

            String sqlSuperPeers = "CREATE TABLE IF NOT EXISTS superpeers (\n"
                    + "	id integer PRIMARY KEY,\n"
                    + "	region text NOT NULL,\n"
                    + "	ip text NOT NULL,\n"
                    + " port integer NOT NULL\n"
                    + ");";

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                stmt.execute(sqlPeers);
                stmt.execute(sqlSuperPeers);
                System.out.println("Created peers and superpeers tables.");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void insertPeer(String name, String ip, int port) {
        String sql = "INSERT INTO peers(name, ip, port, root) VALUES(?,?,?,0)";

        try (Connection conn = this.connect("peers");
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, ip);
            pstmt.setInt(3, port);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + name + " into peer db.", e);
        }
    }

    public void insertNewSuper(String name, String ip, int port) {
        String delSql = "DELETE FROM peers WHERE root = 1";
        String sql = "INSERT INTO peers(name, ip, port, root) VALUES(?,?,?,1)";

        try (Connection conn = this.connect("peers");
             PreparedStatement dpstmt = conn.prepareStatement(delSql)) {
            dpstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + name + " into peer db.", e);
        }

        try (Connection conn = this.connect("peers");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, ip);
            pstmt.setInt(3, port);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + name + " into peer db.", e);
        }
    }

    public void deletePeer(String name) {
        String sql = "DELETE FROM peers WHERE name = ?";

        try (Connection conn = this.connect("peers");
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + name + " from peer db.", e);
        }
    }

    public void updatePeer(String name, String ip, int port, boolean isRoot) {
        String sql = "UPDATE peers SET ip = ? , "
                + "port = ?, "
                + "root = ? "
                + "WHERE name = ?";

        try (Connection conn = this.connect("peers");
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            pstmt.setInt(2, port);
            pstmt.setBoolean(3, isRoot);
            pstmt.setString(4, name);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update" + name + " in peer db.", e);
        }
    }

    public Peer getPeer(String name) {
        String sql = "SELECT id, name, ip, port, root "
                + "FROM peers WHERE name = ?";

        try (Connection conn = this.connect("peers");
            PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1, name);
            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                Peer peer = new Peer(rs.getInt("id"),
                        rs.getString("name"), rs.getString("ip"),
                        rs.getInt("port"), rs.getBoolean("root"));
                return peer;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve peer " + name + " from peer db.", e);
        }
        return null;
    }

    public List<Peer> getPeers() {
        String sql = "SELECT id, name, ip, port, root FROM peers";
        List<Peer> peers = new ArrayList<>();

        try (Connection conn = this.connect("peers");
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                Peer peer = new Peer(rs.getInt("id"),
                  rs.getString("name"), rs.getString("ip"),
                  rs.getInt("port"), rs.getBoolean("root"));
                peers.add(peer);
            }
            return peers;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve peers in peer db.", e);
        }
    }

    public void insertSuperpeer(String region, String ip, int port) {
        String sql = "INSERT INTO superpeers(region, ip, port) VALUES(?,?,?)";

        try (Connection conn = this.connect("superpeers");
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, region);
            pstmt.setString(2, ip);
            pstmt.setInt(3, port);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + region + " into superpeer db.", e);
        }
    }

    public void insertNewSuperInSuper(String region, String ip, int port) {
        String delSql = "DELETE FROM superpeers WHERE region = ?";
        String sql = "INSERT INTO superpeers(region, ip, port) VALUES(?,?,?)";

        try (Connection conn = this.connect("superpeers");
             PreparedStatement pstmt = conn.prepareStatement(delSql)) {
            pstmt.setString(1, region);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + region + " from superpeer db.", e);
        }

        try (Connection conn = this.connect("superpeers");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, region);
            pstmt.setString(2, ip);
            pstmt.setInt(3, port);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to insert" + region + " into superpeer db.", e);
        }
    }

    public void deleteSuperpeer(String region) {
        String sql = "DELETE FROM superpeers WHERE region = ?";

        try (Connection conn = this.connect("superpeers");
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, region);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to delete" + region + " from superpeer db.", e);
        }
    }

    public void updateSuperpeer(String region, String ip, int port) {
        String sql = "UPDATE superpeers SET ip = ? , "
                + "port = ? "
                + "WHERE region = ?";

        try (Connection conn = this.connect("superpeers");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ip);
            pstmt.setInt(2, port);
            pstmt.setString(3, region);
            // update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to update" + region + " in superpeer db.", e);
        }
    }

    public Superpeer getSuperpeer(String region) {
        String sql = "SELECT id, region, ip, port "
                + "FROM superpeers WHERE region = ?";

        try (Connection conn = this.connect("superpeers");
            PreparedStatement pstmt  = conn.prepareStatement(sql)){
            pstmt.setString(1, region);
            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                Superpeer superpeer = new Superpeer(rs.getInt("id"),
                        rs.getString("region"), rs.getString("ip"),
                        rs.getInt("port"));
                return superpeer;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve peer " + region + " from superpeer db.", e);
        }
        return null;
    }

    public List<Superpeer> getSuperpeers() {
        String sql = "SELECT id, region, ip, port FROM superpeers";
        List<Superpeer> superpeers = new ArrayList<>();

        try (Connection conn = this.connect("superpeers");
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){

            while (rs.next()) {
                Superpeer superpeer = new Superpeer(rs.getInt("id"),
                        rs.getString("region"), rs.getString("ip"),
                        rs.getInt("port"));
                superpeers.add(superpeer);
            }
            return superpeers;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve peers in peer db.", e);
        }
    }

    public int getSuperCount() {
        String sql = "SELECT COUNT(*) "
                + "FROM peers WHERE root = TRUE";

        try (Connection conn = this.connect("peers");
            PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                int count = rs.getInt("total");
                return count;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve count", e);
        }
        return 0;
    }

    public Peer getLocalSuperpeer() {
        String sql = "SELECT id, name, ip, port, root "
                + "FROM peers WHERE root = true";

        try (Connection conn = this.connect("peers");
             PreparedStatement pstmt  = conn.prepareStatement(sql)){
            ResultSet rs  = pstmt.executeQuery();

            while (rs.next()) {
                Peer peer = new Peer(rs.getInt("id"),
                        rs.getString("name"), rs.getString("ip"),
                        rs.getInt("port"), rs.getBoolean("root"));
                return peer;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unable to retrieve local superpeer from peer db.", e);
        }
        return null;
    }

    private Connection connect(String dbName) {
        // SQLite connection string
        String url = "jdbc:sqlite:./" + dbName + ".db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to sql db.", e);
        }
        return conn;
    }

}
