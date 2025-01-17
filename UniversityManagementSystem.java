import java.sql.*;
import java.util.Scanner;

public class UniversityManagementSystem {

    // Database connection
    private static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:university.db");
        } catch (Exception e) {
            System.out.println("Database Connection Error: " + e.getMessage());
        }
        return conn;
    }

    // Initialize database and tables
    private static void initializeDB() {
        String studentsTable = """
            CREATE TABLE IF NOT EXISTS Students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                dob TEXT,
                email TEXT,
                phone TEXT
            )
        """;

        String facultyTable = """
            CREATE TABLE IF NOT EXISTS Faculty (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT,
                phone TEXT
            )
        """;

        String coursesTable = """
            CREATE TABLE IF NOT EXISTS Courses (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_name TEXT NOT NULL,
                faculty_id INTEGER,
                FOREIGN KEY(faculty_id) REFERENCES Faculty(id)
            )
        """;

        String gradesTable = """
            CREATE TABLE IF NOT EXISTS Grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER,
                course_id INTEGER,
                grade TEXT,
                FOREIGN KEY(student_id) REFERENCES Students(id),
                FOREIGN KEY(course_id) REFERENCES Courses(id)
            )
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(studentsTable);
            stmt.execute(facultyTable);
            stmt.execute(coursesTable);
            stmt.execute(gradesTable);
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // Add a student
    private static void addStudent() {
        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO Students (name, dob, email, phone) VALUES (?, ?, ?, ?)")) {
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Student Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Date of Birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine();

            pstmt.setString(1, name);
            pstmt.setString(2, dob);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();
            System.out.println("Student added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    // View students
    private static void viewStudents() {
        try (Connection conn = connect(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery("SELECT * FROM Students")) {
            
            System.out.println("\nStudents:");
            while (rs.next()) {
                System.out.printf("ID: %d, Name: %s, DOB: %s, Email: %s, Phone: %s%n",
                        rs.getInt("id"), rs.getString("name"), rs.getString("dob"), rs.getString("email"), rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving students: " + e.getMessage());
        }
    }

    // Add a course
    private static void addCourse() {
        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO Courses (course_name, faculty_id) VALUES (?, ?)")) {
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Course Name: ");
            String courseName = scanner.nextLine();
            System.out.print("Enter Faculty ID (or leave blank): ");
            String facultyId = scanner.nextLine();

            pstmt.setString(1, courseName);
            if (facultyId.isEmpty()) {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                pstmt.setInt(2, Integer.parseInt(facultyId));
            }
            pstmt.executeUpdate();
            System.out.println("Course added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    // View courses
    private static void viewCourses() {
        try (Connection conn = connect(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery("""
                SELECT Courses.id, Courses.course_name, Faculty.name AS faculty_name
                FROM Courses
                LEFT JOIN Faculty ON Courses.faculty_id = Faculty.id
            """)) {
            
            System.out.println("\nCourses:");
            while (rs.next()) {
                System.out.printf("ID: %d, Course: %s, Faculty: %s%n",
                        rs.getInt("id"), rs.getString("course_name"), 
                        rs.getString("faculty_name") != null ? rs.getString("faculty_name") : "Not Assigned");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving courses: " + e.getMessage());
        }
    }

    // Assign a grade
    private static void assignGrade() {
        try (Connection conn = connect(); 
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO Grades (student_id, course_id, grade) VALUES (?, ?, ?)")) {
            
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Student ID: ");
            int studentId = scanner.nextInt();
            System.out.print("Enter Course ID: ");
            int courseId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            System.out.print("Enter Grade: ");
            String grade = scanner.nextLine();

            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.setString(3, grade);
            pstmt.executeUpdate();
            System.out.println("Grade assigned successfully!");
        } catch (SQLException e) {
            System.out.println("Error assigning grade: " + e.getMessage());
        }
    }

    // View grades
    private static void viewGrades() {
        try (Connection conn = connect(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery("""
                SELECT Students.name AS student_name, Courses.course_name, Grades.grade
                FROM Grades
                JOIN Students ON Grades.student_id = Students.id
                JOIN Courses ON Grades.course_id = Courses.id
            """)) {
            
            System.out.println("\nGrades:");
            while (rs.next()) {
                System.out.printf("Student: %s, Course: %s, Grade: %s%n",
                        rs.getString("student_name"), rs.getString("course_name"), rs.getString("grade"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving grades: " + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {
        initializeDB();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nUniversity Management System");
            System.out.println("1. Add Student");
            System.out.println("2. View Students");
            System.out.println("3. Add Course");
            System.out.println("4. View Courses");
            System.out.println("5. Assign Grade");
            System.out.println("6. View Grades");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> addStudent();
                case 2 -> viewStudents();
                case 3 -> addCourse();
                case 4 -> viewCourses();
                case 5 -> assignGrade();
                case 6 -> viewGrades();
                case 7 -> {
                    System.out.println("Exiting... Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }
}
