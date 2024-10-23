package com.sda;

import java.time.LocalDateTime;
import java.time.Duration;

public class ParkingSection {
    private int id;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String licensePlate;
    private int slotId;

    public ParkingSection(int id, String licensePlate, int slotId) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.slotId = slotId;
        this.entryTime = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public int getSlotId() {
        return slotId;
    }

    public Duration calculateDuration() {
        if (exitTime == null) {
            return Duration.between(entryTime, LocalDateTime.now());
        }
        return Duration.between(entryTime, exitTime);
    }
}
