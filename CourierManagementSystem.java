import java.sql.*;
import java.util.*;

public class CourierManagementSystem {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:courier_management.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String parcelsTable = "CREATE TABLE IF NOT EXISTS parcels (" +
                "parcel_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "sender_name TEXT," +
                "receiver_name TEXT," +
                "pickup_address TEXT," +
                "delivery_address TEXT," +
                "status TEXT," +
                "payment_status TEXT);";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(parcelsTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addParcel(String senderName, String receiverName, String pickupAddress, String deliveryAddress) {
        String sql = "INSERT INTO parcels(sender_name, receiver_name, pickup_address, delivery_address, status, payment_status) " +
                     "VALUES(?, ?, ?, ?, 'Pending Pickup', 'Unpaid');";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, senderName);
            pstmt.setString(2, receiverName);
            pstmt.setString(3, pickupAddress);
            pstmt.setString(4, deliveryAddress);
            pstmt.executeUpdate();
            System.out.println("Parcel added successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updateParcelStatus(int parcelId, String status) {
        String sql = "UPDATE parcels SET status = ? WHERE parcel_id = ?;";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, parcelId);
            pstmt.executeUpdate();
            System.out.println("Parcel status updated successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void updatePaymentStatus(int parcelId, String paymentStatus) {
        String sql = "UPDATE parcels SET payment_status = ? WHERE parcel_id = ?;";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, paymentStatus);
            pstmt.setInt(2, parcelId);
            pstmt.executeUpdate();
            System.out.println("Payment status updated successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewParcels() {
        String sql = "SELECT * FROM parcels;";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("parcel_id") + " | " +
                                   rs.getString("sender_name") + " | " +
                                   rs.getString("receiver_name") + " | " +
                                   rs.getString("pickup_address") + " | " +
                                   rs.getString("delivery_address") + " | " +
                                   rs.getString("status") + " | " +
                                   rs.getString("payment_status"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Parcel\n2. View Parcels\n3. Update Parcel Status\n4. Update Payment Status\n5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter sender name: ");
                    String senderName = scanner.next();
                    System.out.print("Enter receiver name: ");
                    String receiverName = scanner.next();
                    System.out.print("Enter pickup address: ");
                    String pickupAddress = scanner.next();
                    System.out.print("Enter delivery address: ");
                    String deliveryAddress = scanner.next();
                    addParcel(senderName, receiverName, pickupAddress, deliveryAddress);
                    break;
                case 2:
                    viewParcels();
                    break;
                case 3:
                    System.out.print("Enter parcel ID: ");
                    int parcelId = scanner.nextInt();
                    System.out.print("Enter new status: ");
                    String status = scanner.next();
                    updateParcelStatus(parcelId, status);
                    break;
                case 4:
                    System.out.print("Enter parcel ID: ");
                    parcelId = scanner.nextInt();
                    System.out.print("Enter payment status: ");
                    String paymentStatus = scanner.next();
                    updatePaymentStatus(parcelId, paymentStatus);
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
