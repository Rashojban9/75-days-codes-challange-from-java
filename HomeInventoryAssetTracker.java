import java.sql.*;

class Database {
    private static final String URL = "jdbc:sqlite:home_inventory.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    public static void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Items (id INTEGER PRIMARY KEY, category TEXT, name TEXT, value REAL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Warranties (id INTEGER PRIMARY KEY, item_id INTEGER, expiration_date TEXT, FOREIGN KEY(item_id) REFERENCES Items(id))");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization error", e);
        }
    }
}

class Item {
    private String category, name;
    private double value;

    public Item(String category, String name, double value) {
        this.category = category;
        this.name = name;
        this.value = value;
    }

    public void save() {
        String sql = "INSERT INTO Items (category, name, value) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setString(2, name);
            pstmt.setDouble(3, value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving item", e);
        }
    }
}

class Warranty {
    public static void setWarranty(int itemId, String expirationDate) {
        String sql = "INSERT INTO Warranties (item_id, expiration_date) VALUES (?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            pstmt.setString(2, expirationDate);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting warranty", e);
        }
    }
}

class AssetTracker {
    public static double calculateTotalValue() {
        String sql = "SELECT SUM(value) FROM Items";
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total asset value", e);
        }
    }
}

public class HomeInventoryAssetTracker {
    public static void main(String[] args) {
        Database.initialize();
        Item item = new Item("Electronics", "Laptop", 1200.0);
        item.save();
        Warranty.setWarranty(1, "2026-08-10");
        double totalValue = AssetTracker.calculateTotalValue();
        System.out.println("Total asset value: $" + totalValue);
    }
}
