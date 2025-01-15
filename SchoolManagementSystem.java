import java.sql.*;
import java.util.Scanner;

public class SchoolManagementSystem {

    // Connect to SQLite database
    private static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:school.db");
        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
        return conn;
    }

    // Create tables
    private static void createTables() {
        String studentsTable = """
                CREATE TABLE IF NOT EXISTS Students (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    grade TEXT,
                    attendance INTEGER DEFAULT 0
                );
                """;

        String teachersTable = """
                CREATE TABLE IF NOT EXISTS Teachers (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT,
                    subject TEXT
                );
                """;

        String classesTable = """
                CREATE TABLE IF NOT EXISTS Classes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    class_name TEXT,
                    teacher_id INTEGER,
                    FOREIGN KEY(teacher_id) REFERENCES Teachers(id)
                );
                """;

        String examsTable = """
                CREATE TABLE IF NOT EXISTS Exams (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_id INTEGER,
                    subject TEXT,
                    grade TEXT,
                    FOREIGN KEY(student_id) REFERENCES Students(id)
                );
                """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(studentsTable);
            stmt.execute(teachersTable);
            stmt.execute(classesTable);
            stmt.execute(examsTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // Add a student
    private static void addStudent(String name, String grade) {
        String sql = "INSERT INTO Students (name, grade) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, grade);
            pstmt.executeUpdate();
            System.out.println("Student added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    // Add a teacher
    private static void addTeacher(String name, String subject) {
        String sql = "INSERT INTO Teachers (name, subject) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, subject);
            pstmt.executeUpdate();
            System.out.println("Teacher added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding teacher: " + e.getMessage());
        }
    }

    // Assign a class to a teacher
    private static void assignClass(String className, int teacherId) {
        String sql = "INSERT INTO Classes (class_name, teacher_id) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, className);
            pstmt.setInt(2, teacherId);
            pstmt.executeUpdate();
            System.out.println("Class assigned successfully!");
        } catch (SQLException e) {
            System.out.println("Error assigning class: " + e.getMessage());
        }
    }

    // Add an exam grade
    private static void addExamGrade(int studentId, String subject, String grade) {
        String sql = "INSERT INTO Exams (student_id, subject, grade) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setString(2, subject);
            pstmt.setString(3, grade);
            pstmt.executeUpdate();
            System.out.println("Exam grade added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding exam grade: " + e.getMessage());
        }
    }

    // Generate report card
    private static void generateReportCard(int studentId) {
        String sql = "SELECT subject, grade FROM Exams WHERE student_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Report Card:");
            while (rs.next()) {
                System.out.println("Subject: " + rs.getString("subject") + ", Grade: " + rs.getString("grade"));
            }
        } catch (SQLException e) {
            System.out.println("Error generating report card: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        createTables();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nSchool Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Add Teacher");
            System.out.println("3. Assign Class");
            System.out.println("4. Add Exam Grade");
            System.out.println("5. Generate Report Card");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter Student Name: ");
                    String studentName = scanner.nextLine();
                    System.out.print("Enter Grade: ");
                    String grade = scanner.nextLine();
                    addStudent(studentName, grade);
                    break;
                case 2:
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter Teacher Name: ");
                    String teacherName = scanner.nextLine();
                    System.out.print("Enter Subject: ");
                    String subject = scanner.nextLine();
                    addTeacher(teacherName, subject);
                    break;
                case 3:
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter Class Name: ");
                    String className = scanner.nextLine();
                    System.out.print("Enter Teacher ID: ");
                    int teacherId = scanner.nextInt();
                    assignClass(className, teacherId);
                    break;
                case 4:
                    System.out.print("Enter Student ID: ");
                    int studentId = scanner.nextInt();
                    scanner.nextLine();  // Consume newline
                    System.out.print("Enter Subject: ");
                    String examSubject = scanner.nextLine();
                    System.out.print("Enter Grade: ");
                    String examGrade = scanner.nextLine();
                    addExamGrade(studentId, examSubject, examGrade);
                    break;
                case 5:
                    System.out.print("Enter Student ID: ");
                    int reportStudentId = scanner.nextInt();
                    generateReportCard(reportStudentId);
                    break;
                case 6:
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
