import java.sql.*;
import java.util.Scanner;

// Base Entity Classes
class Student {
    int id;
    String name;
    String dob;
    String email;
    String phone;

    public Student(String name, String dob, String email, String phone) {
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.phone = phone;
    }
}

class Faculty {
    int id;
    String name;
    String email;
    String phone;

    public Faculty(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}

class Course {
    int id;
    String courseName;
    int facultyId;

    public Course(String courseName, int facultyId) {
        this.courseName = courseName;
        this.facultyId = facultyId;
    }
}

class Grade {
    int id;
    int studentId;
    int courseId;
    String grade;

    public Grade(int studentId, int courseId, String grade) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.grade = grade;
    }
}

// Main University Management System
public class UniversityManagementSystem {
    private static final String DATABASE_URL = "jdbc:sqlite:university.db";

    // Database Connection
    private static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DATABASE_URL);
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e.getMessage());
            return null;
        }
    }

    // Initialize Database and Tables
    private static void initializeDB() {
        String[] tables = {
            """
            CREATE TABLE IF NOT EXISTS Students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                dob TEXT,
                email TEXT,
                phone TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS Faculty (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT,
                phone TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS Courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_name TEXT NOT NULL,
                faculty_id INTEGER,
                FOREIGN KEY(faculty_id) REFERENCES Faculty(id)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS Grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER,
                course_id INTEGER,
                grade TEXT,
                FOREIGN KEY(student_id) REFERENCES Students(id),
                FOREIGN KEY(course_id) REFERENCES Courses(id)
            )
            """
        };

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                stmt.execute(table);
            }
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // Add a Student
    private static void addStudent(Student student) {
        String sql = "INSERT INTO Students (name, dob, email, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.name);
            pstmt.setString(2, student.dob);
            pstmt.setString(3, student.email);
            pstmt.setString(4, student.phone);
            pstmt.executeUpdate();
            System.out.println("Student added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    // Add a Course
    private static void addCourse(Course course) {
        String sql = "INSERT INTO Courses (course_name, faculty_id) VALUES (?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.courseName);
            pstmt.setInt(2, course.facultyId);
            pstmt.executeUpdate();
            System.out.println("Course added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    // Assign a Grade
    private static void assignGrade(Grade grade) {
        String sql = "INSERT INTO Grades (student_id, course_id, grade) VALUES (?, ?, ?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grade.studentId);
            pstmt.setInt(2, grade.courseId);
            pstmt.setString(3, grade.grade);
            pstmt.executeUpdate();
            System.out.println("Grade assigned successfully!");
        } catch (SQLException e) {
            System.out.println("Error assigning grade: " + e.getMessage());
        }
    }

    // Main Menu
    private static void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nUniversity Management System");
            System.out.println("1. Add Student");
            System.out.println("2. Add Course");
            System.out.println("3. Assign Grade");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Student Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
                    String dob = scanner.nextLine();
                    System.out.print("Enter Email: ");
                    String email = scanner.nextLine();
                    System.out.print("Enter Phone: ");
                    String phone = scanner.nextLine();
                    addStudent(new Student(name, dob, email, phone));
                }
                case 2 -> {
                    System.out.print("Enter Course Name: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Enter Faculty ID: ");
                    int facultyId = scanner.nextInt();
                    addCourse(new Course(courseName, facultyId));
                }
                case 3 -> {
                    System.out.print("Enter Student ID: ");
                    int studentId = scanner.nextInt();
                    System.out.print("Enter Course ID: ");
                    int courseId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter Grade: ");
                    String grade = scanner.nextLine();
                    assignGrade(new Grade(studentId, courseId, grade));
                }
                case 4 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }

    public static void main(String[] args) {
        initializeDB();
        mainMenu();
    }
}
