package ParkingLot.strategy;

import ParkingLot.enums.VehicleType;

public interface FeeStrategey {
    double calculate(VehicleType type,long hour);
}
