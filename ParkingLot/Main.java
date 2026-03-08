package ParkingLot;

import ParkingLot.entity.Bike;
import ParkingLot.entity.Car;
import ParkingLot.entity.ParkingFloor;
import ParkingLot.entity.ParkingLot;
import ParkingLot.entity.PaymentGate;
import ParkingLot.entity.Ticket;
import ParkingLot.entity.Truck;
import ParkingLot.entity.Vehicle;
import ParkingLot.strategy.CardPayment;
import ParkingLot.strategy.CashPayment;
import ParkingLot.strategy.UpiPayment;

public class Main {
    
    public static void main(String[] args) {
        ParkingLot lot = ParkingLot.getInstance();
        lot.addFloor(new ParkingFloor(1, 5, 10, 2));
        lot.addFloor(new ParkingFloor(2, 5, 10, 2));
        lot.addPaymentGate(new PaymentGate("GATE-A"));
        // lot.addPaymentGate(new PaymentGate("GATE-B"));

        // Vehicles arrive
        Vehicle car1  = new Car("KA-01-HH-1234");
        Vehicle bike1 = new Bike("MH-02-AB-5678");
        Vehicle truck1= new Truck("DL-03-XY-9999");

        lot.displayAvailability();

        Ticket t1 = lot.entry(car1);
        Ticket t2 = lot.entry(bike1);
        Ticket t3 = lot.entry(truck1);

        lot.displayAvailability();

        // Exit with different payment modes
        lot.exit("KA-01-HH-1234", new CardPayment(), "GATE-A");
        lot.exit("MH-02-AB-5678", new UpiPayment(),  "GATE-B");
        lot.exit("DL-03-XY-9999", new CashPayment(), "GATE-A");

        lot.displayAvailability();
    }
}
