package TrafficSignalSystem.entity;

import java.util.HashMap;
import java.util.Map;

import TrafficSignalSystem.enums.Directions;
import TrafficSignalSystem.enums.SignalColor;
import TrafficSignalSystem.state.GreenState;
import TrafficSignalSystem.state.RedState;
import TrafficSignalSystem.state.SignalState;

public class TrafficLight {
    private Directions direction;
    private SignalState currentState;
    private Map<SignalColor,Integer> durations; //color -> durations

    public TrafficLight(Directions directions, Map<SignalColor,Integer> durations){
        this.direction=directions;
        this.durations=new HashMap<SignalColor,Integer>(durations);
        this.currentState=new RedState();
    }

    public void tick() throws Exception{
        int duration=currentState.getDuration(this);
        System.out.printf("[%s] %s for %d seconds%n", direction, currentState.getColor(), duration);
        Thread.sleep(duration*1000l);
        currentState.handle(this);
    }

    public Integer getDurations(SignalColor color) {
        return durations.getOrDefault(color ,5); //default 5 sec
       
    }

    public void forceGreen(){
        System.out.println("[" + direction + "] MANUAL OVERRIDE -> GREEN");  
        this.currentState=new GreenState();   
    }

    public void forceRed(){
        this.currentState=new RedState();
    }

    public void setState(SignalState state){
        this.currentState=state;
    }

    public SignalColor getCurrentColor(){//optional
        return currentState.getColor();
    }

    public Directions getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return direction + ": " + currentState.getColor();
    }
} 
