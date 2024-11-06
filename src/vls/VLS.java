package vls;

import java.util.Scanner;

public class VLS {
    public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    VehicleLogSystem vls = new VehicleLogSystem();

    boolean exit = false;

    while (!exit) {
        System.out.println("\n--- Vehicle Log System Menu ---");
        System.out.println("1. Add Vehicle");
        System.out.println("2. Add Vehicle Log");
        System.out.println("3. Display Vehicle Logs");
        System.out.println("4. Display Available Vehicles");
        System.out.println("5. Update Vehicle Log");
        System.out.println("6. Delete Vehicle Log");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                // Add a vehicle (no changes needed)
                System.out.print("Enter plate number: ");
                String plateNo = scanner.nextLine();
                System.out.print("Enter vehicle brand: ");
                String brand = scanner.nextLine();
                System.out.print("Enter vehicle model: ");
                String model = scanner.nextLine();

                // Driver details
                System.out.print("Enter driver ID: ");
                String driverId = scanner.nextLine();
                System.out.print("Enter driver name: ");
                String driverName = scanner.nextLine();
                System.out.print("Enter driver contact: ");
                String driverContact = scanner.nextLine();

                Vehicle vehicle = new Vehicle(plateNo, brand, model, driverId, driverName, driverContact);
                vls.addVehicle(vehicle);
                break;

            case 2:
                vls.displayAvailableVehicles();
                System.out.print("Enter plate number: ");
                plateNo = scanner.nextLine();

                // Check if the plate number is valid
                Vehicle logVehicle = vls.getVehicleByPlateNo(plateNo);
                if (logVehicle == null) {
                    System.out.println("Invalid plate number. Please try again.");
                    break;  // Exit the current iteration and return to the menu
                }

                // If the vehicle is found, proceed to add the log
                System.out.print("Enter oil used: ");
                String oilUsed = scanner.nextLine();
                System.out.print("Enter date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                System.out.print("Enter purpose of use: ");
                String purpose = scanner.nextLine();

                // Create the log and add it
                VehicleLog log = new VehicleLog(logVehicle, oilUsed, date, purpose);
                vls.addLog(log);
                break;

            case 3:
                vls.displayLogs();
                break;

            case 4:
                vls.displayAvailableVehicles();
                break;

            case 5:
                System.out.print("Enter plate number: ");
                plateNo = scanner.nextLine();
                System.out.print("Enter new date (YYYY-MM-DD): ");
                String newDate = scanner.nextLine();
                System.out.print("Enter new purpose of use: ");
                String newPurpose = scanner.nextLine();

                vls.updateVehicleLog(plateNo, newDate, newPurpose);
                break;

            case 6:
                vls.displayLogs();
                System.out.print("Enter the plate number of the log to delete: ");
                String plateToDelete = scanner.nextLine();
                vls.deleteLog(plateToDelete);
                break;

            case 0:
                exit = true;
                System.out.println("Exiting the system.");
                break;

            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    scanner.close();
}
}