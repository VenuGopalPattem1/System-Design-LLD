package ParkingLot.strategy;


public interface PayementStrategy {
    boolean pay(double amount,String ticketId);
    String getPayementMode();
}
