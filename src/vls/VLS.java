package vls;

import java.util.Scanner;

public class VLS {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        VehicleLogSystem vls = new VehicleLogSystem();  // Initialize the vehicle log system
        boolean running = true;

        while (running) {
            // Display the main menu
            System.out.println("\n--- Vehicle Log System ---");
            System.out.println("1. Add Vehicle");
            System.out.println("2. Add Vehicle Log");
            System.out.println("3. View Vehicle Logs");
            System.out.println("4. View Available Vehicles");
            System.out.println("5. Mark Vehicle as Done");
            System.out.println("6. Delete Vehicle Log");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    // Add a vehicle
                    System.out.print("Enter plate number: ");
                    String plateNo = scanner.nextLine();
                    System.out.print("Enter brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();
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
                    // Add a vehicle log
                    System.out.print("Enter plate number: ");
                    plateNo = scanner.nextLine();
                    Vehicle logVehicle = vls.getVehicleByPlateNo(plateNo);
                    if (logVehicle == null) {
                        System.out.println("Vehicle not found.");
                        break;
                    }

                    System.out.print("Enter oil used: ");
                    String oilUsed = scanner.nextLine();
                    System.out.print("Enter date (YYYY-MM-DD): ");
                    String date = scanner.nextLine();
                    System.out.print("Enter purpose of use: ");
                    String purpose = scanner.nextLine();

                    VehicleLog log = new VehicleLog(logVehicle, oilUsed, date, purpose);
                    vls.addLog(log);
                    break;

                case 3:
                    // View vehicle logs
                    vls.displayLogs();
                    break;

                case 4:
                    // View available vehicles
                    vls.displayAvailableVehicles();
                    break;

                case 5:
                    // Mark vehicle as done
                    System.out.print("Enter plate number to mark as done: ");
                    plateNo = scanner.nextLine();
                    vls.markVehicleAsDone(plateNo);
                    break;

                case 6:
                    // Delete a vehicle log
                    System.out.print("Enter plate number to delete log: ");
                    plateNo = scanner.nextLine();
                    vls.deleteLog(plateNo);
                    break;

                case 7:
                    // Exit the program
                    running = false;
                    System.out.println("Exiting the Vehicle Log System. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }
}
