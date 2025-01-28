import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DatabaseManager: Handles all database operations
class DatabaseManager {
    private final String url;

    public DatabaseManager(String dbUrl) {
        this.url = dbUrl;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url);
    }

    public void executeUpdate(String sql) throws SQLException {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = connect();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
}

// Model class: Represents a single car model
class CarModel {
    private final String model;
    private final String brand;
    private final int inventory;
    private final double price;

    public CarModel(String model, String brand, int inventory, double price) {
        this.model = model;
        this.brand = brand;
        this.inventory = inventory;
        this.price = price;
    }

    public String getModel() {
        return model;
    }

    public String getBrand() {
        return brand;
    }

    public int getInventory() {
        return inventory;
    }

    public double getPrice() {
        return price;
    }
}

// ReportGenerator: Generates detailed reports
class ReportGenerator {
    private final DatabaseManager dbManager;

    public ReportGenerator(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void generateTotalSalesByModel() {
        String sql = "SELECT model, SUM(quantity) AS total_sales FROM sales GROUP BY model;";
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            System.out.println("Total Sales by Model:");
            while (rs.next()) {
                String model = rs.getString("model");
                int totalSales = rs.getInt("total_sales");
                System.out.printf("Model: %s, Total Sales: %d\n", model, totalSales);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateRevenueReport() {
        String sql = "SELECT model, SUM(quantity * price) AS revenue FROM sales GROUP BY model;";
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            System.out.println("Revenue Report:");
            while (rs.next()) {
                String model = rs.getString("model");
                double revenue = rs.getDouble("revenue");
                System.out.printf("Model: %s, Revenue: %.2f\n", model, revenue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void generateInventoryReport() {
        String sql = "SELECT * FROM inventory;";
        try (ResultSet rs = dbManager.executeQuery(sql)) {
            System.out.println("Inventory Report:");
            while (rs.next()) {
                String model = rs.getString("model");
                String brand = rs.getString("brand");
                int inventory = rs.getInt("inventory");
                double price = rs.getDouble("price");
                System.out.printf("Model: %s, Brand: %s, Inventory: %d, Price: %.2f\n", model, brand, inventory, price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Main application class
public class ReportApplication {
    public static void main(String[] args) {
        // Database setup
        String dbUrl = "jdbc:sqlite:car_dealership.db";
        DatabaseManager dbManager = new DatabaseManager(dbUrl);

        // Initialize tables (for demonstration purposes)
        initializeDatabase(dbManager);

        // Generate reports
        ReportGenerator reportGenerator = new ReportGenerator(dbManager);
        reportGenerator.generateTotalSalesByModel();
        reportGenerator.generateRevenueReport();
        reportGenerator.generateInventoryReport();
    }

    private static void initializeDatabase(DatabaseManager dbManager) {
        try {
            // Create inventory table
            String createInventoryTable = "CREATE TABLE IF NOT EXISTS inventory (\n"
                    + "model TEXT PRIMARY KEY,\n"
                    + "brand TEXT,\n"
                    + "inventory INTEGER,\n"
                    + "price REAL\n"
                    + ");";
            dbManager.executeUpdate(createInventoryTable);

            // Create sales table
            String createSalesTable = "CREATE TABLE IF NOT EXISTS sales (\n"
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                    + "model TEXT,\n"
                    + "quantity INTEGER,\n"
                    + "price REAL,\n"
                    + "FOREIGN KEY (model) REFERENCES inventory (model)\n"
                    + ");";
            dbManager.executeUpdate(createSalesTable);

            // Insert sample data
            String insertInventory = "INSERT OR IGNORE INTO inventory (model, brand, inventory, price) VALUES\n"
                    + "('Model X', 'Tesla', 50, 79999.99),\n"
                    + "('Model S', 'Tesla', 30, 89999.99),\n"
                    + "('Mustang', 'Ford', 20, 55999.99);";
            dbManager.executeUpdate(insertInventory);

            String insertSales = "INSERT INTO sales (model, quantity, price) VALUES\n"
                    + "('Model X', 10, 79999.99),\n"
                    + "('Model S', 5, 89999.99),\n"
                    + "('Mustang', 2, 55999.99);";
            dbManager.executeUpdate(insertSales);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}