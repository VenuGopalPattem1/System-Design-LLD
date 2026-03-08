package ParkingLot.entity;

import ParkingLot.enums.VehicleType;

public class Car extends Vehicle{
    public Car(String noPLate){
        super(noPLate,VehicleType.CAR);
    }
}
