import java.sql.*;
import java.util.*;

public class RestaurantManagementSystem {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:restaurant_management.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String tablesTable = "CREATE TABLE IF NOT EXISTS tables (" +
                "table_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "table_number INTEGER," +
                "is_reserved INTEGER DEFAULT 0);";

        String menuTable = "CREATE TABLE IF NOT EXISTS menu (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "price REAL);";

        String ordersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "table_id INTEGER," +
                "order_details TEXT," +
                "total_amount REAL," +
                "status TEXT," +
                "FOREIGN KEY(table_id) REFERENCES tables(table_id));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(tablesTable);
            stmt.execute(menuTable);
            stmt.execute(ordersTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addMenuItem(String name, double price) {
        String sql = "INSERT INTO menu(name, price) VALUES(?, ?);";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.executeUpdate();
            System.out.println("Menu item added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void reserveTable(int tableNumber) {
        String checkSql = "SELECT is_reserved FROM tables WHERE table_number = ?;";
        String updateSql = "UPDATE tables SET is_reserved = 1 WHERE table_number = ?;";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setInt(1, tableNumber);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt("is_reserved") == 0) {
                updateStmt.setInt(1, tableNumber);
                updateStmt.executeUpdate();
                System.out.println("Table reserved successfully.");
            } else {
                System.out.println("Table is already reserved or does not exist.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createOrder(int tableId, String orderDetails, double totalAmount) {
        String sql = "INSERT INTO orders(table_id, order_details, total_amount, status) VALUES(?, ?, ?, 'Pending');";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, tableId);
            pstmt.setString(2, orderDetails);
            pstmt.setDouble(3, totalAmount);
            pstmt.executeUpdate();
            System.out.println("Order created successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?;";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            pstmt.executeUpdate();
            System.out.println("Order status updated successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewMenu() {
        String sql = "SELECT * FROM menu;";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Item ID: " + rs.getInt("item_id") + ", Name: " + rs.getString("name") + ", Price: " + rs.getDouble("price"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Menu Item\n2. View Menu\n3. Reserve Table\n4. Create Order\n5. Update Order Status\n6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter item name: ");
                    String name = scanner.next();
                    System.out.print("Enter item price: ");
                    double price = scanner.nextDouble();
                    addMenuItem(name, price);
                    break;
                case 2:
                    viewMenu();
                    break;
                case 3:
                    System.out.print("Enter table number to reserve: ");
                    int tableNumber = scanner.nextInt();
                    reserveTable(tableNumber);
                    break;
                case 4:
                    System.out.print("Enter table ID: ");
                    int tableId = scanner.nextInt();
                    System.out.print("Enter order details: ");
                    scanner.nextLine();  // Consume newline
                    String orderDetails = scanner.nextLine();
                    System.out.print("Enter total amount: ");
                    double totalAmount = scanner.nextDouble();
                    createOrder(tableId, orderDetails, totalAmount);
                    break;
                case 5:
                    System.out.print("Enter order ID: ");
                    int orderId = scanner.nextInt();
                    System.out.print("Enter new status: ");
                    String status = scanner.next();
                    updateOrderStatus(orderId, status);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
