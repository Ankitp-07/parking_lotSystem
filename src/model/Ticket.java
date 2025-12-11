package model;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private int slotId;
    private String vehicleNo;
    private String vehicleType;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double amount;
    private String status;
    
    public Ticket() {}
    
    public Ticket(int slotId, String vehicleNo, String vehicleType, LocalDateTime entryTime, String status) {
        this.slotId = slotId;
        this.vehicleNo = vehicleNo;
        this.vehicleType = vehicleType;
        this.entryTime = entryTime;
        this.status = status;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }
    
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }
    
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    
    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }
    
    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
