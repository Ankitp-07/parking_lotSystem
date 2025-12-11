package dao;

import model.Ticket;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class TicketDAO {
    
    public int createTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (slot_id, vehicle_no, vehicle_type, entry_time, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ticket.getSlotId());
            stmt.setString(2, ticket.getVehicleNo());
            stmt.setString(3, ticket.getVehicleType());
            stmt.setTimestamp(4, Timestamp.valueOf(ticket.getEntryTime()));
            stmt.setString(5, ticket.getStatus());
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }
    
    public Ticket findActiveTicketByVehicleNo(String vehicleNo) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE vehicle_no = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vehicleNo);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setSlotId(rs.getInt("slot_id"));
                ticket.setVehicleNo(rs.getString("vehicle_no"));
                ticket.setVehicleType(rs.getString("vehicle_type"));
                ticket.setEntryTime(rs.getTimestamp("entry_time").toLocalDateTime());
                if (rs.getTimestamp("exit_time") != null) {
                    ticket.setExitTime(rs.getTimestamp("exit_time").toLocalDateTime());
                }
                ticket.setAmount(rs.getDouble("amount"));
                ticket.setStatus(rs.getString("status"));
                return ticket;
            }
        }
        return null;
    }
    
    public void updateTicketOnExit(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET exit_time = ?, amount = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(ticket.getExitTime()));
            stmt.setDouble(2, ticket.getAmount());
            stmt.setString(3, ticket.getStatus());
            stmt.setInt(4, ticket.getId());
            stmt.executeUpdate();
        }
    }
    
    public List<Ticket> getRecentHistory(int limit) throws SQLException {
        List<Ticket> history = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE status = 'PAID' ORDER BY exit_time DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setId(rs.getInt("id"));
                ticket.setSlotId(rs.getInt("slot_id"));
                ticket.setVehicleNo(rs.getString("vehicle_no"));
                ticket.setVehicleType(rs.getString("vehicle_type"));
                ticket.setEntryTime(rs.getTimestamp("entry_time").toLocalDateTime());
                ticket.setExitTime(rs.getTimestamp("exit_time").toLocalDateTime());
                ticket.setAmount(rs.getDouble("amount"));
                ticket.setStatus(rs.getString("status"));
                history.add(ticket);
            }
        }
        return history;
    }
}
