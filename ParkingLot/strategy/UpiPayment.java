package ParkingLot.strategy;

public class UpiPayment implements PayementStrategy{

    @Override
    public boolean pay(double amount, String ticketId) {
        System.out.println("[UPI] Collecting " + amount + " for ticket: " + ticketId);
        return true;
    }

    @Override
    public String getPayementMode() {
        return "UPI PAYMENT";
    }
    
}
