package TrafficSignalSystem;

import TrafficSignalSystem.entity.Intersection;
import TrafficSignalSystem.entity.SignalConfig;
import TrafficSignalSystem.entity.TrafficLight;
import TrafficSignalSystem.enums.Directions;

public class Main {
    public static void main(String[] args) throws Exception{
        Intersection is=new Intersection();
        is.addSignal(new TrafficLight(Directions.NORTH, 
            new SignalConfig().green(30).yellow(5).red(60).build()));
        is.addSignal(new TrafficLight(Directions.SOUTH,
            new SignalConfig().green(30).yellow(5).red(60).build()));
        is.addSignal(new TrafficLight(Directions.EAST,
            new SignalConfig().green(20).yellow(4).red(70).build()));
        is.addSignal(new TrafficLight(Directions.WEST,
            new SignalConfig().green(20).yellow(4).red(70).build()));

        is.start(4);

        // Manual override after 5 seconds
        Thread.sleep(5000);
        is.manualOverride(Directions.EAST);
    }
}
