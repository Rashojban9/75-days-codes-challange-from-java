import java.sql.*;
import java.util.Scanner;

// Database Manager
class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:sales_management.db";
    
    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
            String createSalesTable = "CREATE TABLE IF NOT EXISTS sales (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "customer_name TEXT NOT NULL," +
                    "model TEXT NOT NULL," +
                    "sale_date TEXT NOT NULL," +
                    "price REAL NOT NULL)";
            stmt.execute(createSalesTable);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
}

// Sale Entity
class Sale {
    private String customerName;
    private String model;
    private String saleDate;
    private double price;

    public Sale(String customerName, String model, String saleDate, double price) {
        this.customerName = customerName;
        this.model = model;
        this.saleDate = saleDate;
        this.price = price;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getModel() {
        return model;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public double getPrice() {
        return price;
    }
}

// Sales Manager
class SalesManager {
    private DatabaseManager dbManager;

    public SalesManager(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void recordSale(Sale sale) {
        String query = "INSERT INTO sales (customer_name, model, sale_date, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, sale.getCustomerName());
            pstmt.setString(2, sale.getModel());
            pstmt.setString(3, sale.getSaleDate());
            pstmt.setDouble(4, sale.getPrice());
            pstmt.executeUpdate();
            System.out.println("Sale recorded successfully.");
        } catch (SQLException e) {
            System.out.println("Error recording sale: " + e.getMessage());
        }
    }

    public void displaySalesHistory(String filterType, String filterValue) {
        String query;

        switch (filterType.toLowerCase()) {
            case "model":
                query = "SELECT * FROM sales WHERE model = ?";
                break;
            case "customer":
                query = "SELECT * FROM sales WHERE customer_name = ?";
                break;
            case "date":
                query = "SELECT * FROM sales WHERE sale_date = ?";
                break;
            default:
                System.out.println("Invalid filter type.");
                return;
        }

        try (Connection conn = dbManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, filterValue);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Customer: " + rs.getString("customer_name") + ", Model: " + rs.getString("model") + ", Date: " + rs.getString("sale_date") + ", Price: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching sales history: " + e.getMessage());
        }
    }

    public void generateSalesReport() {
        String query = "SELECT COUNT(*) AS total_sales, SUM(price) AS total_revenue FROM sales";

        try (Connection conn = dbManager.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                System.out.println("Total Sales: " + rs.getInt("total_sales"));
                System.out.println("Total Revenue: $" + rs.getDouble("total_revenue"));
            }
        } catch (SQLException e) {
            System.out.println("Error generating sales report: " + e.getMessage());
        }
    }
}

// Main Application
public class SalesManagementApp {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        SalesManager salesManager = new SalesManager(dbManager);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Sales Management System ---");
            System.out.println("1. Record a Sale");
            System.out.println("2. View Sales History");
            System.out.println("3. Generate Sales Report");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter customer name: ");
                    String customerName = scanner.nextLine();
                    System.out.print("Enter phone model: ");
                    String model = scanner.nextLine();
                    System.out.print("Enter sale date (YYYY-MM-DD): ");
                    String saleDate = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();

                    Sale sale = new Sale(customerName, model, saleDate, price);
                    salesManager.recordSale(sale);
                    break;

                case 2:
                    System.out.print("Filter by (model/customer/date): ");
                    String filterType = scanner.nextLine();
                    System.out.print("Enter value for " + filterType + ": ");
                    String filterValue = scanner.nextLine();
                    salesManager.displaySalesHistory(filterType, filterValue);
                    break;

                case 3:
                    salesManager.generateSalesReport();
                    break;

                case 4:
                    System.out.println("Exiting system. Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
