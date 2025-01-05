import java.sql.*;
import java.util.Scanner;

public class BankingManagementSystem {

    // Connect to SQLite database
    private static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Ensure driver is loaded
            conn = DriverManager.getConnection("jdbc:sqlite:bank.db");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    // Create necessary tables
    private static void createTables() {
        String accountsTable = "CREATE TABLE IF NOT EXISTS Accounts (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "balance REAL)";

        String loansTable = "CREATE TABLE IF NOT EXISTS Loans (" +
                "id INTEGER PRIMARY KEY, " +
                "account_id INTEGER, " +
                "amount REAL, " +
                "interest_rate REAL, " +
                "FOREIGN KEY(account_id) REFERENCES Accounts(id))";

        try (Connection conn = connect()) {
            if (conn == null) { // Check connection validity
                System.out.println("Failed to connect to database.");
                return;
            }
            Statement stmt = conn.createStatement();
            stmt.execute(accountsTable);
            stmt.execute(loansTable);
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a new account
    private static void addAccount() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Initial Balance: ");
        double balance = sc.nextDouble();

        String sql = "INSERT INTO Accounts(name, balance) VALUES(?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setDouble(2, balance);
            pstmt.executeUpdate();
            System.out.println("Account created successfully!");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Perform deposit
    private static void deposit() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();
        System.out.print("Enter Amount to Deposit: ");
        double amount = sc.nextDouble();

        String sql = "UPDATE Accounts SET balance = balance + ? WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("Deposit successful!");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Perform withdrawal
    private static void withdraw() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();
        System.out.print("Enter Amount to Withdraw: ");
        double amount = sc.nextDouble();

        String sql = "UPDATE Accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, id);
            pstmt.setDouble(3, amount);
            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Withdrawal successful!");
            } else {
                System.out.println("Insufficient balance or account not found!");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Generate account statement
    private static void viewStatement() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();

        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Account ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Balance: $" + rs.getDouble("balance"));
            } else {
                System.out.println("Account not found!");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Main menu
    public static void main(String[] args) {
        createTables();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nBanking Management System");
            System.out.println("1. Add Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. View Statement");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addAccount();
                    break;
                case 2:
                    deposit();
                    break;
                case 3:
                    withdraw();
                    break;
                case 4:
                    viewStatement();
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
