package vls;

public class VehicleLog {
    public Vehicle vehicle;
    public String oilUsed;
    public String date;
    public String purposeOfUse;

    public VehicleLog(Vehicle vehicle, String oilUsed, String date, String purposeOfUse) {
        this.vehicle = vehicle;
        this.oilUsed = oilUsed;
        this.date = date;
        this.purposeOfUse = purposeOfUse;
    }

    public String getDriverInfo() {
        return "Driver: " + vehicle.driverName + " (ID: " + vehicle.driverId + ")";
    }
}
