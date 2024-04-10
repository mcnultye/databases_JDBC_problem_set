import java.io.Console;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class InventoryPresentationLayer {
    private static InventoryManagerDAL GetDal() {
        Scanner credentialScanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        // String input
        String userName = credentialScanner.nextLine();
        System.out.print("Enter password: ");
        String password = credentialScanner.nextLine();
        return new InventoryManagerDAL("Inventory", userName, password);
    }

    public static void main(String[] args) {
    try {
            // Get database connection details from InventoryPresentationLayer
            InventoryManagerDAL inventoryDAL = InventoryPresentationLayer.GetDal();

            // Instantiate BusinessLogic class
            InventoryBusinessLogic businessLogic = new InventoryBusinessLogic();

            // Scanner for user input
            Scanner scanner = new Scanner(System.in);

            InventoryManagerDAL dal = new InventoryManagerDAL("Inventory", "username", "password");
            // Display menu options
            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Add item to inventory");
                System.out.println("2. Search inventory");
                System.out.println("3. Save search results to file");
                System.out.println("4. Generate Sales Report");
                System.out.print("Enter your choice: ");

                // Read user choice
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                // Perform corresponding action based on choice
                switch (choice) {
                    case 1:
                        performWorkflowOne(inventoryDAL, scanner);
                        break;
                    case 2:
                        performWorkflowTwo(inventoryDAL, businessLogic, scanner);
                        break;
                    case 3:
                        performWorkflowThree(inventoryDAL, businessLogic, scanner);
                        break;
                    case 4:
                        performWorkflowFour(inventoryDAL, businessLogic, scanner);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 4.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to perform Workflow One: Add item to inventory
    private static void performWorkflowOne(InventoryManagerDAL inventoryDAL, Scanner scanner) throws SQLException {
        System.out.println("Enter details to add an item to inventory:");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        System.out.print("Description: ");
        String description = scanner.nextLine();
        System.out.print("Magic: ");
        String magic = scanner.nextLine();
        System.out.print("Danger Level: ");
        int dangerLevel = scanner.nextInt();
        System.out.print("Price Paid: ");
        BigDecimal pricePaid = scanner.nextBigDecimal();
        System.out.print("Price Selling: ");
        BigDecimal priceSell = scanner.nextBigDecimal();
        inventoryDAL.addNewItem(name, quantity, description, magic, dangerLevel, pricePaid, priceSell);
        System.out.println("Item added to inventory successfully.");
    }

    // Method to perform Workflow Two: Search inventory
    private static void performWorkflowTwo(InventoryManagerDAL inventoryDAL, InventoryBusinessLogic businessLogic, Scanner scanner) throws SQLException {
        System.out.println("Enter search filters for inventory:");
        System.out.print("Danger Level (0 if not specified): ");
        int dangerLevelFilter = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        System.out.print("Magic Type (leave empty if not specified): ");
        String magicTypeFilter = scanner.nextLine();
        System.out.print("Maximum Price (0 if not specified): ");
        BigDecimal maxPriceFilter = scanner.nextBigDecimal();
        scanner.nextLine(); // Consume newline character
        ResultSet resultSet = inventoryDAL.searchInventory(dangerLevelFilter, magicTypeFilter, maxPriceFilter);
        // Display search results
        while (resultSet.next()) {
            String itemName = resultSet.getString("name");
            int itemQuantity = resultSet.getInt("quantity");
            String itemDescription = resultSet.getString("description");
            // Retrieve other columns as needed...
            System.out.println("Name: " + itemName + ", Quantity: " + itemQuantity + ", Description: " + itemDescription);
        }
        resultSet.close(); // Close the ResultSet
    }

    // Method to perform Workflow Three: Save search results to a file
    private static void performWorkflowThree(InventoryManagerDAL inventoryDAL, InventoryBusinessLogic businessLogic, Scanner scanner) throws SQLException {
        System.out.println("Enter file path to save search results:");
        String filePath = scanner.next();
        scanner.nextLine(); // Consume newline character
        ResultSet resultSet = inventoryDAL.searchInventory(0, filePath, null); // Search with empty filters to get all items
        businessLogic.saveSearchResultsToFile(resultSet, filePath);
    }

    private static void performWorkflowFour(InventoryManagerDAL inventoryDAL, InventoryBusinessLogic businessLogic, Scanner scanner) throws SQLException {
        System.out.println("Enter file path to save sales report:");
        String filePath = scanner.next();
        scanner.nextLine(); // Consume newline character

        businessLogic.generateSalesReport(inventoryDAL.getSalesReport(), filePath);
        System.out.println("Sales report generated and saved successfully.");
    }
}
