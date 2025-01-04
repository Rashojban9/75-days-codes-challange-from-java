import java.util.*;

// Product class to store product details
class Product {
    int id;
    String name;
    int quantity;
    double price;

    Product(int id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Quantity: " + quantity + ", Price: $" + price;
    }
}

// Inventory Management System class
public class InventoryManagementSystem {

    // List to store products
    static List<Product> products = new ArrayList<>();

    // Add a new product
    static void addProduct() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        System.out.print("Enter Price: ");
        double price = sc.nextDouble();
        products.add(new Product(id, name, quantity, price));
        System.out.println("Product added successfully!");
    }

    // View all products
    static void viewProducts() {
        System.out.println("\nList of Products:");
        for (Product p : products) {
            System.out.println(p);
        }
    }

    // Update stock
    static void updateStock() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        System.out.print("Enter New Quantity: ");
        int newQuantity = sc.nextInt();
        for (Product p : products) {
            if (p.id == id) {
                p.quantity = newQuantity;
                System.out.println("Stock updated successfully!");
                return;
            }
        }
        System.out.println("Product not found!");
    }

    // Process sale
    static void processSale() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Product ID: ");
        int id = sc.nextInt();
        System.out.print("Enter Quantity Sold: ");
        int quantitySold = sc.nextInt();
        for (Product p : products) {
            if (p.id == id) {
                if (p.quantity >= quantitySold) {
                    p.quantity -= quantitySold;
                    System.out.println("Sale processed successfully! Total: $" + (p.price * quantitySold));
                } else {
                    System.out.println("Insufficient stock!");
                }
                return;
            }
        }
        System.out.println("Product not found!");
    }

    // Main menu
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nInventory Management System");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Stock");
            System.out.println("4. Process Sale");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addProduct();
                    break;
                case 2:
                    viewProducts();
                    break;
                case 3:
                    updateStock();
                    break;
                case 4:
                    processSale();
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
