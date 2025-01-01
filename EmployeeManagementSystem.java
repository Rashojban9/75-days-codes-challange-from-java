import java.util.*;

class Employee {
    int id;
    String name;
    String department;
    double salary;
    boolean attendance;

    Employee(int id, String name, String department, double salary) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.salary = salary;
        this.attendance = false;
    }
}

public class EmployeeManagementSystem {
    private static Map<Integer, Employee> employees = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);

    // Add Employee
    public static void addEmployee() {
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Department: ");
        String department = scanner.nextLine();

        System.out.print("Enter Salary: ");
        double salary = scanner.nextDouble();

        employees.put(id, new Employee(id, name, department, salary));
        System.out.println("Employee added successfully!\n");
    }

    // Display Employees
    public static void displayEmployees() {
        if (employees.isEmpty()) {
            System.out.println("No employees to display.\n");
            return;
        }
        for (Employee e : employees.values()) {
            System.out.println("ID: " + e.id + ", Name: " + e.name + ", Department: " + e.department + ", Salary: " + e.salary + ", Attendance: " + (e.attendance ? "Present" : "Absent"));
        }
        System.out.println();
    }

    // Mark Attendance
    public static void markAttendance() {
        System.out.print("Enter Employee ID to mark attendance: ");
        int id = scanner.nextInt();
        if (employees.containsKey(id)) {
            employees.get(id).attendance = true;
            System.out.println("Attendance marked successfully!\n");
        } else {
            System.out.println("Employee not found.\n");
        }
    }

    // Remove Employee
    public static void removeEmployee() {
        System.out.print("Enter Employee ID to remove: ");
        int id = scanner.nextInt();
        if (employees.containsKey(id)) {
            employees.remove(id);
            System.out.println("Employee removed successfully!\n");
        } else {
            System.out.println("Employee not found.\n");
        }
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("--- Employee Management System ---");
            System.out.println("1. Add Employee");
            System.out.println("2. Display Employees");
            System.out.println("3. Mark Attendance");
            System.out.println("4. Remove Employee");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    displayEmployees();
                    break;
                case 3:
                    markAttendance();
                    break;
                case 4:
                    removeEmployee();
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
