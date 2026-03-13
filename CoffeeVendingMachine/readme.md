# ☕ Coffee Vending Machine — LLD Interview Complete Guide

> Everything you need to discuss in a machine coding / LLD interview on a Coffee Vending Machine. Use this as your preparation checklist.

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Complete Flow — Step by Step](#2-complete-flow--step-by-step)
3. [Class Design & Responsibilities](#3-class-design--responsibilities)
4. [State Machine — Deep Dive](#4-state-machine--deep-dive)
5. [Design Patterns Used](#5-design-patterns-used)
6. [Edge Cases to Proactively Cover](#6-edge-cases-to-proactively-cover)
7. [SOLID Principles Checklist](#7-solid-principles-checklist)
8. [Concurrency & Thread Safety](#8-concurrency--thread-safety)
9. [Extensibility Discussion](#9-extensibility-discussion)
10. [Testing Strategy](#10-testing-strategy)
11. [What to Ask the Interviewer](#11-what-to-ask-the-interviewer)
12. [Interview Flow & Time Tips](#12-interview-flow--time-tips)
13. [Quick Reference Card](#13-quick-reference-card)

---

## 1. System Overview

```
┌─────────────────────────────────────────────┐
│              Coffee Vending Machine          │
│                                             │
│  User inserts coin ──► PaymentService       │
│  User selects coffee ──► CoffeeMachine      │
│       │                                     │
│       ├──► InventoryManager (check stock)   │
│       ├──► PaymentService (check balance)   │
│       └──► brew() ──► dispense coffee       │
└─────────────────────────────────────────────┘
```

### Functional Requirements
- Display menu with prices
- Accept coin/cash payments
- Brew selected coffee if ingredients are available
- Return change after payment
- Cancel and refund at any time
- Admin: refill ingredients, add new recipes

### Non-Functional Requirements
- Thread-safe (one brew at a time)
- Extensible for new coffee types and payment methods
- Fail-safe: no brew if ingredients or balance is insufficient

---

## 2. Complete Flow — Step by Step

### Happy Path Flow
```
1. User views menu          → showMenu()
2. User inserts coins       → insertCoin(amount)
3. Balance updated          → PaymentService.balance += amount
4. User selects coffee      → selectCoffee(CoffeeType.CAPPUCCINO)
5. State check              → MachineState == IDLE? ✅
6. Recipe lookup            → menu.get(CAPPUCCINO)
7. Balance check            → balance >= price? ✅
8. Inventory check          → hasEnough(recipe.ingredients)? ✅
9. State → PROCESSING       → prevents concurrent brews
10. Deduct ingredients      → InventoryManager.deduct()
11. Collect payment         → PaymentService.collectPayment(price)
12. Calculate change        → balance - price
13. Dispense coffee         → print "Enjoy your CAPPUCCINO!"
14. Return change           → print "Change: $0.50"
15. State → IDLE            → machine ready again
```

### Insufficient Balance Flow
```
1. User inserts $1.00
2. Selects CAPPUCCINO ($2.50)
3. Balance check FAILS → print "Insufficient balance"
4. State stays IDLE
5. User inserts more coins → retries
```

### Cancel Flow
```
1. User inserts coins
2. User calls cancel()
3. Full balance refunded
4. State reset to IDLE
```

### Out of Ingredient Flow
```
1. User has sufficient balance
2. Inventory check FAILS
3. Print "Insufficient ingredients"
4. Balance NOT consumed
5. User can cancel and get refund
```

---

## 3. Class Design & Responsibilities

### Single Responsibility — each class does ONE thing

| Class | Responsibility |
|---|---|
| `CoffeeMachine` | Orchestrates the overall flow — coordinates all components |
| `InventoryManager` | Tracks ingredient stock, checks and deducts quantities |
| `PaymentService` | Manages balance, validates payment, calculates change |
| `CoffeeRecipe` | Holds recipe data (ingredients + price) — pure data model |
| `CoffeeType` (enum) | Type-safe representation of coffee options |
| `Ingredient` (enum) | Type-safe representation of ingredients |
| `MachineState` (enum) | Represents machine lifecycle states |

### Dependency Map
```
CoffeeMachine
    ├── HAS-A  InventoryManager
    ├── HAS-A  PaymentService
    ├── HAS-A  Map<CoffeeType, CoffeeRecipe>   (the menu)
    └── HAS-A  MachineState

CoffeeRecipe
    └── HAS-A  Map<Ingredient, Integer>        (ingredients + quantities)
```

---

## 4. State Machine — Deep Dive

The machine has 3 states — this is a key discussion point:

```
         insertCoin() / showMenu()
    ┌──────────────────────────────┐
    │                              │
  IDLE ──── selectCoffee() ──► PROCESSING ──── brew complete ──► IDLE
    │                              │
    │         cancel()             │
    └──────────────────────────────┘
    │
    ▼
OUT_OF_SERVICE   (admin sets this when restocking)
```

### State Transition Table

| Current State | Action | Next State | Notes |
|---|---|---|---|
| IDLE | insertCoin() | IDLE | Balance updated |
| IDLE | selectCoffee() | PROCESSING | Only if checks pass |
| IDLE | cancel() | IDLE | Refund issued |
| PROCESSING | brew complete | IDLE | Coffee dispensed |
| OUT_OF_SERVICE | any user action | — | Throws exception |

### Why State Matters (say this in interview)
> "Without a state machine, two concurrent threads could both pass the IDLE check and trigger simultaneous brews. The PROCESSING state acts as a mutex at the business logic level, before we even talk about `synchronized`."

### Advanced: State Pattern (mention for brownie points)
```java
// Instead of enum + if/else, use full State Pattern for complex machines
public interface MachineState {
    void insertCoin(double amount, CoffeeMachine context);
    void selectCoffee(CoffeeType type, CoffeeMachine context);
    void cancel(CoffeeMachine context);
}

public class IdleState implements MachineState { ... }
public class ProcessingState implements MachineState { ... }
public class OutOfServiceState implements MachineState { ... }
```
Say: *"For this interview scope, enum + checks is clean. In production with 6+ states, I'd use the full State Pattern."*

---

## 5. Design Patterns Used

### Pattern 1: Strategy — Payment Method
```java
// Abstract the payment method so we can swap implementations
public interface PaymentStrategy {
    void insertMoney(double amount);
    boolean hasSufficientBalance(double price);
    double collectPayment(double price);
    double refund();
}

public class CoinPayment implements PaymentStrategy { ... }
public class CardPayment  implements PaymentStrategy { ... }
public class UpiPayment   implements PaymentStrategy { ... }

// CoffeeMachine uses the interface, not the concrete class
public class CoffeeMachine {
    private final PaymentStrategy payment;  // injected
}
```
**Why:** Adding UPI or card support doesn't touch `CoffeeMachine` at all.

---

### Pattern 2: Factory — Recipe Creation
```java
public class RecipeFactory {
    public static CoffeeRecipe createEspresso() {
        return new CoffeeRecipe(
            CoffeeType.ESPRESSO,
            Map.of(Ingredient.WATER, 50, Ingredient.COFFEE_BEANS, 20),
            1.50
        );
    }
    // ... other factory methods
}
```
**Why:** Centralizes recipe creation logic — easy to unit test and modify recipes.

---

### Pattern 3: State — Machine Lifecycle
```java
// Simple version: enum
private MachineState state = MachineState.IDLE;

// Advanced version: full State Pattern (see Section 4)
```
**Why:** Prevents invalid transitions — you can't brew while already brewing.

---

### Pattern 4: Template Method — Brew Process
```java
// Define the brewing skeleton, let subclasses override specific steps
public abstract class BrewingProcess {
    // Template method — defines the steps
    public final void brew(CoffeeRecipe recipe) {
        heatWater();
        grindBeans(recipe);
        extractCoffee(recipe);
        addMilkIfNeeded(recipe);   // hook — optional step
        dispense();
    }

    protected abstract void extractCoffee(CoffeeRecipe recipe);
    protected void addMilkIfNeeded(CoffeeRecipe recipe) {}  // default: do nothing
}

public class EspressoBrewProcess   extends BrewingProcess { ... }
public class CappuccinoBrewProcess extends BrewingProcess { ... }
```
**Why:** Each coffee type has a different brewing process but shares common steps.

---

### Pattern 5: Observer — Low Stock Alerts
```java
public interface InventoryObserver {
    void onLowStock(Ingredient ingredient, int remaining);
}

public class AdminNotifier implements InventoryObserver {
    public void onLowStock(Ingredient ingredient, int remaining) {
        System.out.println("ALERT: Low stock on " + ingredient + " (" + remaining + " left)");
    }
}

// InventoryManager notifies observers when stock drops below threshold
public class InventoryManager {
    private final List<InventoryObserver> observers = new ArrayList<>();
    private static final int LOW_STOCK_THRESHOLD = 100;

    public void deduct(Map<Ingredient, Integer> required) {
        required.forEach((ing, qty) -> {
            stock.merge(ing, -qty, Integer::sum);
            if (stock.get(ing) < LOW_STOCK_THRESHOLD) {
                observers.forEach(o -> o.onLowStock(ing, stock.get(ing)));
            }
        });
    }
}
```
**Why:** Machine can alert an admin system without tight coupling to notification logic.

---

### Pattern 6: Facade — CoffeeMachine itself
The `CoffeeMachine` class IS the Facade — it hides the complexity of `InventoryManager`, `PaymentService`, and state management behind a simple API:
```java
machine.insertCoin(2.00);
machine.selectCoffee(CoffeeType.LATTE);
```
The caller doesn't know about ingredients, state transitions, or change calculation.

---

## 6. Edge Cases to Proactively Cover

Raise these BEFORE the interviewer asks — shows senior-level thinking:

| Edge Case | Handling |
|---|---|
| **Negative coin amount** | `if (amount <= 0) throw IllegalArgumentException` in PaymentService |
| **Null coffee type selected** | Null check + `throw IllegalArgumentException` |
| **Coffee not on menu** | `menu.get(type) == null` → throw `CoffeeNotAvailableException` |
| **Insufficient balance** | Print message, retain balance, stay in IDLE — don't throw |
| **Out of one ingredient** | Print which ingredient is missing, retain balance |
| **Machine in PROCESSING state** | Throw `IllegalStateException("Machine is busy")` |
| **Machine OUT_OF_SERVICE** | Throw `IllegalStateException("Machine is out of service")` |
| **Cancel with zero balance** | No refund message, no error — silently succeed |
| **Exact change — zero change** | Don't print change message if change == 0 |
| **Refill beyond max capacity** | Add `MAX_CAPACITY` constant in InventoryManager |
| **Add duplicate recipe** | `menu.putIfAbsent()` or throw `RecipeAlreadyExistsException` |
| **Integer overflow on click count** | Use `long` for counters in analytics |

### Custom Exceptions to Define
```java
public class InsufficientIngredientsException extends RuntimeException { ... }
public class InsufficientBalanceException      extends RuntimeException { ... }
public class CoffeeNotAvailableException       extends RuntimeException { ... }
public class MachineNotReadyException          extends RuntimeException { ... }
```

---

## 7. SOLID Principles Checklist

| Principle | Applied Where | How |
|---|---|---|
| **S** — Single Responsibility | Every class | `PaymentService` only handles money; `InventoryManager` only handles stock |
| **O** — Open/Closed | `PaymentStrategy` interface | Add new payment types without modifying existing code |
| **O** — Open/Closed | `menu` Map | Add new coffee types by adding entries — no code changes |
| **L** — Liskov Substitution | `PaymentStrategy` impls | `CardPayment` can replace `CoinPayment` without breaking `CoffeeMachine` |
| **I** — Interface Segregation | `PaymentStrategy` | Focused interface — no bloated methods unrelated to payment |
| **D** — Dependency Inversion | `CoffeeMachine` constructor | Depends on `PaymentStrategy` interface, not `CoinPayment` directly |

---

## 8. Concurrency & Thread Safety

### The Problem
```
Thread A: selectCoffee(LATTE)  → passes balance check  (balance = $5)
Thread B: selectCoffee(LATTE)  → passes balance check  (balance = $5)
Thread A: collectPayment($3)   → balance = $2
Thread B: collectPayment($3)   → balance = -$1  ← BUG!
```

### Solution 1: synchronized method (simple)
```java
public synchronized void selectCoffee(CoffeeType type) {
    // only one thread at a time
}
```

### Solution 2: ReentrantLock (more control)
```java
private final ReentrantLock brewLock = new ReentrantLock();

public void selectCoffee(CoffeeType type) {
    if (!brewLock.tryLock()) {
        throw new MachineNotReadyException("Machine is busy");
    }
    try {
        // brew logic
    } finally {
        brewLock.unlock();  // ALWAYS release in finally
    }
}
```

### Solution 3: State as guard (what we implemented)
```java
// MachineState.PROCESSING acts as a logical mutex
// Combined with synchronized for full safety
if (state != MachineState.IDLE) throw new IllegalStateException("Busy");
state = MachineState.PROCESSING;
// ... brew ...
state = MachineState.IDLE;
```

**Say in interview:** *"In a single-threaded machine coding context, the state guard is sufficient. In a real concurrent system, I'd wrap `selectCoffee()` with `synchronized` or `ReentrantLock` to prevent race conditions between the state check and state assignment."*

---

## 9. Extensibility Discussion

### Adding a New Coffee Type
```java
// Just add to the menu — ZERO other changes
menu.put(CoffeeType.MOCHA, new CoffeeRecipe(
    CoffeeType.MOCHA,
    Map.of(Ingredient.WATER, 50, Ingredient.MILK, 100,
           Ingredient.COFFEE_BEANS, 25, Ingredient.CHOCOLATE, 20),
    3.50
));
```

### Adding a New Payment Method
```java
// Implement the interface — ZERO changes to CoffeeMachine
public class QRCodePayment implements PaymentStrategy {
    public void insertMoney(double amount) { /* scan QR */ }
    public boolean hasSufficientBalance(double price) { /* check wallet */ }
    // ...
}
// Inject into CoffeeMachine constructor
```

### Adding an Admin Interface
```java
public class AdminService {
    private final InventoryManager inventory;
    private final CoffeeMachine machine;

    public void refillIngredient(Ingredient ing, int qty) {
        inventory.refill(ing, qty);
    }

    public void addNewRecipe(CoffeeRecipe recipe) {
        machine.addToMenu(recipe);
    }

    public void setOutOfService() {
        machine.setState(MachineState.OUT_OF_SERVICE);
    }

    public Map<Ingredient, Integer> checkStock() {
        return inventory.getStock();
    }
}
```

### Adding Drink Customization
```java
public class DrinkCustomization {
    private int extraSugar   = 0;   // units
    private int extraMilk    = 0;   // ml
    private boolean decaf    = false;
}

// selectCoffee overload
public void selectCoffee(CoffeeType type, DrinkCustomization custom) { ... }
```

---

## 10. Testing Strategy

### Unit Tests
```java
// PaymentService
@Test void shouldAcceptValidCoinAmount()
@Test void shouldRejectNegativeOrZeroCoin()
@Test void shouldReturnCorrectChange()
@Test void shouldRefundFullBalance()
@Test void shouldReturnTrueWhenBalanceSufficient()

// InventoryManager
@Test void shouldReturnTrueWhenIngredientsAvailable()
@Test void shouldReturnFalseWhenIngredientShort()
@Test void shouldDeductCorrectQuantities()
@Test void shouldAlertObserverOnLowStock()
@Test void shouldRefillCorrectly()

// CoffeeMachine (integration)
@Test void shouldBrewCoffeeOnHappyPath()
@Test void shouldNotBrewWithInsufficientBalance()
@Test void shouldNotBrewWithInsufficientIngredients()
@Test void shouldReturnChangeAfterBrew()
@Test void shouldRefundOnCancel()
@Test void shouldThrowWhenMachineOutOfService()
@Test void shouldThrowWhenMachineBusy()
```

### Concurrency Tests
```java
@Test void shouldNotAllowConcurrentBrews() {
    ExecutorService pool = Executors.newFixedThreadPool(10);
    // 10 threads all try to brew simultaneously
    // Only 1 should succeed, others should get MachineNotReadyException
}
```

### Tools: JUnit 5 + Mockito

```java
// Mock InventoryManager to test CoffeeMachine in isolation
@Mock InventoryManager inventory;
when(inventory.hasEnough(any())).thenReturn(true);
doNothing().when(inventory).deduct(any());
```

---

## 11. What to Ask the Interviewer

Ask these at the start — it signals seniority and shapes your design:

1. **"Should the same coin insertion API handle both coins and notes, or just coins?"**
   → Determines if you need a `PaymentStrategy` abstraction from the start

2. **"Do we need user authentication — e.g., loyalty card or app-based ordering?"**
   → Adds a `UserSession` concept to the design

3. **"Should the machine support drink customization — extra sugar, milk level?"**
   → Adds `DrinkCustomization` parameter to `selectCoffee()`

4. **"Is there an admin mode — for refilling and adding new recipes?"**
   → Adds an `AdminService` class with protected operations

5. **"Do we need to persist state — e.g., survive a power cycle?"**
   → In-memory is fine for interview; mention DB/file persistence for production

6. **"Should low-stock trigger an alert, or just silently fail?"**
   → Introduces the Observer pattern for inventory monitoring

---

## 12. Interview Flow & Time Tips

### Suggested 30-Minute Timeline

```
0:00 – 0:03  │ Clarify requirements — ask the 6 questions above
0:03 – 0:06  │ Define enums: CoffeeType, Ingredient, MachineState
0:06 – 0:09  │ Define models: CoffeeRecipe, (DrinkCustomization if needed)
0:09 – 0:12  │ Define InventoryManager + PaymentService
0:12 – 0:22  │ Implement CoffeeMachine: loadMenu → insertCoin → selectCoffee → brew
0:22 – 0:25  │ Add edge cases: state guards, null checks, insufficient checks
0:25 – 0:28  │ Walk through design patterns: State, Strategy, Observer
0:28 – 0:30  │ Discuss extensibility + concurrency
```

### Phrases That Impress Interviewers

- *"I'm using an enum for `MachineState` now, but if this machine had 6+ states with complex transitions, I'd refactor to the full State Pattern."*

- *"PaymentService is abstracted behind an interface so we can add card or UPI payments without touching any brewing logic — that's the Strategy Pattern."*

- *"I'm checking state before brewing and setting it to PROCESSING immediately — in a multi-threaded environment I'd wrap this in `synchronized` to prevent the TOCTOU race condition."*

- *"The machine itself acts as a Facade — the caller just says `selectCoffee()` and doesn't know anything about ingredient deduction or state transitions."*

- *"For low-stock alerts I'd add an Observer — `InventoryManager` notifies registered listeners when stock drops below a threshold, without being tightly coupled to any notification mechanism."*

### Common Follow-up Questions & Answers

**Q: What if two people press the button at the same time?**
A: The `MachineState.PROCESSING` guard prevents it. For full thread safety, wrap `selectCoffee()` with `synchronized` or `ReentrantLock`.

**Q: How do you add a new coffee type?**
A: Just add an entry to the `menu` Map in `loadMenu()`. The rest of the system works automatically — that's Open/Closed Principle.

**Q: What if the machine runs out of water mid-brew?**
A: We check inventory *before* brewing begins (defensive check). We never enter `brew()` without confirming all ingredients are available.

**Q: How would you support hot vs iced drinks?**
A: Add a `DrinkTemperature` enum and a `DrinkCustomization` class. Pass it to `selectCoffee()` and adjust the brewing process accordingly — likely using Template Method Pattern.

---

## 13. Quick Reference Card

```
Classes:      CoffeeMachine, InventoryManager, PaymentService,
              CoffeeRecipe, CoffeeType, Ingredient, MachineState

States:       IDLE → PROCESSING → IDLE
              IDLE → OUT_OF_SERVICE (admin only)

Checks order: 1. MachineState == IDLE
              2. Recipe exists in menu
              3. balance >= price
              4. ingredients sufficient
              → All pass → brew

Patterns:     State    → MachineState enum / State interface
              Strategy → PaymentStrategy (coin, card, UPI)
              Factory  → RecipeFactory
              Observer → Low stock alerts
              Facade   → CoffeeMachine itself
              Template → BrewingProcess steps

Thread safe:  synchronized on selectCoffee()
              OR ReentrantLock with try/finally
              OR state guard (PROCESSING) as logical mutex

Extend by:    New coffee    → add to menu Map
              New payment   → implement PaymentStrategy
              New alert     → implement InventoryObserver
              Customization → add DrinkCustomization param
```

---

*You've got this — go brew that interview! ☕🚀*