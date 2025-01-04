import java.sql.*;
import java.util.Scanner;

// Product class
class Product {
    int id;
    String name;
    int quantity;
    double price;

    Product(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }
}

// Inventory Management System class
public class InventoryManagementSystem {

    // SQLite connection
    private static Connection connect() {
        Connection conn = null;
        try {
            // Connect to SQLite database
            conn = DriverManager.getConnection("jdbc:sqlite:inventory.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Create table if not exists
    static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Products (" +
                     "id INTEGER PRIMARY KEY, " +
                     "name TEXT, " +
                     "quantity INTEGER, " +
                     "price REAL)";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Add a new product
    static void addProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        System.out.print("Enter Price: ");
        double price = sc.nextDouble();

        String sql = "INSERT INTO Products(id, name, quantity, price) VALUES(?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, price);
            pstmt.executeUpdate();
            System.out.println("Product added successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // View all products
    static void viewProducts() {
        String sql = "SELECT * FROM Products";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\nList of Products:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                                   ", Name: " + rs.getString("name") +
                                   ", Quantity: " + rs.getInt("quantity") +
                                   ", Price: $" + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Update stock
    static void updateStock() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        System.out.print("Enter New Quantity: ");
        int quantity = sc.nextInt();

        String sql = "UPDATE Products SET quantity = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, id);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Stock updated successfully!");
            } else {
                System.out.println("Product not found!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Process sale
    static void processSale() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        System.out.print("Enter Quantity Sold: ");
        int quantitySold = sc.nextInt();

        String selectSQL = "SELECT quantity, price FROM Products WHERE id = ?";
        String updateSQL = "UPDATE Products SET quantity = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {

            selectStmt.setInt(1, id);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                if (currentQuantity >= quantitySold) {
                    int newQuantity = currentQuantity - quantitySold;

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, newQuantity);
                        updateStmt.setInt(2, id);
                        updateStmt.executeUpdate();
                        System.out.println("Sale processed successfully! Total: $" + (price * quantitySold));
                    }
                } else {
                    System.out.println("Insufficient stock!");
                }
            } else {
                System.out.println("Product not found!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        createTable(); // Ensure table exists

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nInventory Management System");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Stock");
            System.out.println("4. Process Sale");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewProducts();
                    break;
                case 3:
                    updateStock();
                    break;
                case 4:
                    processSale();
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
