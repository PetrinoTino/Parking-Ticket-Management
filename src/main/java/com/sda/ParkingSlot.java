package com.sda;
public class ParkingSlot {
    private int id;
    private boolean isAvailable;
    private String position;
    private static final int TOTAL_SLOTS = 20;

    public ParkingSlot(int id, String position) {
        this.id = id;
        this.position = position;
        this.isAvailable = true;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailability(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getPosition() {
        return position;
    }

    public static int getTotalSlots() {
        return TOTAL_SLOTS;
    }
}