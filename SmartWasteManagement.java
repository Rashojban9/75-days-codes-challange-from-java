import java.sql.*;
import java.util.*;

// Database Connection & Table Initialization
class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:waste_management.db";

    public static Connection connect() {
        try { return DriverManager.getConnection(URL); } 
        catch (SQLException e) { System.out.println("DB Error: " + e.getMessage()); return null; }
    }

    public static void initializeDB() {
        String[] queries = {
            "CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY, name TEXT, role TEXT, email TEXT, password TEXT);",
            "CREATE TABLE IF NOT EXISTS WasteBins (id INTEGER PRIMARY KEY, location TEXT, capacity INTEGER, currentLevel INTEGER, lastCollected TEXT);",
            "CREATE TABLE IF NOT EXISTS WasteCollection (id INTEGER PRIMARY KEY, binId INTEGER, collectorId INTEGER, scheduledDate TEXT, status TEXT);",
            "CREATE TABLE IF NOT EXISTS Recyclables (id INTEGER PRIMARY KEY, type TEXT, quantity INTEGER, binId INTEGER);"
        };
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            for (String query : queries) stmt.execute(query);
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

// User Model & Authentication
class User {
    int id; String name, role, email, password;
    User(int id, String name, String role, String email, String password) {
        this.id = id; this.name = name; this.role = role; this.email = email; this.password = password;
    }
}

class UserService {
    public boolean login(String email, String password) {
        String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email); pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}

// WasteBin Model
class WasteBin {
    int id, capacity, currentLevel; String location, lastCollected;
    WasteBin(int id, String location, int capacity, int currentLevel, String lastCollected) {
        this.id = id; this.location = location; this.capacity = capacity;
        this.currentLevel = currentLevel; this.lastCollected = lastCollected;
    }
}

// WasteBin DAO with Alerts & Updates
class WasteBinDAO {
    void insertWasteBin(WasteBin bin) {
        String sql = "INSERT INTO WasteBins (location, capacity, currentLevel, lastCollected) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bin.location); pstmt.setInt(2, bin.capacity);
            pstmt.setInt(3, bin.currentLevel); pstmt.setString(4, bin.lastCollected); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    void updateWasteBinLevel(int id, int newLevel) {
        String sql = "UPDATE WasteBins SET currentLevel = ? WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newLevel); pstmt.setInt(2, id); pstmt.executeUpdate();
            if (newLevel >= 80) System.out.println("⚠ Alert: Waste Bin " + id + " is almost full! Schedule a collection.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    void deleteWasteBin(int id) {
        String sql = "DELETE FROM WasteBins WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    List<WasteBin> searchByLocation(String keyword) {
        List<WasteBin> bins = new ArrayList<>();
        String sql = "SELECT * FROM WasteBins WHERE location LIKE ?";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) bins.add(new WasteBin(rs.getInt("id"), rs.getString("location"),
                    rs.getInt("capacity"), rs.getInt("currentLevel"), rs.getString("lastCollected")));
        } catch (SQLException e) { e.printStackTrace(); }
        return bins;
    }
}

// Waste Collection Service with Reports
class WasteCollectionService {
    void scheduleCollection(int binId, int collectorId, String date) {
        String sql = "INSERT INTO WasteCollection (binId, collectorId, scheduledDate, status) VALUES (?, ?, ?, 'Scheduled')";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, binId); pstmt.setInt(2, collectorId); pstmt.setString(3, date); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    void generateCollectionReport() {
        String sql = "SELECT * FROM WasteCollection";
        try (Connection conn = DatabaseHelper.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Bin ID: " + rs.getInt("binId") + ", Collector ID: " + rs.getInt("collectorId") +
                        ", Date: " + rs.getString("scheduledDate") + ", Status: " + rs.getString("status"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

// Recyclable Service with Deletion
class RecyclableService {
    void addRecyclable(String type, int quantity, int binId) {
        String sql = "INSERT INTO Recyclables (type, quantity, binId) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type); pstmt.setInt(2, quantity); pstmt.setInt(3, binId); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    void deleteRecyclable(int id) {
        String sql = "DELETE FROM Recyclables WHERE id = ?";
        try (Connection conn = DatabaseHelper.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id); pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}

// Main Class
public class SmartWasteManagement {
    public static void main(String[] args) {
        DatabaseHelper.initializeDB();

        WasteBinDAO binDAO = new WasteBinDAO();
        WasteCollectionService collectionService = new WasteCollectionService();
        RecyclableService recyclableService = new RecyclableService();
        UserService userService = new UserService();

        if (userService.login("admin@example.com", "password")) {
            binDAO.insertWasteBin(new WasteBin(0, "City Center", 100, 90, "2024-01-31"));
            binDAO.updateWasteBinLevel(1, 85);
            collectionService.scheduleCollection(1, 1, "2024-02-05");
            recyclableService.addRecyclable("Plastic", 30, 1);
            binDAO.searchByLocation("City").forEach(bin -> System.out.println("Found Bin at: " + bin.location));
            collectionService.generateCollectionReport();
        } else {
            System.out.println("Login Failed!");
        }
    }
}
