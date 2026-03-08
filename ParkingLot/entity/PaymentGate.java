package ParkingLot.entity;

import java.time.LocalDateTime;

import ParkingLot.enums.PaymentStatus;
import ParkingLot.strategy.FeeStrategey;
import ParkingLot.strategy.HourlyFee;
import ParkingLot.strategy.PayementStrategy;

public class PaymentGate {
    private String gateId;
    private FeeStrategey feeStrategy;

     public String getGateId() {
        return gateId;
    }


    public PaymentGate(String gateId) {
        this.gateId = gateId;
        this.feeStrategy = new HourlyFee();
    }

    public boolean processPayment(Ticket ticket, PayementStrategy paymentStrategy) {
        ticket.setExitTime(LocalDateTime.now());

        long hours = ticket.getParkingDurationHours();
        double fee = feeStrategy.calculate(ticket.getVehicle().getSize(), hours);

        System.out.println("\n--- PAYMENT GATE [" + gateId + "] ---");
        System.out.println("Ticket ID   : " + ticket.getId());
        System.out.println("Vehicle     : " + ticket.getVehicle().getNoPlate());
        System.out.println("Duration    : " + hours + " hour(s)");
        System.out.println("Amount Due  : " + fee);
        System.out.println("Mode        : " + paymentStrategy.getPayementMode());

        boolean success = paymentStrategy.pay(fee, ticket.getId());
        if(success){
            ticket.setPaymentStatus(PaymentStatus.SUCCESS);
        }else{
            ticket.setPaymentStatus(PaymentStatus.FAILED);
        }
        // ticket.setPaymentStatus((success) ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);

        System.out.println("Status      : " + ticket.getPaymentStatus());
        System.out.println("-----------------------------------\n");

        return success;
    }
}
