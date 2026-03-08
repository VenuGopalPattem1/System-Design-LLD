package ParkingLot.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ParkingLot.enums.VehicleType;

public class ParkingFloor {
    private int lvlNo;
    private List<ParkingSpot> spots;

    public ParkingFloor(int lvlvNo, int bikes, int cars ,int trucks){
        this.lvlNo=lvlvNo;
        this.spots=new ArrayList<>();
        int id=1;
        for(int i=0;i<bikes;i++) spots.add(new ParkingSpot(id++, VehicleType.MOTORCYCLE));
        for(int i=0;i<cars;i++) spots.add(new ParkingSpot(id++, VehicleType.CAR));
        for(int i=0;i<trucks;i++) spots.add(new ParkingSpot(id++, VehicleType.TRUCK));
    }

    public ParkingSpot findAvailableSpot(VehicleType type){
        return spots.stream().filter(a->a.getAllowedType()==type && a.isAvailable())
        .findFirst()
        .orElse(null);
    }

    public Map<VehicleType,Integer> getAvaialablestats(){
        Map<VehicleType,Integer> hm=new HashMap<>();
        for(VehicleType v:VehicleType.values()){
            int cnt=(int)spots.stream().filter(s->s.getAllowedType()==v&&s.isAvailable())
            .count();
            hm.put(v,cnt);
        }
        return hm;
    }

    public int getLvlNo() {
        return lvlNo;
    }
}
