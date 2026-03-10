# 🏧 Vending Machine — LLD Revision Notes

---

https://github.com/ashishps1/awesome-low-level-design/blob/main/problems/vending-machine.md

## 📦 Classes & Interfaces (10 Components)

| # | Name | Type | Purpose |
|---|------|------|---------|
| 1 | `Product` | Class | Stores name and price |
| 2 | `Coin` | Enum | ONE, TWO, FIVE, TEN |
| 3 | `Note` | Enum | TEN, TWENTY, FIFTY, HUNDRED |
| 4 | `Inventory` | Class | HashMap of Product → quantity |
| 5 | `VendingMachineState` | Interface | 5 methods all states must implement |
| 6 | `IdleState` | Class | Waits for product selection |
| 7 | `ReadyState` | Class | Waits for payment |
| 8 | `DispenseState` | Class | Dispenses product + returns change |
| 9 | `VendingMachine` | Class | Singleton, holds state + payment |
| 10 | `VendingMachineDemo` | Class | Main method, runs all test cases |

---

## 🔄 State Flow

```
[IDLE] ──selectProduct()──► [READY] ──enough money──► [DISPENSE] ──► [IDLE]
          (product set)       (insert coins/notes)      (dispense + change)

[READY] ──returnChange()──► [IDLE]   (cancel/refund)
```

---

## 🧩 VendingMachineState Interface

```java
interface VendingMachineState {
    void selectProduct(Product product);
    void insertCoin(Coin coin);
    void insertNote(Note note);
    void dispenseProduct();
    void returnChange();
}
```
> Every state implements all 5 methods. Invalid actions just print a warning message.

---

## 🟡 IdleState — Key Behavior

| Method | Action |
|--------|--------|
| `selectProduct()` | Check stock → set product → switch to ReadyState |
| `insertCoin()` | ❌ "Select a product first" |
| `insertNote()` | ❌ "Select a product first" |
| `dispenseProduct()` | ❌ "Select a product first" |
| `returnChange()` | ❌ "No change to return" |

---

## 🟠 ReadyState — Key Behavior

| Method | Action |
|--------|--------|
| `selectProduct()` | ❌ "Already selected: X" |
| `insertCoin()` | Add to totalPayment → check if enough → dispense |
| `insertNote()` | Add to totalPayment → check if enough → dispense |
| `dispenseProduct()` | ❌ "Need ₹X more" |
| `returnChange()` | ✅ Refund totalPayment → reset → go to IdleState |

---

## 🟢 DispenseState — Key Behavior

| Method | Action |
|--------|--------|
| `selectProduct()` | ❌ "Please wait, dispensing..." |
| `insertCoin()` | ❌ "Please wait, dispensing..." |
| `insertNote()` | ❌ "Please wait, dispensing..." |
| `dispenseProduct()` | ✅ inventory.dispense() → print receipt → returnChange() |
| `returnChange()` | ✅ change = paid - price → reset → go to IdleState |

---

## 🏗️ VendingMachine — Singleton

```java
// Only one instance ever
private static VendingMachine instance;

public static VendingMachine getInstance() {
    if (instance == null) instance = new VendingMachine();
    return instance;
}
```

**Holds:**
- `Inventory inventory`
- `VendingMachineState currentState` (starts as idleState)
- `Product selectedProduct`
- `double totalPayment`
- References to all 3 state objects (idleState, readyState, dispenseState)

**All public methods delegate to currentState:**
```java
public void selectProduct(Product p) { currentState.selectProduct(p); }
public void insertCoin(Coin c)       { currentState.insertCoin(c); }
public void insertNote(Note n)       { currentState.insertNote(n); }
```

---

## 📦 Inventory — Key Methods

```java
void addProduct(Product p, int qty)   // add/restock
boolean isAvailable(Product p)        // qty > 0 ?
void dispense(Product p)              // qty-- (throws if OOS)
void display()                        // print all products
```

> Uses `HashMap<Product, Integer>`. In production → use `ConcurrentHashMap`.

---

## 💡 Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **State** | IdleState / ReadyState / DispenseState | Avoid if-else chains for machine behavior |
| **Singleton** | VendingMachine | Only one machine instance exists |
| **Delegation** | VendingMachine → currentState | Machine delegates all actions to active state |

---

## ✅ Test Cases to Cover in Demo

| Case | Steps | Expected |
|------|-------|----------|
| Normal buy (exact) | select → insertCoins = price | Dispense, no change |
| Overpay | select → insertNote > price | Dispense + return change |
| Underpay | select → insertCoin < price | "Need ₹X more" |
| Top up | select → insert less → insert more | Eventually dispenses |
| Out of stock | select OOS product | "Out of stock" |
| Cancel | select → insertMoney → returnChange() | Refund money |
| Insert before select | insertCoin in IDLE | "Select a product first" |

---

## ⏱️ 120-Min Coding Plan

```
0–10   min  →  Clarify requirements, draw class diagram
10–25  min  →  Product, Coin, Note, Inventory
25–45  min  →  VendingMachineState interface + IdleState
45–70  min  →  ReadyState + DispenseState
70–90  min  →  VendingMachine (Singleton + delegation)
90–110 min  →  VendingMachineDemo (all test cases)
110–120 min →  Edge cases, clean up, explain design
```

---

## 🗣️ Things to Say Out Loud in Interview

- **State Pattern** removes if-else: each state handles its own valid/invalid operations
- **Singleton** ensures one machine instance — use `synchronized` if multithreaded
- **Delegation** — VendingMachine doesn't contain logic, just forwards to current state
- **Inventory** uses `HashMap`; mention `ConcurrentHashMap` for thread safety in production
- **State transition** is done inside state classes (e.g., ReadyState switches to DispenseState)
- `returnChange()` in ReadyState handles **cancel before payment** — important edge case

---

## 🔑 Quick Enum Reference

```java
enum Coin { ONE(1), TWO(2), FIVE(5), TEN(10); }
enum Note { TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100); }
// Access value: Coin.FIVE.getValue() → 5
```