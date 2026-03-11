package TrafficSignalSystem.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import TrafficSignalSystem.enums.Directions;

public class Intersection {
    private Map<Directions,TrafficLight> signals=new HashMap<>();
    private List<Directions> order=new ArrayList<>();
    int currentidx=0;

    private volatile boolean running = false;
    private Thread cyclingThread;

    public void addSignal(TrafficLight light) {
        signals.put(light.getDirection(), light);
        order.add(light.getDirection());
    }

    public void start(int totalCycles) {
        running = true;
        cyclingThread = new Thread(() -> {
            try {
                for (int i = 0; i < totalCycles && running; i++) {
                    System.out.println("\n>>> Cycle " + (i + 1));
                    nextCycle();
                    printStatus();
                }
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Intersection stopped.");
        });
        cyclingThread.start();
    }

    public void stop() {
        running = false;
        if (cyclingThread != null) cyclingThread.interrupt();
    }


    // Advance one full GREEN→YELLOW→RED cycle for the current direction

    public void nextCycle() throws Exception{
        Directions active=order.get(currentidx);
        // Set active direction to GREEN, all others RED
        for(Map.Entry<Directions,TrafficLight> e:signals.entrySet()){
            if(e.getKey()==active){
                e.getValue().forceGreen();
            }else{
                e.getValue().forceRed();
            }
        }

        TrafficLight actveLight=signals.get(active);
        // GREEN phase
        actveLight.tick();// GREEN → YELLOW

        // YELLOW phase
        actveLight.tick();// YELLOW → RED

        currentidx=(currentidx+1)%order.size();
    }

    // Manual override: force a specific direction green immediately
    public void manualOverride(Directions direction) throws Exception {
        System.out.println("\n=== MANUAL OVERRIDE for " + direction + " ===");
        if (!signals.containsKey(direction)) {
            System.out.println("Direction not found: " + direction);
            return;
        }
        // Jump cycle index to this direction
        currentidx = order.indexOf(direction);
        nextCycle();
    }

    public void printStatus() {
        System.out.println("\n--- Intersection Status ---");
        signals.values().forEach(System.out::println);
        System.out.println("---------------------------\n");
    }

}
