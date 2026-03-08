package ParkingLot.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import ParkingLot.enums.PaymentStatus;

public class Ticket {
    private String id;
    private Vehicle vehicle;
    private ParkingSpot spot;
    private int level;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private PaymentStatus paymentStatus;

     public Ticket(Vehicle vehicle, ParkingSpot spot, int level){
        this.id="TKT-"+UUID.randomUUID().toString().substring(0,9).toUpperCase();
        this.vehicle=vehicle;
        this.spot=spot;
        this.level=level;
        this.entryTime=LocalDateTime.now();
        this.paymentStatus=PaymentStatus.PENDING;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public long getParkingDurationHours(){
        LocalDateTime end=(exitTime!=null)?exitTime:LocalDateTime.now();
        long minutes=ChronoUnit.MINUTES.between(entryTime, end);
        return Math.max(1,(long)Math.ceil(minutes/60.0));//minimum 1 hour
    }

    public String getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public int getLevel() {
        return level;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

   
}
