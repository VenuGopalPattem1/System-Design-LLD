package ParkingLot.entity;

import ParkingLot.enums.VehicleType;

public class Bike extends Vehicle{
    public Bike(String noPlate){
        super(noPlate, VehicleType.MOTORCYCLE);
    }
}
