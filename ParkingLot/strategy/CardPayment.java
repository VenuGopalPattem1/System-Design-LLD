package ParkingLot.strategy;

public class CardPayment implements PayementStrategy{

    @Override
    public boolean pay(double amount, String ticketId) {
        System.out.println("[CARD] Collecting " + amount + " for ticket: " + ticketId);
        return true;
    }

    @Override
    public String getPayementMode() {
        return "CARD PAYMENT";
    }
    
}
