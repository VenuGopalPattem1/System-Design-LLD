package ParkingLot.strategy;

import java.util.Map;

import ParkingLot.enums.VehicleType;

public class HourlyFee implements FeeStrategey{
    Map<VehicleType,Double> hm=Map.of(
        VehicleType.MOTORCYCLE ,20.0,
        VehicleType.CAR,50.0,
        VehicleType.TRUCK,100.0
    );
    @Override
    public double calculate(VehicleType type, long hour) {
        return hm.get(type)*hour;
    }
    
}
