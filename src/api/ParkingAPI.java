package api;

import com.sun.net.httpserver.*;
import service.ParkingService;
import dao.TicketDAO;
import dao.ParkingSlotDAO;
import model.Ticket;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.*;

public class ParkingAPI {
    private static final TicketDAO ticketDAO = new TicketDAO();
    private static final ParkingSlotDAO slotDAO = new ParkingSlotDAO();
    
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        server.createContext("/api/park", new ParkHandler());
        server.createContext("/api/exit", new ExitHandler());
        server.createContext("/api/status", new StatusHandler());
        server.createContext("/api/search", new SearchHandler());
        server.createContext("/api/history", new HistoryHandler());
        server.createContext("/api/parked", new ParkedHandler());
        server.createContext("/", new StaticHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("Server started: http://localhost:8080");
        System.out.println("Open browser and go to: http://localhost:8080");
    }
    
    static class ParkHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            if ("POST".equals(ex.getRequestMethod())) {
                String body = new String(ex.getRequestBody().readAllBytes());
                String[] parts = body.split("&");
                String type = parts[0].split("=")[1];
                String number = parts[1].split("=")[1];
                
                try {
                    var slot = slotDAO.findFreeSlotByType(type);
                    if (slot == null) {
                        sendJSON(ex, 400, "{\"error\":\"No space available\"}");
                        return;
                    }
                    
                    var ticket = new Ticket(slot.getId(), number, type, 
                        java.time.LocalDateTime.now(), "ACTIVE");
                    int ticketId = ticketDAO.createTicket(ticket);
                    slotDAO.markSlotOccupied(slot.getId());
                    
                    sendJSON(ex, 200, String.format(
                        "{\"success\":true,\"ticketId\":%d,\"slot\":\"%s-%d\"}",
                        ticketId, type, slot.getSlotNumber()));
                } catch (Exception e) {
                    sendJSON(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        }
    }
    
    static class ExitHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            if ("POST".equals(ex.getRequestMethod())) {
                String body = new String(ex.getRequestBody().readAllBytes());
                String vehicleNo = body.split("=")[1];
                
                try {
                    var ticket = ticketDAO.findActiveTicketByVehicleNo(vehicleNo);
                    if (ticket == null) {
                        sendJSON(ex, 404, "{\"error\":\"Vehicle not found\"}");
                        return;
                    }
                    
                    var exitTime = java.time.LocalDateTime.now();
                    ticket.setExitTime(exitTime);
                    
                    long minutes = java.time.Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
                    long hours = (long) Math.ceil(minutes / 60.0);
                    if (hours == 0) hours = 1;
                    
                    double amount = calculateAmount(ticket.getVehicleType(), hours);
                    ticket.setAmount(amount);
                    ticket.setStatus("PAID");
                    
                    ticketDAO.updateTicketOnExit(ticket);
                    slotDAO.markSlotFree(ticket.getSlotId());
                    
                    sendJSON(ex, 200, String.format(
                        "{\"success\":true,\"amount\":%.2f,\"hours\":%d}", amount, hours));
                } catch (Exception e) {
                    sendJSON(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        }
    }
    
    static class StatusHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            try {
                var total = slotDAO.countTotalSlotsByType();
                var available = slotDAO.countAvailableSlotsByType();
                
                String json = String.format(
                    "{\"car\":{\"total\":%d,\"available\":%d},\"bike\":{\"total\":%d,\"available\":%d}}",
                    total.getOrDefault("CAR", 0), available.getOrDefault("CAR", 0),
                    total.getOrDefault("BIKE", 0), available.getOrDefault("BIKE", 0));
                
                sendJSON(ex, 200, json);
            } catch (Exception e) {
                sendJSON(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    static class SearchHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            String query = ex.getRequestURI().getQuery();
            String vehicleNo = query.split("=")[1];
            
            try {
                var ticket = ticketDAO.findActiveTicketByVehicleNo(vehicleNo);
                if (ticket == null) {
                    sendJSON(ex, 404, "{\"found\":false}");
                    return;
                }
                
                long minutes = java.time.Duration.between(ticket.getEntryTime(), 
                    java.time.LocalDateTime.now()).toMinutes();
                
                String json = String.format(
                    "{\"found\":true,\"type\":\"%s\",\"ticketId\":%d,\"minutes\":%d}",
                    ticket.getVehicleType(), ticket.getId(), minutes);
                
                sendJSON(ex, 200, json);
            } catch (Exception e) {
                sendJSON(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    static class HistoryHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            try {
                var history = ticketDAO.getRecentHistory(10);
                StringBuilder json = new StringBuilder("[");
                
                for (int i = 0; i < history.size(); i++) {
                    var t = history.get(i);
                    json.append(String.format(
                        "{\"vehicleNo\":\"%s\",\"type\":\"%s\",\"amount\":%.2f}",
                        t.getVehicleNo(), t.getVehicleType(), t.getAmount()));
                    if (i < history.size() - 1) json.append(",");
                }
                json.append("]");
                
                sendJSON(ex, 200, json.toString());
            } catch (Exception e) {
                sendJSON(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    static class ParkedHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            setCORS(ex);
            try {
                var occupied = slotDAO.getOccupiedSlots();
                StringBuilder json = new StringBuilder("[");
                
                for (int i = 0; i < occupied.size(); i++) {
                    var slot = occupied.get(i);
                    var ticket = ticketDAO.findActiveTicketByVehicleNo(slot.get("vehicle_no"));
                    
                    long minutes = 0;
                    if (ticket != null) {
                        minutes = java.time.Duration.between(ticket.getEntryTime(), 
                            java.time.LocalDateTime.now()).toMinutes();
                    }
                    long hours = minutes / 60;
                    long mins = minutes % 60;
                    
                    json.append(String.format(
                        "{\"vehicleNo\":\"%s\",\"type\":\"%s\",\"slot\":\"%s-%s\",\"duration\":\"%dh %dm\"}",
                        slot.get("vehicle_no"), slot.get("vehicle_type"), 
                        slot.get("vehicle_type"), slot.get("slot_number"), hours, mins));
                    if (i < occupied.size() - 1) json.append(",");
                }
                json.append("]");
                
                sendJSON(ex, 200, json.toString());
            } catch (Exception e) {
                sendJSON(ex, 500, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        }
    }
    
    static class StaticHandler implements HttpHandler {
        public void handle(HttpExchange ex) throws IOException {
            String path = ex.getRequestURI().getPath();
            if (path.equals("/")) path = "/index.html";
            
            File file = new File("web" + path);
            if (file.exists()) {
                String contentType = "text/html";
                if (path.endsWith(".css")) contentType = "text/css";
                if (path.endsWith(".js")) contentType = "application/javascript";
                
                ex.getResponseHeaders().set("Content-Type", contentType);
                ex.sendResponseHeaders(200, file.length());
                
                try (OutputStream os = ex.getResponseBody();
                     FileInputStream fs = new FileInputStream(file)) {
                    fs.transferTo(os);
                }
            } else {
                String response = "404 Not Found";
                ex.sendResponseHeaders(404, response.length());
                ex.getResponseBody().write(response.getBytes());
                ex.getResponseBody().close();
            }
        }
    }
    
    static void setCORS(HttpExchange ex) {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,POST");
    }
    
    static void sendJSON(HttpExchange ex, int code, String json) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(code, json.length());
        ex.getResponseBody().write(json.getBytes());
        ex.getResponseBody().close();
    }
    
    static double calculateAmount(String type, long hours) {
        if ("CAR".equals(type)) {
            return hours <= 2 ? 30 : 30 + (hours - 2) * 10;
        } else {
            return hours <= 2 ? 20 : 20 + (hours - 2) * 5;
        }
    }
}
