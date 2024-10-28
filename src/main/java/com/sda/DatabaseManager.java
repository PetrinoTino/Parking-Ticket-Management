package com.sda;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/parking_management";
    private static final String USER = "--//--"; // Your user
    private static final String PASSWORD = "--//-- "; // Your Password

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(URL, USER, PASSWORD);

    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS ParkingSlot (" +
                    "id INT PRIMARY KEY," +
                    "isAvailable BOOLEAN," +
                    "position VARCHAR(50))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Customer (" +
                    "id INT PRIMARY KEY," +
                    "firstName VARCHAR(50)," +
                    "lastName VARCHAR(50)," +
                    "isMember BOOLEAN)");

            stmt.execute("CREATE TABLE IF NOT EXISTS ParkingSection (" +
                    "id INT PRIMARY KEY," +
                    "entryTime DATETIME," +
                    "exitTime DATETIME," +
                    "licensePlate VARCHAR(20)," +
                    "slotId INT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Ticket (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "price DECIMAL(10,2)," +
                    "duration INT," +
                    "customerName VARCHAR(100)," +
                    "isMember BOOLEAN)");

            for (int i = 1; i <= ParkingSlot.getTotalSlots(); i++) {
                ParkingSlot slot = new ParkingSlot(i, "P" + i);
                saveParkingSlot(slot);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveParkingSlot(ParkingSlot slot) {
        String sql = "INSERT INTO ParkingSlot (id, isAvailable, position) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE isAvailable = ?, position = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, slot.getId());
            pstmt.setBoolean(2, slot.isAvailable());
            pstmt.setString(3, slot.getPosition());
            pstmt.setBoolean(4, slot.isAvailable());
            pstmt.setString(5, slot.getPosition());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveCustomer(Customer customer) {
        String sql = "INSERT INTO Customer (id, firstName, lastName, isMember) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customer.getId());
            pstmt.setString(2, customer.getFirstName());
            pstmt.setString(3, customer.getLastName());
            pstmt.setBoolean(4, customer.isMember());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveParkingSection(ParkingSection section) {
        String sql = "INSERT INTO ParkingSection (id, entryTime, exitTime, licensePlate, slotId) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, section.getId());
            pstmt.setTimestamp(2, Timestamp.valueOf(section.getEntryTime()));
            pstmt.setTimestamp(3, section.getExitTime() != null ? Timestamp.valueOf(section.getExitTime()) : null);
            pstmt.setString(4, section.getLicensePlate());
            pstmt.setInt(5, section.getSlotId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveTicket(Ticket ticket) {
        String sql = "INSERT INTO Ticket (price, duration, customerName, isMember) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, ticket.getPrice());
            pstmt.setLong(2, ticket.getDuration().toMinutes());
            pstmt.setString(3, ticket.getCustomerName());
            pstmt.setBoolean(4, ticket.isMember());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<ParkingSlot> getAvailableParkingSlots() {
        String sql = "SELECT * FROM ParkingSlot WHERE isAvailable = TRUE";
        List<ParkingSlot> availableSlots = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ParkingSlot slot = new ParkingSlot(rs.getInt("id"), rs.getString("position"));
                slot.setAvailability(rs.getBoolean("isAvailable"));
                availableSlots.add(slot);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return availableSlots;
    }

    public static List<ParkingSection> getCurrentlyParkedCars() {
        String sql = "SELECT * FROM ParkingSection WHERE exitTime IS NULL";
        List<ParkingSection> parkedCars = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ParkingSection section = new ParkingSection(
                        rs.getInt("id"),
                        rs.getString("licensePlate"),
                        rs.getInt("slotId")
                );
                parkedCars.add(section);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return parkedCars;
    }

    public static double getTotalEarnings() {
        String sql = "SELECT SUM(price) as total FROM Ticket";
        double totalEarnings = 0;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                totalEarnings = rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return totalEarnings;
    }

    public static void updateParkingSlotAvailability(int id, boolean isAvailable) {
        String sql = "UPDATE ParkingSlot SET isAvailable = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, isAvailable);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static ParkingSection getParkingSectionByLicensePlate(String licensePlate) {
        String sql = "SELECT * FROM ParkingSection WHERE licensePlate = ? AND exitTime IS NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new ParkingSection(
                        rs.getInt("id"),
                        rs.getString("licensePlate"),
                        rs.getInt("slotId")
                );
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Lidhja me bazën e të dhënave u krye me sukses!");
        } catch (SQLException e) {
            System.out.println("Gabim gjatë lidhjes me bazën e të dhënave: " + e.getMessage());
        }
    }
}