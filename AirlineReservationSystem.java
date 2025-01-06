import java.sql.*;
import java.util.*;

public class AirlineReservationSystem {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:airline.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String flightsTable = "CREATE TABLE IF NOT EXISTS flights (" +
                "flight_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "flight_name TEXT," +
                "source TEXT," +
                "destination TEXT," +
                "departure_time TEXT," +
                "arrival_time TEXT," +
                "seats_available INTEGER);";

        String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "flight_id INTEGER," +
                "customer_name TEXT," +
                "contact_number TEXT," +
                "status TEXT," +
                "FOREIGN KEY (flight_id) REFERENCES flights (flight_id));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(flightsTable);
            stmt.execute(bookingsTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addFlight(String name, String source, String destination, String depTime, String arrTime, int seats) {
        String sql = "INSERT INTO flights(flight_name, source, destination, departure_time, arrival_time, seats_available) VALUES(?, ?, ?, ?, ?, ?);";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, source);
            pstmt.setString(3, destination);
            pstmt.setString(4, depTime);
            pstmt.setString(5, arrTime);
            pstmt.setInt(6, seats);
            pstmt.executeUpdate();
            System.out.println("Flight added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void bookTicket(int flightId, String customerName, String contactNumber) {
        String sql = "INSERT INTO bookings(flight_id, customer_name, contact_number, status) VALUES(?, ?, ?, 'Booked');";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, flightId);
            pstmt.setString(2, customerName);
            pstmt.setString(3, contactNumber);
            pstmt.executeUpdate();
            System.out.println("Ticket booked successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void cancelTicket(int bookingId) {
        String sql = "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?;";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.executeUpdate();
            System.out.println("Ticket cancelled successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewFlights() {
        String sql = "SELECT * FROM flights;";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(rs.getInt("flight_id") + " | " +
                        rs.getString("flight_name") + " | " +
                        rs.getString("source") + " -> " +
                        rs.getString("destination") + " | " +
                        rs.getString("departure_time") + " - " +
                        rs.getString("arrival_time") + " | Seats: " +
                        rs.getInt("seats_available"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Flight\n2. View Flights\n3. Book Ticket\n4. Cancel Ticket\n5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter flight name: ");
                    String name = scanner.next();
                    System.out.print("Enter source: ");
                    String source = scanner.next();
                    System.out.print("Enter destination: ");
                    String destination = scanner.next();
                    System.out.print("Enter departure time: ");
                    String depTime = scanner.next();
                    System.out.print("Enter arrival time: ");
                    String arrTime = scanner.next();
                    System.out.print("Enter seats available: ");
                    int seats = scanner.nextInt();
                    addFlight(name, source, destination, depTime, arrTime, seats);
                    break;
                case 2:
                    viewFlights();
                    break;
                case 3:
                    System.out.print("Enter flight ID: ");
                    int flightId = scanner.nextInt();
                    System.out.print("Enter customer name: ");
                    String customerName = scanner.next();
                    System.out.print("Enter contact number: ");
                    String contactNumber = scanner.next();
                    bookTicket(flightId, customerName, contactNumber);
                    break;
                case 4:
                    System.out.print("Enter booking ID: ");
                    int bookingId = scanner.nextInt();
                    cancelTicket(bookingId);
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
