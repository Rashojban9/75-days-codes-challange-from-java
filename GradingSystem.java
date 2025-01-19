import java.sql.*;
import java.util.Scanner;

class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:grading_system.db";
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            initializeDatabase();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY, name TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS grades (id INTEGER PRIMARY KEY, student_id INTEGER, course TEXT, grade TEXT, FOREIGN KEY(student_id) REFERENCES students(id))");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

class Student {
    private int id;
    private String name;

    public Student(int id, String name) { this.id = id; this.name = name; }
    public Student(String name) { this.name = name; }
    public int getId() { return id; }
    public String getName() { return name; }
}

class Grade {
    private int studentId;
    private String course, grade;

    public Grade(int studentId, String course, String grade) {
        this.studentId = studentId;
        this.course = course;
        this.grade = grade;
    }

    public int getStudentId() { return studentId; }
    public String getCourse() { return course; }
    public String getGrade() { return grade; }
}

public class GradingSystem {
    private DatabaseManager dbManager;
    private Scanner scanner;

    public GradingSystem() {
        dbManager = new DatabaseManager();
        scanner = new Scanner(System.in);
    }

    public void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement("INSERT INTO students(name) VALUES(?)")) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            System.out.println("Student added!");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    public void assignGrade() {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter course: ");
        String course = scanner.nextLine();
        System.out.print("Enter grade: ");
        String grade = scanner.nextLine();
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement("INSERT INTO grades(student_id, course, grade) VALUES(?, ?, ?)")) {
            stmt.setInt(1, studentId);
            stmt.setString(2, course);
            stmt.setString(3, grade);
            stmt.executeUpdate();
            System.out.println("Grade assigned!");
        } catch (SQLException e) {
            System.out.println("Error assigning grade: " + e.getMessage());
        }
    }

    public void displayReport() {
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement("SELECT s.name, g.course, g.grade FROM students s JOIN grades g ON s.id = g.student_id WHERE s.id = ?")) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("\nReport for Student ID " + studentId + ":");
            while (rs.next()) {
                System.out.printf("Name: %s, Course: %s, Grade: %s%n", rs.getString("name"), rs.getString("course"), rs.getString("grade"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching report: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            System.out.println("\n1. Add Student\n2. Assign Grade\n3. Display Report\n4. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> assignGrade();
                case 3 -> displayReport();
                case 4 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    public static void main(String[] args) {
        new GradingSystem().run();
    }
}
