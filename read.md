# Designing a Parking Lot System

## Requirements
1. The parking lot should have multiple levels, each level with a certain number of parking spots.
2. The parking lot should support different types of vehicles, such as cars, motorcycles, and trucks.
3. Each parking spot should be able to accommodate a specific type of vehicle.
4. The system should assign a parking spot to a vehicle upon entry and release it when the vehicle exits.
5. The system should track the availability of parking spots and provide real-time information to customers.
6. The system should handle multiple entry and exit points and support concurrent access.

## Classes, Interfaces and Enumerations
1. The **ParkingLot** class follows the Singleton pattern to ensure only one instance of the parking lot exists. It maintains a list of levels and provides methods to park and unpark vehicles.
2. The **ParkingFloor** class represents a level in the parking lot and contains a list of parking spots. It handles parking and unparking of vehicles within the level.
3. The **ParkingSpot** class represents an individual parking spot and tracks the availability and the parked vehicle.
4. The **Vehicle** class is an abstract base class for different types of vehicles. It is extended by Car, Motorcycle, and Truck classes.
5. The **VehicleSize** enum defines the different types of vehicles supported by the parking lot.
6. Multi-threading is achieved through the use of synchronized keyword on critical sections to ensure thread safety.
7. The **Main** class demonstrates the usage of the parking lot system.

## Design Patterns Used:
1. Singleton Pattern: Ensures only one instance of the ParkingLot class.
2. Factory Pattern (optional extension): Could be used for creating vehicles based on input.
3. Observer Pattern (optional extension): Could notify customers about available spots.
4. Strategy Pattern: promotes loose coupling and dynamic interchangable logic

# Parking Lot LLD — Step by Step (Memory-First Approach)

---

## The Secret: Use a STORY, not code

> Don't memorize code. Memorize the **story of what happens** when a car enters a parking lot. The code will follow naturally.

---

## THE STORY (Learn this first)

```
A car arrives at the gate.
The parking lot finds an empty spot.
It gives the driver a ticket.
The driver parks and leaves their car.
Later, they return, pay at the gate, and drive away.
The spot is now free for the next car.
```

**Every single class in your design comes from this one story.**

---

## STEP 1 — Memorize the 6 Core Classes

Use this phrase to remember them:

> **"Very Pretty Flowers, Tickle Little Pets"**

| Letter | Class | What it is |
|---|---|---|
| **V** | `Vehicle` | The car/bike/truck |
| **P** | `ParkingSpot` | One individual slot |
| **F** | `ParkingFloor` | One level (holds many spots) |
| **T** | `Ticket` | Proof of entry, tracks time |
| **L** | `ParkingLot` | The whole system (Singleton) |
| **P** | `PaymentGate` | Where you pay on exit |

> 💡 Every time you see a new LLD problem, your first job is always: **find these 6 types of things** (the thing, the container, the building, the receipt, the manager, the exit).

---

## STEP 2 — Memorize the Flow (Entry → Exit)

```
ENTRY
  │
  ▼
ParkingLot.entry(vehicle)
  │   "Who has a free spot?"
  ▼
ParkingFloor.findAvailableSpot(vehicleSize)
  │   "I found one!"
  ▼
ParkingSpot.park(vehicle)
  │   "Spot is now OCCUPIED"
  ▼
new Ticket(vehicle, spot, level)
  │   "Here's your receipt with entry time"
  ▼
Driver parks 🚗

─────────────── TIME PASSES ───────────────

EXIT
  │
  ▼
ParkingLot.exit(licensePlate, paymentMode, gate)
  │   "Find the ticket"
  ▼
PaymentGate.processPayment(ticket, paymentStrategy)
  │   "Calculate hours × rate = fee"
  ▼
PaymentStrategy.pay(amount)         ← Cash / Card / UPI
  │   "Money collected!"
  ▼
ParkingSpot.unpark()
  │   "Spot is now AVAILABLE again"
  ▼
Driver leaves 🚗💨
```

> Read this flow 3 times. Close your eyes. Narrate it like a story.

---

## STEP 3 — Understand WHY Each Design Decision Was Made

> This is what separates good candidates. Don't just say **what** — say **why**.

---

### Decision 1: Why is `ParkingLot` a Singleton?

**Say this in interview:**
> *"There is physically only ONE parking lot. If I create two instances, they'll have different lists of floors — that's a data inconsistency bug. Singleton ensures the whole system shares one source of truth."*

**Remember it as:** One building = one object. Always.

---

### Decision 2: Why is `Vehicle` abstract?

**Say this in interview:**
> *"You never park a generic 'vehicle'. You park a Car, or a Bike, or a Truck. But all of them share a license plate and a size. So I put the common stuff in an abstract Vehicle, and let Car/Bike/Truck extend it."*

**Remember it as:** Abstract = "I exist only to be inherited, never alone."

---

### Decision 3: Why is Payment a Strategy Pattern?

**Say this in interview:**
> *"Today we support Cash, Card, UPI. Tomorrow the business might add PayPal or crypto. If I hardcode `if cash... else if card...` in PaymentGate, I have to edit that class every time. Instead, I define a PaymentStrategy interface with one method: `pay()`. Each mode implements it. PaymentGate doesn't know or care which one — it just calls `pay()`. This follows the Open/Closed Principle: open for extension, closed for modification."*

**Remember it as:** Strategy = plug-and-play. Swap without breaking.

---

### Decision 4: Why `synchronized` on `park()` and `unpark()`?

**Say this in interview:**
> *"Imagine two cars arrive at the exact same millisecond. Both threads scan the floor, both see Spot #5 as available, both try to park there. Without synchronization, you get a race condition — two cars in one spot. `synchronized` ensures only one thread can execute `park()` at a time on a given spot."*

**Remember it as:** Two cars, one spot = disaster. `synchronized` = bouncer at the door.

---

### Decision 5: Why `ConcurrentHashMap` for active tickets?

**Say this in interview:**
> *"A regular HashMap is not thread-safe. If two vehicles exit simultaneously and both try to remove their ticket, it can corrupt the map. ConcurrentHashMap handles concurrent reads and writes safely without locking the entire map — it's more performant than wrapping the whole thing in synchronized."*

**Remember it as:** Many exits at once = use ConcurrentHashMap, not regular HashMap.

---

## STEP 4 — The 3 Patterns to Always Mention

> Memorize this table. These 3 patterns appear in almost every LLD interview.

```
┌─────────────┬──────────────────────────┬────────────────────────────────┐
│ Pattern     │ Used In                  │ One-line reason                │
├─────────────┼──────────────────────────┼────────────────────────────────┤
│ Singleton   │ ParkingLot               │ Only one lot should exist      │
│ Strategy    │ PaymentStrategy          │ Swap payment modes easily      │
│             │ FeeStrategy              │ Swap fee rules easily          │
│ Abstract    │ Vehicle → Car/Bike/Truck │ Share fields, force subtypes   │
└─────────────┴──────────────────────────┴────────────────────────────────┘
```

---

## STEP 5 — Edge Cases Script (Say These Even If Not Asked)

> Proactively saying edge cases = senior engineer energy.

After explaining your design, say:
> *"A few edge cases I've handled or want to call out:"*

```
1. "Lot is full"
   → entry() returns null. I'd show 'Lot Full' at entry gate display.

2. "Two cars want the same spot simultaneously"
   → Handled by synchronized on park(). Only one thread wins.

3. "Payment fails"
   → spot.unpark() is NOT called. Car stays. Ticket stays active.
      Driver must retry payment.

4. "Minimum billing"
   → Even 5 minutes = charged 1 full hour. Enforced in
      getParkingDurationHours() using Math.ceil().

5. "Wrong vehicle type for spot"
   → park() checks vehicle.getSize() == spot.getAllowedSize().
      Returns false if mismatch.

6. "Invalid ticket on exit"
   → activeTickets.get() returns null. Log error. Deny exit.
      Flag for attendant.
```

---

## STEP 6 — How to OPEN Your Answer in Interview

> The first 60 seconds are critical. Use this script:

**Interviewer:** *"Design a Parking Lot system."*

**You say:**

> *"Sure! Before I start coding, let me quickly clarify scope and then walk you through my thinking.*
>
> *So when I think of a parking lot in real life — a car arrives, we find it a spot, give it a ticket, it parks. Later it returns, pays based on how long it stayed, and the spot becomes free. Every class I design will map directly to that real-world flow.*
>
> *My core entities will be: Vehicle, ParkingSpot, ParkingFloor, Ticket, ParkingLot, and a PaymentGate.*
>
> *For design patterns — ParkingLot will be a Singleton since only one lot exists. Payment will use the Strategy pattern so we can support Cash, Card, UPI without changing the core logic. Vehicle will be abstract since you never park a generic vehicle.*
>
> *For concurrency — spots will use synchronized on park and unpark to prevent race conditions, and I'll use ConcurrentHashMap for the active tickets map.*
>
> *Shall I start writing the code now?"*

---

> ✅ That 60-second answer already shows: requirements thinking, real-world mapping, design patterns, concurrency awareness. Most candidates don't say any of this before coding.

---

## STEP 7 — Revision Technique (Stick It to Memory)

Do this over 4 days:

```
DAY 1 — Read the full story + flow diagram. Don't touch code.
         Close your eyes and narrate the entry→exit flow.

DAY 2 — Write class names from memory (the 6 classes).
         For each class, write: what it holds + what it does.
         Check against notes.

DAY 3 — Write the code from scratch. No copy-paste.
         Don't worry if it's not perfect — just write it.
         Focus on: entry(), exit(), park(), processPayment()

DAY 4 — Do a mock explanation out loud (record yourself).
         Explain it like you're teaching a junior dev.
         If you can teach it, you've learned it.
```

> 💡 **The Rule:** If you can explain it to a 10-year-old using a real parking lot story, you can explain it to any interviewer.

---

## ⚡ THE CHEAT SHEET (Screenshot This)

```
CLASSES:    Vehicle → ParkingSpot → ParkingFloor → ParkingLot
            Ticket, PaymentGate, PaymentStrategy, FeeStrategy

FLOW:       entry() → findSpot() → park() → new Ticket()
            exit()  → processPayment() → pay() → unpark()

PATTERNS:   Singleton  = ParkingLot (one lot)
            Strategy   = Payment + Fee (plug & play)
            Abstract   = Vehicle (never alone)

THREADS:    synchronized  on park() / unpark()
            ConcurrentHashMap for activeTickets

EDGE CASES: Full lot | Payment fail | Min billing |
            Wrong spot type | Invalid ticket | Race condition
```