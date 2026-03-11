package TrafficSignalSystem.state;

import TrafficSignalSystem.entity.TrafficLight;
import TrafficSignalSystem.enums.SignalColor;

public class GreenState implements SignalState {

    @Override
    public void handle(TrafficLight light) {
        System.out.println("[" + light.getDirection() + "] GREEN -> transitioning to YELLOW");
        light.setState(new YellowState());
    }

    @Override
    public SignalColor getColor() {
       return SignalColor.GREEN;
    }

    @Override
    public int getDuration(TrafficLight light) {
        return light.getDurations(SignalColor.GREEN);
    }
    
}
