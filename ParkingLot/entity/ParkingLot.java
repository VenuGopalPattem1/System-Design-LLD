package ParkingLot.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ParkingLot.strategy.PayementStrategy;

public class ParkingLot {
    private static volatile ParkingLot instance;

    public static ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot("City Center Parking Lot");
        }
        return instance;
    }

    // state
    private String name;
    private List<ParkingFloor> floors;
    private List<PaymentGate> paymentGates;
    private Map<String, Ticket> activeTickets; // licensePlate → Ticket

    private ParkingLot(String name) {
        this.name = name;
        this.floors = new ArrayList<>();
        this.paymentGates = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    public void addPaymentGate(PaymentGate gate) {
        paymentGates.add(gate);
    }

    // entry
    public Ticket entry(Vehicle vehicle) {
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findAvailableSpot(vehicle.getSize());
            if (spot != null && spot.park(vehicle)) {
                Ticket ticket = new Ticket(vehicle, spot, floor.getLvlNo());
                activeTickets.put(vehicle.getNoPlate(), ticket);
                System.out.println("[ENTRY] " + vehicle.getNoPlate()
                        + " -> Level " + floor.getLvlNo()
                        + ", Spot " + spot.getId()
                        + " | Ticket: " + ticket.getId());
                return ticket;
            }
        }
        System.out.println("[ENTRY DENIED] No spot available for " + vehicle.getNoPlate());
        return null;
    }

    // exit
    public boolean exit(String licNo, PayementStrategy strategy, String gateId) {
        Ticket ticket = activeTickets.get(licNo);
        if (ticket == null) {
            System.out.println("[ERROR] No active ticket for: " + licNo);
            return false;
        }

        // find payment gate
        PaymentGate gate = paymentGates.stream().filter(s -> s.getGateId().equals(gateId))
                .findFirst()
                .orElse(paymentGates.get(0));

        boolean paid = gate.processPayment(ticket, strategy);
        if (paid) {
            ticket.getSpot().unpark();
            activeTickets.remove(licNo);
            System.out.println("[EXIT] " + licNo + " has left. Spot freed.");
        }
        return paid;

    }

    // --- AVAILABILITY DISPLAY ---
    public void displayAvailability() {
        System.out.println("\n========= LIVE AVAILABILITY =========");
        for (ParkingFloor floor : floors) {
            System.out.println("Level " + floor.getLvlNo() + ":");
            floor.getAvaialablestats()
                    .forEach((size, count) -> System.out.println("  " + size + " -> " + count + " spots free"));
        }
        System.out.println("=====================================\n");
    }
}
