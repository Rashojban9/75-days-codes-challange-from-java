import java.sql.*;
import java.util.*;

class Database {
    private static final String URL = "jdbc:sqlite:vehicles.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error", e);
        }
    }

    public static void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Vehicles (id INTEGER PRIMARY KEY, brand TEXT, model TEXT, year INTEGER)");
            stmt.execute("CREATE TABLE IF NOT EXISTS FuelLog (id INTEGER PRIMARY KEY, vehicle_id INTEGER, mileage INTEGER, fuel DOUBLE, FOREIGN KEY(vehicle_id) REFERENCES Vehicles(id))");
            stmt.execute("CREATE TABLE IF NOT EXISTS Reminders (id INTEGER PRIMARY KEY, vehicle_id INTEGER, type TEXT, date TEXT, FOREIGN KEY(vehicle_id) REFERENCES Vehicles(id))");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization error", e);
        }
    }
}

class Vehicle {
    private int id;
    private String brand, model;
    private int year;

    public Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    public void save() {
        String sql = "INSERT INTO Vehicles (brand, model, year) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, brand);
            pstmt.setString(2, model);
            pstmt.setInt(3, year);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving vehicle", e);
        }
    }
}

class FuelLog {
    public static void logFuel(int vehicleId, int mileage, double fuel) {
        String sql = "INSERT INTO FuelLog (vehicle_id, mileage, fuel) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            pstmt.setInt(2, mileage);
            pstmt.setDouble(3, fuel);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error logging fuel", e);
        }
    }
}

class Reminder {
    public static void setReminder(int vehicleId, String type, String date) {
        String sql = "INSERT INTO Reminders (vehicle_id, type, date) VALUES (?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            pstmt.setString(2, type);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error setting reminder", e);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Database.initialize();
        Vehicle car = new Vehicle("Toyota", "Camry", 2020);
        car.save();
        FuelLog.logFuel(1, 12000, 40);
        Reminder.setReminder(1, "Service", "2025-06-15");
        System.out.println("Vehicle registered and logs updated.");
    }
}