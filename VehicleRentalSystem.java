import java.sql.*;
import java.util.*;

public class VehicleRentalSystem {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:vehicle_rental.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String vehiclesTable = "CREATE TABLE IF NOT EXISTS vehicles (" +
                "vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "vehicle_name TEXT," +
                "type TEXT," +
                "rental_price REAL," +
                "status TEXT);";

        String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "vehicle_id INTEGER," +
                "customer_name TEXT," +
                "contact_number TEXT," +
                "booking_date TEXT," +
                "return_date TEXT," +
                "total_cost REAL," +
                "status TEXT," +
                "FOREIGN KEY (vehicle_id) REFERENCES vehicles (vehicle_id));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(vehiclesTable);
            stmt.execute(bookingsTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addVehicle(String name, String type, double price) {
        String sql = "INSERT INTO vehicles(vehicle_name, type, rental_price, status) VALUES(?, ?, ?, 'Available');";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, type);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            System.out.println("Vehicle added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void bookVehicle(int vehicleId, String customerName, String contact, String bookingDate, String returnDate, double cost) {
        String sql = "INSERT INTO bookings(vehicle_id, customer_name, contact_number, booking_date, return_date, total_cost, status) VALUES(?, ?, ?, ?, ?, ?, 'Booked');";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, vehicleId);
            pstmt.setString(2, customerName);
            pstmt.setString(3, contact);
            pstmt.setString(4, bookingDate);
            pstmt.setString(5, returnDate);
            pstmt.setDouble(6, cost);
            pstmt.executeUpdate();

            String updateVehicle = "UPDATE vehicles SET status = 'Booked' WHERE vehicle_id = ?;";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateVehicle)) {
                updateStmt.setInt(1, vehicleId);
                updateStmt.executeUpdate();
            }
            System.out.println("Vehicle booked successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void returnVehicle(int bookingId) {
        String sql = "UPDATE bookings SET status = 'Returned' WHERE booking_id = ?;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();

            String vehicleIdQuery = "SELECT vehicle_id FROM bookings WHERE booking_id = ?;";
            try (PreparedStatement stmt = conn.prepareStatement(vehicleIdQuery)) {
                stmt.setInt(1, bookingId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int vehicleId = rs.getInt("vehicle_id");
                    String updateVehicle = "UPDATE vehicles SET status = 'Available' WHERE vehicle_id = ?;";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateVehicle)) {
                        updateStmt.setInt(1, vehicleId);
                        updateStmt.executeUpdate();
                    }
                }
            }
            System.out.println("Vehicle returned successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewVehicles() {
        String sql = "SELECT * FROM vehicles;";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("vehicle_id") + " | " +
                        rs.getString("vehicle_name") + " | " +
                        rs.getString("type") + " | " +
                        rs.getDouble("rental_price") + " | " +
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Vehicle\n2. View Vehicles\n3. Book Vehicle\n4. Return Vehicle\n5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter vehicle name: ");
                    String name = scanner.next();
                    System.out.print("Enter type: ");
                    String type = scanner.next();
                    System.out.print("Enter rental price: ");
                    double price = scanner.nextDouble();
                    addVehicle(name, type, price);
                    break;
                case 2:
                    viewVehicles();
                    break;
                case 3:
                    System.out.print("Enter vehicle ID: ");
                    int vehicleId = scanner.nextInt();
                    System.out.print("Enter customer name: ");
                    String customerName = scanner.next();
                    System.out.print("Enter contact number: ");
                    String contact = scanner.next();
                    System.out.print("Enter booking date: ");
                    String bookingDate = scanner.next();
                    System.out.print("Enter return date: ");
                    String returnDate = scanner.next();
                    System.out.print("Enter total cost: ");
                    double cost = scanner.nextDouble();
                    bookVehicle(vehicleId, customerName, contact, bookingDate, returnDate, cost);
                    break;
                case 4:
                    System.out.print("Enter booking ID: ");
                    int bookingId = scanner.nextInt();
                    returnVehicle(bookingId);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
