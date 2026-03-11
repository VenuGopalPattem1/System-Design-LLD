package TrafficSignalSystem.state;

import TrafficSignalSystem.entity.TrafficLight;
import TrafficSignalSystem.enums.SignalColor;

public interface SignalState {
    void handle(TrafficLight light);
    SignalColor getColor();
    int getDuration(TrafficLight light);
}
