package model;

public class ParkingSlot {
    private int id;
    private int slotNumber;
    private String vehicleType;
    private boolean occupied;
    
    public ParkingSlot() {}
    
    public ParkingSlot(int id, int slotNumber, String vehicleType, boolean occupied) {
        this.id = id;
        this.slotNumber = slotNumber;
        this.vehicleType = vehicleType;
        this.occupied = occupied;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getSlotNumber() { return slotNumber; }
    public void setSlotNumber(int slotNumber) { this.slotNumber = slotNumber; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
}
