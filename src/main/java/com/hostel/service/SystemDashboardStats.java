package com.hostel.service;

public class SystemDashboardStats {
    public int totalStudents;
    public int totalRooms;
    public int occupiedRooms;
    public int availableRooms;
    public int partiallyOccupiedRooms;
    public int fullRooms;
    public int maintenanceRooms;
    public int totalCapacity;
    public int totalOccupancy;
    public double occupancyPercentage;
    public long pendingLeaves;
    public long openComplaints;
    public long criticalComplaints;
    public double totalUnpaidFees;

    public int getTotalStudents() { return totalStudents; }
    public int getTotalRooms() { return totalRooms; }
    public int getOccupiedRooms() { return occupiedRooms; }
    public int getAvailableRooms() { return availableRooms; }
    public int getPartiallyOccupiedRooms() { return partiallyOccupiedRooms; }
    public int getFullRooms() { return fullRooms; }
    public int getMaintenanceRooms() { return maintenanceRooms; }
    public int getTotalCapacity() { return totalCapacity; }
    public int getTotalOccupancy() { return totalOccupancy; }
    public double getOccupancyPercentage() { return occupancyPercentage; }
    public long getPendingLeaves() { return pendingLeaves; }
    public long getOpenComplaints() { return openComplaints; }
    public long getCriticalComplaints() { return criticalComplaints; }
    public double getTotalUnpaidFees() { return totalUnpaidFees; }
}
