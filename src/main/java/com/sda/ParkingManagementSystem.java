package com.sda;

import java.time.Duration;
import java.util.Scanner;
import java.util.List;
import java.time.LocalDateTime;

public class ParkingManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final int ADD_CAR = 1;
    private static final int GENERATE_TICKET = 2;
    private static final int SHOW_AVAILABLE_SLOTS = 3;
    private static final int DISPLAY_PARKED_CARS = 4;
    private static final int SHOW_TOTAL_EARNINGS = 5;
    private static final int EXIT = 6;

    public static void main(String[] args) {
        DatabaseManager.testConnection();
        DatabaseManager.initializeDatabase();

        while (true) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case ADD_CAR:
                    addCar();
                    break;
                case GENERATE_TICKET:
                    generateTicket();
                    break;
                case SHOW_AVAILABLE_SLOTS:
                    showAvailableSlots();
                    break;
                case DISPLAY_PARKED_CARS:
                    displayParkedCars();
                    break;
                case SHOW_TOTAL_EARNINGS:
                    showTotalEarnings();
                    break;
                case EXIT:
                    System.out.println("Duke dalë nga sistemi...");
                    return;
                default:
                    System.out.println("Zgjedhje e pavlefshme. Ju lutem provoni përsëri.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n--- Parking Tirana ---");
        System.out.println("1. Shto një makinë në parking");
        System.out.println("2. Gjenero biletë dhe llogarit tarifën");
        System.out.println("3. Shfaq vendet e lira të parkimit");
        System.out.println("4. Shfaq makinat aktualisht të parkuara");
        System.out.println("5. Shfaq fitimet totale");
        System.out.println("6. Dil");
        System.out.print("Zgjidhni një opsion: ");
    }

    private static int getUserChoice() {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                return choice;
            } catch (NumberFormatException e) {
                System.out.print("Ju lutem vendosni një numër të vlefshëm: ");
            }
        }
    }

    private static void addCar() {
        System.out.print("Vendosni targën e makinës: ");
        String licensePlate = scanner.nextLine().trim();

        System.out.print("Vendosni emrin e klientit: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Vendosni mbiemrin e klientit: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("A është klienti anëtar? (po/jo): ");
        boolean isMember = scanner.nextLine().trim().equalsIgnoreCase("po");

        List<ParkingSlot> availableSlots = DatabaseManager.getAvailableParkingSlots();
        if (availableSlots.isEmpty()) {
            System.out.println("Na vjen keq, nuk ka vende parkimi të disponueshme.");
            return;
        }
        ParkingSlot slot = availableSlots.get(0);
        DatabaseManager.updateParkingSlotAvailability(slot.getId(), false);

        Customer customer = new Customer((int) System.currentTimeMillis(), firstName, lastName, isMember);
        DatabaseManager.saveCustomer(customer);

        ParkingSection section = new ParkingSection((int) System.currentTimeMillis(), licensePlate, slot.getId());
        DatabaseManager.saveParkingSection(section);

        System.out.println("Makina u shtua me sukses në parking.");
    }

    private static void generateTicket() {
        System.out.print("Vendosni targën e makinës: ");
        String licensePlate = scanner.nextLine().trim();

        ParkingSection section = DatabaseManager.getParkingSectionByLicensePlate(licensePlate);
        if (section == null) {
            System.out.println("Makina nuk u gjet në parking.");
            return;
        }

        section.setExitTime(LocalDateTime.now());
        int slotId = section.getSlotId();

        Duration duration = section.calculateDuration();

        System.out.print("Vendosni emrin e plotë të klientit: ");
        String customerName = scanner.nextLine().trim();

        System.out.print("A është klienti anëtar? (po/jo): ");
        boolean isMember = scanner.nextLine().trim().equalsIgnoreCase("po");

        double ticketPrice = (((Duration) duration).toHours() * Ticket.RATE_PER_HOUR) +
                ((duration.toMinutes() % 60) * Ticket.RATE_PER_MINUTE);

        if (isMember) {
            ticketPrice *= 0.8; // Zbritja për anëtarët
        }

        Ticket ticket = new Ticket((int) System.currentTimeMillis(), ticketPrice, customerName, isMember, duration);
        DatabaseManager.saveTicket(ticket);

        DatabaseManager.updateParkingSlotAvailability(slotId, true);

        System.out.println("Bileta u gjenerua:");
        System.out.println(ticket);
    }

    private static void showAvailableSlots() {
        List<ParkingSlot> availableSlots = DatabaseManager.getAvailableParkingSlots();
        System.out.println("Vende parkimi të disponueshme: " + availableSlots.size() + " nga " + ParkingSlot.getTotalSlots());
        for (ParkingSlot slot : availableSlots) {
            System.out.println("Vendi " + slot.getId() + " në pozicionin " + slot.getPosition());
        }
    }

    private static void displayParkedCars() {
        List<ParkingSection> parkedCars = DatabaseManager.getCurrentlyParkedCars();
        System.out.println("Makinat aktualisht të parkuara:");
        for (ParkingSection section : parkedCars) {
            System.out.println("Targa: " + section.getLicensePlate() + ", Vendi: " + section.getSlotId());
        }
    }

    private static void showTotalEarnings() {
        double totalEarnings = DatabaseManager.getTotalEarnings();
        System.out.printf("Fitimet totale: $%.2f\n", totalEarnings);
    }
}
