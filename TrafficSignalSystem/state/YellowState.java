package TrafficSignalSystem.state;

import TrafficSignalSystem.entity.TrafficLight;
import TrafficSignalSystem.enums.SignalColor;

public class YellowState implements SignalState{

    @Override
    public void handle(TrafficLight light) {
        System.out.println("[" + light.getDirection() + "] YELLOW -> transitioning to RED");
        light.setState(new RedState());
    }

    @Override
    public SignalColor getColor() {
       return SignalColor.YELLOW;
    }

    @Override
    public int getDuration(TrafficLight light) {
        return light.getDurations(SignalColor.YELLOW);
    }
    
}
