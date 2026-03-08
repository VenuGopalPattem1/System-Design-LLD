package ParkingLot.entity;

import ParkingLot.enums.SpotStatus;
import ParkingLot.enums.VehicleType;

public class ParkingSpot {
    private int id;
    private Vehicle vehicle;
    private SpotStatus spotStatus;
    private VehicleType allowedType;
    
    public ParkingSpot(int id, VehicleType allowedType) {
        this.id = id;
        this.spotStatus = SpotStatus.AVAILABLE;
        this.allowedType = allowedType;
    }

    public boolean isAvailable(){
        if(spotStatus==SpotStatus.AVAILABLE){
            return true;
        }
        return false;
    }

    public boolean park(Vehicle vehicle){
        if(!isAvailable()||vehicle.getSize()!=allowedType) return false;
        this.vehicle=vehicle;
        this.spotStatus=SpotStatus.OCCUPIED;
        return true;
    }

    public Vehicle unpark(){
        Vehicle v=vehicle;
        vehicle=null;
        spotStatus=SpotStatus.AVAILABLE;
        return v;
    }
    public int getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public SpotStatus getSpotStatus() {
        return spotStatus;
    }

    public VehicleType getAllowedType() {
        return allowedType;
    }


    
}
