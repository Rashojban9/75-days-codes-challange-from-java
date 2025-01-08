import java.sql.*;
import java.util.*;

public class RealEstateManagementSystem {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:real_estate.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String propertiesTable = "CREATE TABLE IF NOT EXISTS properties (" +
                "property_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "property_name TEXT," +
                "location TEXT," +
                "price REAL," +
                "status TEXT);";

        String agentsTable = "CREATE TABLE IF NOT EXISTS agents (" +
                "agent_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "contact TEXT);";

        String clientsTable = "CREATE TABLE IF NOT EXISTS clients (" +
                "client_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "contact TEXT);";

        String bookingsTable = "CREATE TABLE IF NOT EXISTS bookings (" +
                "booking_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "property_id INTEGER," +
                "client_id INTEGER," +
                "agent_id INTEGER," +
                "booking_date TEXT," +
                "status TEXT," +
                "FOREIGN KEY (property_id) REFERENCES properties(property_id)," +
                "FOREIGN KEY (client_id) REFERENCES clients(client_id)," +
                "FOREIGN KEY (agent_id) REFERENCES agents(agent_id));";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(propertiesTable);
            stmt.execute(agentsTable);
            stmt.execute(clientsTable);
            stmt.execute(bookingsTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addProperty(String name, String location, double price) {
        String sql = "INSERT INTO properties(property_name, location, price, status) VALUES(?, ?, ?, 'Available');";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, location);
            pstmt.setDouble(3, price);
            pstmt.executeUpdate();
            System.out.println("Property added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addAgent(String name, String contact) {
        String sql = "INSERT INTO agents(name, contact) VALUES(?, ?);";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.executeUpdate();
            System.out.println("Agent added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addClient(String name, String contact) {
        String sql = "INSERT INTO clients(name, contact) VALUES(?, ?);";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, contact);
            pstmt.executeUpdate();
            System.out.println("Client added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void bookProperty(int propertyId, int clientId, int agentId, String bookingDate) {
        String sql = "INSERT INTO bookings(property_id, client_id, agent_id, booking_date, status) VALUES(?, ?, ?, ?, 'Booked');";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, propertyId);
            pstmt.setInt(2, clientId);
            pstmt.setInt(3, agentId);
            pstmt.setString(4, bookingDate);
            pstmt.executeUpdate();

            String updateProperty = "UPDATE properties SET status = 'Booked' WHERE property_id = ?;";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateProperty)) {
                updateStmt.setInt(1, propertyId);
                updateStmt.executeUpdate();
            }
            System.out.println("Property booked successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewProperties() {
        String sql = "SELECT * FROM properties;";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("property_id") + " | " +
                        rs.getString("property_name") + " | " +
                        rs.getString("location") + " | " +
                        rs.getDouble("price") + " | " +
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
            System.out.println("1. Add Property\n2. View Properties\n3. Add Agent\n4. Add Client\n5. Book Property\n6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter property name: ");
                    String name = scanner.next();
                    System.out.print("Enter location: ");
                    String location = scanner.next();
                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();
                    addProperty(name, location, price);
                    break;
                case 2:
                    viewProperties();
                    break;
                case 3:
                    System.out.print("Enter agent name: ");
                    String agentName = scanner.next();
                    System.out.print("Enter contact: ");
                    String agentContact = scanner.next();
                    addAgent(agentName, agentContact);
                    break;
                case 4:
                    System.out.print("Enter client name: ");
                    String clientName = scanner.next();
                    System.out.print("Enter contact: ");
                    String clientContact = scanner.next();
                    addClient(clientName, clientContact);
                    break;
                case 5:
                    System.out.print("Enter property ID: ");
                    int propertyId = scanner.nextInt();
                    System.out.print("Enter client ID: ");
                    int clientId = scanner.nextInt();
                    System.out.print("Enter agent ID: ");
                    int agentId = scanner.nextInt();
                    System.out.print("Enter booking date: ");
                    String bookingDate = scanner.next();
                    bookProperty(propertyId, clientId, agentId, bookingDate);
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
