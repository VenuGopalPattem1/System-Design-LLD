package VendingMachine.entity;

import VendingMachine.enums.Coins;
import VendingMachine.enums.Notes;
import VendingMachine.state.DispenseState;
import VendingMachine.state.IdleState;
import VendingMachine.state.ReadyState;
import VendingMachine.state.VendingMachineState;

public class VendingMachine {
    private static VendingMachine instance;

    private final Inventory inventory;
    private final VendingMachineState idleState;
    private final VendingMachineState readyState;
    private final VendingMachineState dispenseState;

    private VendingMachineState currentState;
    private Product selectedProduct;
    private double totalPayment;

    private VendingMachine() {
        inventory = new Inventory();
        idleState = new IdleState(this);
        readyState = new ReadyState(this);
        dispenseState = new DispenseState(this);
        currentState = idleState;
    }

    public static VendingMachine getInstance() {
        if (instance == null)
            instance = new VendingMachine();
        return instance;
    }

    // Delegate to current state
    public void selectProduct(Product p) {
        currentState.selectProduct(p);
    }

    public void insertCoin(Coins c) {
        currentState.insertCoin(c);
    }

    public void insertNote(Notes n) {
        currentState.insertNote(n);
    }

    public void dispenseProduct() {
        currentState.dispenseProduct();
    }

    public void returnChange() {
        currentState.returnChange();
    }

    // Getters / Setters used by state classes

    public Inventory getInventory() {
        return inventory;
    }

    public VendingMachineState getIdleState() {
        return idleState;
    }

    public VendingMachineState getReadyState() {
        return readyState;
    }

    public VendingMachineState getDispenseState() {
        return dispenseState;
    }

    public VendingMachineState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(VendingMachineState currentState) {
        this.currentState = currentState;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(Product selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }

    public void addPayment(double amt) {
        totalPayment += amt;
    }

    public void resetPayment() {
        totalPayment = 0;
    }
}
