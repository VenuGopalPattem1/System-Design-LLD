package VendingMachine.state;

import VendingMachine.entity.Product;
import VendingMachine.entity.VendingMachine;
import VendingMachine.enums.Coins;
import VendingMachine.enums.Notes;

public class ReadyState implements VendingMachineState {
    private VendingMachine machine;

    public ReadyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void selectProduct(Product p) {
        System.out.println("Already selected: " + machine.getSelectedProduct().getName());
    }

    @Override
    public void insertCoin(Coins c) {
        machine.addPayment(c.getVal());
        printBalance();
        checkAndDispense();

    }

    @Override
    public void insertNote(Notes n) {
        machine.addPayment(n.getVal());
        printBalance();
        checkAndDispense();
    }

    @Override
    public void dispenseProduct() {
        double need = machine.getSelectedProduct().getPrice() - machine.getTotalPayment();
        System.out.println("Need Rs" + (int) need + " more.");
    }

    @Override
    public void returnChange() {
        System.out.println("Refunding Rs" + (int) machine.getTotalPayment());
        machine.resetPayment();
        machine.setSelectedProduct(null);
        machine.setCurrentState(machine.getIdleState());
    }

    private void printBalance() {
        System.out.println("Paid: Rs" + (int) machine.getTotalPayment()
                + " / Rs" + (int) machine.getSelectedProduct().getPrice());
    }

    private void checkAndDispense() {
        if (machine.getTotalPayment() >= machine.getSelectedProduct().getPrice()) {
            machine.setCurrentState(machine.getDispenseState());
            machine.dispenseProduct();
        }
    }
}
