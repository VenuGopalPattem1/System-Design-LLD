# Traffic Signal System — LLD Revision Guide

---

## Problem Statement

Design a Traffic Signal System for a 4-way intersection that:
- Cycles traffic lights automatically in round-robin order
- Allows manual override to force any direction GREEN immediately
- Uses the **State Design Pattern** for light transitions
- Supports configurable durations per direction per color

---

## Requirements

### Functional
- 4 directions: NORTH, SOUTH, EAST, WEST
- 3 states per light: GREEN → YELLOW → RED → GREEN (repeats)
- Only ONE direction is GREEN at a time — all others are RED
- Each direction and color has its own configurable duration
- Manual override: jump any direction to GREEN, skip the queue
- After override, normal cycle resumes from that direction onward

### Non-Functional
- Extensible: new directions or states = minimal code change
- Thread-safe: cycling on background thread, override interrupts cleanly
- Configurable: durations set at construction, not hardcoded

---

## Real World Analogy

```
              NORTH
            [ 🔴 RED ]
               │  │
[ 🟢 GREEN ] ──┼──┼── [ 🔴 RED ]
  WEST                   EAST
               │  │
            [ 🔴 RED ]
              SOUTH
```

- 4 physical poles, one per direction
- Each pole talks to drivers coming FROM that direction
- Only one pole goes GREEN at a time — others stay RED
- YELLOW = warning before RED, not permission to go

---

## Class Responsibilities

| Class | Single Responsibility |
|---|---|
| `Direction` | Enum — NORTH, SOUTH, EAST, WEST |
| `SignalColor` | Enum — GREEN, YELLOW, RED |
| `SignalState` | Interface — defines handle(), getColor(), getDuration() |
| `GreenState` | Knows: GREEN lasts X seconds, next state is YELLOW |
| `YellowState` | Knows: YELLOW lasts X seconds, next state is RED |
| `RedState` | Knows: RED lasts X seconds, next state is GREEN |
| `TrafficLight` | Holds its own state, direction, durations — transitions itself |
| `SignalConfig` | Builder — readable way to configure durations |
| `Intersection` | Coordinates all lights, owns cycle order and threading |
| `Main` | Wires everything together, fires the starting gun |

---

## State Pattern — Core Idea

`TrafficLight` never says `if GREEN then go YELLOW`.  
The **state object itself** decides the next state.

```
TrafficLight has → currentState (GreenState / YellowState / RedState)

tick() is called:
  1. currentState.getDuration()  → how long to sleep
  2. Thread.sleep(duration)      → wait
  3. currentState.handle(this)   → state decides next state and calls setState()
```

Transition chain:
```
GreenState.handle()  → light.setState(new YellowState())
YellowState.handle() → light.setState(new RedState())
RedState.handle()    → light.setState(new GreenState())
```

---

## Complete Flow

### Step 1 — Object Creation (Main)

```
new SignalConfig().green(30).yellow(5).red(60).build()
  → returns Map { GREEN=30, YELLOW=5, RED=60 }

new TrafficLight(Direction.NORTH, durations)
  → direction   = NORTH
  → durations   = { GREEN=30, YELLOW=5, RED=60 }
  → currentState = RedState   ← all start RED

intersection.addSignal(north)
  → signals    = { NORTH → TrafficLight }
  → cycleOrder = [ NORTH ]

After all 4 addSignal calls:
  → signals    = { NORTH, SOUTH, EAST, WEST }
  → cycleOrder = [ NORTH, SOUTH, EAST, WEST ]
  → currentIndex = 0
```

### Step 2 — intersection.start(8)

Spawns a **background thread** that loops 8 times calling `cycleNext()`.  
Main thread stays free — that's why manual override can fire mid-cycle.

```
Main Thread              Background Thread
    │                          │
    ├── start(8) ─────────────►│ loop i=0..7
    │                          │   cycleNext()
    │  (free to do other work) │   printStatus()
```

### Step 3 — cycleNext() for NORTH (currentIndex = 0)

```
active = cycleOrder[0] = NORTH

Set lights:
  NORTH → forceGreen()  → currentState = GreenState
  SOUTH → forceRed()    → currentState = RedState
  EAST  → forceRed()    → currentState = RedState
  WEST  → forceRed()    → currentState = RedState

tick() call 1 — GREEN phase:
  getDuration(GREEN) = 30
  Thread.sleep(30000)
  GreenState.handle() → setState(new YellowState())

tick() call 2 — YELLOW phase:
  getDuration(YELLOW) = 5
  Thread.sleep(5000)
  YellowState.handle() → setState(new RedState())

currentIndex = (0 + 1) % 4 = 1   → SOUTH is next
```

### Step 4 — Round Robin Continues

```
Cycle 1: NORTH  GREEN(30s) → YELLOW(5s) → RED
Cycle 2: SOUTH  GREEN(30s) → YELLOW(5s) → RED
Cycle 3: EAST   GREEN(20s) → YELLOW(4s) → RED
Cycle 4: WEST   GREEN(20s) → YELLOW(4s) → RED
Cycle 5: NORTH  again...   ← wraps via % 4
```

### Step 5 — Manual Override (EAST at t=5s)

```
Thread.sleep(5000) in Main fires

manualOverride(EAST):
  1. stop()
       → running = false
       → cyclingThread.interrupt()  ← kills NORTH's 30s sleep mid-way

  2. currentIndex = cycleOrder.indexOf(EAST) = 2

  3. cycleNext()  ← runs on Main thread
       → EAST forceGreen, others forceRed
       → EAST tick() GREEN(20s) → YELLOW(4s) → RED

  4. start(MAX_VALUE)  ← new background thread
       → resumes from currentIndex=3 (WEST is next)
```

### Timeline

```
t=0s   NORTH GREEN starts (30s planned)
t=5s   Override fires → NORTH interrupted
       EAST forced GREEN
t=5s   EAST GREEN (20s)
t=25s  EAST YELLOW (4s)
t=29s  Resume auto cycle → WEST GREEN
t=49s  WEST YELLOW → NORTH → SOUTH → ...
```

---

## Why Each Class Exists

### Why SignalConfig?
Without it, Main needs raw map construction everywhere — messy and unreadable.
```java
// Without SignalConfig
Map<SignalColor, Integer> d = new HashMap<>();
d.put(GREEN, 30); d.put(YELLOW, 5); d.put(RED, 60);

// With SignalConfig
new SignalConfig().green(30).yellow(5).red(60).build()
```

### Why TrafficLight?
Encapsulates one pole's state, duration, and transitions.  
It doesn't know about other lights or cycling order — just itself.

### Why Intersection?
`TrafficLight` only knows itself. Someone must:
- Decide NORTH goes GREEN while others go RED
- Manage the order: NORTH → SOUTH → EAST → WEST
- Run the background thread
- Handle override across all lights

Without `Intersection`, all of this lands in `Main` making it a god class.

### Why Main?
Purely the composition root. Answers:
- Which directions exist?
- What are their durations?
- How many cycles?
- When to override?

---

## Design Patterns Used

| Pattern | Where |
|---|---|
| **State** | SignalState interface + Green/Yellow/RedState — each state knows its successor |
| **Builder** | SignalConfig — fluent API for duration configuration |
| **Single Responsibility** | Each class has exactly one job |
| **Open/Closed** | Add new state (e.g. FLASHING_RED) = one new class, zero existing changes |

---

## Edge Cases

| Edge Case | How It's Handled |
|---|---|
| Override while GREEN phase is running | `cyclingThread.interrupt()` wakes the sleeping thread immediately |
| Override with unknown direction | Early return with log message — no crash |
| Two overrides fired quickly | Second override stops the thread started by first, then starts fresh |
| Cycle count reaches limit | `running = false`, thread exits cleanly after loop ends |
| All lights start RED at boot | Every `TrafficLight` initializes with `new RedState()` — safe default |
| Adding a T-intersection (3 directions) | Just add 3 signals instead of 4 — zero other code changes |
| Different durations per direction | Each `TrafficLight` holds its own `durations` map independently |

---

## Extending the System

### Add a new direction (e.g. NORTH_EAST)
```java
// 1. Add to enum
public enum Direction { NORTH, SOUTH, EAST, WEST, NORTH_EAST }

// 2. Add signal in Main
intersection.addSignal(new TrafficLight(Direction.NORTH_EAST,
    new SignalConfig().green(15).yellow(3).red(80).build()));

// That's it. Everything else works automatically.
```

### Add a new state (e.g. FLASHING_RED)
```java
// 1. Create the class
public class FlashingRedState implements SignalState {
    public void handle(TrafficLight light) { light.setState(new GreenState()); }
    public SignalColor getColor() { return SignalColor.FLASHING_RED; }
    public int getDuration(TrafficLight light) { return light.getDuration(SignalColor.FLASHING_RED); }
}

// 2. Update RedState to go to FlashingRed instead of Green (or chain it in)
// 3. Add FLASHING_RED to SignalColor enum
```

---

## Quick Revision Checklist

- [ ] State Pattern: state object decides next state, not TrafficLight
- [ ] Only ONE direction GREEN at a time — enforced in cycleNext()
- [ ] forceGreen() and forceRed() bypass normal state transitions
- [ ] tick() = sleep(duration) then handle() = transition
- [ ] Background thread handles auto cycling
- [ ] interrupt() is how override stops the sleeping thread
- [ ] currentIndex % cycleOrder.size() = round robin
- [ ] SignalConfig is just a readability wrapper — could be replaced with a raw Map