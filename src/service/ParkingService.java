package service;

import dao.ParkingSlotDAO;
import dao.TicketDAO;
import model.ParkingSlot;
import model.Ticket;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ParkingService {
    private final ParkingSlotDAO slotDAO = new ParkingSlotDAO();
    private final TicketDAO ticketDAO = new TicketDAO();
    
    private static final int CAR_BASE_HOURS = 2;
    private static final double CAR_BASE_RATE = 30.0;
    private static final double CAR_EXTRA_RATE = 10.0;
    
    private static final int BIKE_BASE_HOURS = 2;
    private static final double BIKE_BASE_RATE = 20.0;
    private static final double BIKE_EXTRA_RATE = 5.0;
    
    public void parkVehicle(String vehicleType, String vehicleNo) {
        try {
            ParkingSlot slot = slotDAO.findFreeSlotByType(vehicleType);
            
            if (slot == null) {
                System.out.println("❌ No space available for " + vehicleType);
                return;
            }
            
            Ticket ticket = new Ticket(
                slot.getId(),
                vehicleNo,
                vehicleType,
                LocalDateTime.now(),
                "ACTIVE"
            );
            
            int ticketId = ticketDAO.createTicket(ticket);
            slotDAO.markSlotOccupied(slot.getId());
            
            System.out.println("\n✅ Vehicle parked successfully!");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("Ticket ID: " + ticketId);
            System.out.println("Slot Number: " + vehicleType + "-" + slot.getSlotNumber());
            System.out.println("Vehicle Number: " + vehicleNo);
            System.out.println("Entry Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
            
        } catch (SQLException e) {
            System.out.println("❌ Error parking vehicle: " + e.getMessage());
        }
    }
    
    public void exitVehicle(String vehicleNo) {
        try {
            Ticket ticket = ticketDAO.findActiveTicketByVehicleNo(vehicleNo);
            
            if (ticket == null) {
                System.out.println("❌ No active ticket found for vehicle number: " + vehicleNo);
                return;
            }
            
            LocalDateTime exitTime = LocalDateTime.now();
            ticket.setExitTime(exitTime);
            
            long durationMinutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
            long durationHours = (long) Math.ceil(durationMinutes / 60.0);
            if (durationHours == 0) durationHours = 1;
            
            double amount = calculateAmount(ticket.getVehicleType(), durationHours);
            ticket.setAmount(amount);
            ticket.setStatus("PAID");
            
            ticketDAO.updateTicketOnExit(ticket);
            slotDAO.markSlotFree(ticket.getSlotId());
            
            printBill(ticket, durationHours);
            
        } catch (SQLException e) {
            System.out.println("❌ Error processing exit: " + e.getMessage());
        }
    }
    
    private double calculateAmount(String vehicleType, long durationHours) {
        double baseRate, extraRate;
        int baseHours;
        
        if ("CAR".equals(vehicleType)) {
            baseHours = CAR_BASE_HOURS;
            baseRate = CAR_BASE_RATE;
            extraRate = CAR_EXTRA_RATE;
        } else {
            baseHours = BIKE_BASE_HOURS;
            baseRate = BIKE_BASE_RATE;
            extraRate = BIKE_EXTRA_RATE;
        }
        
        if (durationHours <= baseHours) {
            return baseRate;
        } else {
            long extraHours = durationHours - baseHours;
            return baseRate + (extraHours * extraRate);
        }
    }
    
    private void printBill(Ticket ticket, long durationHours) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        PARKING LOT BILL                ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Vehicle Number: " + String.format("%-22s", ticket.getVehicleNo()) + "║");
        System.out.println("║ Vehicle Type:   " + String.format("%-22s", ticket.getVehicleType()) + "║");
        System.out.println("║ Entry Time:     " + String.format("%-22s", ticket.getEntryTime().format(formatter)) + "║");
        System.out.println("║ Exit Time:      " + String.format("%-22s", ticket.getExitTime().format(formatter)) + "║");
        System.out.println("║ Duration:       " + String.format("%-22s", durationHours + " hour(s)") + "║");
        System.out.println("║ Amount:         " + String.format("₹%-21.2f", ticket.getAmount()) + "║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    public void showStatus() {
        try {
            Map<String, Integer> totalSlots = slotDAO.countTotalSlotsByType();
            Map<String, Integer> availableSlots = slotDAO.countAvailableSlotsByType();
            
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║      PARKING LOT STATUS                ║");
            System.out.println("╠════════════════════════════════════════╣");
            
            for (String type : totalSlots.keySet()) {
                int total = totalSlots.get(type);
                int available = availableSlots.getOrDefault(type, 0);
                int occupied = total - available;
                
                System.out.println("║ " + type + " Slots:                           ║");
                System.out.println("║   Total:     " + String.format("%-23d", total) + "║");
                System.out.println("║   Available: " + String.format("%-23d", available) + "║");
                System.out.println("║   Occupied:  " + String.format("%-23d", occupied) + "║");
                System.out.println("╠════════════════════════════════════════╣");
            }
            
            List<Map<String, String>> occupiedSlots = slotDAO.getOccupiedSlots();
            if (!occupiedSlots.isEmpty()) {
                System.out.println("║ OCCUPIED SLOTS:                        ║");
                System.out.println("╠════════════════════════════════════════╣");
                for (Map<String, String> slot : occupiedSlots) {
                    String slotInfo = slot.get("vehicle_type") + "-" + slot.get("slot_number") + 
                                     " → " + slot.get("vehicle_no");
                    System.out.println("║ " + String.format("%-38s", slotInfo) + "║");
                }
            }
            
            System.out.println("╚════════════════════════════════════════╝\n");
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching status: " + e.getMessage());
        }
    }
    
    public void searchVehicle(String vehicleNo) {
        try {
            Ticket ticket = ticketDAO.findActiveTicketByVehicleNo(vehicleNo);
            
            if (ticket == null) {
                System.out.println("\n❌ Vehicle " + vehicleNo + " is not currently parked.\n");
                return;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            long durationMinutes = Duration.between(ticket.getEntryTime(), LocalDateTime.now()).toMinutes();
            long hours = durationMinutes / 60;
            long minutes = durationMinutes % 60;
            
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║        VEHICLE FOUND ✅                 ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║ Vehicle Number: " + String.format("%-22s", vehicleNo) + "║");
            System.out.println("║ Vehicle Type:   " + String.format("%-22s", ticket.getVehicleType()) + "║");
            System.out.println("║ Ticket ID:      " + String.format("%-22s", ticket.getId()) + "║");
            System.out.println("║ Entry Time:     " + String.format("%-22s", ticket.getEntryTime().format(formatter)) + "║");
            System.out.println("║ Parked Since:   " + String.format("%-22s", hours + "h " + minutes + "m") + "║");
            System.out.println("║ Status:         " + String.format("%-22s", "ACTIVE") + "║");
            System.out.println("╚════════════════════════════════════════╝\n");
            
        } catch (SQLException e) {
            System.out.println("❌ Error searching vehicle: " + e.getMessage());
        }
    }
    
    public void viewAllParkedVehicles() {
        try {
            List<Map<String, String>> occupiedSlots = slotDAO.getOccupiedSlots();
            
            if (occupiedSlots.isEmpty()) {
                System.out.println("\n✅ No vehicles currently parked. All slots are empty!\n");
                return;
            }
            
            System.out.println("\n╔════════════════════════════════════════════════════════════╗");
            System.out.println("║              ALL PARKED VEHICLES (" + occupiedSlots.size() + " total)                ║");
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            System.out.println("║  Slot      │  Type  │  Vehicle Number                     ║");
            System.out.println("╠════════════════════════════════════════════════════════════╣");
            
            for (Map<String, String> slot : occupiedSlots) {
                String slotNum = String.format("%-9s", slot.get("vehicle_type") + "-" + slot.get("slot_number"));
                String type = String.format("%-6s", slot.get("vehicle_type"));
                String vehicleNo = String.format("%-35s", slot.get("vehicle_no"));
                System.out.println("║  " + slotNum + "│  " + type + "│  " + vehicleNo + "║");
            }
            
            System.out.println("╚════════════════════════════════════════════════════════════╝\n");
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching parked vehicles: " + e.getMessage());
        }
    }
    
    public void viewParkingHistory() {
        try {
            List<Ticket> history = ticketDAO.getRecentHistory(10);
            
            if (history.isEmpty()) {
                System.out.println("\n📋 No parking history available yet.\n");
                return;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM HH:mm");
            
            System.out.println("\n╔═══════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    PARKING HISTORY (Last 10)                      ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
            System.out.println("║ Vehicle No    │ Type │ Entry        │ Exit         │ Amount        ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════════╣");
            
            for (Ticket ticket : history) {
                String vehicleNo = String.format("%-13s", ticket.getVehicleNo());
                String type = String.format("%-4s", ticket.getVehicleType());
                String entry = String.format("%-12s", ticket.getEntryTime().format(formatter));
                String exit = String.format("%-12s", ticket.getExitTime().format(formatter));
                String amount = String.format("₹%-13.2f", ticket.getAmount());
                System.out.println("║ " + vehicleNo + "│ " + type + " │ " + entry + " │ " + exit + " │ " + amount + "║");
            }
            
            System.out.println("╚═══════════════════════════════════════════════════════════════════╝\n");
            
        } catch (SQLException e) {
            System.out.println("❌ Error fetching history: " + e.getMessage());
        }
    }
}
