import java.sql.*;
import java.util.*;

public class OnlineVotingSystem {

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:online_voting.db");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private static void createTables() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," +
                "password TEXT," +
                "role TEXT);";

        String votersTable = "CREATE TABLE IF NOT EXISTS voters (" +
                "voter_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "voter_card TEXT UNIQUE," +
                "has_voted INTEGER DEFAULT 0);";

        String candidatesTable = "CREATE TABLE IF NOT EXISTS candidates (" +
                "candidate_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "votes INTEGER DEFAULT 0);";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(votersTable);
            stmt.execute(candidatesTable);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void registerVoter(String name, String voterCard) {
        String sql = "INSERT INTO voters(name, voter_card) VALUES(?, ?);";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, voterCard);
            pstmt.executeUpdate();
            System.out.println("Voter registered successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void vote(String voterCard, int candidateId) {
        String checkVoterSql = "SELECT has_voted FROM voters WHERE voter_card = ?;";
        String updateVoteSql = "UPDATE candidates SET votes = votes + 1 WHERE candidate_id = ?;";
        String updateVoterSql = "UPDATE voters SET has_voted = 1 WHERE voter_card = ?;";

        try (Connection conn = connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkVoterSql);
             PreparedStatement voteStmt = conn.prepareStatement(updateVoteSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateVoterSql)) {

            checkStmt.setString(1, voterCard);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt("has_voted") == 0) {
                voteStmt.setInt(1, candidateId);
                voteStmt.executeUpdate();

                updateStmt.setString(1, voterCard);
                updateStmt.executeUpdate();

                System.out.println("Vote cast successfully.");
            } else {
                System.out.println("Voter has already voted or does not exist.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void displayResults() {
        String sql = "SELECT * FROM candidates;";
        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("Candidate: " + rs.getString("name") + " | Votes: " + rs.getInt("votes"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Register Voter\n2. Vote\n3. Display Results\n4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter voter name: ");
                    String name = scanner.next();
                    System.out.print("Enter voter card: ");
                    String voterCard = scanner.next();
                    registerVoter(name, voterCard);
                    break;
                case 2:
                    System.out.print("Enter voter card: ");
                    voterCard = scanner.next();
                    System.out.print("Enter candidate ID: ");
                    int candidateId = scanner.nextInt();
                    vote(voterCard, candidateId);
                    break;
                case 3:
                    displayResults();
                    break;
                case 4:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }
}
