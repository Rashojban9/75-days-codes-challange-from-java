import java.sql.*;
import java.util.*;

class Mobile {
    private int id;
    private String name;
    private String brand;
    private double price;
    private String specifications;

    public Mobile(int id, String name, String brand, double price, String specifications) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.specifications = specifications;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public double getPrice() {
        return price;
    }

    public String getSpecifications() {
        return specifications;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Brand: " + brand + ", Price: " + price + ", Specifications: " + specifications;
    }
}

class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:mobiles.db";

    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS Mobiles (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "brand TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "specifications TEXT NOT NULL);";
            statement.execute(createTableQuery);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void addMobile(String name, String brand, double price, String specifications) {
        String insertQuery = "INSERT INTO Mobiles (name, brand, price, specifications) VALUES (?, ?, ?, ?);";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, brand);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, specifications);
            preparedStatement.executeUpdate();
            System.out.println("Mobile added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding mobile: " + e.getMessage());
        }
    }

    public static void updateMobile(int id, String name, String brand, double price, String specifications) {
        String updateQuery = "UPDATE Mobiles SET name = ?, brand = ?, price = ?, specifications = ? WHERE id = ?;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, brand);
            preparedStatement.setDouble(3, price);
            preparedStatement.setString(4, specifications);
            preparedStatement.setInt(5, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mobile updated successfully.");
            } else {
                System.out.println("Mobile not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating mobile: " + e.getMessage());
        }
    }

    public static void removeMobile(int id) {
        String deleteQuery = "DELETE FROM Mobiles WHERE id = ?;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Mobile removed successfully.");
            } else {
                System.out.println("Mobile not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error removing mobile: " + e.getMessage());
        }
    }

    public static void searchMobileByName(String name) {
        String searchQuery = "SELECT * FROM Mobiles WHERE name LIKE ?;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(searchQuery)) {
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(new Mobile(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("brand"),
                        resultSet.getDouble("price"),
                        resultSet.getString("specifications")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching mobile: " + e.getMessage());
        }
    }

    public static void searchMobileByBrand(String brand) {
        String searchQuery = "SELECT * FROM Mobiles WHERE brand LIKE ?;";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement preparedStatement = connection.prepareStatement(searchQuery)) {
            preparedStatement.setString(1, "%" + brand + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println(new Mobile(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("brand"),
                        resultSet.getDouble("price"),
                        resultSet.getString("specifications")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error searching mobile: " + e.getMessage());
        }
    }
}

public class MobileManagementApp {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nMobile Model Management System");
            System.out.println("1. Add Mobile");
            System.out.println("2. Update Mobile");
            System.out.println("3. Remove Mobile");
            System.out.println("4. Search Mobile by Name");
            System.out.println("5. Search Mobile by Brand");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Enter specifications: ");
                    String specifications = scanner.nextLine();
                    DatabaseManager.addMobile(name, brand, price, specifications);
                }
                case 2 -> {
                    System.out.print("Enter mobile ID to update: ");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter new brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Enter new price: ");
                    double price = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Enter new specifications: ");
                    String specifications = scanner.nextLine();
                    DatabaseManager.updateMobile(id, name, brand, price, specifications);
                }
                case 3 -> {
                    System.out.print("Enter mobile ID to remove: ");
                    int id = scanner.nextInt();
                    DatabaseManager.removeMobile(id);
                }
                case 4 -> {
                    System.out.print("Enter name to search: ");
                    String name = scanner.nextLine();
                    DatabaseManager.searchMobileByName(name);
                }
                case 5 -> {
                    System.out.print("Enter brand to search: ");
                    String brand = scanner.nextLine();
                    DatabaseManager.searchMobileByBrand(brand);
                }
                case 6 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 6);

        scanner.close();
    }
}
