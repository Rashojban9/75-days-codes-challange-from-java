import java.sql.*;
import java.util.Scanner;

// Database Manager Class
class DatabaseManager {
    private Connection connection;

    public DatabaseManager(String dbName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            initializeDatabase();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        String createBrandsTable = """
            CREATE TABLE IF NOT EXISTS brands (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL
            );
        """;

        String createMobilesTable = """
            CREATE TABLE IF NOT EXISTS mobiles (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                brand_id INTEGER NOT NULL,
                model TEXT NOT NULL,
                price REAL NOT NULL,
                stock INTEGER NOT NULL,
                FOREIGN KEY (brand_id) REFERENCES brands (id)
            );
        """;

        String createCustomersTable = """
            CREATE TABLE IF NOT EXISTS customers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                email TEXT
            );
        """;

        String createSalesTable = """
            CREATE TABLE IF NOT EXISTS sales (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                mobile_id INTEGER NOT NULL,
                customer_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                FOREIGN KEY (mobile_id) REFERENCES mobiles (id),
                FOREIGN KEY (customer_id) REFERENCES customers (id)
            );
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(createBrandsTable);
            statement.execute(createMobilesTable);
            statement.execute(createCustomersTable);
            statement.execute(createSalesTable);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public void close() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing database: " + e.getMessage());
        }
    }
}

// Brand Management Class
class BrandManager {
    private DatabaseManager db;

    public BrandManager(DatabaseManager db) {
        this.db = db;
    }

    public void addBrand() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter brand name: ");
        String name = scanner.nextLine();

        String query = "INSERT INTO brands (name) VALUES (?);";
        try (PreparedStatement statement = db.prepareStatement(query)) {
            statement.setString(1, name);
            statement.executeUpdate();
            System.out.println("Brand added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding brand: " + e.getMessage());
        }
    }

    public void viewBrands() {
        String query = "SELECT * FROM brands;";
        try (PreparedStatement statement = db.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nAvailable Brands:");
            while (resultSet.next()) {
                System.out.printf("ID: %d, Name: %s%n", resultSet.getInt("id"), resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving brands: " + e.getMessage());
        }
    }
}

// Mobile Management Class
class MobileManager {
    private DatabaseManager db;

    public MobileManager(DatabaseManager db) {
        this.db = db;
    }

    public void addMobile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter brand ID: ");
        int brandId = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter model name: ");
        String model = scanner.nextLine();

        System.out.print("Enter price: ");
        double price = scanner.nextDouble();

        System.out.print("Enter stock quantity: ");
        int stock = scanner.nextInt();

        String query = "INSERT INTO mobiles (brand_id, model, price, stock) VALUES (?, ?, ?, ?);";
        try (PreparedStatement statement = db.prepareStatement(query)) {
            statement.setInt(1, brandId);
            statement.setString(2, model);
            statement.setDouble(3, price);
            statement.setInt(4, stock);
            statement.executeUpdate();
            System.out.println("Mobile added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding mobile: " + e.getMessage());
        }
    }

    public void viewMobiles() {
        String query = """
            SELECT m.id, b.name AS brand, m.model, m.price, m.stock
            FROM mobiles m
            JOIN brands b ON m.brand_id = b.id;
        """;
        try (PreparedStatement statement = db.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nAvailable Mobiles:");
            while (resultSet.next()) {
                System.out.printf("ID: %d, Brand: %s, Model: %s, Price: %.2f, Stock: %d%n",
                        resultSet.getInt("id"), resultSet.getString("brand"), resultSet.getString("model"),
                        resultSet.getDouble("price"), resultSet.getInt("stock"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving mobiles: " + e.getMessage());
        }
    }
}

// Customer Management Class
class CustomerManager {
    private DatabaseManager db;

    public CustomerManager(DatabaseManager db) {
        this.db = db;
    }

    public void addCustomer() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter customer name: ");
        String name = scanner.nextLine();

        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        String query = "INSERT INTO customers (name, phone, email) VALUES (?, ?, ?);";
        try (PreparedStatement statement = db.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, phone);
            statement.setString(3, email);
            statement.executeUpdate();
            System.out.println("Customer added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
        }
    }

    public void viewCustomers() {
        String query = "SELECT * FROM customers;";
        try (PreparedStatement statement = db.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            System.out.println("\nRegistered Customers:");
            while (resultSet.next()) {
                System.out.printf("ID: %d, Name: %s, Phone: %s, Email: %s%n",
                        resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("phone"),
                        resultSet.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving customers: " + e.getMessage());
        }
    }
}

// Main System Class
public class MobileManagementSystem {
    private DatabaseManager db;
    private BrandManager brandManager;
    private MobileManager mobileManager;
    private CustomerManager customerManager;

    public MobileManagementSystem() {
        db = new DatabaseManager("mobile_management.db");
        brandManager = new BrandManager(db);
        mobileManager = new MobileManager(db);
        customerManager = new CustomerManager(db);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Mobile Management System ===");
            System.out.println("1. Add Brand");
            System.out.println("2. View Brands");
            System.out.println("3. Add Mobile");
            System.out.println("4. View Mobiles");
            System.out.println("5. Add Customer");
            System.out.println("6. View Customers");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> brandManager.addBrand();
                case 2 -> brandManager.viewBrands();
                case 3 -> mobileManager.addMobile();
                case 4 -> mobileManager.viewMobiles();
                case 5 -> customerManager.addCustomer();
                case 6 -> customerManager.viewCustomers();
                case 7 -> {
                    db.close();
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        MobileManagementSystem system = new MobileManagementSystem();
        system.run();
    }
}
