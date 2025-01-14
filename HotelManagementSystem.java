import java.sql.*;
import java.util.Scanner;

public class HotelManagementSystem {

    // Connect to SQLite database
    private static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:hotel.db");
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
        return conn;
    }

    // Create necessary tables
    private static void createTables() {
        String roomsTable = """
            CREATE TABLE IF NOT EXISTS Rooms (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                room_number TEXT,
                room_type TEXT,
                price_per_night REAL,
                availability TEXT
            )
        """;

        String guestsTable = """
            CREATE TABLE IF NOT EXISTS Guests (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                contact TEXT,
                room_id INTEGER,
                check_in_date TEXT,
                check_out_date TEXT,
                total_bill REAL,
                FOREIGN KEY(room_id) REFERENCES Rooms(id)
            )
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(roomsTable);
            stmt.execute(guestsTable);
            System.out.println("Tables initialized successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // Add a new room
    private static void addRoom(String roomNumber, String roomType, double pricePerNight) {
        String sql = "INSERT INTO Rooms (room_number, room_type, price_per_night, availability) VALUES (?, ?, ?, 'Available')";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);
            pstmt.setString(2, roomType);
            pstmt.setDouble(3, pricePerNight);
            pstmt.executeUpdate();
            System.out.println("Room added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding room: " + e.getMessage());
        }
    }

    // Book a room
    private static void bookRoom(String name, String contact, int roomId, String checkInDate, String checkOutDate) {
        String checkRoomAvailability = "SELECT availability, price_per_night FROM Rooms WHERE id = ?";
        String updateRoomStatus = "UPDATE Rooms SET availability = 'Occupied' WHERE id = ?";
        String addGuest = """
            INSERT INTO Guests (name, contact, room_id, check_in_date, check_out_date, total_bill)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);

            try (PreparedStatement roomCheckStmt = conn.prepareStatement(checkRoomAvailability)) {
                roomCheckStmt.setInt(1, roomId);
                ResultSet rs = roomCheckStmt.executeQuery();
                if (rs.next() && rs.getString("availability").equalsIgnoreCase("Available")) {
                    double pricePerNight = rs.getDouble("price_per_night");

                    // Calculate total bill based on stay duration
                    long stayDuration = (java.sql.Date.valueOf(checkOutDate).getTime()
                            - java.sql.Date.valueOf(checkInDate).getTime()) / (1000 * 60 * 60 * 24);
                    double totalBill = stayDuration * pricePerNight;

                    try (PreparedStatement updateRoomStmt = conn.prepareStatement(updateRoomStatus);
                         PreparedStatement addGuestStmt = conn.prepareStatement(addGuest)) {
                        // Update room status
                        updateRoomStmt.setInt(1, roomId);
                        updateRoomStmt.executeUpdate();

                        // Add guest details
                        addGuestStmt.setString(1, name);
                        addGuestStmt.setString(2, contact);
                        addGuestStmt.setInt(3, roomId);
                        addGuestStmt.setString(4, checkInDate);
                        addGuestStmt.setString(5, checkOutDate);
                        addGuestStmt.setDouble(6, totalBill);
                        addGuestStmt.executeUpdate();

                        conn.commit();
                        System.out.println("Room booked successfully! Total Bill: $" + totalBill);
                    }
                } else {
                    System.out.println("Room is not available!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error booking room: " + e.getMessage());
        }
    }

    // List available rooms
    private static void listAvailableRooms() {
        String sql = "SELECT * FROM Rooms WHERE availability = 'Available'";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nAvailable Rooms:");
            System.out.printf("%-5s %-15s %-15s %-10s\n", "ID", "Room Number", "Room Type", "Price/Night");
            while (rs.next()) {
                System.out.printf("%-5d %-15s %-15s %-10.2f\n",
                        rs.getInt("id"), rs.getString("room_number"), rs.getString("room_type"), rs.getDouble("price_per_night"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching rooms: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nHotel Management System");
            System.out.println("1. Add Room");
            System.out.println("2. Book Room");
            System.out.println("3. List Available Rooms");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine();
                    System.out.print("Enter Room Number: ");
                    String roomNumber = scanner.nextLine();
                    System.out.print("Enter Room Type: ");
                    String roomType = scanner.nextLine();
                    System.out.print("Enter Price per Night: ");
                    double pricePerNight = scanner.nextDouble();
                    addRoom(roomNumber, roomType, pricePerNight);
                    break;
                case 2:
                    scanner.nextLine();
                    System.out.print("Enter Guest Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Guest Contact: ");
                    String contact = scanner.nextLine();
                    System.out.print("Enter Room ID to Book: ");
                    int roomId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Check-In Date (YYYY-MM-DD): ");
                    String checkInDate = scanner.nextLine();
                    System.out.print("Enter Check-Out Date (YYYY-MM-DD): ");
                    String checkOutDate = scanner.nextLine();
                    bookRoom(name, contact, roomId, checkInDate, checkOutDate);
                    break;
                case 3:
                    listAvailableRooms();
                    break;
                case 4:
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
