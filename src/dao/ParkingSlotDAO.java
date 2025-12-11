package dao;

import model.ParkingSlot;
import util.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class ParkingSlotDAO {
    
    public ParkingSlot findFreeSlotByType(String vehicleType) throws SQLException {
        String sql = "SELECT * FROM slots WHERE vehicle_type = ? AND occupied = FALSE LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicleType);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new ParkingSlot(
                    rs.getInt("id"),
                    rs.getInt("slot_number"),
                    rs.getString("vehicle_type"),
                    rs.getBoolean("occupied")
                );
            }
        }
        return null;
    }
    
    public void markSlotOccupied(int slotId) throws SQLException {
        String sql = "UPDATE slots SET occupied = TRUE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, slotId);
            stmt.executeUpdate();
        }
    }
    
    public void markSlotFree(int slotId) throws SQLException {
        String sql = "UPDATE slots SET occupied = FALSE WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, slotId);
            stmt.executeUpdate();
        }
    }
    
    public Map<String, Integer> countTotalSlotsByType() throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT vehicle_type, COUNT(*) as total FROM slots GROUP BY vehicle_type";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                counts.put(rs.getString("vehicle_type"), rs.getInt("total"));
            }
        }
        return counts;
    }
    
    public Map<String, Integer> countAvailableSlotsByType() throws SQLException {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT vehicle_type, COUNT(*) as available FROM slots WHERE occupied = FALSE GROUP BY vehicle_type";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                counts.put(rs.getString("vehicle_type"), rs.getInt("available"));
            }
        }
        return counts;
    }
    
    public List<Map<String, String>> getOccupiedSlots() throws SQLException {
        List<Map<String, String>> occupiedSlots = new ArrayList<>();
        String sql = "SELECT s.slot_number, s.vehicle_type, t.vehicle_no " +
                     "FROM slots s JOIN tickets t ON s.id = t.slot_id " +
                     "WHERE s.occupied = TRUE AND t.status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, String> slot = new HashMap<>();
                slot.put("slot_number", String.valueOf(rs.getInt("slot_number")));
                slot.put("vehicle_type", rs.getString("vehicle_type"));
                slot.put("vehicle_no", rs.getString("vehicle_no"));
                occupiedSlots.add(slot);
            }
        }
        return occupiedSlots;
    }
}
