import java.sql.*;
import java.util.*;

// Database Manager
class DatabaseManager {
    private static final String URL = "jdbc:sqlite:pet_shelter.db";

    public static Connection connect() {
        try { return DriverManager.getConnection(URL); } 
        catch (SQLException e) { System.out.println("DB Error: " + e.getMessage()); return null; }
    }

    public static void initialize() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS pets (id INTEGER PRIMARY KEY, name TEXT, species TEXT, breed TEXT, age INTEGER, health TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS adopters (id INTEGER PRIMARY KEY, name TEXT, contact TEXT)");
            System.out.println("Database Ready.");
        } catch (SQLException e) { System.out.println("Init Error: " + e.getMessage()); }
    }
}

// Pet Model
abstract class Pet {
    int id, age; String name, breed, healthStatus;
    public Pet(int id, String name, String breed, int age, String healthStatus) {
        this.id = id; this.name = name; this.breed = breed; this.age = age; this.healthStatus = healthStatus;
    }
    public abstract void displayInfo();
}

// Dog & Cat Models
class Dog extends Pet {
    public Dog(int id, String name, String breed, int age, String healthStatus) { super(id, name, breed, age, healthStatus); }
    public void displayInfo() { System.out.println("🐶 Dog: " + name + " | Breed: " + breed + " | Age: " + age + " | Health: " + healthStatus); }
}
class Cat extends Pet {
    public Cat(int id, String name, String breed, int age, String healthStatus) { super(id, name, breed, age, healthStatus); }
    public void displayInfo() { System.out.println("🐱 Cat: " + name + " | Breed: " + breed + " | Age: " + age + " | Health: " + healthStatus); }
}

// Adopter Model
class Adopter {
    int id; String name, contact;
    public Adopter(int id, String name, String contact) { this.id = id; this.name = name; this.contact = contact; }
    public void displayInfo() { System.out.println("👤 Adopter: " + name + " | Contact: " + contact); }
}

// Pet Service
class PetService {
    public static void addPet(String name, String species, String breed, int age, String health) {
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO pets (name, species, breed, age, health) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setString(1, name); pstmt.setString(2, species); pstmt.setString(3, breed); pstmt.setInt(4, age); pstmt.setString(5, health);
            pstmt.executeUpdate(); System.out.println(name + " added to the shelter.");
        } catch (SQLException e) { System.out.println("Add Error: " + e.getMessage()); }
    }

    public static List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<>();
        try (Connection conn = DatabaseManager.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM pets")) {
            while (rs.next()) {
                int id = rs.getInt("id"), age = rs.getInt("age");
                String name = rs.getString("name"), species = rs.getString("species"), breed = rs.getString("breed"), health = rs.getString("health");
                pets.add(species.equalsIgnoreCase("dog") ? new Dog(id, name, breed, age, health) : new Cat(id, name, breed, age, health));
            }
        } catch (SQLException e) { System.out.println("Fetch Error: " + e.getMessage()); }
        return pets;
    }

    public static void searchPetByName(String name) {
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM pets WHERE name LIKE ?")) {
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("Found: " + rs.getString("name") + " | Breed: " + rs.getString("breed"));
            }
        } catch (SQLException e) { System.out.println("Search Error: " + e.getMessage()); }
    }

    public static void updatePetHealth(int petId, String newHealth) {
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement("UPDATE pets SET health = ? WHERE id = ?")) {
            pstmt.setString(1, newHealth); pstmt.setInt(2, petId);
            pstmt.executeUpdate(); System.out.println("Pet health updated.");
        } catch (SQLException e) { System.out.println("Update Error: " + e.getMessage()); }
    }

    public static void deletePet(int petId) {
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM pets WHERE id = ?")) {
            pstmt.setInt(1, petId);
            pstmt.executeUpdate(); System.out.println("Pet removed.");
        } catch (SQLException e) { System.out.println("Delete Error: " + e.getMessage()); }
    }
}

// Adopter Service
class AdopterService {
    public static void registerAdopter(String name, String contact) {
        try (Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement("INSERT INTO adopters (name, contact) VALUES (?, ?)")) {
            pstmt.setString(1, name); pstmt.setString(2, contact);
            pstmt.executeUpdate(); System.out.println(name + " registered as adopter.");
        } catch (SQLException e) { System.out.println("Register Error: " + e.getMessage()); }
    }

    public static void viewAllAdopters() {
        try (Connection conn = DatabaseManager.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM adopters")) {
            while (rs.next()) {
                System.out.println("👤 " + rs.getString("name") + " | Contact: " + rs.getString("contact"));
            }
        } catch (SQLException e) { System.out.println("Fetch Error: " + e.getMessage()); }
    }
}

// Main Application
public class PetShelterManagement {
    public static void main(String[] args) {
        DatabaseManager.initialize();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n📌 Pet Shelter Management\n1. Register Pet\n2. View Pets\n3. Search Pet\n4. Update Pet Health\n5. Delete Pet\n6. Register Adopter\n7. View Adopters\n8. Exit\nEnter choice: ");
            int choice = scanner.nextInt(); scanner.nextLine();
            if (choice == 1) {
                System.out.print("Enter Name: "); String name = scanner.nextLine();
                System.out.print("Enter Species (dog/cat): "); String species = scanner.nextLine();
                System.out.print("Enter Breed: "); String breed = scanner.nextLine();
                System.out.print("Enter Age: "); int age = scanner.nextInt(); scanner.nextLine();
                System.out.print("Enter Health Status: "); String health = scanner.nextLine();
                PetService.addPet(name, species, breed, age, health);
            } else if (choice == 2) { PetService.getAllPets().forEach(Pet::displayInfo); }
            else if (choice == 3) { System.out.print("Enter Name: "); PetService.searchPetByName(scanner.nextLine()); }
            else if (choice == 4) { System.out.print("Enter Pet ID: "); int id = scanner.nextInt(); scanner.nextLine(); System.out.print("New Health: "); PetService.updatePetHealth(id, scanner.nextLine()); }
            else if (choice == 5) { System.out.print("Enter Pet ID: "); PetService.deletePet(scanner.nextInt()); scanner.nextLine(); }
            else if (choice == 6) { System.out.print("Adopter Name: "); String name = scanner.nextLine(); System.out.print("Contact: "); AdopterService.registerAdopter(name, scanner.nextLine()); }
            else if (choice == 7) { AdopterService.viewAllAdopters(); }
            else { break; }
        }
        scanner.close();
    }
}
