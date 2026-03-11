package TrafficSignalSystem.state;

import TrafficSignalSystem.entity.TrafficLight;
import TrafficSignalSystem.enums.SignalColor;

public class RedState implements SignalState{

    @Override
    public void handle(TrafficLight light) {
        System.out.println("[" + light.getDirection() + "] RED -> transitioning to GREEN");
        light.setState(new GreenState());
    }

    @Override
    public SignalColor getColor() {
       return SignalColor.RED;
    }

    @Override
    public int getDuration(TrafficLight light) {
       return light.getDurations(SignalColor.RED);
    }
    
}
