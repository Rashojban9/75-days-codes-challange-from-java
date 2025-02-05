import java.sql.*;
import java.util.*;

class Database {
    private static final String URL = "jdbc:sqlite:finance.db";
    
    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    public static void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            String createTables = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    type TEXT,
                    category TEXT,
                    amount REAL,
                    date TEXT
                );
                CREATE TABLE IF NOT EXISTS budgets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category TEXT UNIQUE,
                    limit_amount REAL
                );
            """;
            stmt.executeUpdate(createTables);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Transaction {
    private String type, category, date;
    private double amount;

    public Transaction(String type, String category, double amount, String date) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    public void save() {
        String sql = "INSERT INTO transactions (type, category, amount, date) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            pstmt.setString(2, category);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Budget {
    public static void setBudget(String category, double limit) {
        String sql = "INSERT INTO budgets (category, limit_amount) VALUES (?, ?) ON CONFLICT(category) DO UPDATE SET limit_amount=?";
        try (Connection conn = Database.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category);
            pstmt.setDouble(2, limit);
            pstmt.setDouble(3, limit);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

class Report {
    public static void generateMonthlyReport() {
        String sql = "SELECT category, SUM(amount) as total FROM transactions WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now') GROUP BY category";
        try (Connection conn = Database.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("Monthly Financial Report:");
            while (rs.next()) {
                System.out.println(rs.getString("category") + ": " + rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

public class FinanceTracker {
    public static void main(String[] args) {
        Database.initialize();
        Transaction t1 = new Transaction("Expense", "Food", 50.0, "2025-02-05");
        t1.save();
        Budget.setBudget("Food", 500);
        Report.generateMonthlyReport();
    }
}
