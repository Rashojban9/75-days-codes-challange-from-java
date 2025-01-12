import java.sql.*;
import java.util.Scanner;

public class GymManagementSystem {

    // Database connection method
    private static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:gym.db");
        } catch (Exception e) {
            System.out.println("Connection Error: " + e.getMessage());
        }
        return conn;
    }

    // Create necessary tables
    private static void createTables() {
        String membersTable = """
            CREATE TABLE IF NOT EXISTS Members (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                age INTEGER,
                contact TEXT,
                subscription_type TEXT,
                subscription_end_date TEXT
            )
        """;

        String attendanceTable = """
            CREATE TABLE IF NOT EXISTS Attendance (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                member_id INTEGER,
                date TEXT,
                FOREIGN KEY(member_id) REFERENCES Members(id)
            )
        """;

        String trainersTable = """
            CREATE TABLE IF NOT EXISTS Trainers (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                expertise TEXT,
                schedule TEXT
            )
        """;

        String paymentsTable = """
            CREATE TABLE IF NOT EXISTS Payments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                member_id INTEGER,
                amount REAL,
                payment_date TEXT,
                FOREIGN KEY(member_id) REFERENCES Members(id)
            )
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(membersTable);
            stmt.execute(attendanceTable);
            stmt.execute(trainersTable);
            stmt.execute(paymentsTable);
            System.out.println("Tables created successfully!");
        } catch (SQLException e) {
            System.out.println("Table Creation Error: " + e.getMessage());
        }
    }

    // Add a new member
    private static void addMember(String name, int age, String contact, String subscriptionType, String subscriptionEndDate) {
        String sql = "INSERT INTO Members (name, age, contact, subscription_type, subscription_end_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, contact);
            pstmt.setString(4, subscriptionType);
            pstmt.setString(5, subscriptionEndDate);
            pstmt.executeUpdate();
            System.out.println("Member added successfully!");
        } catch (SQLException e) {
            System.out.println("Error Adding Member: " + e.getMessage());
        }
    }

    // Track attendance
    private static void markAttendance(int memberId, String date) {
        String sql = "INSERT INTO Attendance (member_id, date) VALUES (?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setString(2, date);
            pstmt.executeUpdate();
            System.out.println("Attendance marked successfully!");
        } catch (SQLException e) {
            System.out.println("Error Marking Attendance: " + e.getMessage());
        }
    }

    // Add a new trainer
    private static void addTrainer(String name, String expertise, String schedule) {
        String sql = "INSERT INTO Trainers (name, expertise, schedule) VALUES (?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, expertise);
            pstmt.setString(3, schedule);
            pstmt.executeUpdate();
            System.out.println("Trainer added successfully!");
        } catch (SQLException e) {
            System.out.println("Error Adding Trainer: " + e.getMessage());
        }
    }

    // Record a payment
    private static void recordPayment(int memberId, double amount, String paymentDate) {
        String sql = "INSERT INTO Payments (member_id, amount, payment_date) VALUES (?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, paymentDate);
            pstmt.executeUpdate();
            System.out.println("Payment recorded successfully!");
        } catch (SQLException e) {
            System.out.println("Error Recording Payment: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nGym Management System");
            System.out.println("1. Add Member");
            System.out.println("2. Mark Attendance");
            System.out.println("3. Add Trainer");
            System.out.println("4. Record Payment");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Age: ");
                    int age = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Contact: ");
                    String contact = scanner.nextLine();
                    System.out.print("Enter Subscription Type: ");
                    String subscriptionType = scanner.nextLine();
                    System.out.print("Enter Subscription End Date (YYYY-MM-DD): ");
                    String subscriptionEndDate = scanner.nextLine();
                    addMember(name, age, contact, subscriptionType, subscriptionEndDate);
                }
                case 2 -> {
                    System.out.print("Enter Member ID: ");
                    int memberId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter Date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    markAttendance(memberId, date);
                }
                case 3 -> {
                    scanner.nextLine();
                    System.out.print("Enter Trainer Name: ");
                    String trainerName = scanner.nextLine();
                    System.out.print("Enter Expertise: ");
                    String expertise = scanner.nextLine();
                    System.out.print("Enter Schedule: ");
                    String schedule = scanner.nextLine();
                    addTrainer(trainerName, expertise, schedule);
                }
                case 4 -> {
                    System.out.print("Enter Member ID: ");
                    int memberId = scanner.nextInt();
                    System.out.print("Enter Payment Amount: ");
                    double amount = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Enter Payment Date (YYYY-MM-DD): ");
                    String paymentDate = scanner.nextLine();
                    recordPayment(memberId, amount, paymentDate);
                }
                case 5 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
