import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Class representing a Course
class Course {
    private int courseId;
    private String courseName;
    private String instructorName;

    public Course(int courseId, String courseName, String instructorName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.instructorName = instructorName;
    }

    public Course(String courseName, String instructorName) {
        this(-1, courseName, instructorName);
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    @Override
    public String toString() {
        return "Course ID: " + courseId + ", Name: " + courseName + ", Instructor: " + instructorName;
    }
}

// Class representing a Student
class Student {
    private int studentId;
    private String studentName;

    public Student(int studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public Student(String studentName) {
        this(-1, studentName);
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    @Override
    public String toString() {
        return "Student ID: " + studentId + ", Name: " + studentName;
    }
}

// Class for managing the database
class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:course_management.db";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS Courses (" +
                    "course_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "course_name TEXT NOT NULL, " +
                    "instructor_name TEXT NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS Students (" +
                    "student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_name TEXT NOT NULL)");
            stmt.execute("CREATE TABLE IF NOT EXISTS CourseAssignments (" +
                    "assignment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "course_id INTEGER NOT NULL, " +
                    "student_id INTEGER NOT NULL, " +
                    "FOREIGN KEY (course_id) REFERENCES Courses(course_id), " +
                    "FOREIGN KEY (student_id) REFERENCES Students(student_id))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCourse(Course course) {
        String query = "INSERT INTO Courses (course_name, instructor_name) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, course.getCourseName());
            pstmt.setString(2, course.getInstructorName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addStudent(Student student) {
        String query = "INSERT INTO Students (student_name) VALUES (?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, student.getStudentName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Course> getCourses() {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM Courses";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                courses.add(new Course(rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();
        String query = "SELECT * FROM Students";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                students.add(new Student(rs.getInt("student_id"),
                        rs.getString("student_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void assignStudentToCourse(int courseId, int studentId) {
        String query = "INSERT INTO CourseAssignments (course_id, student_id) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, courseId);
            pstmt.setInt(2, studentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

// Main Application Class
public class CourseManagementSystem {
    public static void main(String[] args) {
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nCourse Management System");
            System.out.println("1. Add Course");
            System.out.println("2. List Courses");
            System.out.println("3. Add Student");
            System.out.println("4. List Students");
            System.out.println("5. Assign Student to Course");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter course name: ");
                    String courseName = scanner.nextLine();
                    System.out.print("Enter instructor name: ");
                    String instructorName = scanner.nextLine();
                    dbManager.addCourse(new Course(courseName, instructorName));
                    System.out.println("Course added successfully.");
                }
                case 2 -> {
                    List<Course> courses = dbManager.getCourses();
                    courses.forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("Enter student name: ");
                    String studentName = scanner.nextLine();
                    dbManager.addStudent(new Student(studentName));
                    System.out.println("Student added successfully.");
                }
                case 4 -> {
                    List<Student> students = dbManager.getStudents();
                    students.forEach(System.out::println);
                }
                case 5 -> {
                    System.out.print("Enter course ID: ");
                    int courseId = scanner.nextInt();
                    System.out.print("Enter student ID: ");
                    int studentId = scanner.nextInt();
                    dbManager.assignStudentToCourse(courseId, studentId);
                    System.out.println("Student assigned to course successfully.");
                }
                case 6 -> {
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
