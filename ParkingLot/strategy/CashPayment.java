package ParkingLot.strategy;


public class CashPayment implements PayementStrategy{

    @Override
    public boolean pay(double amount, String ticketId) {
        System.out.println("[CASH] Collecting " + amount + " for ticket: " + ticketId);
        return true;
    }

    @Override
    public String getPayementMode() {
        return "CASH PAYMENT";
    }
    
}
