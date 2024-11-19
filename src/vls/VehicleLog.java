package vls;

public class VehicleLog {
    public Vehicle vehicle;
    public String oilUsed;
    public String date;
    public String purposeOfUse;
    public String status;

    public VehicleLog(Vehicle vehicle, String oilUsed, String date, String purposeOfUse) {
        this.vehicle = vehicle;
        this.oilUsed = oilUsed;
        this.date = date;
        this.purposeOfUse = purposeOfUse;
        this.status = "in use";  // Default status
    }

    // Method to display log with driver info
    public String getLogInfo() {
        return String.format("Plate No: %s, Oil Used: %s, Date: %s, Purpose: %s, Status: %s, Driver Info: %s",
                vehicle.plateNo, oilUsed, date, purposeOfUse, status, vehicle.getDriverInfo());
    }
}
