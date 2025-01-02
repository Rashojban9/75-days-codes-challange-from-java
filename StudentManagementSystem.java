import java.util.*;

class Student {
    int id;
    String name;
    int age;
    double grade;
    boolean attendance;

    Student(int id, String name, int age, double grade) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.attendance = false;
    }
}

public class StudentManagementSystem {
    private static Map<Integer, Student> students = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    // Add Student
    public static void addStudent() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Age: ");
        int age = scanner.nextInt();

        System.out.print("Enter Grade: ");
        double grade = scanner.nextDouble();

        students.put(id, new Student(id, name, age, grade));
        System.out.println("Student added successfully!\n");
    }

    // Display Students
    public static void displayStudents() {
        if (students.isEmpty()) {
            System.out.println("No students to display.\n");
            return;
        }
        for (Student s : students.values()) {
            System.out.println("ID: " + s.id + ", Name: " + s.name + ", Age: " + s.age + ", Grade: " + s.grade + ", Attendance: " + (s.attendance ? "Present" : "Absent"));
        }
        System.out.println();
    }

    // Mark Attendance
    public static void markAttendance() {
        System.out.print("Enter Student ID to mark attendance: ");
        int id = scanner.nextInt();
        if (students.containsKey(id)) {
            students.get(id).attendance = true;
            System.out.println("Attendance marked successfully!\n");
        } else {
            System.out.println("Student not found.\n");
        }
    }

    // Remove Student
    public static void removeStudent() {
        System.out.print("Enter Student ID to remove: ");
        int id = scanner.nextInt();
        if (students.containsKey(id)) {
            students.remove(id);
            System.out.println("Student removed successfully!\n");
        } else {
            System.out.println("Student not found.\n");
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("--- Student Management System ---");
            System.out.println("1. Add Student");
            System.out.println("2. Display Students");
            System.out.println("3. Mark Attendance");
            System.out.println("4. Remove Student");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    displayStudents();
                    break;
                case 3:
                    markAttendance();
                    break;
                case 4:
                    removeStudent();
                    break;
                case 5:
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please try again.\n");
            }
        }
    }
}
