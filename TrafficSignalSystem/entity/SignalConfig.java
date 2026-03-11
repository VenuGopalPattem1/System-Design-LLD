package TrafficSignalSystem.entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import TrafficSignalSystem.enums.SignalColor;

public class SignalConfig {
    private final Map<SignalColor, Integer> durations = new HashMap<>();

     public SignalConfig green(int seconds) {
        durations.put(SignalColor.GREEN, seconds);
        return this;
    }

    public SignalConfig yellow(int seconds) {
        durations.put(SignalColor.YELLOW, seconds);
        return this;
    }

    public SignalConfig red(int seconds) {
        durations.put(SignalColor.RED, seconds);
        return this;
    }

    public Map<SignalColor, Integer> build() {
        // return durations
        return Collections.unmodifiableMap(durations);
    }
}
