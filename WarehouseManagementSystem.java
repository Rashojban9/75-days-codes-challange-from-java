import java.sql.*;
import java.util.Scanner;

public class WarehouseManagementSystem {

    // Database connection
    private static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:warehouse.db");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Create database tables
    private static void createTables() {
        String productsTable = "CREATE TABLE IF NOT EXISTS Products (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "quantity INTEGER, " +
                "price REAL, " +
                "low_stock_threshold INTEGER)";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(productsTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add a product
    private static void addProduct(String name, int quantity, double price, int lowStockThreshold) {
        String sql = "INSERT INTO Products (name, quantity, price, low_stock_threshold) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, price);
            pstmt.setInt(4, lowStockThreshold);
            pstmt.executeUpdate();
            System.out.println("Product added successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // View all products
    private static void viewProducts() {
        String sql = "SELECT * FROM Products";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Product List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Quantity: " + rs.getInt("quantity") +
                        ", Price: $" + rs.getDouble("price") +
                        ", Low Stock Threshold: " + rs.getInt("low_stock_threshold"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Update stock
    private static void updateStock(int productId, int newQuantity) {
        String sql = "UPDATE Products SET quantity = ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Stock updated successfully!");
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Delete a product
    private static void deleteProduct(int productId) {
        String sql = "DELETE FROM Products WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Check low stock
    private static void checkLowStock() {
        String sql = "SELECT * FROM Products WHERE quantity < low_stock_threshold";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Low Stock Products ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Name: " + rs.getString("name") +
                        ", Quantity: " + rs.getInt("quantity") +
                        ", Low Stock Threshold: " + rs.getInt("low_stock_threshold"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Warehouse Management System ---");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Stock");
            System.out.println("4. Delete Product");
            System.out.println("5. Check Low Stock");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("Enter Product Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Quantity: ");
                    int quantity = scanner.nextInt();
                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();
                    System.out.print("Enter Low Stock Threshold: ");
                    int lowStockThreshold = scanner.nextInt();
                    addProduct(name, quantity, price, lowStockThreshold);
                    break;
                case 2:
                    viewProducts();
                    break;
                case 3:
                    System.out.print("Enter Product ID: ");
                    int productId = scanner.nextInt();
                    System.out.print("Enter New Quantity: ");
                    int newQuantity = scanner.nextInt();
                    updateStock(productId, newQuantity);
                    break;
                case 4:
                    System.out.print("Enter Product ID: ");
                    int deleteId = scanner.nextInt();
                    deleteProduct(deleteId);
                    break;
                case 5:
                    checkLowStock();
                    break;
                case 6:
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
