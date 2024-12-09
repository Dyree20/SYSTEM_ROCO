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

    // Add a log entry for a vehicle with validation
    public void addLog(VehicleLog log) {
        String checkStatusSQL = "SELECT status FROM vehicle_logs WHERE plate_no = ? ORDER BY id DESC LIMIT 1";
        String addLogSQL = "INSERT INTO vehicle_logs (plate_no, oil_used, date, purpose_of_use, status) VALUES (?, ?, ?, ?, 'in use')";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkStatusSQL)) {
            checkStmt.setString(1, log.vehicle.plateNo);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                String lastStatus = rs.getString("status");
                if (lastStatus.equals("in use")) {
                    System.out.println("This vehicle is already in use. Cannot use it again until marked as done.");
                    return;
                }
            }

            // Proceed to add log
            try (PreparedStatement stmt = connection.prepareStatement(addLogSQL)) {
                stmt.setString(1, log.vehicle.plateNo);
                stmt.setString(2, log.oilUsed);
                stmt.setString(3, log.date);
                stmt.setString(4, log.purposeOfUse);
                stmt.executeUpdate();
                System.out.println("Vehicle log added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Error adding vehicle log: " + e.getMessage());
        }
    }

    // Display all logs in a table format
    public void displayLogs() {
        String sql = "SELECT * FROM vehicle_logs";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Vehicle Logs ---");
            System.out.println("+------------+----------+------------+------------+--------------------+-------------+");
            System.out.println("| Plate No  | Oil Used | Date       | Purpose    | Status             | Driver Info |");
            System.out.println("+------------+----------+------------+------------+--------------------+-------------+");

            while (rs.next()) {
                String plateNo = rs.getString("plate_no");
                String oilUsed = rs.getString("oil_used");
                String date = rs.getString("date");
                String purposeOfUse = rs.getString("purpose_of_use");
                String status = rs.getString("status");

                Vehicle vehicle = getVehicleByPlateNo(plateNo);
                String driverInfo = vehicle != null ? vehicle.getDriverInfo() : "N/A";

                System.out.printf("| %-10s | %-8s | %-10s | %-10s | %-18s | %-11s |\n",
                        plateNo, oilUsed, date, purposeOfUse, status, driverInfo);
            }
            System.out.println("+------------+----------+------------+------------+--------------------+-------------+");

        } catch (SQLException e) {
            System.out.println("Error displaying logs: " + e.getMessage());
        }
    }

    // Display available vehicles in a table format (without vehicles in use)
    public void displayAvailableVehicles() {
        String sql = "SELECT * FROM vehicles WHERE plate_no NOT IN (SELECT plate_no FROM vehicle_logs WHERE status = 'in use')";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\n--- Available Vehicles ---");
            System.out.println("+------------+---------+-------+------------+--------------+-----------------+");
            System.out.println("| Plate No  | Brand   | Model | Driver ID  | Driver Name | Driver Contact  |");
            System.out.println("+------------+---------+-------+------------+--------------+-----------------+");

            while (rs.next()) {
                String plateNo = rs.getString("plate_no");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                String driverId = rs.getString("driver_id");
                String driverName = rs.getString("driver_name");
                String driverContact = rs.getString("driver_contact");

                System.out.printf("| %-10s | %-7s | %-5s | %-10s | %-12s | %-15s |\n",
                        plateNo, brand, model, driverId, driverName, driverContact);
            }
            System.out.println("+------------+---------+-------+------------+--------------+-----------------+");

        } catch (SQLException e) {
            System.out.println("Error displaying available vehicles: " + e.getMessage());
        }
    }

    // Mark vehicle as done (status change from "in use" to "done")
    public void markVehicleAsDone(String plateNo) {
        String sql = "UPDATE vehicle_logs SET status = 'done' WHERE plate_no = ? AND status = 'in use'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plateNo);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Vehicle marked as done.");
            } else {
                System.out.println("No active log found for this vehicle or it's already marked as done.");
            }
        } catch (SQLException e) {
            System.out.println("Error marking vehicle as done: " + e.getMessage());
        }
    }

    // Delete vehicle log
    public void deleteLog(String plateNo) {
        String sql = "DELETE FROM vehicle_logs WHERE plate_no = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plateNo);
            stmt.executeUpdate();
            System.out.println("Vehicle log deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting vehicle log: " + e.getMessage());
        }
    }
}
