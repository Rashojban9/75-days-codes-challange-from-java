import java.util.*;

// Patient class to store patient details
class Patient {
    int id;
    String name;
    int age;
    String disease;

    Patient(int id, String name, int age, String disease) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.disease = disease;
    }

    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Age: " + age + ", Disease: " + disease;
    }
}

// Appointment class to store appointment details
class Appointment {
    int patientId;
    String doctorName;
    String date;

    Appointment(int patientId, String doctorName, String date) {
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.date = date;
    }

    public String toString() {
        return "Patient ID: " + patientId + ", Doctor: " + doctorName + ", Date: " + date;
    }
}

// Main Hospital Management System class
public class HospitalManagementSystem {

    // Lists to store patient and appointment records
    static List<Patient> patients = new ArrayList<>();
    static List<Appointment> appointments = new ArrayList<>();

    // Add a new patient
    static void addPatient() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Patient ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Disease: ");
        String disease = sc.nextLine();
        patients.add(new Patient(id, name, age, disease));
        System.out.println("Patient added successfully!");
    }

    // View all patients
    static void viewPatients() {
        System.out.println("\nList of Patients:");
        for (Patient p : patients) {
            System.out.println(p);
        }
    }

    // Schedule an appointment
    static void addAppointment() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Patient ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Doctor Name: ");
        String doctorName = sc.nextLine();
        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        appointments.add(new Appointment(id, doctorName, date));
        System.out.println("Appointment scheduled successfully!");
    }

    // View all appointments
    static void viewAppointments() {
        System.out.println("\nList of Appointments:");
        for (Appointment a : appointments) {
            System.out.println(a);
        }
    }

    // Main menu
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nHospital Management System");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Add Appointment");
            System.out.println("4. View Appointments");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addPatient();
                    break;
                case 2:
                    viewPatients();
                    break;
                case 3:
                    addAppointment();
                    break;
                case 4:
                    viewAppointments();
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
