package vls;

import java.sql.*;

public class VehicleLogSystem {
    private Connection connection;

    public VehicleLogSystem() {
        connection = connectDB();
        if (connection != null) {
            createTable();
        }
    }

    // Connect to SQLite database
    private Connection connectDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:vls.db");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e.getMessage());
            return null;
        }
    }

    // Create tables for vehicles and vehicle logs
    private void createTable() {
        String dropVehiclesTable = "DROP TABLE IF EXISTS vehicles";
        String dropLogsTable = "DROP TABLE IF EXISTS vehicle_logs";

        String createVehiclesTable = "CREATE TABLE IF NOT EXISTS vehicles (" +
                "plate_no TEXT PRIMARY KEY, brand TEXT, model TEXT, driver_id TEXT, driver_name TEXT, driver_contact TEXT)";
        String createLogsTable = "CREATE TABLE IF NOT EXISTS vehicle_logs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, plate_no TEXT, oil_used TEXT, date TEXT, purpose_of_use TEXT, status TEXT, " +
                "FOREIGN KEY(plate_no) REFERENCES vehicles(plate_no))";

        try (Statement stmt = connection.createStatement()) {
            // Drop old tables if they exist
            stmt.executeUpdate(dropVehiclesTable);
            stmt.executeUpdate(dropLogsTable);

            // Create tables with the correct schema
            stmt.executeUpdate(createVehiclesTable);
            stmt.executeUpdate(createLogsTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    // Fetch vehicle by plate number
    public Vehicle getVehicleByPlateNo(String plateNo) {
        String sql = "SELECT * FROM vehicles WHERE plate_no = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, plateNo);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String brand = rs.getString("brand");
                    String model = rs.getString("model");
                    String driverId = rs.getString("driver_id");
                    String driverName = rs.getString("driver_name");
                    String driverContact = rs.getString("driver_contact");
                    
                    return new Vehicle(plateNo, brand, model, driverId, driverName, driverContact);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching vehicle: " + e.getMessage());
        }
        return null;  // Vehicle not found
    }

    // Add vehicle and driver to the database
    public void addVehicle(Vehicle vehicle) {
        String sql = "INSERT OR IGNORE INTO vehicles (plate_no, brand, model, driver_id, driver_name, driver_contact) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehicle.plateNo);
            statement.setString(2, vehicle.brand);
            statement.setString(3, vehicle.model);
            statement.setString(4, vehicle.driverId);
            statement.setString(5, vehicle.driverName);
            statement.setString(6, vehicle.driverContact);
            statement.executeUpdate();
            System.out.println("Vehicle and driver added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding vehicle: " + e.getMessage());
        }
    }

    // Add a log entry for a vehicle
    public void addLog(VehicleLog log) {
        String checkStatusSQL = "SELECT status FROM vehicle_logs WHERE plate_no = ? ORDER BY id DESC LIMIT 1";
        String addLogSQL = "INSERT INTO vehicle_logs (plate_no, oil_used, date, purpose_of_use, status) VALUES (?, ?, ?, ?, 'in use')";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkStatusSQL)) {
            checkStmt.setString(1, log.vehicle.plateNo);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && "in use".equalsIgnoreCase(rs.getString("status"))) {
                System.out.println("Vehicle is currently in use. Cannot add a new log.");
                return;
            }
            try (PreparedStatement statement = connection.prepareStatement(addLogSQL)) {
                statement.setString(1, log.vehicle.plateNo);
                statement.setString(2, log.oilUsed);
                statement.setString(3, log.date);
                statement.setString(4, log.purposeOfUse);
                statement.executeUpdate();
                System.out.println("Log added successfully. Vehicle marked as 'in use'.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding log: " + e.getMessage());
        }
    }

    // Display vehicle logs with driver information in table format
    public void displayLogs() {
        String sql = "SELECT * FROM vehicle_logs";
        System.out.println("\nVehicle Logs:\n");
        System.out.printf("%-15s %-15s %-12s %-20s %-10s %-50s%n", 
                          "Plate No", "Oil Used", "Date", "Purpose of Use", "Status", "Driver Info");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------");

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                String plateNo = rs.getString("plate_no");
                Vehicle vehicle = getVehicleByPlateNo(plateNo);  // Fetch the vehicle details including driver info
                String oilUsed = rs.getString("oil_used");
                String date = rs.getString("date");
                String purposeOfUse = rs.getString("purpose_of_use");
                String status = rs.getString("status");

                // Create VehicleLog object to display all info including driver info
                VehicleLog log = new VehicleLog(vehicle, oilUsed, date, purposeOfUse);
                log.status = status; // Set the status of the log
                System.out.printf("%-15s %-15s %-12s %-20s %-10s %-50s%n",
                        log.vehicle.plateNo, log.oilUsed, log.date, log.purposeOfUse, log.status, log.vehicle.getDriverInfo());
            }
        } catch (SQLException e) {
            System.out.println("Error displaying logs: " + e.getMessage());
        }
    }

    // Display available vehicles (not in use) in table format
    public void displayAvailableVehicles() {
        String sql = "SELECT * FROM vehicles WHERE plate_no NOT IN (SELECT plate_no FROM vehicle_logs WHERE status = 'in use')";
        System.out.println("\nAvailable Vehicles:\n");
        System.out.printf("%-15s %-15s %-12s %-25s %-15s %-50s%n", 
                          "Plate No", "Brand", "Model", "Driver Name", "Driver Contact", "Driver Info");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------");

        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                String plateNo = rs.getString("plate_no");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String driverName = rs.getString("driver_name");
                String driverContact = rs.getString("driver_contact");

                System.out.printf("%-15s %-15s %-12s %-25s %-15s %-50s%n",
                        plateNo, brand, model, driverName, driverContact, 
                        "Driver: " + driverName + " (Contact: " + driverContact + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error displaying available vehicles: " + e.getMessage());
        }
    }

    // Update vehicle log (change purpose or date)
    public void updateVehicleLog(String plateNo, String newDate, String newPurpose) {
        String sql = "UPDATE vehicle_logs SET date = ?, purpose_of_use = ? WHERE plate_no = ? AND status = 'in use'";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newDate);
            statement.setString(2, newPurpose);
            statement.setString(3, plateNo);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Log updated successfully.");
            } else {
                System.out.println("No active log found for this vehicle.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating log: " + e.getMessage());
        }
    }

    // Delete a vehicle log entry
    public void deleteLog(String plateNo) {
        String sql = "DELETE FROM vehicle_logs WHERE plate_no = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, plateNo);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Log deleted successfully.");
            } else {
                System.out.println("No log found for this plate number.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting log: " + e.getMessage());
        }
    }
}
