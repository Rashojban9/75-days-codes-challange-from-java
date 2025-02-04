import java.sql.*;
import java.util.Scanner;

public class EventTicketSystem {
    private static final String DB_URL = "jdbc:sqlite:events.db";
    
    public static void main(String[] args) {
        createDatabase();
        displayMenu();
    }
    
    private static void createDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String eventTable = "CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY, name TEXT, seats INTEGER)";
            String bookingTable = "CREATE TABLE IF NOT EXISTS bookings (id INTEGER PRIMARY KEY, event_id INTEGER, name TEXT, seat_no INTEGER)";
            stmt.execute(eventTable);
            stmt.execute(bookingTable);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
    
    private static void displayMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. List Events\n2. Book Ticket\n3. Cancel Booking\n4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            
            switch (choice) {
                case 1:
                    listEvents();
                    break;
                case 2:
                    bookTicket(scanner);
                    break;
                case 3:
                    cancelBooking(scanner);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
    
    private static void listEvents() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM events")) {
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("name") + " (Seats: " + rs.getInt("seats") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching events: " + e.getMessage());
        }
    }
    
    private static void bookTicket(Scanner scanner) {
        System.out.print("Enter Event ID: ");
        int eventId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Your Name: ");
        String name = scanner.nextLine();
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement checkSeats = conn.prepareStatement("SELECT seats FROM events WHERE id=?");
            checkSeats.setInt(1, eventId);
            ResultSet rs = checkSeats.executeQuery();
            
            if (rs.next() && rs.getInt("seats") > 0) {
                int availableSeats = rs.getInt("seats");
                int assignedSeat = availableSeats;
                
                PreparedStatement insertBooking = conn.prepareStatement("INSERT INTO bookings (event_id, name, seat_no) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                insertBooking.setInt(1, eventId);
                insertBooking.setString(2, name);
                insertBooking.setInt(3, assignedSeat);
                insertBooking.executeUpdate();
                
                PreparedStatement updateSeats = conn.prepareStatement("UPDATE events SET seats = seats - 1 WHERE id = ?");
                updateSeats.setInt(1, eventId);
                updateSeats.executeUpdate();
                
                System.out.println("Ticket booked successfully! Assigned Seat: " + assignedSeat);
            } else {
                System.out.println("No seats available for this event.");
            }
        } catch (SQLException e) {
            System.out.println("Error booking ticket: " + e.getMessage());
        }
    }
    
    private static void cancelBooking(Scanner scanner) {
        System.out.print("Enter Booking ID to cancel: ");
        int bookingId = scanner.nextInt();
        
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement getBooking = conn.prepareStatement("SELECT event_id, seat_no FROM bookings WHERE id=?");
            getBooking.setInt(1, bookingId);
            ResultSet rs = getBooking.executeQuery();
            
            if (rs.next()) {
                int eventId = rs.getInt("event_id");
                int seatNo = rs.getInt("seat_no");
                
                PreparedStatement deleteBooking = conn.prepareStatement("DELETE FROM bookings WHERE id=?");
                deleteBooking.setInt(1, bookingId);
                deleteBooking.executeUpdate();
                
                PreparedStatement updateSeats = conn.prepareStatement("UPDATE events SET seats = seats + 1 WHERE id=?");
                updateSeats.setInt(1, eventId);
                updateSeats.executeUpdate();
                
                System.out.println("Booking canceled. Seat " + seatNo + " is now available.");
            } else {
                System.out.println("Booking ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error canceling booking: " + e.getMessage());
        }
    }
}
