package ParkingLot.entity;

import ParkingLot.enums.VehicleType;

public abstract class Vehicle {
    private String noPlate;
    private VehicleType size;

    public Vehicle(String noPlate,VehicleType vehicleType){
        this.noPlate=noPlate;
        this.size=vehicleType;
    }
    public String getNoPlate() {
        return noPlate;
    }
    public VehicleType getSize() {
        return size;
    }
}