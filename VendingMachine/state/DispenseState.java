package VendingMachine.state;

import VendingMachine.entity.Product;
import VendingMachine.entity.VendingMachine;
import VendingMachine.enums.Coins;
import VendingMachine.enums.Notes;

public class DispenseState implements VendingMachineState{
    private VendingMachine machine;

    public DispenseState(VendingMachine machine){
        this.machine=machine;
    }
    @Override
    public void selectProduct(Product p) {
        System.out.println("Please wait, dispensing...");
    }

    @Override
    public void insertCoin(Coins c) {
       System.out.println("Please wait, dispensing...");
    }

    @Override
    public void insertNote(Notes n) {
        System.out.println("Please wait, dispensing...");
    }

    @Override
    public void dispenseProduct() {
        Product p=machine.getSelectedProduct();
        machine.getInventory().dispense(p);
        System.out.println("\n Dispensed: " + p.getName());
        returnChange();
    }

    @Override
    public void returnChange() {
        double change=machine.getTotalPayment()-machine.getSelectedProduct().getPrice();
        if(change>0){
            if (change > 0) System.out.println(" Change returned: Rs" + (int) change);
        }
        machine.resetPayment();
        machine.setSelectedProduct(null);
        machine.setCurrentState(machine.getIdleState());
    }

}
