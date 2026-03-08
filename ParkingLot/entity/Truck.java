package ParkingLot.entity;

import ParkingLot.enums.VehicleType;

public class Truck extends Vehicle {
    public Truck(String noPlate){
        super(noPlate, VehicleType.TRUCK);
    }
}
