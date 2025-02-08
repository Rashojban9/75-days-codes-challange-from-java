import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DatabaseManager {
    private static final String URL = "jdbc:sqlite:waste_management.db";
    
    static {
        try (Connection conn = DriverManager.getConnection(URL); 
             Statement stmt = conn.createStatement()) {
            String createBinsTable = "CREATE TABLE IF NOT EXISTS WasteBin (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "location TEXT, " +
                    "fillLevel INTEGER, " +
                    "category TEXT)";
            stmt.execute(createBinsTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertBin(String location, int fillLevel, String category) {
        String sql = "INSERT INTO WasteBin (location, fillLevel, category) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, location);
            pstmt.setInt(2, fillLevel);
            pstmt.setString(3, category);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<WasteBin> getFullBins() {
        List<WasteBin> bins = new ArrayList<>();
        String sql = "SELECT * FROM WasteBin WHERE fillLevel >= 100";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                bins.add(new WasteBin(rs.getInt("id"), rs.getString("location"), rs.getInt("fillLevel"), rs.getString("category")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bins;
    }
}

class WasteBin {
    private int id;
    private String location;
    private int fillLevel;
    private String category;

    public WasteBin(int id, String location, int fillLevel, String category) {
        this.id = id;
        this.location = location;
        this.fillLevel = fillLevel;
        this.category = category;
    }
    
    public int getFillLevel() { return fillLevel; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
}

class WasteCollector {
    public static void notifyCollectors() {
        List<WasteBin> fullBins = DatabaseManager.getFullBins();
        for (WasteBin bin : fullBins) {
            System.out.println("Collector Alert: Bin at " + bin.getLocation() + " is full (" + bin.getCategory() + ")");
        }
    }
}

public class SmartWasteSystem {
    public static void main(String[] args) {
        DatabaseManager.insertBin("Central Park", 100, "Recyclable");
        DatabaseManager.insertBin("Downtown", 80, "Organic");
        DatabaseManager.insertBin("Industrial Zone", 120, "Hazardous");
        
        WasteCollector.notifyCollectors();
    }
}
