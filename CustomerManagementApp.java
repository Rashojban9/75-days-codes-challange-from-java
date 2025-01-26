import java.sql.*;
import java.util.Scanner;

// Customer class
class Customer {
    private int id;
    private String name, phone, email;

    public Customer(int id, String name, String phone, String email) {
        this.id = id; this.name = name; this.phone = phone; this.email = email;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Phone: " + phone + " | Email: " + email;
    }
}

// Data Access Object (DAO)
class CustomerDAO {
    private static final String DB_URL = "jdbc:sqlite:customer_management.db";

    public CustomerDAO() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS customers (id INTEGER PRIMARY KEY, name TEXT, phone TEXT, email TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCustomer(String name, String phone, String email) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customers (name, phone, email) VALUES (?, ?, ?)")) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
            System.out.println("Customer added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCustomer(int id, String name, String phone, String email) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE customers SET name = ?, phone = ?, email = ? WHERE id = ?")) {
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setInt(4, id);
            if (pstmt.executeUpdate() > 0) System.out.println("Customer updated!");
            else System.out.println("Customer not found.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeCustomer(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM customers WHERE id = ?")) {
            pstmt.setInt(1, id);
            if (pstmt.executeUpdate() > 0) System.out.println("Customer removed!");
            else System.out.println("Customer not found.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listCustomers() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
            System.out.println("\nRegistered Customers:");
            while (rs.next()) {
                System.out.println(new Customer(rs.getInt("id"), rs.getString("name"), rs.getString("phone"), rs.getString("email")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Main Application
public class CustomerManagementApp {
    public static void main(String[] args) {
        CustomerDAO dao = new CustomerDAO();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Add Customer | 2. Update Customer | 3. Remove Customer | 4. View Customers | 5. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Phone: ");
                    String phone = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    dao.addCustomer(name, phone, email);
                }
                case 2 -> {
                    System.out.print("ID to update: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("New Name: ");
                    String name = scanner.nextLine();
                    System.out.print("New Phone: ");
                    String phone = scanner.nextLine();
                    System.out.print("New Email: ");
                    String email = scanner.nextLine();
                    dao.updateCustomer(id, name, phone, email);
                }
                case 3 -> {
                    System.out.print("ID to remove: ");
                    int id = scanner.nextInt();
                    dao.removeCustomer(id);
                }
                case 4 -> dao.listCustomers();
                case 5 -> {
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
