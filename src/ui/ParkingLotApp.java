package ui;

import service.ParkingService;
import java.util.Scanner;

public class ParkingLotApp {
    private static final ParkingService parkingService = new ParkingService();
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        clearScreen();
        showWelcomeBanner();
        
        while (true) {
            showMenu();
            int choice = getIntInput("\n>> Enter your choice: ");
            System.out.println();
            
            switch (choice) {
                case 1: parkVehicle(); break;
                case 2: exitVehicle(); break;
                case 3: showStatus(); break;
                case 4: searchVehicle(); break;
                case 5: viewAllParkedVehicles(); break;
                case 6: viewParkingHistory(); break;
                case 7: showPricingInfo(); break;
                case 0:
                    showExitMessage();
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please enter 0-7.\n");
            }
            
            pressEnterToContinue();
        }
    }
    
    private static void showWelcomeBanner() {
        System.out.println("===================================================");
        System.out.println("     PARKING LOT MANAGEMENT SYSTEM");
        System.out.println("        Welcome to Smart Parking!");
        System.out.println("===================================================\n");
    }
    
    private static void showMenu() {
        System.out.println("===================================================");
        System.out.println("                  MAIN MENU");
        System.out.println("===================================================");
        System.out.println("  1. Park Vehicle (CAR/BIKE)");
        System.out.println("  2. Exit Vehicle & Generate Bill");
        System.out.println("  3. Show Parking Status");
        System.out.println("  4. Search Vehicle by Number");
        System.out.println("  5. View All Parked Vehicles");
        System.out.println("  6. View Parking History (Last 10)");
        System.out.println("  7. Show Pricing Information");
        System.out.println("  0. Exit System");
        System.out.println("===================================================");
    }
    
    private static void parkVehicle() {
        System.out.println("\n--- PARK VEHICLE ---");
        System.out.print("Enter vehicle type (CAR/BIKE): ");
        String vehicleType = scanner.nextLine().trim().toUpperCase();
        
        if (!vehicleType.equals("CAR") && !vehicleType.equals("BIKE")) {
            System.out.println("Invalid vehicle type! Please enter CAR or BIKE.\n");
            return;
        }
        
        System.out.print("Enter vehicle number (e.g., UP14AB1234): ");
        String vehicleNo = scanner.nextLine().trim().toUpperCase();
        
        if (vehicleNo.isEmpty()) {
            System.out.println("Vehicle number cannot be empty!\n");
            return;
        }
        
        parkingService.parkVehicle(vehicleType, vehicleNo);
    }
    
    private static void exitVehicle() {
        System.out.println("\n--- EXIT VEHICLE & BILLING ---");
        System.out.print("Enter vehicle number: ");
        String vehicleNo = scanner.nextLine().trim().toUpperCase();
        
        if (vehicleNo.isEmpty()) {
            System.out.println("Vehicle number cannot be empty!\n");
            return;
        }
        
        parkingService.exitVehicle(vehicleNo);
    }
    
    private static void showStatus() {
        parkingService.showStatus();
    }
    
    private static void searchVehicle() {
        System.out.println("\n--- SEARCH VEHICLE ---");
        System.out.print("Enter vehicle number: ");
        String vehicleNo = scanner.nextLine().trim().toUpperCase();
        
        if (vehicleNo.isEmpty()) {
            System.out.println("Vehicle number cannot be empty!\n");
            return;
        }
        
        parkingService.searchVehicle(vehicleNo);
    }
    
    private static void viewAllParkedVehicles() {
        parkingService.viewAllParkedVehicles();
    }
    
    private static void viewParkingHistory() {
        parkingService.viewParkingHistory();
    }
    
    private static void showPricingInfo() {
        System.out.println("\n===================================================");
        System.out.println("            PRICING INFORMATION");
        System.out.println("===================================================");
        System.out.println("  CAR PARKING:");
        System.out.println("    - First 2 hours:  Rs.30");
        System.out.println("    - After 2 hours:  Rs.10 per hour");
        System.out.println();
        System.out.println("  BIKE PARKING:");
        System.out.println("    - First 2 hours:  Rs.20");
        System.out.println("    - After 2 hours:  Rs.5 per hour");
        System.out.println();
        System.out.println("  Minimum billing: 1 hour");
        System.out.println("  Duration rounded up to nearest hour");
        System.out.println("===================================================\n");
    }
    
    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private static void pressEnterToContinue() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
        clearScreen();
    }
    
    private static void clearScreen() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
    
    private static void showExitMessage() {
        System.out.println("\n===================================================");
        System.out.println("     Thank you for using our system!");
        System.out.println("              Drive Safe!");
        System.out.println("===================================================\n");
    }
}
